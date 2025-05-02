package it.unibo.kickify.ui.screens.register

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import it.unibo.kickify.R
import it.unibo.kickify.ui.KickifyRoute
import it.unibo.kickify.ui.composables.AppBar
import it.unibo.kickify.ui.composables.EmailRoundedTextField
import it.unibo.kickify.ui.composables.PasswordRoundedTextField
import it.unibo.kickify.ui.composables.UsernameRoundedTextField
import it.unibo.kickify.ui.theme.MediumGray

@Composable
fun RegisterScreen(
    navController: NavController
) {
    val ctx = LocalContext.current
    Scaffold (
        topBar = {
            AppBar(
                navController,
                title = ""
            )
        }
    )  { contentPadding ->
        val registerScreenModifier = Modifier.fillMaxWidth()
            .padding(horizontal = 24.dp)

        val scrollState = rememberScrollState()
        
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(contentPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = stringResource(R.string.signup_title),
                fontSize = 32.sp,
                textAlign = TextAlign.Center,
                modifier = registerScreenModifier
            )
            Spacer(modifier = Modifier.height(15.dp))
            Text(
                text = stringResource(R.string.signup_text),
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = registerScreenModifier
            )

            Spacer(Modifier.height(20.dp))
            Text(
                text = stringResource(R.string.signup_yourname),
                modifier = registerScreenModifier
            )
            UsernameRoundedTextField(modifier = registerScreenModifier)
            Spacer(modifier = Modifier.height(15.dp))

            Text(
                text = stringResource(R.string.emailAddress),
                modifier = registerScreenModifier
            )
            EmailRoundedTextField(
                modifier = registerScreenModifier,
                placeholderString = stringResource(R.string.emailAddress)
            ) { }
            Spacer(modifier = Modifier.height(15.dp))

            Text(
                stringResource(R.string.password),
                modifier = registerScreenModifier
            )
            PasswordRoundedTextField(modifier = registerScreenModifier) { }

            Spacer(modifier = Modifier.height(15.dp))
            Button(
                onClick = {
                    /* if(registration successful) {
                        navController.navigate(KickifyRoute.Home){
                            popUpTo(KickifyRoute.Home) { inclusive = true }
                        }
                    } else { */
                    Toast.makeText(ctx, "Registration error", Toast.LENGTH_LONG).show()
                    // }
                },
                modifier = registerScreenModifier,
            ) {
                Text(
                    text = stringResource(R.string.signup_button),
                )
            }
            Spacer(modifier = Modifier.height(15.dp))
            Button(
                onClick = { /* google oauth login */ },
                modifier = registerScreenModifier,
                colors = ButtonDefaults.buttonColors(containerColor = MediumGray)
            ) {
                Image(
                    painter = painterResource(R.drawable.google_icon),
                    contentDescription = "Login with Google"
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = stringResource(R.string.signin_signup_continueGoogle),
                )
            }
            Spacer(modifier = Modifier.height(15.dp))

            TextButton(
                onClick = {
                    navController.navigate(KickifyRoute.Login) {
                        popUpTo(KickifyRoute.Login) { inclusive = true }
                    }
                },
                modifier = registerScreenModifier,
            ) {
                Text(
                    text = stringResource(R.string.signup_alreadyPartOfCrew) + " " + stringResource(R.string.signin_button),
                    color = MaterialTheme.colorScheme.inverseSurface
                )
            }
        }
    }
}