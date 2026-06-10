package com.ishan.kbc.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ishan.kbc.R
import com.ishan.kbc.ui.theme.Background
import com.ishan.kbc.ui.theme.Gold
import com.ishan.kbc.ui.theme.GoldDark
import com.ishan.kbc.ui.theme.OnPrimary
import com.ishan.kbc.ui.theme.OnSurface
import com.ishan.kbc.ui.theme.OnSurfaceVariant
import com.ishan.kbc.ui.theme.Primary
import com.ishan.kbc.ui.theme.PrimaryContainer
import com.ishan.kbc.ui.theme.SurfaceContainerHighest
import com.ishan.kbc.ui.theme.SurfaceContainerLow

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

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Box(modifier = Modifier.fillMaxSize().background(Background))

        // Ambient glows
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .size(350.dp)
                    .align(Alignment.TopStart)
                    .offset(x = -80.dp, y = -80.dp)
                    .background(Primary.copy(alpha = 0.07f))
                    .clip(CircleShape)
                    .scale(2f)
            )
            Box(
                modifier = Modifier
                    .size(400.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = 100.dp, y = 100.dp)
                    .background(PrimaryContainer.copy(alpha = 0.06f))
                    .clip(CircleShape)
                    .scale(2f)
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.displayMedium,
                color = Gold,
                letterSpacing = 4.sp,
                fontWeight = FontWeight.ExtraBold,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.login_tagline),
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurfaceVariant,
            )
            Spacer(Modifier.height(32.dp))

            // Glass panel
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(SurfaceContainerLow.copy(alpha = 0.85f))
                    .padding(24.dp),
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Custom animated tabs
                    AuthTabs(
                        selectedTab = state.tab,
                        onTabSelected = viewModel::setTab,
                    )
                    Spacer(Modifier.height(24.dp))

                    AnimatedVisibility(
                        visible = state.tab == AuthTab.Login,
                        enter = fadeIn(tween(300)) + slideInVertically { it / 4 },
                        exit = fadeOut(tween(200)),
                    ) {
                        LoginForm(
                            emailOrUsername = state.emailOrUsername,
                            password = state.password,
                            isPasswordVisible = state.isPasswordVisible,
                            isLoading = state.isLoading,
                            error = state.error,
                            viewModel = viewModel,
                            onEmailOrUsername = viewModel::onEmailOrUsername,
                            onPassword = viewModel::onPassword,
                            onTogglePassword = viewModel::togglePasswordVisibility,
                            onLogin = viewModel::login,
                            onSendOtp = viewModel::sendOtp,
                        )
                    }

                    AnimatedVisibility(
                        visible = state.tab == AuthTab.SignUp,
                        enter = fadeIn(tween(300)) + slideInVertically { it / 4 },
                        exit = fadeOut(tween(200)),
                    ) {
                        SignUpForm(
                            email = state.email,
                            username = state.username,
                            password = state.password,
                            displayName = state.displayName,
                            isPasswordVisible = state.isPasswordVisible,
                            isLoading = state.isLoading,
                            error = state.error,
                            onEmail = viewModel::onEmail,
                            onUsername = viewModel::onUsername,
                            onPassword = viewModel::onPassword,
                            onDisplayName = viewModel::onDisplayName,
                            onTogglePassword = viewModel::togglePasswordVisibility,
                            onRegister = viewModel::register,
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            // Social logins
            Text(
                text = stringResource(R.string.login_or_continue_with),
                style = MaterialTheme.typography.labelMedium,
                color = OnSurfaceVariant,
            )
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                SocialButton(icon = R.drawable.ic_google, label = "Google", onClick = viewModel::loginWithGoogle)
                SocialButton(icon = R.drawable.ic_facebook, label = "Facebook", onClick = viewModel::loginWithFacebook)
                SocialButton(icon = R.drawable.ic_phone, label = "Phone", onClick = viewModel::sendOtp)
            }
        }
    }
}

@Composable
private fun AuthTabs(
    selectedTab: AuthTab,
    onTabSelected: (AuthTab) -> Unit,
) {
    val tabWidth = 0.5f
    Box(
        modifier = Modifier
            .fillMaxWidth(tabWidth)
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceContainerHighest.copy(alpha = 0.6f))
            .padding(4.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            AuthTab.entries.forEach { tab ->
                val isSelected = selectedTab == tab
                val bgAlpha by animateFloatAsState(
                    targetValue = if (isSelected) 1f else 0f,
                    animationSpec = tween(300),
                    label = "tabBg",
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected) Gold.copy(alpha = 0.25f) else Color.Transparent
                        )
                        .clickable { onTabSelected(tab) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = when (tab) {
                            AuthTab.Login -> stringResource(R.string.login_title)
                            AuthTab.SignUp -> stringResource(R.string.register_title)
                        },
                        color = if (isSelected) Gold else OnSurfaceVariant,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    )
                }
            }
        }
    }
}

