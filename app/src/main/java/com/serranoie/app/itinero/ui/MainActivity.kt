/*
 * Copyright (c) 2025 Isaac Serrano.
 *
 * File: MainActivity.kt
 * Project: Itinero
 * Module: Itinero.app.main
 *
 * This file belongs to the project: Itinero.
 * Last edited: 03 junio 2025
 */

package com.serranoie.app.itinero.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.messaging.FirebaseMessaging
import com.serranoie.app.core.navigation.Route
import com.serranoie.app.designsystemlib.ui.network.NetworkObserver
import com.serranoie.app.designsystemlib.ui.network.NetworkStatusBar
import com.serranoie.app.designsystemlib.ui.theme.ItineroTheme
import com.serranoie.app.itinero.navigation.NavGraph
import com.serranoie.app.itinero.utils.WorkManagerScheduler
import com.serranoie.core.settings.SettingsViewModel
import com.serranoie.itinero.core.domain.exception.UnauthorizedException
import com.serranoie.itinero.core.domain.model.DeviceType
import com.serranoie.itinero.core.domain.repository.AuthPreferencesRepository
import com.serranoie.itinero.core.domain.repository.AuthRepository
import com.serranoie.itinero.core.domain.repository.FCMRepository
import com.serranoie.itinero.core.domain.repository.NotificationRepository
import com.serranoie.itinero.core.domain.result.Result
import com.serranoie.itinero.core.domain.usecase.CheckAndUpdateLanguageOnAppEntryUseCase
import com.serranoie.itinero.core.domain.usecase.LogoutObserverUseCase
import com.serranoie.itinero.core.domain.usecase.TravelUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.KoinAndroidContext

