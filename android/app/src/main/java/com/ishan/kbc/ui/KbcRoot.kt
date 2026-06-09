package com.ishan.kbc.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ishan.kbc.data.local.PreferencesManager
import com.ishan.kbc.ui.auth.AuthViewModel
import com.ishan.kbc.ui.auth.LoginScreen
import com.ishan.kbc.ui.auth.RegisterScreen
import com.ishan.kbc.ui.daily.DailyChallengeScreen
import com.ishan.kbc.ui.game.GameScreen
import com.ishan.kbc.ui.home.HomeScreen
import com.ishan.kbc.ui.leaderboard.LeaderboardScreen
import com.ishan.kbc.ui.multiplayer.MultiplayerLobbyScreen
import com.ishan.kbc.ui.profile.ProfileScreen
import com.ishan.kbc.ui.settings.SettingsScreen
import com.ishan.kbc.ui.shop.ShopScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val GAME = "game"
    const val DAILY = "daily"
    const val LEADERBOARD = "leaderboard"
    const val MULTIPLAYER = "multiplayer"
    const val PROFILE = "profile"
    const val SETTINGS = "settings"
    const val SHOP = "shop"
}

@HiltViewModel
class RootViewModel @Inject constructor(
    prefs: PreferencesManager,
) : ViewModel() {
    val isAuthed: StateFlow<Boolean?> = prefs.authToken
        .map { !it.isNullOrBlank() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)
}

@Composable
fun KbcRoot(
    rootViewModel: RootViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
) {
    val nav: NavHostController = rememberNavController()
    val isAuthed by rootViewModel.isAuthed.collectAsState()
    val startDest = when (isAuthed) {
        null -> Routes.LOGIN
        true -> Routes.HOME
        false -> Routes.LOGIN
    }
    NavHost(navController = nav, startDestination = startDest) {
        composable(Routes.LOGIN) {
            LoginScreen(
                viewModel = authViewModel,
                onAuthenticated = {
                    nav.navigate(Routes.HOME) { popUpTo(Routes.LOGIN) { inclusive = true } }
                },
                onGoRegister = { nav.navigate(Routes.REGISTER) },
            )
        }
        composable(Routes.REGISTER) {
            RegisterScreen(
                viewModel = authViewModel,
                onAuthenticated = {
                    nav.navigate(Routes.HOME) { popUpTo(Routes.LOGIN) { inclusive = true } }
                },
                onBack = { nav.popBackStack() },
            )
        }
        composable(Routes.HOME) {
            HomeScreen(
                onPlay = { nav.navigate(Routes.GAME) },
                onDaily = { nav.navigate(Routes.DAILY) },
                onLeaderboard = { nav.navigate(Routes.LEADERBOARD) },
                onMultiplayer = { nav.navigate(Routes.MULTIPLAYER) },
                onShop = { nav.navigate(Routes.SHOP) },
                onProfile = { nav.navigate(Routes.PROFILE) },
                onSettings = { nav.navigate(Routes.SETTINGS) },
                onLogout = {
                    authViewModel.logout()
                    nav.navigate(Routes.LOGIN) { popUpTo(0) }
                },
            )
        }
        composable(Routes.GAME) {
            GameScreen(onExit = { nav.popBackStack() })
        }
        composable(Routes.DAILY) {
            DailyChallengeScreen(onBack = { nav.popBackStack() })
        }
        composable(Routes.LEADERBOARD) {
            LeaderboardScreen(onBack = { nav.popBackStack() })
        }
        composable(Routes.MULTIPLAYER) {
            MultiplayerLobbyScreen(onBack = { nav.popBackStack() })
        }
        composable(Routes.PROFILE) {
            ProfileScreen(onBack = { nav.popBackStack() })
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(
                onBack = { nav.popBackStack() },
                onSignOut = {
                    authViewModel.logout()
                    nav.navigate(Routes.LOGIN) { popUpTo(0) }
                },
            )
        }
        composable(Routes.SHOP) {
            ShopScreen(onBack = { nav.popBackStack() })
        }
    }
}