@Composable
private fun LoginForm(
    emailOrUsername: String,
    password: String,
    isPasswordVisible: Boolean,
    isLoading: Boolean,
    error: String?,
    viewModel: AuthViewModel,
    onEmailOrUsername: (String) -> Unit,
    onPassword: (String) -> Unit,
    onTogglePassword: () -> Unit,
    onLogin: () -> Unit,
    onSendOtp: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = emailOrUsername,
            onValueChange = onEmailOrUsername,
            label = { Text(stringResource(R.string.hint_email)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
            ),
            colors = outlinedFieldColors(),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = password,
            onValueChange = onPassword,
            label = { Text(stringResource(R.string.hint_password)) },
            singleLine = true,
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = onTogglePassword) {
                    Text(
                        text = if (isPasswordVisible) "Hide" else "Show",
                        color = Gold,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
            ),
            colors = outlinedFieldColors(),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.login_forgot_password),
            color = Gold.copy(alpha = 0.8f),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.fillMaxWidth().clickable { viewModel.forgotPassword() }.padding(vertical = 4.dp),
            textAlign = TextAlign.End,
        )
        Spacer(Modifier.height(16.dp))

        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                textAlign = TextAlign.Center,
            )
        }

        // 3D Gold Login button
        GoldButton(
            text = stringResource(R.string.cta_login),
            onClick = onLogin,
            enabled = !isLoading,
        )
        if (isLoading) {
            Spacer(Modifier.height(8.dp))
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp).align(Alignment.CenterHorizontally),
                color = Gold,
                strokeWidth = 2.dp,
            )
        }
        Spacer(Modifier.height(16.dp))
        // OTP option
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = stringResource(R.string.login_via_otp),
                color = OnSurface,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.clickable(onClick = onSendOtp).padding(vertical = 4.dp),
            )
        }
    }
}

@Composable
private fun SignUpForm(
    email: String,
    username: String,
    password: String,
    displayName: String,
    isPasswordVisible: Boolean,
    isLoading: Boolean,
    error: String?,
    onEmail: (String) -> Unit,
    onUsername: (String) -> Unit,
    onPassword: (String) -> Unit,
    onDisplayName: (String) -> Unit,
    onTogglePassword: () -> Unit,
    onRegister: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = displayName,
            onValueChange = onDisplayName,
            label = { Text(stringResource(R.string.hint_display_name)) },
            singleLine = true,
            colors = outlinedFieldColors(),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            value = email,
            onValueChange = onEmail,
            label = { Text(stringResource(R.string.hint_email)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
            ),
            colors = outlinedFieldColors(),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            value = username,
            onValueChange = onUsername,
            label = { Text("Username") },
            singleLine = true,
            colors = outlinedFieldColors(),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            value = password,
            onValueChange = onPassword,
            label = { Text(stringResource(R.string.hint_password)) },
            singleLine = true,
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = onTogglePassword) {
                    Text(
                        text = if (isPasswordVisible) "Hide" else "Show",
                        color = Gold,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
            ),
            colors = outlinedFieldColors(),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(16.dp))

        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                textAlign = TextAlign.Center,
            )
        }

        GoldButton(
            text = stringResource(R.string.cta_register),
            onClick = onRegister,
            enabled = !isLoading,
        )
        if (isLoading) {
            Spacer(Modifier.height(8.dp))
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp).align(Alignment.CenterHorizontally),
                color = Gold,
                strokeWidth = 2.dp,
            )
        }
    }
}

@Composable
private fun GoldButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val goldBrush = Brush.verticalGradient(
        colors = listOf(Gold, GoldDark),
    )
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = SurfaceContainerHighest.copy(alpha = 0.4f),
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 6.dp,
            pressedElevation = 2.dp,
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(goldBrush, RoundedCornerShape(14.dp)),
        )
        Text(
            text = text,
            color = OnPrimary,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

@Composable
private fun SocialButton(icon: Int, label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceContainerHighest.copy(alpha = 0.6f))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = label,
                tint = OnSurface,
                modifier = Modifier.size(24.dp),
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = label,
                color = OnSurfaceVariant,
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}

private val outlinedFieldColors = @Composable {
    OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Gold,
        unfocusedBorderColor = SurfaceContainerHighest,
        cursorColor = Gold,
        focusedLabelColor = Gold,
        unfocusedLabelColor = OnSurfaceVariant,
        focusedTextColor = OnSurface,
        unfocusedTextColor = OnSurface,
    )
}
