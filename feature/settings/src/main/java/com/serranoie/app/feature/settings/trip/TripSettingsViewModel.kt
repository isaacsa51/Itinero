package com.serranoie.app.feature.settings.trip

import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.serranoie.itinero.core.domain.model.MembershipStatus
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

sealed interface TripDeletionUiState {
    data object Idle : TripDeletionUiState
    data object Loading : TripDeletionUiState
    data object Success : TripDeletionUiState
    data class Error(val message: String) : TripDeletionUiState
}

sealed interface TripLeaveTripUiState {
    data object Idle : TripLeaveTripUiState
    data object Loading : TripLeaveTripUiState
    data object Success : TripLeaveTripUiState
    data class Error(val message: String) : TripLeaveTripUiState
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

    private val _currentUserMembershipStatus = MutableStateFlow<MembershipStatus?>(null)
    val currentUserMembershipStatus: StateFlow<MembershipStatus?> = _currentUserMembershipStatus

    private val _deletionUiState = MutableStateFlow<TripDeletionUiState>(TripDeletionUiState.Idle)
    val deletionUiState: StateFlow<TripDeletionUiState> = _deletionUiState

    private val _leaveTripUiState =
        MutableStateFlow<TripLeaveTripUiState>(TripLeaveTripUiState.Idle)
    val leaveTripUiState: StateFlow<TripLeaveTripUiState> = _leaveTripUiState

    companion object {
        private const val TAG = "TripSettingsViewModel"
        private const val DEEP_LINK_URL_PREFIX = "itinero://join?code="
    }

    fun setQrText(groupCode: String) {
        _qrText.value = "$DEEP_LINK_URL_PREFIX$groupCode"
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
            when (val result = travelUseCase.getAllMembers(groupCode)) {
                is Result.Success -> {
                    _membersUiState.value = TripMembersUiState.Success(result.data)
                }

                is Result.Error -> {
                    _membersUiState.value = TripMembersUiState.Error(
                        result.exception.message ?: "Failed to fetch members"
                    )
                }
            }
        }
    }

    fun fetchCurrentUserMembershipStatus(groupCode: String) {
        viewModelScope.launch {
            try {
                android.util.Log.d(
                    TAG,
                    "Fetching user membership status for groupCode: $groupCode"
                )

                when (val result =
                    travelUseCase.getCurrentUserMembershipStatus(groupCode)) {
                    is Result.Success -> {
                        android.util.Log.d(
                            TAG,
                            "Successfully fetched user membership: ${result.data}"
                        )
                        _currentUserMembershipStatus.value = result.data
                    }

                    is Result.Error -> {
                        android.util.Log.e(
                            TAG,
                            "Failed to fetch user membership: ${result.exception.message}"
                        )
                        _currentUserMembershipStatus.value = null
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Exception in fetchCurrentUserMembershipStatus", e)
                _currentUserMembershipStatus.value = null
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
            when (val result = travelUseCase.acceptMemberToTrip(groupCode, memberId)) {
                is Result.Success -> {
                    onSuccess()
                    fetchMembers(groupCode)
                }

                is Result.Error -> {
                    val errorMessage = result.exception.message ?: "Failed to accept member"
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
            when (val result = travelUseCase.rejectMember(groupCode, memberId)) {
                is Result.Success -> {
                    onSuccess()
                    fetchMembers(groupCode)
                }

                is Result.Error -> {
                    val errorMessage = result.exception.message ?: "Failed to reject member"
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
            when (val result = travelUseCase.removeMember(groupCode, memberId)) {
                is Result.Success -> {
                    onSuccess()
                    fetchMembers(groupCode)
                }

                is Result.Error -> {
                    val errorMessage = result.exception.message ?: "Failed to remove member"
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
            when (val result = travelUseCase.makeOwner(groupCode, memberId)) {
                is Result.Success -> {
                    onSuccess()
                    fetchMembers(groupCode)
                    fetchCurrentUserMembershipStatus(groupCode)
                }

                is Result.Error -> {
                    val errorMessage = result.exception.message ?: "Failed to transfer ownership"
                    onError(errorMessage)
                }
            }
        }
    }

    fun leaveTripCurrentTrip(
        groupCode: String,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _leaveTripUiState.value = TripLeaveTripUiState.Loading
            when (val result = travelUseCase.leaveTrip(groupCode)) {
                is Result.Success -> {
                    _leaveTripUiState.value = TripLeaveTripUiState.Success
                    onSuccess()
                }

                is Result.Error -> {
                    val errorMessage = result.exception.message ?: "Failed to leave trip"
                    _leaveTripUiState.value = TripLeaveTripUiState.Error(errorMessage)
                    onError(errorMessage)
                }
            }
        }
    }

    fun deleteTrip(
        groupCode: String,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _deletionUiState.value = TripDeletionUiState.Loading
            when (val result = travelUseCase.deleteTrip.invoke(groupCode)) {
                is Result.Success -> {
                    _deletionUiState.value = TripDeletionUiState.Success
                    onSuccess()
                }

                is Result.Error -> {
                    val errorMessage = result.exception.message ?: "Failed to delete trip"
                    _deletionUiState.value = TripDeletionUiState.Error(errorMessage)
                    onError(errorMessage)
                }
            }
        }
    }

    fun resetDeletionState() {
        _deletionUiState.value = TripDeletionUiState.Idle
    }

    fun resetLeaveTripState() {
        _leaveTripUiState.value = TripLeaveTripUiState.Idle
    }

    private fun generateQRCodeBitmap(content: String): Bitmap {
        val hints = hashMapOf<EncodeHintType, Any>().apply {
            put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H)
            put(EncodeHintType.MARGIN, 1)
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
