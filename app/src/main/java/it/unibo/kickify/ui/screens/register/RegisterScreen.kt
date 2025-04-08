package it.unibo.kickify.ui.screens.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.unibo.kickify.R
import it.unibo.kickify.ui.theme.KickifyTheme
import it.unibo.kickify.ui.theme.MediumGray

@Preview
@Composable
fun RegisterScreen() {
    KickifyTheme {
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(
                text = stringResource(R.string.signup_title),
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 32.sp,
                modifier = Modifier.padding(bottom = 20.dp)
            )
            Text(
                text = stringResource(R.string.signup_text),
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 20.dp)
                    .padding(horizontal = 32.dp)
            )
            Text(
                text = stringResource(R.string.signup_yourname),
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.fillMaxWidth().padding(start = 32.dp)
            )
            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = {
                    Text(
                        stringResource(R.string.signup_yourname),
                        color = Color.Gray,
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(30.dp))
                    .padding(horizontal = 32.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.emailAddress),
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.fillMaxWidth().padding(start = 32.dp)
            )
            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = {
                    Text(
                        stringResource(R.string.emailAddress),
                        color = Color.Gray,
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(30.dp))
                    .padding(horizontal = 32.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                stringResource(R.string.password),
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.fillMaxWidth().padding(start = 32.dp)
            )
            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = {
                    Text(
                        stringResource(R.string.password),
                        color = Color.Gray,
                    )
                },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(30.dp))
                    .padding(horizontal = 32.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { /* standard login */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
            ) {
                Text(
                    text = stringResource(R.string.signup_button),
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
                )
            }

            TextButton(
                onClick = { /* go to login page */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .padding(vertical = 20.dp)
            ) {
                Text(
                    text = stringResource(R.string.signup_alreadyPartOfCrew),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}