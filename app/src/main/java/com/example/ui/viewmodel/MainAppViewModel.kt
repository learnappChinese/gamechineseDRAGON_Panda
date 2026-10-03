package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AppScreen {
    HOME,
    GAME_HUB,
    BOSS_INTRO,
    BOSS_BATTLE
}

data class UserProfileState(
    val streakDays: Int = 7,
    val completedLessons: Int = 12,
    val xpPoints: Int = 156,
    val dailyGoalCurrent: Int = 6,
    val dailyGoalTotal: Int = 10,
    val coins: Int = 240
)

class MainAppViewModel : ViewModel() {
    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _userProfile = MutableStateFlow(UserProfileState())
    val userProfile: StateFlow<UserProfileState> = _userProfile.asStateFlow()

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun onBossBattleVictory(earnedXp: Int = 100, earnedCoins: Int = 50) {
        val current = _userProfile.value
        _userProfile.value = current.copy(
            xpPoints = current.xpPoints + earnedXp,
            coins = current.coins + earnedCoins,
            dailyGoalCurrent = (current.dailyGoalCurrent + 1).coerceAtMost(current.dailyGoalTotal)
        )
    }
}
