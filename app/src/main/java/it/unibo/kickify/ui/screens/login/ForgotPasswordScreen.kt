package it.unibo.kickify.ui.screens.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import it.unibo.kickify.R
import it.unibo.kickify.ui.composables.AppBar
import it.unibo.kickify.ui.theme.Black

@Preview
@Composable
fun ForgotPasswordScreen(
    navController: NavController = NavController(LocalContext.current)
) {
    Scaffold(
        topBar = {
            AppBar(
                navController,
                title = stringResource(R.string.signin_forgotPassword)
            )
        },
        bottomBar = {
        }
    ) { contentPadding ->
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
                modifier = Modifier.padding(bottom = 20.dp)
            )
            Text(
                text = stringResource(R.string.forgotpsw_text),
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 20.dp)
            )
            Text(
                text = stringResource(R.string.emailAddress),
                modifier = Modifier.fillMaxWidth()
                    .padding(start = 32.dp)
            )
            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = {
                    Text(
                        stringResource(R.string.forgotpsw_templateEmail),
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

            Button(
                onClick = { /* standard login */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
            ) {
                Text(
                    text = stringResource(R.string.continue_button),
                )
            }
        }
    }
}
