package com.example.matchmaker.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.matchmaker.data.db.ProfileStatus
import com.example.matchmaker.data.repository.MatchRepository
import com.example.matchmaker.domain.MatchScoreCalculator
import com.example.matchmaker.domain.MyProfile
import com.example.matchmaker.ui.list.MatchCardItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

data class MatchListUiState(
    val items: List<MatchCardItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isEmpty: Boolean = false
)

class MatchListViewModel(
    private val repository: MatchRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MatchListUiState())
    val uiState: StateFlow<MatchListUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAll()
                .map { entities ->
                    entities.map { e ->
                        MatchCardItem(
                            entity = e,
                            matchScore = MatchScoreCalculator.compute(
                                MyProfile.age,
                                MyProfile.city,
                                e.age,
                                e.city
                            )
                        )
                    }
                }
                .collect { items ->
                    _uiState.value = _uiState.value.copy(
                        items = items,
                        isEmpty = items.isEmpty() && !_uiState.value.isLoading
                    )
                }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                repository.refresh()
                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Something went wrong"
                )
            }
        }
    }

    fun accept(item: MatchCardItem) {
        viewModelScope.launch {
            repository.updateStatus(item.entity.id, ProfileStatus.ACCEPTED)
        }
    }

    fun decline(item: MatchCardItem) {
        viewModelScope.launch {
            repository.updateStatus(item.entity.id, ProfileStatus.DECLINED)
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    class Factory(
        private val repository: MatchRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MatchListViewModel(repository) as T
        }
    }
}
