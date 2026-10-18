package com.beyondthescreen.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.beyondthescreen.recommendation.Catalog
import com.beyondthescreen.recommendation.Title

@Composable
fun HomeScreen(
    titles: List<Title>,
    bedrockConfigured: Boolean,
    modelLabel: String,
    onOpenDetail: (String) -> Unit,
    onPlay: (String) -> Unit,
) {
    val featured = titles.first { it.id == Catalog.FEATURED_ID }
    val playable = titles.filter { it.playable }
    val bridges = titles.filter { !it.playable }
    val primary = remember { FocusRequester() }
    LaunchedEffect(Unit) { runCatching { primary.requestFocus() } }
    Column(
        Modifier
            .fillMaxSize()
            .background(Ink)
            .padding(horizontal = 36.dp, vertical = 20.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("FIRE TV  ·  CROSS-MEDIA", style = BType.label)
                Text("Beyond the Screen", style = BType.title)
            }
            ModeChip(bedrockConfigured, modelLabel)
        }
        Hero(featured, primary, Modifier.weight(1f).padding(vertical = 12.dp), onOpenDetail, onPlay)
        Shelf("Plays in this app", "Creative Commons films, with credit on screen.", playable, onOpenDetail)
        Spacer(Modifier.height(8.dp))
        Shelf("Bridge titles", "Named for the recommendation. No studio footage.", bridges, onOpenDetail)
    }
}

@Composable
private fun Hero(
    title: Title,
    primary: FocusRequester,
    modifier: Modifier,
    onOpenDetail: (String) -> Unit,
    onPlay: (String) -> Unit,
) {
    val palette = motifPalette(title.motif)
    Row(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Brush.horizontalGradient(listOf(palette.field, Ink))),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1.2f).padding(28.dp)) {
            Text("FEATURED BRIDGE", style = BType.label, color = palette.accent)
            Spacer(Modifier.height(8.dp))
            Text(title.name, style = BType.display, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(8.dp))
            Text(title.logline, style = BType.italic, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TvButton("Beyond the screen", { onOpenDetail(title.id) }, primary = true, requester = primary)
                TvButton("Play Sintel", { onPlay("sintel") })
            }
        }
        MotifArt(
            title.motif,
            Modifier
                .padding(24.dp)
                .width(240.dp)
                .fillMaxHeight()
                .clip(RoundedCornerShape(24.dp)),
        )
    }
}

@Composable
private fun Shelf(title: String, subtitle: String, titles: List<Title>, onOpen: (String) -> Unit) {
    Column {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(title, style = BType.cardTitle)
            Spacer(Modifier.width(12.dp))
            Text(subtitle, style = BType.meta, modifier = Modifier.padding(bottom = 2.dp))
        }
        Spacer(Modifier.height(8.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 4.dp, horizontal = 2.dp),
        ) {
            items(titles, key = { it.id }) { item ->
                TitleCard(item, onClick = { onOpen(item.id) }, modifier = Modifier.width(176.dp).height(100.dp))
            }
        }
    }
}
