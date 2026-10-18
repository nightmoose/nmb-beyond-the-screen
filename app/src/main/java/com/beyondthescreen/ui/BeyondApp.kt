package com.beyondthescreen.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.beyondthescreen.BeyondApplication

@Composable
fun BeyondApp(viewModel: BeyondViewModel = beyondViewModel()) {
    val ui by viewModel.state.collectAsState()
    BackHandler(enabled = ui.backStack.isNotEmpty()) { viewModel.back() }
    Box(Modifier.fillMaxSize().background(Ink)) {
        when (val screen = ui.screen) {
            Screen.Home -> HomeScreen(
                titles = viewModel.catalog,
                bedrockConfigured = viewModel.bedrockConfigured,
                modelLabel = viewModel.modelLabel,
                onOpenDetail = viewModel::openDetail,
                onPlay = viewModel::openPlayer,
            )
            is Screen.Detail -> {
                val title = viewModel.catalog.first { it.id == screen.titleId }
                DetailScreen(
                    title = title,
                    result = ui.recommendations[title.id],
                    loading = title.id in ui.loading,
                    onWatch = { viewModel.openPlayer(title.id) },
                )
            }
            is Screen.Player -> {
                val title = viewModel.catalog.first { it.id == screen.titleId }
                PlayerScreen(
                    title = title,
                    result = ui.recommendations[title.id],
                    loading = title.id in ui.loading,
                )
            }
        }
    }
}

@Composable
private fun beyondViewModel(): BeyondViewModel {
    val app = LocalContext.current.applicationContext as BeyondApplication
    return viewModel(factory = BeyondViewModelFactory(app))
}
