package it.unibo.kickify.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.unibo.kickify.R
import it.unibo.kickify.ui.screens.productList.FilterScreen
import it.unibo.kickify.ui.theme.BluePrimary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(navController: NavController, title: String) {

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    var showSheet by remember { mutableStateOf(false) }

    CenterAlignedTopAppBar(
        title = {
            if(title == stringResource(R.string.app_name)) {
                if (isSystemInDarkTheme()) {
                    Image(
                        painterResource(R.drawable.kickify_dark_banner),
                        "logo",
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier.height(40.dp)
                    )
                } else {
                    Image(
                        painterResource(R.drawable.kickify_light_banner),
                        "logo",
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier.height(40.dp)
                    )
                }

            } else {
                Text(text = title,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        },
        navigationIcon = {
            if (navController.previousBackStackEntry != null) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Go Back")
                }
            } else {
                IconButton(onClick = {  }) {
                    Icon(Icons.Outlined.Menu, "Menu")
                }
            }
        },
        actions = {
            if (title == stringResource(R.string.app_name)) {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(Icons.Outlined.ShoppingBag, contentDescription = "Cart")
                }
            }
            if(title == stringResource(R.string.notificationscreen_title)){
                TextButton(onClick = { /**/ }) {
                    Text(
                        text = stringResource(R.string.notificationscreen_clearAll),
                        color = BluePrimary
                    )
                }
            }
            if(title.contains("Shoes")){ // Popular shoes, etc.
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            showSheet = true
                            sheetState.show()
                        }
                    }
                ) {
                    Icon(Icons.Outlined.Tune, contentDescription = "Filter")
                }
            }
            if(showSheet){
                FilterScreen(
                    onDismissRequest = {
                        coroutineScope.launch {
                            showSheet = false
                            sheetState.hide()
                        }
                    },
                    sheetState = sheetState,
                    onResetFilter = {}
                )
            }
            if (title == stringResource(R.string.profileScreen_title)){
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(Icons.Outlined.Edit, contentDescription = "Edit profile")
                }
            }
            /*if (title != "Settings") {
                IconButton(onClick = { navController.navigate(Settings) }) {
                    Icon(Icons.Outlined.Settings, "Settings")
                }
            }*/
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
            navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
            actionIconContentColor = MaterialTheme.colorScheme.onBackground
        )
    )
}