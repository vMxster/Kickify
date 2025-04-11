package it.unibo.kickify.ui.screens.cart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import it.unibo.kickify.ui.composables.CartItem
import it.unibo.kickify.ui.composables.CartResume
import it.unibo.kickify.ui.theme.KickifyTheme

@Preview
@Composable
fun CartScreen(){
    KickifyTheme {
        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            CartItem("Nike Pegasus", 69.99, 41)
            Spacer(Modifier.height(15.dp))
            CartItem("Nike Zoom 2K", 129.99, 38)
            Spacer(Modifier.height(15.dp))
            CartItem("Nike Air Zoom", 87.99, 45 )

            Spacer(Modifier.height(20.dp))
            CartResume(subTotal = (69.99+129.99+87.99), shipping = 10.0)
        }
    }
}