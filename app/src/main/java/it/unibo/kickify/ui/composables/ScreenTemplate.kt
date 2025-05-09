package it.unibo.kickify.ui.composables

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@Composable
fun ScreenTemplate(
    screenTitle: String,
    navController: NavController,
    showTopAppBar: Boolean,
    showBottomAppBar: Boolean,
    content: @Composable (PaddingValues) -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(drawerState) {
                SideMenuContent()
            }
        }
    ) {
        Scaffold(
            topBar = {
                if(showTopAppBar) {
                    AppBar(
                        navController,
                        title = screenTitle,
                        onNavigationClick = {
                            if (navController.previousBackStackEntry != null) {
                                navController.popBackStack()
                            } else {
                                scope.launch { drawerState.open() }
                            }
                        }
                    )
                }
            },
            bottomBar = {
                if(showBottomAppBar) { BottomBar(navController) }
            }
        ) { contentPadding ->
            content(contentPadding)
        }
    }
}