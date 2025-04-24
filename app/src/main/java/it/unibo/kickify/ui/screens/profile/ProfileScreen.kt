package it.unibo.kickify.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import it.unibo.kickify.R
import it.unibo.kickify.ui.composables.AppBar
import it.unibo.kickify.ui.composables.EmailRoundedTextField
import it.unibo.kickify.ui.composables.PasswordRoundedTextField
import it.unibo.kickify.ui.composables.UsernameRoundedTextField
import it.unibo.kickify.ui.theme.BluePrimary
import it.unibo.kickify.ui.theme.GhostWhite
import it.unibo.kickify.ui.theme.LightGray

@Composable
fun ProfileScreen(
    navController: NavController = NavController(LocalContext.current)
){
    Scaffold(
        topBar = {
            AppBar(
                navController,
                title = stringResource(R.string.profileScreen_title)
            )
        },
        bottomBar = { }
    ) { contentPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(horizontal = 16.dp)
        ) {
            val profileScreenModifier = Modifier.fillMaxWidth()
                .padding(horizontal = 24.dp)
            
            ProfileImageWithChangeButton(false)

            Text(
                modifier = Modifier.fillMaxWidth()
                    .padding(top = 4.dp),
                text = "Mario Rossi",
                textAlign = TextAlign.Center,
                fontSize = 22.sp
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.profileScreen_fullName),
                modifier = profileScreenModifier
            )
            UsernameRoundedTextField(modifier = profileScreenModifier)
            Spacer(modifier = Modifier.height(15.dp))

            Text(
                text = stringResource(R.string.emailAddress),
                modifier = profileScreenModifier
            )
            EmailRoundedTextField(
                modifier = profileScreenModifier,
                placeholderString = stringResource(R.string.emailAddress)
            ) { }
            Spacer(modifier = Modifier.height(15.dp))

            Text(
                stringResource(R.string.password),
                modifier = profileScreenModifier
            )
            PasswordRoundedTextField(modifier = profileScreenModifier) { }

        }
    }
}

@Composable
fun ProfileImageWithChangeButton(hasUserImage: Boolean){
    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier.size(150.dp)
    ){
        if(!hasUserImage) {
            IconButton(
                onClick = { },
                modifier = Modifier
                    //.fillMaxSize()
                    .size(150.dp)
                    .clip(CircleShape)
                    .align(Alignment.TopCenter)
            ) {
                Icon(
                    imageVector = Icons.Outlined.AccountCircle,
                    contentDescription = "",
                    modifier = Modifier.size(140.dp)
                )
            }
        } else {
            // user image
        }

        IconButton(
            onClick = { },
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .align(Alignment.BottomCenter),
            colors = IconButtonColors(
                containerColor = BluePrimary,
                contentColor = GhostWhite,
                disabledContainerColor = LightGray,
                disabledContentColor = GhostWhite,
            )
        ) {
            Icon(
                imageVector = Icons.Outlined.CameraAlt,
                contentDescription = "Camera Icon",
                modifier = Modifier.size(30.dp)
            )
        }
    }
}