package com.ishan.kbc.ui.achievements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ishan.kbc.domain.model.Achievement
import com.ishan.kbc.domain.repository.AchievementRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AchievementState(
    val achievements: List<Achievement> = emptyList(),
    val milestones: List<com.ishan.kbc.domain.model.Milestone> = emptyList(),
    val totalMastery: Float = 0f,
    val selectedAchievement: Achievement? = null,
    val loading: Boolean = true,
)

@HiltViewModel
class AchievementViewModel @Inject constructor(
    private val achievementRepository: AchievementRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(AchievementState())
    val state: StateFlow<AchievementState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            achievementRepository.getAchievements()
                .onSuccess { data ->
                    _state.update {
                        it.copy(
                            achievements = data.achievements,
                            milestones = data.milestones,
                            totalMastery = data.totalMastery,
                            loading = false,
                        )
                    }
                }
        }
    }

    fun selectAchievement(achievement: Achievement) {
        _state.update { it.copy(selectedAchievement = achievement) }
    }

    fun clearSelection() {
        _state.update { it.copy(selectedAchievement = null) }
    }
}
