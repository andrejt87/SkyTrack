package com.skytrack.app.ui.airportpicker

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skytrack.app.data.model.Airport
import com.skytrack.app.data.repository.AirportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AirportPickerUiState(
    val query: String           = "",
    val results: List<Airport>  = emptyList(),
    val isLoading: Boolean      = false,
    val pickerType: String      = "departure"
)

/**
 * ViewModel for the airport search picker.
 * On airport selection, the chosen Airport is stored in SavedStateHandle
 * so the calling screen (FlightSetupScreen via NavController.previousBackStackEntry) can observe it.
 */
@OptIn(FlowPreview::class)
@HiltViewModel
class AirportPickerViewModel @Inject constructor(
    private val airportRepository: AirportRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val pickerType: String = savedStateHandle["type"] ?: "departure"

    private val _query   = MutableStateFlow("")
    private val _results = MutableStateFlow<List<Airport>>(emptyList())
    private val _loading = MutableStateFlow(false)

    val uiState: StateFlow<AirportPickerUiState> = combine(
        _query, _results, _loading
    ) { query, results, loading ->
        AirportPickerUiState(query = query, results = results, isLoading = loading, pickerType = pickerType)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AirportPickerUiState(pickerType = pickerType)
    )

    // Expose selected airport so the NavController back-stack can retrieve it
    private val _selectedAirport = MutableStateFlow<Airport?>(null)
    val selectedAirport: StateFlow<Airport?> = _selectedAirport.asStateFlow()

    init {
        viewModelScope.launch {
            _query
                .debounce(250)
                .filter { it.length >= 2 }
                .collect { query ->
                    _loading.value = true
                    _results.value = airportRepository.searchAirports(query)
                    _loading.value = false
                }
        }
    }

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
        if (newQuery.length < 2) _results.value = emptyList()
    }

    /**
     * Stores the selected airport so the composable can retrieve it before navigating back.
     */
    fun selectAirport(airport: Airport) {
        _selectedAirport.value = airport
        // Store in SavedStateHandle so it survives process death
        savedStateHandle["selected_airport_iata"] = airport.iata
        savedStateHandle["selected_airport_name"] = airport.name
    }
}
