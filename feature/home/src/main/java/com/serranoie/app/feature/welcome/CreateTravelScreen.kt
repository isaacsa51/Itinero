package com.serranoie.app.feature.welcome

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.serranoie.app.designsystemlib.ui.ComponentPreview
import com.serranoie.app.designsystemlib.ui.DevicePreview
import com.serranoie.app.designsystemlib.ui.PreviewWrapper
import com.serranoie.app.designsystemlib.ui.theme.component.IButton
import com.serranoie.app.designsystemlib.ui.theme.component.ITextField
import com.serranoie.app.designsystemlib.ui.theme.component.SelectField
import com.serranoie.app.designsystemlib.ui.utils.Constants.basePadding
import com.serranoie.app.designsystemlib.ui.utils.Constants.commonCornerRadius
import com.serranoie.app.designsystemlib.ui.utils.Constants.extraSmallPadding
import com.serranoie.app.designsystemlib.ui.utils.Constants.mediumPadding
import com.serranoie.app.designsystemlib.ui.utils.Constants.smallPadding
import com.serranoie.app.feature.TravelUiState
import com.serranoie.app.feature.home.R
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CreateTravelScreen(
    uiState: TravelUiState = TravelUiState.Idle,
    onTravelCreated: (String, String, String, String, String, String, String, String, String, String, String, String, String, String) -> Unit = { _, _, _, _, _, _, _, _, _, _, _, _, _, _ -> },
    onNavigateBack: () -> Unit = {}
) {

    val snackState = remember { SnackbarHostState() }
    SnackbarHost(hostState = snackState, Modifier.zIndex(1f))

    val scope = rememberCoroutineScope()

    var showDatePicker by remember { mutableStateOf(false) }

    var groupName by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var summary by remember { mutableStateOf("") }
    var accommodationName by remember { mutableStateOf("") }
    var accommodationPhone by remember { mutableStateOf("") }
    var accommodationCheckIn by remember { mutableStateOf("") }
    var accommodationCheckOut by remember { mutableStateOf("") }
    var accommodationLocation by remember { mutableStateOf("") }
    var accommodationMapUri by remember { mutableStateOf("") }
    var reservationCode by remember { mutableStateOf("") }
    var extraInfo by remember { mutableStateOf("") }
    var additionalInfo by remember { mutableStateOf("") }

    fun getPrettyDate(dateString: String): String {
        // dateString is in "yyyy-MM-dd"
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            val outputFormat = SimpleDateFormat("d 'of' MMMM", Locale.getDefault())
            outputFormat.format(date!!)
        } catch (e: Exception) {
            dateString
        }
    }

    // Pager setup
    val pages = listOf("Basic", "Accommodation", "Additional")
    var currentPage by remember { mutableStateOf(0) }

    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    val dateRangePickerState = rememberDateRangePickerState()
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    var showDateSheet by remember { mutableStateOf(false) }

    if (showDateSheet) {
        ModalBottomSheet(
            sheetState = bottomSheetState,
            onDismissRequest = { showDateSheet = false },
            shape = RoundedCornerShape(
                topStart = commonCornerRadius, topEnd = commonCornerRadius
            ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Box(modifier = Modifier
                    .weight(1f, fill = false)
                    .height(600.dp)
                    .fillMaxWidth()) {
                    DateRangePickerSample(dateRangePickerState)
                }
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 16.dp, bottom = 24.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    IButton(
                        onClick = {
                            coroutineScope.launch {
                                if (dateRangePickerState.selectedStartDateMillis != null && dateRangePickerState.selectedEndDateMillis != null) {
                                    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                    startDate =
                                        formatter.format(Date(dateRangePickerState.selectedStartDateMillis!!))
                                    endDate =
                                        formatter.format(Date(dateRangePickerState.selectedEndDateMillis!!))
                                }
                                bottomSheetState.hide()
                                showDateSheet = false
                            }
                        },
                        text = { Text("Done") }
                    )
                }
            }
        }
    }

    Scaffold(topBar = {
        ProgressTopBar(
            questionIndex = currentPage,
            totalQuestionsCount = pages.size,
            onClosePressed = onNavigateBack
        )
    }, bottomBar = {
        Surface {
            Row(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp),
            ) {
                if (currentPage > 0) {
                    IButton(
                        onClick = {
                            currentPage = (currentPage - 1).coerceAtLeast(0)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        enabled = uiState !is TravelUiState.Loading,
                        text = { Text("Back") })
                    Spacer(modifier = Modifier.width(16.dp))
                }
                if (currentPage == pages.lastIndex) {
                    IButton(
                        onClick = {
                            Log.d(
                                "ISAAC",
                                "Create Trip clicked with destination: $destination, dates: $startDate - $endDate"
                            )
                            onTravelCreated(
                                groupName,
                                destination,
                                startDate,
                                endDate,
                                summary,
                                accommodationName,
                                accommodationPhone,
                                accommodationCheckIn,
                                accommodationCheckOut,
                                accommodationLocation,
                                accommodationMapUri,
                                reservationCode,
                                extraInfo,
                                additionalInfo,
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        enabled = destination.isNotBlank() && startDate.isNotBlank() && endDate.isNotBlank() && uiState !is TravelUiState.Loading,
                        text = {
                            if (uiState is TravelUiState.Loading) {
                                LoadingIndicator()
                            } else {
                                Text("Done")
                            }
                        })
                } else {

                    val canGoNext = when (pages[currentPage]) {
                        "Basic" -> groupName.isNotBlank() && destination.isNotBlank() && startDate.isNotBlank() && endDate.isNotBlank() && summary.isNotBlank()
                        "Accommodation" -> accommodationName.isNotBlank() && accommodationPhone.isNotBlank() && accommodationCheckIn.isNotBlank() && accommodationCheckOut.isNotBlank() && accommodationLocation.isNotBlank()
                        "Additional" -> reservationCode.isNotBlank() && extraInfo.isNotBlank() && additionalInfo.isNotBlank()
                        else -> true
                    }
                    IButton(
                        onClick = {
                            currentPage = (currentPage + 1).coerceAtMost(pages.lastIndex)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        enabled = canGoNext && uiState !is TravelUiState.Loading,
                        text = { Text("Next") })
                }
            }
        }
    }, snackbarHost = { SnackbarHost(snackState) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = basePadding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            when (pages[currentPage]) {
                "Basic" -> {
                    SectionLabel(
                        text = "Basic Information",
                        description = "Help us know more about your trip via adding basic information.",
                        imageRes = R.drawable.img_basic_info,
                        modifier = Modifier.padding(bottom = basePadding)
                    )

                    Text(
                        text = "What name should this trip have?",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 0.dp)
                    )
                    ITextField(
                        value = groupName,
                        onValueChange = { groupName = it },
                        label = "Group Name",
                        leadingIcon = Icons.Default.Hotel,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = extraSmallPadding)
                    )

                    Text(
                        text = "Where are you going on your trip?",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.fillMaxWidth()
                    )
                    ITextField(
                        value = destination,
                        onValueChange = { destination = it },
                        label = "Destination",
                        leadingIcon = Icons.Default.LocationOn,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = extraSmallPadding)
                    )

                    Text(
                        text = "Give your trip a brief summary for easy referencing.",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.fillMaxWidth()
                    )
                    ITextField(
                        value = summary,
                        onValueChange = { summary = it },
                        label = "Summary",
                        leadingIcon = Icons.Default.Description,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = extraSmallPadding)
                    )

                    SelectField(
                        value = if (startDate.isNotBlank() && endDate.isNotBlank()) {
                            "${getPrettyDate(startDate)} to ${getPrettyDate(endDate)}"
                        } else {
                            "Select travel dates"
                        },
                        onSelect = {
                            coroutineScope.launch {
                                showDateSheet = true
                            }
                        },
                        label = "When will your trip start and end?",
                        leadingIcon = Icons.Rounded.CalendarToday,
                        containerColor = Color.Transparent,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = mediumPadding)
                    )
                }

                "Accommodation" -> {
                    SectionLabel(
                        text = "Accommodation Details",
                        description = "Add details about your accommodation, if applicable.",
                        imageRes = R.drawable.img_accomodation,
                        modifier = Modifier.padding(bottom = basePadding)
                    )

                    Text(
                        text = "Where are you staying during your trip?",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    ITextField(
                        value = accommodationName,
                        onValueChange = { accommodationName = it },
                        label = "Accommodation Name",
                        leadingIcon = Icons.Default.Hotel,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "A phone number helps in contacting your accommodation.",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    ITextField(
                        value = accommodationPhone,
                        onValueChange = { accommodationPhone = it },
                        label = "Accommodation Phone",
                        leadingIcon = Icons.Default.Numbers,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "When will you check in to your accommodation?",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    ITextField(
                        value = accommodationCheckIn,
                        onValueChange = { accommodationCheckIn = it },
                        label = "Check-in Date/Time",
                        leadingIcon = Icons.Rounded.CalendarToday,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "When will you check out of your accommodation?",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    ITextField(
                        value = accommodationCheckOut,
                        onValueChange = { accommodationCheckOut = it },
                        label = "Check-out Date/Time",
                        leadingIcon = Icons.Rounded.CalendarToday,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // TODO: Use Places SDK to indentify correctly the location

                    Text(
                        text = "What's the address or location of your accommodation?",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    ITextField(
                        value = accommodationLocation,
                        onValueChange = { accommodationLocation = it },
                        label = "Accommodation Location",
                        leadingIcon = Icons.Default.LocationOn,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "Provide a map link for easy navigation (optional).",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    ITextField(
                        value = accommodationMapUri,
                        onValueChange = { accommodationMapUri = it },
                        label = "Map URI",
                        leadingIcon = Icons.Default.LocationOn,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                "Additional" -> {
                    SectionLabel(
                        text = "Additional Information",
                        description = "Add any additional information that might be relevant to your trip.",
                        imageRes = R.drawable.img_extra_info,
                        modifier = Modifier.padding(bottom = basePadding)
                    )

                    Text(
                        text = "If you have a reservation code, enter it here for easier check-in.",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    ITextField(
                        value = reservationCode,
                        onValueChange = { reservationCode = it },
                        label = "Reservation Code",
                        leadingIcon = Icons.Default.Numbers,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "Add anything important about your trip that wasn't covered above.",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    ITextField(
                        value = extraInfo,
                        onValueChange = { extraInfo = it },
                        label = "Extra Info",
                        leadingIcon = Icons.Default.Info,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "Is there any more information you want to add about this trip?",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    ITextField(
                        value = additionalInfo,
                        onValueChange = { additionalInfo = it },
                        label = "Additional Info",
                        leadingIcon = Icons.Default.Info,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                }
            }
        }
        if (uiState is TravelUiState.Loading) {
            Box(
                modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
            ) {
                LoadingIndicator()
            }
        }
    }
}

fun getFormattedDate(timeInMillis: Long): String {
    val calender = Calendar.getInstance()
    calender.timeInMillis = timeInMillis
    val dateFormat = SimpleDateFormat("dd/MM/yyyy")
    return dateFormat.format(calender.timeInMillis)
}

fun dateValidator(): (Long) -> Boolean {
    return { timeInMillis ->
        val endCalenderDate = Calendar.getInstance()
        endCalenderDate.timeInMillis = timeInMillis
        endCalenderDate.set(Calendar.DATE, Calendar.DATE + 20)
        timeInMillis > Calendar.getInstance().timeInMillis && timeInMillis < endCalenderDate.timeInMillis
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DateRangePickerSample(state: DateRangePickerState) {
    DateRangePicker(
        state = state, modifier = Modifier, title = {
        Text(
            text = "Select the start & end of the trip",
            style = MaterialTheme.typography.titleLargeEmphasized,
            modifier = Modifier.padding(basePadding)
        )
    }, headline = {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = if (state.selectedStartDateMillis != null) {
                        getFormattedDate(state.selectedStartDateMillis!!)
                    } else {
                        "Start Date"
                    },
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            Box(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = if (state.selectedEndDateMillis != null) {
                        getFormattedDate(state.selectedEndDateMillis!!)
                    } else {
                        "End Date"
                    },
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }, showModeToggle = false, colors = DatePickerDefaults.colors(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        headlineContentColor = MaterialTheme.colorScheme.primary,
        weekdayContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        subheadContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        yearContentColor = MaterialTheme.colorScheme.secondary,
        currentYearContentColor = MaterialTheme.colorScheme.secondary,
        selectedYearContainerColor = MaterialTheme.colorScheme.tertiary,
        disabledDayContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        dayInSelectionRangeContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
        dayInSelectionRangeContentColor = MaterialTheme.colorScheme.onTertiaryContainer,
        selectedDayContainerColor = MaterialTheme.colorScheme.tertiary,
    )
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SectionLabel(text: String, description: String, imageRes: Int, modifier: Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            modifier = Modifier.fillMaxWidth(0.8f),
            painter = painterResource(imageRes),
            contentDescription = null,
            contentScale = ContentScale.Fit
        )

        Text(
            text = text,
            style = MaterialTheme.typography.titleLargeEmphasized,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = smallPadding)
        )

        Text(
            text = description,
            style = MaterialTheme.typography.labelMediumEmphasized,
            modifier = Modifier.padding(vertical = extraSmallPadding)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressTopBar(
    questionIndex: Int,
    totalQuestionsCount: Int,
    onClosePressed: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        CenterAlignedTopAppBar(
            navigationIcon = {
                IconButton(
                    onClick = onClosePressed,
                    modifier = Modifier.padding(4.dp),
                ) {
                    Icon(
                        Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Go back",
                    )
                }
            },
            title = {
                TopAppBarTitle(
                    questionIndex = questionIndex,
                    totalQuestionsCount = totalQuestionsCount,
                )
            },
        )

        val animatedProgress by animateFloatAsState(
            targetValue = (questionIndex + 1) / totalQuestionsCount.toFloat(),
            animationSpec = androidx.compose.material3.ProgressIndicatorDefaults.ProgressAnimationSpec,
        )
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.tertiary,
            trackColor = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.13f),
            strokeCap = androidx.compose.material3.ProgressIndicatorDefaults.LinearStrokeCap,
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun TopAppBarTitle(
    questionIndex: Int,
    totalQuestionsCount: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Create new trip",
            style = MaterialTheme.typography.titleMediumEmphasized,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Row(horizontalArrangement = Arrangement.Center) {
            Text(
                text = (questionIndex + 1).toString(),
                style = MaterialTheme.typography.labelMediumEmphasized,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = " of $totalQuestionsCount",
                style = MaterialTheme.typography.labelMediumEmphasized,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
            )
        }
    }
}

@ComponentPreview
@Composable
private fun SurveyTopBarPreview() {
    PreviewWrapper {
        ProgressTopBar(
            questionIndex = 3,
            totalQuestionsCount = 6,
            onClosePressed = { },
        )
    }
}

@DevicePreview
@Composable
private fun CreateTravelScreenPreview() {
    PreviewWrapper {
        CreateTravelScreen(
            onTravelCreated = { _, _, _, _, _, _, _, _, _, _, _, _, _, _ -> },
            onNavigateBack = {})
    }
}

@ComponentPreview
@Composable
fun GreetingPreview16() {
    val state = rememberDateRangePickerState()
    PreviewWrapper {
        DateRangePickerSample(state)
    }
}