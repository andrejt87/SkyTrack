package com.skytrack.app.data.map

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Monitors network connectivity via registerDefaultNetworkCallback.
 * Only counts WiFi/Cellular/Ethernet as "online" (not VPN alone).
 */
object NetworkMonitor {

    private const val TAG = "NetworkMonitor"

    private val _isOnline = MutableStateFlow(false)
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    private var initialized = false
    private var cm: ConnectivityManager? = null

    fun init(context: Context) {
        if (initialized) return
        initialized = true

        cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        _isOnline.value = checkOnline()
        Log.i(TAG, "Initial online state: ${_isOnline.value}")

        try {
            cm!!.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    val online = checkOnline()
                    Log.i(TAG, "Callback: AVAILABLE, online=$online")
                    _isOnline.value = online
                }
                override fun onLost(network: Network) {
                    val online = checkOnline()
                    Log.i(TAG, "Callback: LOST, online=$online")
                    _isOnline.value = online
                }
                override fun onCapabilitiesChanged(network: Network, caps: NetworkCapabilities) {
                    val online = checkOnline()
                    _isOnline.value = online
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, "Failed to register callback", e)
        }
    }

    private fun checkOnline(): Boolean {
        val manager = cm ?: return false
        val net = manager.activeNetwork ?: return false
        val caps = manager.getNetworkCapabilities(net) ?: return false
        val hasRealTransport = caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                               caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                               caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        return hasRealTransport &&
               caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
