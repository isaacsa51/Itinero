package com.serranoie.app.feature.welcome.camera

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ScannerViewModel : ViewModel() {
    private val _scannedCode = MutableStateFlow<String?>(null)
    val scannedCode = _scannedCode.asStateFlow()

    fun onQrCodeScanned(code: String) {
        _scannedCode.value = code
    }

    fun clearScannedCode() {
        _scannedCode.value = null
    }
}
