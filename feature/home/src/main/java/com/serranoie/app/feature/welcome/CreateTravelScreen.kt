package com.serranoie.app.feature.welcome

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.serranoie.app.designsystem.ui.PreviewWrapper
import com.serranoie.app.designsystem.ui.ThemePreviews
import com.serranoie.app.feature.TravelUiState
import com.serranoie.app.feature.TravelViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CreateTravelScreen(
    viewModel: TravelViewModel,
    onTravelCreated: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var destination by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var summary by remember { mutableStateOf("") }
    var accommodation by remember { mutableStateOf("") }
    var reservationCode by remember { mutableStateOf("") }
    var extraInfo by remember { mutableStateOf("") }
    var additionalInfo by remember { mutableStateOf("") }

    LaunchedEffect(key1 = true) {
        viewModel.uiState.collectLatest { state ->
            when (state) {
                is TravelUiState.Success<*> -> {
                    onTravelCreated()
                    viewModel.resetState()
                }
                is TravelUiState.Error -> {
                    snackbarHostState.showSnackbar(state.message)
                    viewModel.resetState()
                }
                else -> {} // No action needed for other states
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create New Trip") }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (uiState is TravelUiState.Loading) {
                LoadingIndicator()
            }

            OutlinedTextField(
                value = destination,
                onValueChange = { destination = it },
                label = { Text("Destination") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = startDate,
                onValueChange = { startDate = it },
                label = { Text("Start Date (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = endDate,
                onValueChange = { endDate = it },
                label = { Text("End Date (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = summary,
                onValueChange = { summary = it },
                label = { Text("Summary") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = accommodation,
                onValueChange = { accommodation = it },
                label = { Text("Accommodation") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = reservationCode,
                onValueChange = { reservationCode = it },
                label = { Text("Reservation Code") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = extraInfo,
                onValueChange = { extraInfo = it },
                label = { Text("Extra Info") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = additionalInfo,
                onValueChange = { additionalInfo = it },
                label = { Text("Additional Info") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.createTravel(
                        destination,
                        startDate,
                        endDate,
                        summary,
                        accommodation,
                        reservationCode,
                        extraInfo,
                        additionalInfo
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = destination.isNotBlank() && startDate.isNotBlank() && endDate.isNotBlank()
            ) {
                Text("Create Trip")
            }
        }
    }
}

@ThemePreviews
@Composable
private fun CreateTravelScreenPreview() {
    PreviewWrapper {
//        CreateTravelScreen(
//            viewModel = TravelViewModel(
//                travelUseCase = TravelUseCase(
//                    getAllTravels = GetAllTravelsUseCase(repository = TravelRepositoryImpl()),
//                    getTravelById = GetTravelByIdUseCase(repository = TravelRepositoryImpl()),
//                    joinTravel = JoinTravelUseCase(repository = TravelRepositoryImpl()),
//                    leaveTravel = LeaveTravelUseCase(repository = TravelRepositoryImpl()),
//                    createTravel = CreateTravelUseCase(repository = TravelRepositoryImpl())
//                )
//            ),
//            onTravelCreated = {}
//        )
    }
}