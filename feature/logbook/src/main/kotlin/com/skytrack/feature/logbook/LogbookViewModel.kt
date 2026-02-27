package com.skytrack.feature.logbook

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skytrack.domain.usecase.GetFlightStatsUseCase
import com.skytrack.domain.usecase.GetRecentFlightsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogbookViewModel @Inject constructor(
    private val getRecentFlightsUseCase: GetRecentFlightsUseCase,
    private val getFlightStatsUseCase: GetFlightStatsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LogbookUiState())
    val uiState: StateFlow<LogbookUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val flights = getRecentFlightsUseCase.execute(limit = 50)
            val stats = getFlightStatsUseCase.execute()
            _uiState.update {
                LogbookUiState(
                    flights = flights,
                    stats = stats,
                    isLoading = false
                )
            }
        }
    }
}
