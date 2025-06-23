package com.serranoie.app.itinero.feature.settings.trip

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.serranoie.itinero.core.domain.model.TripMember
import com.serranoie.itinero.core.domain.result.Result
import com.serranoie.itinero.core.domain.usecase.TravelUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface TripMembersUiState {
    data object Idle : TripMembersUiState
    data object Loading : TripMembersUiState
    data class Success(val members: List<TripMember>) : TripMembersUiState
    data class Error(val message: String) : TripMembersUiState
}

class TripSettingsViewModel(
    private val travelUseCase: TravelUseCase
) : ViewModel() {

    private val _qrText = MutableStateFlow("")
    val qrText: StateFlow<String> = _qrText

    private val _qrBitmap = MutableStateFlow<Bitmap?>(null)
    val qrBitmap: StateFlow<Bitmap?> = _qrBitmap

    private val _membersUiState = MutableStateFlow<TripMembersUiState>(TripMembersUiState.Idle)
    val membersUiState: StateFlow<TripMembersUiState> = _membersUiState

    private val _currentUserMember = MutableStateFlow<TripMember?>(null)
    val currentUserMember: StateFlow<TripMember?> = _currentUserMember

    companion object {
        private const val TAG = "TripSettingsViewModel"
    }

    fun setQrText(groupCode: String) {
        _qrText.value = groupCode
    }

    fun generateQrCode() {
        if (_qrText.value.isNotEmpty()) {
            viewModelScope.launch(Dispatchers.IO) {
                val bitmap = generateQRCodeBitmap(_qrText.value)
                _qrBitmap.value = bitmap
            }
        }
    }

    fun fetchMembers(groupCode: String) {
        if (_membersUiState.value is TripMembersUiState.Loading) return

        viewModelScope.launch {
            _membersUiState.value = TripMembersUiState.Loading
            Log.d(TAG, "Fetching members for group: $groupCode")

            when (val result = travelUseCase.getAllMembers(groupCode)) {
                is Result.Success -> {
                    Log.d(TAG, "Successfully fetched ${result.data.size} members")
                    _membersUiState.value = TripMembersUiState.Success(result.data)
                }

                is Result.Error -> {
                    Log.e(
                        TAG,
                        "Failed to fetch members: ${result.exception.message}",
                        result.exception
                    )
                    _membersUiState.value = TripMembersUiState.Error(
                        result.exception.message ?: "Failed to fetch members"
                    )
                }
            }
        }
    }

    fun fetchCurrentUserMembershipStatus(groupCode: String, userId: Int) {
        viewModelScope.launch {
            when (val result = travelUseCase.getCurrentUserMembershipStatus(groupCode, userId)) {
                is Result.Success -> {
                    Log.d(TAG, "Current user status: ${result.data.status}")
                    _currentUserMember.value = result.data
                }

                is Result.Error -> {
                    Log.e(
                        TAG,
                        "Failed to fetch current user status: ${result.exception.message}",
                        result.exception
                    )
                    _currentUserMember.value = null
                }
            }
        }
    }

    fun acceptMember(
        groupCode: String,
        memberId: Int,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            Log.d(TAG, "Accepting member $memberId for group: $groupCode")

            when (val result = travelUseCase.acceptMemberToTrip(groupCode, memberId)) {
                is Result.Success -> {
                    Log.d(TAG, "Successfully accepted member $memberId")
                    onSuccess()
                    // Refresh members list
                    fetchMembers(groupCode)
                }

                is Result.Error -> {
                    val errorMessage = result.exception.message ?: "Failed to accept member"
                    Log.e(TAG, "Failed to accept member $memberId: $errorMessage", result.exception)
                    onError(errorMessage)
                }
            }
        }
    }

    fun rejectMember(
        groupCode: String,
        memberId: Int,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            Log.d(TAG, "Rejecting member $memberId for group: $groupCode")

            when (val result = travelUseCase.rejectMember(groupCode, memberId)) {
                is Result.Success -> {
                    Log.d(TAG, "Successfully rejected member $memberId")
                    onSuccess()
                    // Refresh members list
                    fetchMembers(groupCode)
                }

                is Result.Error -> {
                    val errorMessage = result.exception.message ?: "Failed to reject member"
                    Log.e(TAG, "Failed to reject member $memberId: $errorMessage", result.exception)
                    onError(errorMessage)
                }
            }
        }
    }

    fun removeMember(
        groupCode: String,
        memberId: Int,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            Log.d(TAG, "Removing member $memberId from group: $groupCode")

            when (val result = travelUseCase.removeMember(groupCode, memberId)) {
                is Result.Success -> {
                    Log.d(TAG, "Successfully removed member $memberId")
                    onSuccess()
                    // Refresh members list
                    fetchMembers(groupCode)
                }

                is Result.Error -> {
                    val errorMessage = result.exception.message ?: "Failed to remove member"
                    Log.e(TAG, "Failed to remove member $memberId: $errorMessage", result.exception)
                    onError(errorMessage)
                }
            }
        }
    }

    fun makeOwner(
        groupCode: String,
        memberId: Int,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            Log.d(TAG, "Making member $memberId owner of group: $groupCode")

            when (val result = travelUseCase.makeOwner(groupCode, memberId)) {
                is Result.Success -> {
                    Log.d(TAG, "Successfully made member $memberId owner")
                    onSuccess()
                    // Refresh members list and current user status
                    fetchMembers(groupCode)
                    fetchCurrentUserMembershipStatus(groupCode, memberId)
                }

                is Result.Error -> {
                    val errorMessage = result.exception.message ?: "Failed to transfer ownership"
                    Log.e(
                        TAG,
                        "Failed to make member $memberId owner: $errorMessage",
                        result.exception
                    )
                    onError(errorMessage)
                }
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

        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap[x, y] = if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE
            }
        }

        return bitmap
    }
}
