package com.serranoie.app.feature.welcome.camera

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.view.MotionEvent
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.serranoie.app.designsystem.ui.theme.component.BottomSheetContent
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScannerScreen(
    viewModel: ScannerViewModel = viewModel(),
    onBackPressed: () -> Unit = {}
) {
    val scannedCode by viewModel.scannedCode.collectAsState()

    val context = LocalContext.current
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    var tapCoordinates by remember { mutableStateOf<Offset?>(null) }
    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Handle focus indicator visibility timeout
    LaunchedEffect(tapCoordinates) {
        if (tapCoordinates != null) {
            delay(1000)
            tapCoordinates = null
        }
    }

    // Show bottom sheet when QR code is detected
    if (showSheet && scannedCode != null) {
        QrCodeBottomSheet(
            scannedCode = scannedCode.orEmpty(),
            sheetState = sheetState,
            onDismiss = {
                showSheet = false
                viewModel.clearScannedCode()
            },
            onCopy = {
                clipboardManager.setPrimaryClip(ClipData.newPlainText("QR Code", scannedCode))
                Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
            },
            onShare = {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, scannedCode)
                }
                context.startActivity(Intent.createChooser(intent, "Share QR Code"))
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            CameraPreview(
                onQrCodeScanned = { code ->
                    if (!showSheet) {
                        viewModel.onQrCodeScanned(code)
                        showSheet = true
                    }
                },
                onFocusTap = { coordinates ->
                    tapCoordinates = coordinates
                }
            )

            QrCodeScanGuide()

            FocusIndicator(
                tapCoordinates = tapCoordinates,
                showIndicator = tapCoordinates != null
            )
        }
    }
}

@Composable
private fun CameraPreview(
    onQrCodeScanned: (String) -> Unit,
    onFocusTap: (Offset) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().apply {
                    setSurfaceProvider(previewView.surfaceProvider)
                }

                val analyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(
                            ContextCompat.getMainExecutor(ctx),
                            QRCodeAnalyzer(onQrCodeScanned)
                        )
                    }

                cameraProvider.unbindAll()
                val camera = cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    analyzer
                )

                setupTouchToFocus(previewView, camera, onFocusTap)

                previewView
            }, ContextCompat.getMainExecutor(ctx))

            previewView
        },
        modifier = Modifier.fillMaxSize()
    )
}

private fun setupTouchToFocus(
    previewView: PreviewView,
    camera: androidx.camera.core.Camera,
    onFocusTap: (Offset) -> Unit
) {
    previewView.setOnTouchListener { _, event ->
        if (event.action == MotionEvent.ACTION_DOWN) {
            val meteringPointFactory = SurfaceOrientedMeteringPointFactory(
                previewView.width.toFloat(),
                previewView.height.toFloat()
            )

            val meteringPoint = meteringPointFactory.createPoint(event.x, event.y)
            val focusAction = FocusMeteringAction.Builder(meteringPoint).build()

            camera.cameraControl.startFocusAndMetering(focusAction)
            onFocusTap(Offset(event.x, event.y))
            return@setOnTouchListener true
        }
        false
    }
}

@Composable
private fun FocusIndicator(
    tapCoordinates: Offset?,
    showIndicator: Boolean
) {
    AnimatedVisibility(
        visible = showIndicator,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = Modifier.fillMaxSize()
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            tapCoordinates?.let { offset ->
                Spacer(
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                offset.x.toInt() - 24.dp.toPx().toInt(),
                                offset.y.toInt() - 24.dp.toPx().toInt()
                            )
                        }
                        .size(48.dp)
                        .border(2.dp, Color.White, CircleShape)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QrCodeBottomSheet(
    scannedCode: String,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onCopy: () -> Unit,
    onShare: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        BottomSheetContent(
            scannedCode = scannedCode,
            onCopy = onCopy,
            onShare = onShare,
            onClose = onDismiss
        )
    }
}

@Composable
private fun QrCodeScanGuide() {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Semi-transparent overlay covering the entire screen except the scanner window
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Convert dimensions to pixels for the Canvas
            val scannerSize = 250.dp
            val scannerSizePx = scannerSize.toPx()
            val centerX = size.width / 2
            val centerY = size.height / 2

            val rectSize = Size(size.width, size.height)

            // Draw the semi-transparent dark overlay
            drawRect(
                color = Color.Black.copy(alpha = 0.55f),
                size = rectSize
            )

            // Create a clear window by using the "clear" blend mode
            drawRoundRect(
                color = Color.Transparent,
                topLeft = Offset(
                    centerX - scannerSizePx / 2,
                    centerY - scannerSizePx / 2
                ),
                size = Size(scannerSizePx, scannerSizePx),
                cornerRadius = CornerRadius(12.dp.toPx(), 12.dp.toPx()),
                blendMode = BlendMode.Clear
            )
        }

        // Scanner guide frame (border)
        Box(
            modifier = Modifier
                .size(250.dp)
                .border(
                    width = 3.dp,
                    color = Color.White.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(12.dp)
                )
        )

        // Instruction text
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 100.dp)
                .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Position QR code within the frame",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}