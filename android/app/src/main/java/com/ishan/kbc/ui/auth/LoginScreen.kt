package com.ishan.kbc.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.ishan.kbc.R

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onAuthenticated: () -> Unit,
    onGoRegister: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(state.success) {
        if (state.success) {
            viewModel.consumeSuccess()
            onAuthenticated()
        }
    }
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(stringResource(R.string.login_title), style = MaterialTheme.typography.displayMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(32.dp))
        OutlinedTextField(
            value = state.emailOrUsername,
            onValueChange = viewModel::onEmailOrUsername,
            label = { Text(stringResource(R.string.hint_email)) },
            singleLine = true,
            modifier = Modifier.fillMaxSize(0.95f).height(72.dp),
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = state.password,
            onValueChange = viewModel::onPassword,
            label = { Text(stringResource(R.string.hint_password)) },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxSize(0.95f).height(72.dp),
        )
        Spacer(Modifier.height(20.dp))
        if (state.isLoading) {
            CircularProgressIndicator()
        } else {
            Button(onClick = viewModel::login) { Text(stringResource(R.string.cta_login)) }
        }
        TextButton(onClick = onGoRegister) { Text(stringResource(R.string.cta_register)) }
        if (state.error != null) {
            Spacer(Modifier.height(8.dp))
            Text(state.error!!, color = MaterialTheme.colorScheme.error)
        }
    }
}
