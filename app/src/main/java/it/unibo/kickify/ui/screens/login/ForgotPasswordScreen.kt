package it.unibo.kickify.ui.screens.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import it.unibo.kickify.R
import it.unibo.kickify.ui.KickifyRoute
import it.unibo.kickify.ui.composables.EmailRoundedTextField
import it.unibo.kickify.ui.composables.ScreenTemplate

@Composable
fun ForgotPasswordScreen(
    navController: NavController
) {
    ScreenTemplate(
        screenTitle = stringResource(R.string.signin_forgotPassword),
        navController = navController,
        showTopAppBar = true,
        bottomAppBarContent = { },
        showModalDrawer = false
    ) { contentPadding ->
        val forgotScreenModifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(vertical = 8.dp)

        Column(
            modifier = Modifier.fillMaxSize()
                .padding(contentPadding)
                .padding(top = 20.dp)
                .padding(horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = stringResource(R.string.forgotpsw_title),
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                modifier = forgotScreenModifier
            )
            Text(
                text = stringResource(R.string.forgotpsw_text),
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = forgotScreenModifier
            )
            Text(
                text = stringResource(R.string.emailAddress),
                modifier = forgotScreenModifier.padding(top = 16.dp)
            )

            EmailRoundedTextField(
                modifier = forgotScreenModifier,
                placeholderString = stringResource(R.string.forgotpsw_templateEmail)
            ) {  }
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.navigate(KickifyRoute.OTPScreen) },
                modifier = forgotScreenModifier
            ) {
                Text(
                    text = stringResource(R.string.continue_button),
                )
            }
        }
    }
}
