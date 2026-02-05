package org.igo.mycorc.ui.screen.login

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import org.igo.mycorc.ui.common.CommonTopBar
import org.igo.mycorc.ui.common.Dimens
import org.koin.compose.viewmodel.koinViewModel
import org.igo.mycorc.ui.theme.LocalAppStrings

@Composable
fun LoginScreen() {
    // Получаем context внутри компонента (инкапсуляция)
    val activityContext = getActivityContext()

    val viewModel = koinViewModel<LoginViewModel>()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val strings = LocalAppStrings.current

    var isRegisterMode by remember { mutableStateOf(false) }

    val topBarTitle = if (isRegisterMode) strings.registerTitle else strings.loginTitle

    Scaffold(
        topBar = { CommonTopBar(title = topBarTitle) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(Dimens.SpaceMedium),
                shape = RoundedCornerShape(Dimens.CardCornerRadius),
                elevation = CardDefaults.cardElevation(defaultElevation = Dimens.CardElevation),
                border = BorderStroke(Dimens.BorderWidthStandard, MaterialTheme.colorScheme.outline)
            ) {
                if (isRegisterMode) {
                    RegisterContent(
                        viewModel = viewModel,
                        isLoading = isLoading,
                        error = error,
                        strings = strings,
                        onSwitchToLogin = { isRegisterMode = false }
                    )
                } else {
                    LoginContent(
                        viewModel = viewModel,
                        isLoading = isLoading,
                        error = error,
                        strings = strings,
                        activityContext = activityContext,
                        onSwitchToRegister = { isRegisterMode = true }
                    )
                }
            }
        }
    }
}

@Composable
private fun LoginContent(
    viewModel: LoginViewModel,
    isLoading: Boolean,
    error: String?,
    strings: org.igo.mycorc.domain.strings.AppStrings,
    activityContext: Any?,
    onSwitchToRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(Dimens.SpaceLarge)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.SpaceMedium)
    ) {
        Text(
            strings.authCardTitle,
            style = MaterialTheme.typography.headlineSmall
        )

        // Google Sign-In button (показывается только на Android)
        if (activityContext != null) {
            OutlinedButton(
                onClick = { viewModel.signInWithGoogle(activityContext) },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimens.ButtonHeight),
                shape = RoundedCornerShape(Dimens.InputFieldCornerRadius),
                border = BorderStroke(Dimens.BorderWidthStandard, MaterialTheme.colorScheme.outline)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(Dimens.IconSizeSmall),
                        strokeWidth = Dimens.ProgressIndicatorStrokeWidth
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        modifier = Modifier.size(Dimens.IconSizeSmall)
                    )
                    Spacer(modifier = Modifier.width(Dimens.SpaceSmall))
                    Text(strings.signInWithGoogle)
                }
            }

            // Разделитель "──── или ────"
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = strings.orDivider,
                    modifier = Modifier.padding(horizontal = Dimens.SpaceMedium),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                )
            }
        }

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(strings.emailLabel) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(Dimens.InputFieldCornerRadius)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(strings.passwordLabel) },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(Dimens.InputFieldCornerRadius)
        )

        ErrorMessage(error)

        Button(
            onClick = { viewModel.login(email, password) },
            enabled = !isLoading && email.isNotBlank() && password.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimens.ButtonHeight),
            shape = RoundedCornerShape(Dimens.InputFieldCornerRadius)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(Dimens.IconSizeSmall),
                )
            } else {
                Text(strings.loginButton)
            }
        }

        TextButton(
            onClick = onSwitchToRegister,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(strings.noAccountText + " " + strings.registerLink)
        }
    }
}

@Composable
private fun RegisterContent(
    viewModel: LoginViewModel,
    isLoading: Boolean,
    error: String?,
    strings: org.igo.mycorc.domain.strings.AppStrings,
    onSwitchToLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf<String?>(null) }

    // Показываем либо локальную ошибку (пароли не совпадают), либо серверную
    val displayError = localError ?: error

    Column(
        modifier = Modifier
            .padding(Dimens.SpaceLarge)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.SpaceMedium)
    ) {
        Text(
            strings.registerTitle,
            style = MaterialTheme.typography.headlineSmall
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(strings.emailLabel) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(Dimens.InputFieldCornerRadius)
        )

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                localError = null
            },
            label = { Text(strings.passwordLabel) },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(Dimens.InputFieldCornerRadius)
        )

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                localError = null
            },
            label = { Text(strings.confirmPasswordLabel) },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(Dimens.InputFieldCornerRadius)
        )

        ErrorMessage(displayError)

        Button(
            onClick = {
                if (password != confirmPassword) {
                    localError = strings.passwordsDoNotMatch
                } else {
                    localError = null
                    viewModel.register(email, password)
                }
            },
            enabled = !isLoading && email.isNotBlank() && password.isNotBlank() && confirmPassword.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimens.ButtonHeight),
            shape = RoundedCornerShape(Dimens.InputFieldCornerRadius)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(Dimens.IconSizeSmall),
                )
            } else {
                Text(strings.registerButton)
            }
        }

        TextButton(
            onClick = onSwitchToLogin,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(strings.haveAccountText + " " + strings.signInLink)
        }
    }
}

@Composable
private fun ErrorMessage(error: String?) {
    if (error != null) {
        Surface(
            color = MaterialTheme.colorScheme.errorContainer,
            shape = RoundedCornerShape(Dimens.InputFieldCornerRadius),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.onErrorContainer,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(Dimens.SpaceMedium)
            )
        }
    }
}
