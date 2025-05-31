package it.unibo.kickify.ui.screens.onboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import it.unibo.kickify.R
import it.unibo.kickify.ui.composables.PageIndicator
import it.unibo.kickify.ui.composables.ScreenTemplate
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    navController: NavController,
    onReachedLastPage: () -> Unit
) {
    val totalPages = 3
    val pagerState = rememberPagerState(pageCount = { totalPages })
    val coroutineScope = rememberCoroutineScope()

    ScreenTemplate(
        screenTitle = "",
        navController = navController,
        showTopAppBar = true,
        bottomAppBarContent = { },
        showModalDrawer = false
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                pageSize = PageSize.Fill
            ) { page ->
                OnboardingPage(page = page)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                PageIndicator(
                    numberOfPages = totalPages,
                    currentPage = pagerState.currentPage,
                    modifier = Modifier.padding(start = 16.dp)
                )
                Button(
                    onClick = {
                        if (pagerState.currentPage < 2) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        } else {
                            onReachedLastPage()
                        }
                    },
                ) {
                    Text(
                        text = when (pagerState.currentPage) {
                            0 -> stringResource(R.string.getStarted_button)
                            else -> stringResource(R.string.nextPage_button)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun OnboardingPage(page: Int) {
    when (page) {
        0 -> OnboardingContent(
            title = stringResource(R.string.onboard_title_1),
            description = stringResource(R.string.onboard_text_1),
            imageResourceID = R.drawable.adidas_onboard
        )
        1 -> OnboardingContent(
            title = stringResource(R.string.onboard_title_2),
            description = stringResource(R.string.onboard_text_2),
            imageResourceID = R.drawable.nike_onboard
        )
        else -> OnboardingContent(
            title = stringResource(R.string.onboard_title_3),
            description = stringResource(R.string.onboard_text_3),
            imageResourceID = R.drawable.newbalance_onboard
        )
    }
}

@Composable
fun OnboardingContent(
    title: String,
    description: String,
    imageResourceID: Int
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Bottom
    ) {
        Image(
            painter = painterResource(imageResourceID),
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.FillWidth,
            contentDescription = stringResource(R.string.welcomePage)
        )
        Text(
            text = title,
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 33.sp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = description,
            style = TextStyle(fontWeight = FontWeight.Normal, fontSize = 22.sp)
        )
    }
}