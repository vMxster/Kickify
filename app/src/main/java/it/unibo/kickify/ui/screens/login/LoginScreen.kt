package it.unibo.kickify.ui.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.unibo.kickify.R

@Preview
@Composable
fun LoginScreen() {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.signinTitle),
                color = Color.White,
                fontSize = 28.sp,
                modifier = Modifier.padding(bottom = 20.dp)
            )
            Text(
                text = stringResource(R.string.signinText),
                color = Color.White,
                fontSize = 22.sp,
                modifier = Modifier.padding(bottom = 20.dp)
                    .padding(horizontal = 32.dp)
            )
            Text(
                text = stringResource(R.string.signin_emailAddress),
                color = Color.White,
                modifier = Modifier.fillMaxWidth().padding(start = 32.dp)
            )
            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text(stringResource(R.string.signin_emailAddress), color = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(30.dp))
                    .padding(horizontal = 32.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                stringResource(R.string.signin_password),
                color = Color.White,
                modifier = Modifier.fillMaxWidth().padding(start = 32.dp)
            )
            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text(stringResource(R.string.signin_emailAddress), color = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(30.dp))
                    .padding(horizontal = 32.dp)
            )
            Text(
                text = stringResource(R.string.signin_forgotPassword),
                color = Color.White,
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp, end = 32.dp),
                textAlign = TextAlign.End
            )
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { /* standard login */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
            ) {
                Text(text = stringResource(R.string.signin_button), color = Color.White)
            }
            Spacer(modifier = Modifier.height(15.dp))
            Button(
                onClick = { /* google oauth login */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
            ) {
                Image(
                    painter = painterResource(R.drawable.google_icon),
                    contentDescription = "Login with Google")
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = stringResource(R.string.signin_continueGoogle), color = Color.White)
            }
            Text(
                text = stringResource(R.string.signin_joinForFree),
                color = Color.White,
                modifier = Modifier.padding(bottom = 20.dp, top = 20.dp)
            )
        }
    }
}
