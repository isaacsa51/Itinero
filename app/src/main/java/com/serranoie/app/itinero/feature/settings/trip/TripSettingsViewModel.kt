package com.serranoie.app.itinero.feature.settings.trip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel

class TripSettingsViewModel: ViewModel() {
    private val _qrText = MutableStateFlow("")
    val qrText: StateFlow<String> = _qrText

    private val _qrBitmap = MutableStateFlow<Bitmap?>(null)
    val qrBitmap: StateFlow<Bitmap?> = _qrBitmap

    fun setQrText(text: String) {
        _qrText.value = text
    }

    fun generateQrCode() {
        if (_qrText.value.isNotEmpty()) {
            viewModelScope.launch(Dispatchers.IO) {
                val bitmap = generateQRCodeBitmap(_qrText.value)
                _qrBitmap.value = bitmap
            }
        }
    }

    private fun generateQRCodeBitmap(content: String): Bitmap {
        val hints = hashMapOf<EncodeHintType, Any>().apply {
            put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H)
            put(EncodeHintType.MARGIN, 1) // Margin around the code
            put(EncodeHintType.CHARACTER_SET, "UTF-8")
        }

        val qrCodeWriter = QRCodeWriter()
        val bitMatrix = qrCodeWriter.encode(
            content,
            BarcodeFormat.QR_CODE,
            512,
            512,
            hints
        )

        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = createBitmap(width, height)

        for(x in 0 until width) {
            for(y in 0 until height) {
                bitmap[x, y] = if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE
            }
        }

        return bitmap
    }
}