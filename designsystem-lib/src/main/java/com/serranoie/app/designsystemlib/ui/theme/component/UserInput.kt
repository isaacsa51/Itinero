package com.serranoie.app.designsystemlib.ui.theme.component

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.InsertPhoto
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.serranoie.app.designsystemlib.ui.ComponentPreview
import com.serranoie.app.designsystemlib.ui.PreviewWrapper
import com.serranoie.app.designsystemlib.ui.utils.Constants.basePadding
import com.serranoie.app.designsystemlib.ui.utils.Constants.commonCornerRadius
import com.serranoie.app.designsystemlib.ui.utils.Constants.extraSmallPadding
import com.serranoie.app.designsystemlib.ui.utils.Constants.mediumPadding
import com.serranoie.app.designsystemlib.ui.utils.Constants.smallPadding
import com.serranoie.app.designsystemlib.ui.utils.standardPadding
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue

@ComponentPreview
@Composable
fun UserInputPreview() {
    PreviewWrapper {
        UserInput(onMessageSent = {}, onTypingStarted = {}, onTypingStopped = {})
    }
}

@ComponentPreview
@Composable
fun UserInputWithAttachmentMenuPreview() {
    PreviewWrapper {
        Column {
            AttachmentMenu(
                onDismiss = { },
                onOptionSelected = { }
            )

            Surface(tonalElevation = 2.dp, contentColor = MaterialTheme.colorScheme.secondary) {
                Column {
                    UserInputText(
                        textFieldValue = TextFieldValue(""),
                        onTextChanged = { },
                        keyboardShown = false,
                        onTextFieldFocused = { },
                        onMessageSent = { },
                        focusState = false,
                        onAttachmentClick = { }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UserInput(
    onMessageSent: (String) -> Unit,
    modifier: Modifier = Modifier,
    resetScroll: () -> Unit = {},
    onTypingStarted: () -> Unit = {},
    onTypingStopped: () -> Unit = {},
) {
    var showAttachmentMenu by remember { mutableStateOf(false) }

    if (showAttachmentMenu) {
        BackHandler(onBack = { showAttachmentMenu = false })
    }

    var textState by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue())
    }

    var textFieldFocusState by remember { mutableStateOf(false) }

    var isCurrentlyTyping by remember { mutableStateOf(false) }
    var lastTypingTime by remember { mutableStateOf(0L) }

    // Handle typing detection with debouncing
    LaunchedEffect(textState.text) {
        val currentTime = System.currentTimeMillis()
        lastTypingTime = currentTime

        if (textState.text.isNotEmpty() && !isCurrentlyTyping) {
            isCurrentlyTyping = true
            onTypingStarted()
        } else if (textState.text.isEmpty() && isCurrentlyTyping) {
            isCurrentlyTyping = false
            onTypingStopped()
        }

        if (isCurrentlyTyping) {
            delay(2000)
            if (currentTime == lastTypingTime && isCurrentlyTyping) {
                isCurrentlyTyping = false
                onTypingStopped()
            }
        }
    }

    LaunchedEffect(textFieldFocusState) {
        if (!textFieldFocusState && isCurrentlyTyping) {
            isCurrentlyTyping = false
            onTypingStopped()
        }
    }

    LaunchedEffect(textState.text.isEmpty()) {
        if (textState.text.isEmpty() && isCurrentlyTyping) {
            isCurrentlyTyping = false
            onTypingStopped()
        }
    }

    Surface(tonalElevation = 2.dp, contentColor = MaterialTheme.colorScheme.secondary) {
        Column(modifier = modifier) {
            AnimatedVisibility(
                visible = showAttachmentMenu,
                enter = slideInVertically(
                    initialOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(durationMillis = 300)
                ) + fadeIn(animationSpec = tween(durationMillis = 300)),
                exit = slideOutVertically(
                    targetOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(durationMillis = 200)
                ) + fadeOut(animationSpec = tween(durationMillis = 200))
            ) {
                AttachmentMenu(
                    onDismiss = { showAttachmentMenu = false },
                    onOptionSelected = { option ->
                        showAttachmentMenu = false
                    }
                )
            }

            UserInputText(
                textFieldValue = textState,
                onTextChanged = { textState = it },
                keyboardShown = textFieldFocusState,
                onTextFieldFocused = { focused ->
                    if (focused) {
                        resetScroll()
                        // Close attachment menu when text field gets focus
                        showAttachmentMenu = false
                    }
                    textFieldFocusState = focused
                },
                onMessageSent = {
                    onMessageSent(textState.text)
                    textState = TextFieldValue()
                    resetScroll()
                },
                focusState = textFieldFocusState,
                onAttachmentClick = { showAttachmentMenu = !showAttachmentMenu }
            )
        }
    }
}

private fun TextFieldValue.addText(newString: String): TextFieldValue {
    val newText = this.text.replaceRange(
        this.selection.start, this.selection.end, newString
    )
    val newSelection = TextRange(
        start = newText.length, end = newText.length
    )

    return this.copy(text = newText, selection = newSelection)
}

private val KeyboardShownKey = SemanticsPropertyKey<Boolean>("KeyboardShown")
private var SemanticsPropertyReceiver.keyboardShownProperty by KeyboardShownKey

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalFoundationApi::class)
@Composable
private fun UserInputText(
    keyboardType: KeyboardType = KeyboardType.Text,
    onTextChanged: (TextFieldValue) -> Unit,
    textFieldValue: TextFieldValue,
    keyboardShown: Boolean,
    onTextFieldFocused: (Boolean) -> Unit,
    onMessageSent: (String) -> Unit,
    focusState: Boolean,
    onAttachmentClick: () -> Unit
) {
    val swipeOffset = remember { mutableStateOf(0f) }
    var isRecordingMessage by remember { mutableStateOf(false) }
    val a11ylabel = "User input text field"
    val hasText = textFieldValue.text.isNotBlank()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimatedContent(
            targetState = isRecordingMessage,
            label = "text-field",
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) { recording ->
            if (recording) {
                RecordingIndicator { swipeOffset.value }
            } else {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onAttachmentClick,
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Icon(
                            Icons.Default.AttachFile,
                            contentDescription = "Menu",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        UserInputTextField(
                            textFieldValue,
                            onTextChanged,
                            onTextFieldFocused,
                            keyboardType,
                            focusState,
                            onMessageSent,
                            Modifier
                                .fillMaxWidth()
                                .semantics {
                                    contentDescription = a11ylabel
                                    keyboardShownProperty = keyboardShown
                                })
                    }
                }
            }
        }

        AnimatedContent(
            targetState = hasText, label = "button-state", modifier = Modifier.fillMaxHeight()
        ) { showSendIcon ->
            if (showSendIcon) {
                // Send button
                IconButton(
                    onClick = {
                        if (textFieldValue.text.isNotBlank()) {
                            onMessageSent(textFieldValue.text)
                        }
                    }, modifier = Modifier
                        .fillMaxHeight()
                        .padding(8.dp)
                ) {
                    Icon(
                        Icons.Default.Send,
                        contentDescription = "Send message",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            } else {
                RecordButton(
                    recording = isRecordingMessage,
                    swipeOffset = { swipeOffset.value },
                    onSwipeOffsetChange = { offset -> swipeOffset.value = offset },
                    onStartRecording = {
                        val consumed = !isRecordingMessage
                        isRecordingMessage = true
                        consumed
                    },
                    onFinishRecording = {
                        isRecordingMessage = false
                    },
                    onCancelRecording = {
                        isRecordingMessage = false
                    },
                    modifier = Modifier.fillMaxHeight()
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun BoxScope.UserInputTextField(
    textFieldValue: TextFieldValue,
    onTextChanged: (TextFieldValue) -> Unit,
    onTextFieldFocused: (Boolean) -> Unit,
    keyboardType: KeyboardType,
    focusState: Boolean,
    onMessageSent: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var lastFocusState by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxHeight()
            .padding(start = 32.dp),
        contentAlignment = Alignment.CenterStart
    ) {

        BasicTextField(
            value = textFieldValue,
            onValueChange = { onTextChanged(it) },
            modifier = Modifier
                .fillMaxSize()
                .onFocusChanged { state ->
                    if (lastFocusState != state.isFocused) {
                        onTextFieldFocused(state.isFocused)
                    }
                    lastFocusState = state.isFocused
                },
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType, imeAction = ImeAction.Send
            ),
            keyboardActions = KeyboardActions {
                if (textFieldValue.text.isNotBlank()) {
                    onMessageSent(textFieldValue.text)
                }
            },
            maxLines = 1,
            singleLine = true,
            cursorBrush = SolidColor(LocalContentColor.current),
            textStyle = LocalTextStyle.current.copy(color = LocalContentColor.current),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    innerTextField()
                }
            }
        )
    }

    if (textFieldValue.text.isEmpty() && !focusState) {
        Text(
            modifier = Modifier
                .wrapContentHeight()
                .align(Alignment.CenterStart)
                .padding(start = 32.dp),
            text = "Type a message...",
            style = MaterialTheme.typography.bodyLargeEmphasized.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
        )
    }
}

@Composable
private fun RecordingIndicator(swipeOffset: () -> Float) {
    var duration by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            duration += 1000
        }
    }
    Row(
        Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "pulse")

        val animatedPulse = infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 0.2f,
            animationSpec = infiniteRepeatable(
                tween(2000), repeatMode = RepeatMode.Reverse
            ),
            label = "pulse",
        )
        Box(
            Modifier
                .size(56.dp)
                .padding(24.dp)
                .graphicsLayer {
                    scaleX = animatedPulse.value; scaleY = animatedPulse.value
                }
                .clip(CircleShape)
                .background(Color.Red))

        Box(
            Modifier
                .fillMaxSize()
                .alignByBaseline()
                .clipToBounds()
        ) {
            val swipeThreshold = with(LocalDensity.current) { 200.dp.toPx() }
            Text(
                modifier = Modifier
                    .align(Alignment.Center)
                    .graphicsLayer {
                        translationX = swipeOffset() / 2
                        alpha = 1 - (swipeOffset().absoluteValue / swipeThreshold)
                    },
                textAlign = TextAlign.Center,
                text = "Swipe to cancel recording",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun FunctionalityNotAvailablePopup(onDismiss: () -> Unit) {
    AlertDialog(onDismissRequest = onDismiss, text = {
        Text(
            text = "Functionality not available \uD83D\uDE48",
            style = MaterialTheme.typography.bodyMedium
        )
    }, confirmButton = {
        TextButton(onClick = onDismiss) {
            Text(text = "CLOSE")
        }
    })
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AttachmentMenu(
    onDismiss: () -> Unit,
    onOptionSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .standardPadding(horizontal = basePadding, vertical = mediumPadding)
    ) {
        Text(
            text = "Attach",
            style = MaterialTheme.typography.titleMediumEmphasized,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = smallPadding)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            horizontalArrangement = Arrangement.spacedBy(smallPadding),
            verticalArrangement = Arrangement.spacedBy(smallPadding),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                AttachmentOption(
                    icon = Icons.Filled.Camera,
                    label = "Camera",
                    onClick = { onOptionSelected("camera") }
                )
            }
            item {
                AttachmentOption(
                    icon = Icons.Filled.AttachFile,
                    label = "Document",
                    onClick = { onOptionSelected("document") }
                )
            }
            item {
                AttachmentOption(
                    icon = Icons.Outlined.InsertPhoto,
                    label = "Gallery",
                    onClick = { onOptionSelected("gallery") }
                )
            }
            item {
                AttachmentOption(
                    icon = Icons.Filled.LocationOn,
                    label = "Location",
                    onClick = { onOptionSelected("location") }
                )
            }
        }
    }
}

@Composable
private fun AttachmentOption(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(smallPadding)
    ) {
        Surface(
            modifier = Modifier.size(46.dp),
            shape = RoundedCornerShape(corner = CornerSize(commonCornerRadius)),
            color = MaterialTheme.colorScheme.secondaryContainer
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier
                    .size(24.dp)
                    .padding(12.dp),
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = extraSmallPadding),
            textAlign = TextAlign.Center
        )
    }
}
