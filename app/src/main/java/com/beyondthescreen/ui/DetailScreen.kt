package com.beyondthescreen.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.beyondthescreen.recommendation.RecommendationSet
import com.beyondthescreen.recommendation.Source
import com.beyondthescreen.recommendation.Title

@Composable
fun DetailScreen(
    title: Title,
    result: RecommendationSet?,
    loading: Boolean,
    onWatch: () -> Unit,
) {
    val categories = result?.categories.orEmpty()
    var categoryIndex by remember(title.id, categories.size) { mutableIntStateOf(0) }
    var suggestionIndex by remember(title.id, categoryIndex) { mutableIntStateOf(0) }
    val category = categories.getOrNull(categoryIndex.coerceAtMost((categories.size - 1).coerceAtLeast(0)))
    val suggestion = category?.suggestions?.getOrNull(
        suggestionIndex.coerceIn(0, (category.suggestions.size - 1).coerceAtLeast(0)),
    )
    val firstCard = remember(title.id) { FocusRequester() }
    LaunchedEffect(result?.titleId, result?.source) {
        if (result != null) runCatching { firstCard.requestFocus() }
    }
    Row(Modifier.fillMaxSize().background(Ink)) {
        Column(
            Modifier
                .weight(1f)
                .padding(start = 32.dp, top = 22.dp, end = 16.dp, bottom = 18.dp),
        ) {
            Header(title, result, loading, onWatch)
            Spacer(Modifier.height(12.dp))
            if (categories.isEmpty()) {
                Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                    Text(
                        if (loading) "Looking past the credits..." else "No pairings yet.",
                        style = BType.title,
                    )
                }
            } else {
                Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    Column(
                        Modifier.width(210.dp).fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text("CATEGORIES", style = BType.label)
                        categories.forEachIndexed { index, item ->
                            CategoryChip(
                                label = item.label,
                                selected = index == categoryIndex,
                                onClick = { categoryIndex = index },
                            )
                        }
                    }
                    LazyRow(
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(vertical = 4.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        itemsIndexed(category?.suggestions.orEmpty()) { index, item ->
                            SuggestionCard(
                                suggestion = item,
                                onClick = {},
                                onFocused = { suggestionIndex = index },
                                modifier = Modifier
                                    .width(280.dp)
                                    .fillMaxHeight()
                                    .padding(vertical = 8.dp)
                                    .then(if (index == 0) Modifier.focusRequester(firstCard) else Modifier),
                            )
                        }
                    }
                }
            }
        }
        Dock(
            suggestionName = suggestion?.name ?: title.name,
            why = suggestion?.why ?: title.logline,
            url = suggestion?.url,
            ready = suggestion != null,
            modifier = Modifier.width(320.dp).fillMaxHeight(),
        )
    }
}

@Composable
private fun Header(title: Title, result: RecommendationSet?, loading: Boolean, onWatch: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        MotifArt(
            title.motif,
            Modifier.size(72.dp).clip(RoundedCornerShape(16.dp)),
        )
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(
                listOfNotNull(title.year.toString(), title.kind, title.runtimeLabel).joinToString("  ·  "),
                style = BType.label,
            )
            Text(title.name, style = BType.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(title.logline, style = BType.meta, maxLines = 1, overflow = TextOverflow.Ellipsis)
            val credit = title.attribution?.let { "${it.credit} · ${it.license}" }
            if (credit != null) {
                Text(credit, style = BType.meta, color = Sea, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            val status = when {
                loading && result == null -> "Pairing this title..."
                result?.source == Source.BEDROCK -> "Suggested with Amazon Bedrock"
                result != null -> "Curated picks"
                else -> ""
            }
            if (status.isNotBlank()) Text(status, style = BType.meta, color = Amber)
            if (!result?.note.isNullOrBlank()) {
                Text(result?.note.orEmpty(), style = BType.meta, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
        }
        if (title.playable) {
            Spacer(Modifier.width(12.dp))
            TvButton("Watch", onWatch, primary = true)
        }
    }
}

@Composable
private fun Dock(
    suggestionName: String,
    why: String,
    url: String?,
    ready: Boolean,
    modifier: Modifier,
) {
    val context = LocalContext.current
    Column(
        modifier
            .background(Panel)
            .padding(22.dp),
    ) {
        Text(if (ready) "ON YOUR PHONE" else "THIS TITLE", style = BType.label, color = Sea)
        Spacer(Modifier.height(8.dp))
        Text(suggestionName, style = BType.title, maxLines = 3, overflow = TextOverflow.Ellipsis)
        Spacer(Modifier.height(8.dp))
        Text(why, style = BType.body, maxLines = 4, overflow = TextOverflow.Ellipsis)
        Spacer(Modifier.height(16.dp))
        if (url != null) {
            QrImage(url, Modifier.size(188.dp).clip(RoundedCornerShape(12.dp)).align(Alignment.CenterHorizontally))
            Spacer(Modifier.height(10.dp))
            Text(
                "Scan to search on Amazon. No affiliate tag.",
                style = BType.meta,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
            Spacer(Modifier.weight(1f))
            TvButton(
                "Open search",
                onClick = { openAmazonSearch(context, url) },
                modifier = Modifier.fillMaxWidth(),
            )
        } else {
            Spacer(Modifier.weight(1f))
            Text("Choose a suggestion. The code for that search stays on this side.", style = BType.meta)
        }
    }
}
