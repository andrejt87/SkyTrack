package com.skytrack.app.ui.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skytrack.app.data.repository.FlightRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    flightRepository: FlightRepository
) : ViewModel() {

    /**
     * If there is an active flight, emits its ID; otherwise emits null.
     */
    val activeFlightId = flightRepository.getActiveFlight()
        .map { flight -> flight?.id }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )
}
