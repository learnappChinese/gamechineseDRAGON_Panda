package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AppBottomNavigation
import com.example.ui.screens.BossBattleScreen
import com.example.ui.screens.BossIntroScreen
import com.example.ui.screens.GameHubScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.theme.ChineseBossBattleTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.BossBattleViewModel
import com.example.ui.viewmodel.MainAppViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChineseBossBattleTheme {
                MainAppRoot()
            }
        }
    }
}

@Composable
fun MainAppRoot(
    mainViewModel: MainAppViewModel = viewModel(),
    battleViewModel: BossBattleViewModel = viewModel()
) {
    val currentScreen by mainViewModel.currentScreen.collectAsState()
    val userProfile by mainViewModel.userProfile.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    val showBottomNav = (currentScreen == AppScreen.HOME || currentScreen == AppScreen.GAME_HUB)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomNav) {
                AppBottomNavigation(
                    selectedTabIndex = selectedTab,
                    onTabSelected = { index ->
                        selectedTab = index
                        if (index == 0) {
                            mainViewModel.navigateTo(AppScreen.HOME)
                        } else if (index == 2) {
                            mainViewModel.navigateTo(AppScreen.GAME_HUB)
                        } else {
                            // Stay on current or home for other tabs
                            mainViewModel.navigateTo(AppScreen.HOME)
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                AppScreen.HOME -> {
                    HomeScreen(
                        userProfile = userProfile,
                        onNavigateToGameHub = {
                            selectedTab = 2
                            mainViewModel.navigateTo(AppScreen.GAME_HUB)
                        }
                    )
                }

                AppScreen.GAME_HUB -> {
                    GameHubScreen(
                        onSelectBossBattle = {
                            mainViewModel.navigateTo(AppScreen.BOSS_INTRO)
                        }
                    )
                }

                AppScreen.BOSS_INTRO -> {
                    BossIntroScreen(
                        onBack = {
                            selectedTab = 2
                            mainViewModel.navigateTo(AppScreen.GAME_HUB)
                        },
                        onStartBattle = {
                            battleViewModel.startNewBattle()
                            mainViewModel.navigateTo(AppScreen.BOSS_BATTLE)
                        }
                    )
                }

                AppScreen.BOSS_BATTLE -> {
                    BossBattleScreen(
                        viewModel = battleViewModel,
                        onBackToHub = {
                            selectedTab = 2
                            mainViewModel.navigateTo(AppScreen.GAME_HUB)
                        },
                        onVictory = {
                            mainViewModel.onBossBattleVictory(earnedXp = 100, earnedCoins = 50)
                        }
                    )
                }
            }
        }
    }
}
