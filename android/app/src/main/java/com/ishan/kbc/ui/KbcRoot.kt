package com.ishan.kbc.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ishan.kbc.data.local.PreferencesManager
import com.ishan.kbc.domain.model.GameStatus
import com.ishan.kbc.ui.auth.AuthViewModel
import com.ishan.kbc.ui.auth.LoginScreen
import com.ishan.kbc.ui.auth.RegisterScreen
import com.ishan.kbc.ui.achievements.AchievementScreen
import com.ishan.kbc.ui.components.AppBottomBar
import com.ishan.kbc.ui.components.AppTopBar
import com.ishan.kbc.ui.components.BOTTOM_NAV_ITEMS
import com.ishan.kbc.ui.daily.DailyChallengeScreen
import com.ishan.kbc.ui.fff.FffScreen
import com.ishan.kbc.ui.game.GameScreen
import com.ishan.kbc.ui.game.PostGameScreen
import com.ishan.kbc.ui.home.HomeScreen
import com.ishan.kbc.ui.leaderboard.LeaderboardScreen
import com.ishan.kbc.ui.matchhistory.MatchHistoryScreen
import com.ishan.kbc.ui.multiplayer.MultiplayerLobbyScreen
import com.ishan.kbc.ui.preshow.PreShowScreen
import com.ishan.kbc.ui.profile.ProfileScreen
import com.ishan.kbc.ui.settings.SettingsScreen
import com.ishan.kbc.ui.shop.ShopScreen
import com.ishan.kbc.ui.splash.SplashScreen
import com.ishan.kbc.ui.splash.SplashViewModel
import com.ishan.kbc.ui.tournament.TournamentScreen
import com.ishan.kbc.ui.wallet.WalletScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val MAIN = "main"
    const val GAME = "game"
    const val DAILY = "daily"
    const val LEADERBOARD = "leaderboard"
    const val MULTIPLAYER = "multiplayer"
    const val PROFILE = "profile"
    const val SETTINGS = "settings"
    const val SHOP = "shop"
    const val FFF = "fastest_finger"
    const val PRE_SHOW = "pre_show"
    const val ACHIEVEMENTS = "achievements"
    const val MATCH_HISTORY = "match_history"
    const val TOURNAMENT = "tournament"
    const val POST_GAME = "post_game/{status}/{prize}/{score}/{level}"

    fun postGame(status: String, prize: Int, score: Int, level: Int) =
        "post_game/$status/$prize/$score/$level"
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
        null -> Routes.SPLASH
        true -> Routes.MAIN
        false -> Routes.SPLASH
    }
    NavHost(navController = nav, startDestination = startDest) {
        composable(Routes.SPLASH) {
            SplashScreen(
                onComplete = {
                    val next = if (isAuthed == true) Routes.MAIN else Routes.LOGIN
                    nav.navigate(next) { popUpTo(Routes.SPLASH) { inclusive = true } }
                },
                viewModel = hiltViewModel<SplashViewModel>(),
            )
        }
        composable(Routes.LOGIN) {
            LoginScreen(
                viewModel = authViewModel,
                onAuthenticated = {
                    nav.navigate(Routes.MAIN) { popUpTo(Routes.LOGIN) { inclusive = true } }
                },
            )
        }
        composable(Routes.REGISTER) {
            RegisterScreen(
                viewModel = authViewModel,
                onAuthenticated = {
                    nav.navigate(Routes.MAIN) { popUpTo(Routes.LOGIN) { inclusive = true } }
                },
                onBack = { nav.popBackStack() },
            )
        }
        composable(Routes.MAIN) {
            var bottomNavIndex by rememberSaveable { mutableIntStateOf(0) }

            Column(modifier = Modifier.fillMaxSize()) {
                AppTopBar(
                    walletBalance = "$25,000",
                    onProfileClick = { bottomNavIndex = 3 },
                )

                Box(modifier = Modifier.weight(1f)) {
                    when (bottomNavIndex) {
                        0 -> HomeScreen(
                            onPlay = { nav.navigate(Routes.GAME) },
                            onPreShow = { nav.navigate(Routes.PRE_SHOW) },
                            onFff = { nav.navigate(Routes.FFF) },
                            onDaily = { nav.navigate(Routes.DAILY) },
                            onAchievements = { nav.navigate(Routes.ACHIEVEMENTS) },
                            onMatchHistory = { nav.navigate(Routes.MATCH_HISTORY) },
                            onTournament = { nav.navigate(Routes.TOURNAMENT) },
                            onLeaderboard = { bottomNavIndex = 1 },
                            onMultiplayer = { nav.navigate(Routes.MULTIPLAYER) },
                            onShop = { nav.navigate(Routes.SHOP) },
                            onProfile = { bottomNavIndex = 3 },
                            onSettings = { nav.navigate(Routes.SETTINGS) },
                            onLogout = {
                                authViewModel.logout()
                                nav.navigate(Routes.LOGIN) { popUpTo(0) }
                            },
                        )
                        1 -> LeaderboardScreen(onBack = { bottomNavIndex = 0 })
                        2 -> WalletScreen()
                        3 -> ProfileScreen(onBack = { bottomNavIndex = 0 })
                    }
                }

                AppBottomBar(
                    selectedIndex = bottomNavIndex,
                    onItemSelected = { bottomNavIndex = it },
                )
            }
        }
        composable(Routes.GAME) {
            GameScreen(
                onExit = { status, prize, score, level ->
                    nav.navigate(Routes.postGame(status.name, prize, score, level)) {
                        popUpTo(Routes.GAME) { inclusive = true }
                    }
                },
            )
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
        composable(Routes.FFF) {
            FffScreen(onBack = { nav.popBackStack() })
        }
        composable(Routes.PRE_SHOW) {
            PreShowScreen(
                onEnterArena = { nav.navigate(Routes.GAME) },
                onBack = { nav.popBackStack() },
            )
        }
        composable(Routes.ACHIEVEMENTS) {
            AchievementScreen(onBack = { nav.popBackStack() })
        }
        composable(Routes.MATCH_HISTORY) {
            MatchHistoryScreen(onBack = { nav.popBackStack() })
        }
        composable(Routes.TOURNAMENT) {
            TournamentScreen(onBack = { nav.popBackStack() })
        }
        composable(
            route = Routes.POST_GAME,
            arguments = listOf(
                navArgument("status") { type = NavType.StringType },
                navArgument("prize") { type = NavType.IntType },
                navArgument("score") { type = NavType.IntType },
                navArgument("level") { type = NavType.IntType },
            ),
        ) { entry ->
            val status = GameStatus.valueOf(entry.arguments?.getString("status") ?: "Quit")
            val prize = entry.arguments?.getInt("prize") ?: 0
            val score = entry.arguments?.getInt("score") ?: 0
            val level = entry.arguments?.getInt("level") ?: 1
            PostGameScreen(
                status = status,
                prize = prize,
                score = score,
                level = level,
                onPlayAgain = {
                    nav.navigate(Routes.GAME) { popUpTo(Routes.POST_GAME) { inclusive = true } }
                },
                onHome = {
                    nav.navigate(Routes.MAIN) { popUpTo(Routes.POST_GAME) { inclusive = true } }
                },
            )
        }
    }
}
