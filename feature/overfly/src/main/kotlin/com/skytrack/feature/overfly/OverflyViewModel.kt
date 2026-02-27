package com.skytrack.feature.overfly

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skytrack.domain.usecase.GetCurrentPositionUseCase
import com.skytrack.domain.usecase.GetOverflyInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OverflyViewModel @Inject constructor(
    private val getCurrentPositionUseCase: GetCurrentPositionUseCase,
    private val getOverflyInfoUseCase: GetOverflyInfoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(OverflyUiState())
    val uiState: StateFlow<OverflyUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getCurrentPositionUseCase.execute()
                .sample(5000) // update every 5 seconds
                .collect { position ->
                    val info = getOverflyInfoUseCase.execute(position.latitude, position.longitude)
                    _uiState.update {
                        OverflyUiState(
                            countryName = info.countryName,
                            regionName = info.regionName,
                            oceanName = info.oceanName,
                            isOverOcean = info.oceanName != null
                        )
                    }
                }
        }
    }
}
