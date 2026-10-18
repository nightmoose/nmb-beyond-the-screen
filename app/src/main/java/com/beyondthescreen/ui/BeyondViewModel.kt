package com.beyondthescreen.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.beyondthescreen.BeyondApplication
import com.beyondthescreen.recommendation.Catalog
import com.beyondthescreen.recommendation.RecommendationEngine
import com.beyondthescreen.recommendation.RecommendationSet
import com.beyondthescreen.recommendation.Title
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface Screen {
    data object Home : Screen
    data class Detail(val titleId: String) : Screen
    data class Player(val titleId: String) : Screen
}

data class AppUiState(
    val screen: Screen = Screen.Home,
    val backStack: List<Screen> = emptyList(),
    val recommendations: Map<String, RecommendationSet> = emptyMap(),
    val loading: Set<String> = emptySet(),
)

class BeyondViewModel(
    val catalog: List<Title>,
    private val engine: RecommendationEngine,
    val bedrockConfigured: Boolean,
    val modelLabel: String,
) : ViewModel() {
    private val _state = MutableStateFlow(AppUiState())
    val state: StateFlow<AppUiState> = _state.asStateFlow()
    private val inFlight = mutableSetOf<String>()

    init {
        load(Catalog.FEATURED_ID)
        load("sintel")
    }

    fun openDetail(id: String) = open(Screen.Detail(id))

    fun openPlayer(id: String) = open(Screen.Player(id))

    fun back() {
        _state.update { current ->
            if (current.backStack.isEmpty()) {
                current
            } else {
                current.copy(
                    screen = current.backStack.last(),
                    backStack = current.backStack.dropLast(1),
                )
            }
        }
    }

    private fun open(screen: Screen) {
        _state.update { it.copy(backStack = it.backStack + it.screen, screen = screen) }
        val id = when (screen) {
            is Screen.Detail -> screen.titleId
            is Screen.Player -> screen.titleId
            Screen.Home -> null
        }
        if (id != null) load(id)
    }

    private fun load(id: String) {
        synchronized(inFlight) {
            if (id in _state.value.recommendations || !inFlight.add(id)) return
        }
        viewModelScope.launch {
            _state.update { it.copy(loading = it.loading + id) }
            val result = engine.recommend(catalog.first { it.id == id })
            _state.update {
                it.copy(
                    loading = it.loading - id,
                    recommendations = it.recommendations + (id to result),
                )
            }
            synchronized(inFlight) { inFlight.remove(id) }
        }
    }
}

class BeyondViewModelFactory(
    private val app: BeyondApplication,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val container = app.container
        return BeyondViewModel(
            catalog = container.catalog,
            engine = container.engine,
            bedrockConfigured = container.config.configured,
            modelLabel = container.config.modelLabel(),
        ) as T
    }
}
