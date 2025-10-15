package com.example.risaleezanvakticompose.presentation.screen.tesbihat

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun TesbihatDetailScreen(
    onBackClick: () -> Unit,
    viewModel: TesbihatViewModel = hiltViewModel()
) {
    val htmlContent by viewModel.htmlContent.collectAsState()
    val scrollToId by viewModel.scrollToId.collectAsState()
    var textZoom by remember { mutableIntStateOf(100) }
    var webView by remember { mutableStateOf<WebView?>(null) }
    var shouldScroll by remember { mutableStateOf(false) }

    val systemUiController = rememberSystemUiController()

    DisposableEffect(systemUiController) {
        systemUiController.isStatusBarVisible = false
        systemUiController.isNavigationBarVisible = true // Navigation bar'ı göster (opsiyonel)

        onDispose {
            // Ekrandan çıkınca status bar'ı tekrar göster
            systemUiController.isStatusBarVisible = true
            systemUiController.isNavigationBarVisible = true
        }
    }

    BackHandler {
        onBackClick()
    }

    LaunchedEffect(scrollToId) {
        if (scrollToId != null) {
            shouldScroll = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets(0, 0, 0, 0))
    ) {
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            // Sayfa yüklendikten sonra scroll yap
                            if (shouldScroll && scrollToId != null) {
                                val script = """
                                    var element = document.getElementById('$scrollToId');
                                    if (element) {
                                        element.scrollIntoView({ behavior: 'smooth', block: 'start' });
                                    }
                                """.trimIndent()
                                view?.evaluateJavascript(script, null)
                                shouldScroll = false
                            }
                        }
                    }
                    settings.apply {
                        javaScriptEnabled = true
                        textZoom = 100
                        builtInZoomControls = false
                        displayZoomControls = false
                    }
                    setBackgroundColor(android.graphics.Color.TRANSPARENT)
                    setLayerType(android.view.View.LAYER_TYPE_SOFTWARE, null)
                    webView = this
                }
            },
            update = { view ->
                if (htmlContent.isNotEmpty()) {
                    view.loadDataWithBaseURL(
                        "file:///android_asset/",
                        htmlContent,
                        "text/html",
                        "utf-8",
                        null
                    )
                }
                view.settings.textZoom = textZoom
            },
            modifier = Modifier.fillMaxSize()
        )

        FloatingActionButton(
            onClick = {
                viewModel.clearScrollId()
                onBackClick()
            },
            containerColor = Color.White.copy(alpha = 0.9f),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .size(56.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Kapat",
                tint = Color(0xFF7c3a03)
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FloatingActionButton(
                onClick = {
                    if (textZoom < 200) {
                        textZoom += 10
                        webView?.settings?.textZoom = textZoom
                    }
                },
                containerColor = Color.White.copy(alpha = 0.9f),
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ZoomIn,
                    contentDescription = "Yakınlaştır",
                    tint = Color(0xFF7c3a03)
                )
            }

            FloatingActionButton(
                onClick = {
                    if (textZoom > 50) {
                        textZoom -= 10
                        webView?.settings?.textZoom = textZoom
                    }
                },
                containerColor = Color.White.copy(alpha = 0.9f),
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ZoomOut,
                    contentDescription = "Uzaklaştır",
                    tint = Color(0xFF7c3a03)
                )
            }
        }
    }
}