@OptIn(ExperimentalAnimationApi::class, ExperimentalCoroutinesApi::class, FlowPreview::class)
class MainActivity : ComponentActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var navController: NavHostController
    private val authPreferences: AuthPreferencesRepository by inject()
    private val authRepository: AuthRepository by inject()
    private val travelUseCase: TravelUseCase by inject()
    private val logoutObserverUseCase: LogoutObserverUseCase by inject()
    private val networkObserver: NetworkObserver by inject()
    private val settingsViewModel: SettingsViewModel by inject()
    private val notificationRepository: NotificationRepository by inject()
    private val fcmRepository: FCMRepository by inject()
    private val checkAndUpdateLanguageOnAppEntryUseCase: CheckAndUpdateLanguageOnAppEntryUseCase by inject()

    private var locationPermissionGranted by mutableStateOf(false)
    private var pendingDeepLinkCode: String? = null
    private var showNotificationRationale by mutableStateOf(false)

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            locationPermissionGranted = isGranted
            if (!isGranted) Log.d(TAG, "Location permission denied")
        }

    private val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                onNotificationPermissionGranted()
            } else {
                Log.d(TAG, "Notification permission denied - App will not show notifications")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        checkLanguageOnAppEntry()

        locationPermissionGranted = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        getLocationPermission()
        askNotificationPermission()

        pendingDeepLinkCode = parseGroupCodeFromIntent(intent)

        handleNotificationIntent(intent)

        initializeFirebaseMessaging()

        setContent {
            KoinAndroidContext {
                navController = rememberNavController()

                var isReady by remember { mutableStateOf(false) }
                var startDestination by remember { mutableStateOf("") }
                val isConnected by networkObserver.isConnectedFlow.collectAsState(initial = true)

                splashScreen.setKeepOnScreenCondition { !isReady }

                if (showNotificationRationale) {
                    NotificationPermissionRationaleDialog(onDismiss = {
                        showNotificationRationale = false
                    }, onConfirm = {
                        showNotificationRationale = false
                        requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    })
                }

                LaunchedEffect(Unit) {
                    startDestination = determineStartDestination()
                    isReady = true
                }

                LaunchedEffect(navController, isReady) {
                    if (isReady) {
                        logoutObserverUseCase.logoutEvents.onEach { _: Unit ->
                            Log.d(TAG, "Logout event received, navigating to auth screen")
                            if (navController.currentDestination?.route != Route.AuthNavigation.route) {
                                navController.navigate(Route.AuthNavigation.route) {
                                    popUpTo(0) { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        }.launchIn(this)

                        pendingDeepLinkCode?.let { code ->
                            navigateToJoinWithCode(code)
                            pendingDeepLinkCode = null
                        }

                        val token = authRepository.getAuthToken()
                        if (!token.isNullOrBlank()) {
                            WorkManagerScheduler.schedulePendingMembersCheck(this@MainActivity)
                            WorkManagerScheduler.scheduleOneTimePendingMembersCheck(this@MainActivity)
                        }
                    }
                }

                val isDarkTheme by settingsViewModel.isDarkTheme.collectAsState()
                val isMaterialYou by settingsViewModel.isMaterialYouEnabled.collectAsState()

                ItineroTheme(
                    darkTheme = isDarkTheme, materialYou = isMaterialYou
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize()
                    ) { innerPadding ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                                .consumeWindowInsets(innerPadding)
                        ) {
                            NetworkStatusBar(isConnected = isConnected)
                            if (startDestination.isNotEmpty()) {
                                Box {
                                    NavGraph(
                                        navController = navController,
                                        startDestination = startDestination
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun NotificationPermissionRationaleDialog(
        onDismiss: () -> Unit, onConfirm: () -> Unit
    ) {
        AlertDialog(onDismissRequest = onDismiss, title = {
            Text("Stay Updated with Your Trips")
        }, text = {
            Text(
                "Enable notifications to receive important updates about:\n\n" + "• New expenses added to your trips\n" + "• Pending member requests\n" + "• Payment reminders and settlements\n" + "• Trip updates from other members\n\n" + "You can always disable notifications later in settings."
            )
        }, confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Enable Notifications")
            }
        }, dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Maybe Later")
            }
        })
    }

    private fun initializeFirebaseMessaging() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@addOnCompleteListener
            }
            val token = task.result
            sendTokenToServer(token)
        }
    }

    private fun sendTokenToServer(token: String) {
        lifecycleScope.launch {
            try {
                val deviceId =
                    Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

                when (val result = fcmRepository.saveFCMToken(
                    fcmToken = token, deviceId = deviceId, deviceType = DeviceType.ANDROID
                )) {
                    is Result.Success -> {

                    }

                    is Result.Error -> {
                        Log.e(
                            TAG,
                            "Failed to register FCM token with server: ${result.exception.message}"
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error sending FCM token to server", e)
            }
        }
    }

    private suspend fun subscribeToUserTopics() {
        try {
            Log.d(TAG, "Starting topic subscription process...")

            when (val tripsResult = travelUseCase.getAllTravels()) {
                is Result.Success -> {
                    val trips = tripsResult.data
                    Log.d(TAG, "Found ${trips.size} trips for topic subscription")

                    if (trips.isEmpty()) {
                        Log.d(TAG, "No trips found, skipping topic subscription")
                        return
                    }

                    val tokensResult = fcmRepository.getFCMTokens()
                    when (tokensResult) {
                        is Result.Success -> {
                            val tokens = tokensResult.data
                            Log.d(TAG, "Got ${tokens.size} FCM tokens for topic subscription")

                            if (tokens.isEmpty()) {
                                Log.w(TAG, "No FCM tokens available for topic subscription")
                                return
                            }

                            // Subscribe to topics for each trip the user is part of
                            trips.forEach { trip ->
                                val topicName = "trip_${trip.groupCode}_updates"
                                try {
                                    lifecycleScope.launch {
                                        when (val subscriptionResult =
                                            fcmRepository.subscribeToTopic(tokens, topicName)) {
                                            is Result.Success -> {
                                                if (subscriptionResult.data) {
                                                    Log.d(
                                                        TAG,
                                                        "Successfully subscribed to topic: $topicName"
                                                    )
                                                } else {
                                                    Log.w(
                                                        TAG,
                                                        "Partial failure subscribing to topic: $topicName"
                                                    )
                                                }
                                            }

                                            is Result.Error -> {
                                                Log.e(
                                                    TAG,
                                                    "Failed to subscribe to topic: $topicName",
                                                    subscriptionResult.exception
                                                )
                                            }
                                        }
                                    }
                                } catch (e: Exception) {
                                    Log.e(
                                        TAG, "Exception during topic subscription for $topicName", e
                                    )
                                }
                            }
                        }

                        is Result.Error -> {
                            Log.e(
                                TAG,
                                "Failed to get FCM tokens for topic subscription: ${tokensResult.exception.message}"
                            )
                        }
                    }
                }

                is Result.Error -> {
                    Log.e(
                        TAG,
                        "Failed to get trips for topic subscription: ${tripsResult.exception.message}"
                    )
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during topic subscription process", e)
        }
    }

    private fun onNotificationPermissionGranted() {
        WorkManagerScheduler.schedulePendingMembersCheck(this)
    }

    private fun handleNotificationIntent(intent: Intent) {
        if (intent.hasExtra("action") || intent.extras != null) {
            val action = intent.getStringExtra("action")
            val data = intent.extras

            when (action) {
                "show_pending_members" -> {
                    if (this::navController.isInitialized) {
                        WorkManagerScheduler.scheduleOneTimePendingMembersCheck(this)
                    }
                }

                "show_expense_details" -> {
                    val expenseId = data?.getString("expenseId")
                    val tripId = data?.getString("tripId")
                    val amount = data?.getString("amount")

                    Log.d(
                        TAG,
                        "User tapped expense notification: expense=$expenseId, trip=$tripId, amount=$amount"
                    )

                    if (this::navController.isInitialized && !tripId.isNullOrEmpty()) {
                        navController.navigate("expenses/$tripId") {
                            launchSingleTop = true
                        }
                    }
                }

                else -> {
                    data?.let { bundle ->
                        val notificationType = bundle.getString("type")
                        when (notificationType) {
                            "expense_created" -> {
                                val tripId = bundle.getString("tripId")
                                val expenseId = bundle.getString("expenseId")
                                Log.d(
                                    TAG, "Expense created notification: $expenseId in trip $tripId"
                                )
                            }

                            "trip_update" -> {
                                val tripId = bundle.getString("tripId")
                                Log.d(TAG, "Trip update notification for trip: $tripId")
                            }

                            "member_request" -> {
                                Log.d(TAG, "Member request notification")
                            }
                        }
                    }
                }
            }
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    Log.d(TAG, "Notification permission already granted")
                    onNotificationPermissionGranted()
                }

                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    showNotificationRationale = true
                }

                else -> {
                    requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            Log.d(TAG, "Pre-Android 13 device - notifications enabled by default")
            onNotificationPermissionGranted()
        }
    }

    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun checkLanguageOnAppEntry() {
        lifecycleScope.launch {
            try {
                val result = checkAndUpdateLanguageOnAppEntryUseCase.invoke()
                when (result) {
                    is Result.Success -> {
                        testNotificationLanguage(result.data)
                    }

                    is Result.Error -> {
                        Log.e(TAG, "Language check had issues: ${result.exception.message}")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error during language check: ${e.message}")
            }
        }
    }

    private fun testNotificationLanguage(language: String) {
        lifecycleScope.launch {
            try {
                Log.i(TAG, "TESTING: Making API call with language '$language'")

                val result = travelUseCase.getAllTravels()

                when (result) {
                    is Result.Success -> {
                        Log.i(
                            TAG,
                            "TEST: API call successful - check HTTP logs above for Accept-Language header"
                        )
                    }

                    is Result.Error -> {
                        Log.w(TAG, "TEST: API call failed but header should still be logged")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "TEST: Error during API test: ${e.message}")
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleNotificationIntent(intent)

        val code = parseGroupCodeFromIntent(intent) ?: return
        if (this::navController.isInitialized) {
            navigateToJoinWithCode(code)
        } else {
            pendingDeepLinkCode = code
        }
    }

    private fun navigateToJoinWithCode(code: String) {
        val route = "${Route.JoinTrip.route}?code=$code"
        Log.d(TAG, "Navigating to: $route")
        navController.navigate(route) {
            launchSingleTop = true
        }
    }

    private fun parseGroupCodeFromIntent(intent: Intent?): String? {
        if (intent?.action != Intent.ACTION_VIEW) return null
        val data: Uri = intent.data ?: return null
        val codeParam = data.getQueryParameter("code")
        if (!codeParam.isNullOrBlank()) return codeParam
        return data.lastPathSegment
    }

    private suspend fun determineStartDestination(): String {
        return withContext(Dispatchers.IO) {
            if (!authPreferences.isOnboardingCompleted()) {
                return@withContext Route.AppStartNavigation.route
            }

            val token = authRepository.getAuthToken()
            if (token.isNullOrBlank()) {
                return@withContext Route.AuthNavigation.route
            }

            try {
                validateTokenWithServer()
                Route.WelcomeNavigation.route
            } catch (e: Exception) {
                authRepository.logout()
                Route.AuthNavigation.route
            }
        }
    }

    private suspend fun validateTokenWithServer() {
        try {
            val result = travelUseCase.getAllTravels()
        } catch (e: UnauthorizedException) {
            throw e
        } catch (e: Exception) {
            if (e.message?.contains("404") == true) {
                throw UnauthorizedException("User not found")
            }
        }
    }
}
