package org.igo.mycorc.ui.screen.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.igo.mycorc.ui.common.CommonTopBar
import org.igo.mycorc.ui.common.Dimens
import org.koin.compose.viewmodel.koinViewModel
import org.igo.mycorc.ui.theme.LocalAppStrings

@Composable
fun ProfileScreen() {
    val viewModel = koinViewModel<ProfileViewModel>()
    val user by viewModel.currentUser.collectAsState(initial = null)
    val strings = LocalAppStrings.current

    Scaffold(
        topBar = { CommonTopBar(title = strings.profileTitle) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.TopCenter
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(Dimens.SpaceLarge),
                shape = RoundedCornerShape(Dimens.CardCornerRadius),
                elevation = CardDefaults.cardElevation(defaultElevation = Dimens.CardElevation),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(Dimens.SpaceLarge)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(Dimens.SpaceMedium),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = strings.loggedInAs,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = user?.email ?: strings.loading,
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Spacer(modifier = Modifier.height(Dimens.SpaceSmall))

                    HorizontalDivider()

                    Spacer(modifier = Modifier.height(Dimens.SpaceSmall))

                    Button(
                        onClick = { viewModel.logout() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(Dimens.ButtonHeight),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(strings.logoutButton)
                    }
                }
            }
        }
    }
}
