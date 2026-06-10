package com.ishan.kbc.ui.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onAuthenticated: () -> Unit,
    onBack: () -> Unit,
) {
    // Unified with LoginScreen tabs — switch to SignUp tab and go back
    LaunchedEffect(Unit) {
        viewModel.setTab(AuthTab.SignUp)
        onBack()
    }
}
