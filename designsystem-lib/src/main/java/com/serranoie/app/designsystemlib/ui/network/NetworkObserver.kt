package com.serranoie.app.designsystemlib.ui.network

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.net.NetworkRequest
import androidx.annotation.RequiresPermission
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * A utility class to observe network connectivity changes using a Flow. This helps
 * to monitor internet connectivity in real-time and provides updates in an asynchronous manner.
 *
 * @param context The application context, used to access system services such as ConnectivityManager.
 */
class NetworkObserver(
    context: Context
) {
    // ConnectivityManager is used to check and monitor the device's network state.
    private var connectivityManager: ConnectivityManager? =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    /**
     * A [Flow] that emits a boolean value `true` when the device is connected to the internet
     * and `false` when it loses connection. This provides a reactive approach to
     * handling network connectivity changes.
     */
    val isConnectedFlow: Flow<Boolean>
        get() = callbackFlow {
            val networkCallback = object : ConnectivityManager.NetworkCallback() {
                @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
                override fun onAvailable(network: Network) {
                    connectivityManager?.getNetworkCapabilities(network)?.let {
                        if (it.hasCapability(NET_CAPABILITY_INTERNET) && it.hasCapability(
                                NetworkCapabilities.NET_CAPABILITY_VALIDATED
                            )
                        ) {
                            trySend(true)
                        }
                    }
                }

                // Called when a network is lost.
                override fun onLost(network: Network) {
                    trySend(false)
                }

                // Called when no networks are available or a request fails.
                override fun onUnavailable() {
                    trySend(false)
                }

                // Called when network capabilities change, such as validation status.
                override fun onCapabilitiesChanged(
                    network: Network, capabilities: NetworkCapabilities
                ) {
                    super.onCapabilitiesChanged(network, capabilities)
                    if (capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
                        trySend(true)
                    } else {
                        trySend(false)
                    }
                }
            }

            // Create a NetworkRequest specifying the types of networks and capabilities we are interested in.
            val networkRequest = NetworkRequest.Builder()
                .addCapability(NET_CAPABILITY_INTERNET)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .build()

            // Register the NetworkCallback with the ConnectivityManager to start listening for changes.
            connectivityManager?.registerNetworkCallback(networkRequest, networkCallback)

            // Ensure resources are cleaned up when the flow collector is no longer active.
            awaitClose {
                // Unregister the NetworkCallback to prevent memory leaks.
                connectivityManager?.unregisterNetworkCallback(networkCallback)
            }
        }
}