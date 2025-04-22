package it.unibo.kickify.ui.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import it.unibo.kickify.R
import it.unibo.kickify.ui.composables.EmailRoundedTextField
import it.unibo.kickify.ui.composables.PasswordRoundedTextField
import it.unibo.kickify.ui.theme.MediumGray

@Preview
@Composable
fun LoginScreen(navController: NavController = NavController(LocalContext.current)) {
    Scaffold { contentPadding ->
        val loginScreenModifier = Modifier.fillMaxWidth()
            .padding(horizontal = 24.dp)

        Column(
            modifier = Modifier.fillMaxSize()
                .padding(contentPadding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.signin_Title),
                fontSize = 32.sp,
                textAlign = TextAlign.Center,
                modifier = loginScreenModifier
            )
            Text(
                text = stringResource(R.string.signin_Text),
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = loginScreenModifier
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.emailAddress),
                modifier =loginScreenModifier
            )
            Spacer(modifier = Modifier.height(16.dp))

            EmailRoundedTextField(
                modifier = loginScreenModifier,
                placeholderString = stringResource(R.string.emailAddress)
            ) { }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                stringResource(R.string.password),
                modifier = loginScreenModifier
            )
            Spacer(modifier = Modifier.height(16.dp))
            PasswordRoundedTextField(modifier = loginScreenModifier) { }

            TextButton(
                onClick = { /* go to login page */ },
                modifier = Modifier.align(Alignment.End)
                    .padding(top = 5.dp, end = 28.dp)
            ) {
                Text(
                    text = stringResource(R.string.signin_forgotPassword),
                    textAlign = TextAlign.End,
                    color = MaterialTheme.colorScheme.inverseSurface
                )
            }
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { /* standard login */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
            ) {
                Text(
                    text = stringResource(R.string.signin_button),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
            Spacer(modifier = Modifier.height(15.dp))
            Button(
                onClick = { /* google oauth login */ },
                modifier = Modifier
                    .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MediumGray)
            ) {
                Image(
                    painter = painterResource(R.drawable.google_icon),
                    contentDescription = "Login with Google"
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = stringResource(R.string.signin_signup_continueGoogle),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            TextButton(
                onClick = { /* go to register page */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .padding(vertical = 20.dp)
            ) {
                Text(
                    text = stringResource(R.string.signin_joinForFree),
                    color = MaterialTheme.colorScheme.inverseSurface
                )
            }
        }
    }
}


