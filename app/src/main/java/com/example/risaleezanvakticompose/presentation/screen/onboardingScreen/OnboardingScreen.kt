package com.example.risaleezanvakticompose.presentation.screen.onboardingScreen

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.risaleezanvakticompose.R
import kotlinx.coroutines.launch

data class OnboardingPage(
    val title: String,
    val description: String,
    val icon: ImageVector? = null,
    val imageRes: Int? = null,
    val primaryColor: Color
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onFinishOnboarding: () -> Unit,
    onSkipOnboarding: () -> Unit
) {
    val pages = listOf(
        OnboardingPage(
            title = "Namaz Vakitleri",
            description = "Bulunduğunuz konuma göre güncel namaz vakitlerini öğrenin. Vaktinden önce bildirim alarak hiçbir namazı kaçırmayın.",
            icon = Icons.Default.Notifications,
            primaryColor = Color(0xFFFFD700)
        ),
        OnboardingPage(
            title = "Tesbihat Bilgileri",
            description = "Sabah ve akşam tesbihatlarının fazileti, okunuşu ve anlamları hakkında detaylı bilgiler edinin.",
            imageRes = R.drawable.ic_tasbih_filled,
            primaryColor = Color(0xFFFFD700)
        ),
        OnboardingPage(
            title = "Kıble Yönü",
            description = "Bulunduğunuz konumdan Kabe'nin yönünü hassas pusula sensörleriyle öğrenin ve doğru yöne yönelin.",
            icon = Icons.Default.Explore,
            primaryColor = Color(0xFFFFD700)
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.yeni_arkaplan),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        Scaffold(
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .statusBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    if (pagerState.currentPage < pages.size - 1) {
                        TextButton(
                            onClick = onSkipOnboarding,
                            modifier = Modifier
                                .background(
                                    Color.White.copy(alpha = 0.1f),
                                    RoundedCornerShape(8.dp)
                                )
                        ) {
                            Text(
                                text = "Atla",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        OnboardingPageContent(pages[page])
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(pages.size) { index ->
                        val width = animateDpAsState(
                            targetValue = if (pagerState.currentPage == index) 32.dp else 8.dp,
                            label = "indicator"
                        )
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .height(8.dp)
                                .width(width.value)
                                .clip(CircleShape)
                                .background(
                                    if (pagerState.currentPage == index)
                                        Color(0xFFFFD700)
                                    else
                                        Color.White.copy(alpha = 0.3f)
                                )
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (pagerState.currentPage > 0) {
                        OutlinedButton(
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.White,
                                containerColor = Color.White.copy(alpha = 0.1f)
                            )
                        ) {
                            Text(
                                "Geri",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }

                    Button(
                        onClick = {
                            if (pagerState.currentPage < pages.size - 1) {
                                scope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            } else {
                                onFinishOnboarding()
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFD700),
                            contentColor = Color(0xFF5C1A1A)
                        )
                    ) {
                        Text(
                            text = if (pagerState.currentPage < pages.size - 1) "İleri" else "Başla",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            when {
                page.icon != null -> {
                    Icon(
                        imageVector = page.icon,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = page.primaryColor
                    )
                }

                page.imageRes != null -> {
                    Icon(
                        painter = painterResource(id = page.imageRes),
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = page.primaryColor
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = page.title,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color.White,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = page.description,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = Color.White.copy(alpha = 0.85f),
            lineHeight = 24.sp,
            fontWeight = FontWeight.Medium
        )
    }
}