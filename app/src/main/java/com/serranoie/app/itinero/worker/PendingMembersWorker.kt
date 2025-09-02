package com.serranoie.app.itinero.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.serranoie.itinero.core.domain.model.MemberStatus
import com.serranoie.itinero.core.domain.model.MembershipStatus
import com.serranoie.itinero.core.domain.repository.NotificationRepository
import com.serranoie.itinero.core.domain.result.Result
import com.serranoie.itinero.core.domain.usecase.TravelUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PendingMembersWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val travelUseCase: TravelUseCase by inject()
    private val notificationRepository: NotificationRepository by inject()

    companion object {
        const val WORKER_NAME = "pending_members_checker"
        private const val TAG = "PendingMembersWorker"
    }

    override suspend fun doWork(): Result {
        return try {
            Log.d(TAG, "Checking for pending members...")

            val pendingMembersInfo = checkForPendingMembers()

            if (pendingMembersInfo.isNotEmpty()) {
                val totalPendingCount = pendingMembersInfo.values.sum()
                val tripNames = pendingMembersInfo.keys.toList()

                Log.d(
                    TAG,
                    "Found $totalPendingCount pending members across ${tripNames.size} trips"
                )
                notificationRepository.showPendingMembersNotification(totalPendingCount, tripNames)
            } else {
                Log.d(TAG, "No pending members found")
                notificationRepository.cancelPendingMembersNotification()
            }

            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error checking for pending members", e)
            Result.retry()
        }
    }

    private suspend fun checkForPendingMembers(): Map<String, Int> {
        val pendingMembersInfo = mutableMapOf<String, Int>()

        try {
            // Get all user's trips
            when (val tripsResult = travelUseCase.getAllTravels()) {
                is com.serranoie.itinero.core.domain.result.Result.Success -> {
                    val trips = tripsResult.data

                    for (trip in trips) {
                        // Check if user is owner of this trip
                        when (val statusResult =
                            travelUseCase.getCurrentUserMembershipStatus(trip.groupCode)) {
                            is com.serranoie.itinero.core.domain.result.Result.Success -> {
                                val userStatus = statusResult.data

                                // Only check for pending members if user is owner
                                if (userStatus.isOwner) {
                                    when (val membersResult =
                                        travelUseCase.getAllMembers(trip.groupCode)) {
                                        is com.serranoie.itinero.core.domain.result.Result.Success -> {
                                            val members = membersResult.data
                                            val pendingCount = members.count { member ->
                                                member.status == MemberStatus.PENDING
                                            }

                                            if (pendingCount > 0) {
                                                pendingMembersInfo[trip.groupName] = pendingCount
                                            }
                                        }

                                        is com.serranoie.itinero.core.domain.result.Result.Error -> {
                                            Log.e(
                                                TAG,
                                                "Failed to get members for trip ${trip.groupCode}: ${membersResult.exception.message}"
                                            )
                                        }
                                    }
                                }
                            }

                            is com.serranoie.itinero.core.domain.result.Result.Error -> {
                                Log.e(
                                    TAG,
                                    "Failed to get user status for trip ${trip.groupCode}: ${statusResult.exception.message}"
                                )
                            }
                        }
                    }
                }

                is com.serranoie.itinero.core.domain.result.Result.Error -> {
                    Log.e(TAG, "Failed to get trips: ${tripsResult.exception.message}")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception while checking pending members", e)
        }

        return pendingMembersInfo
    }
}