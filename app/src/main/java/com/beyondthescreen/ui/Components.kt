package com.beyondthescreen.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import com.beyondthescreen.recommendation.Suggestion
import com.beyondthescreen.recommendation.Title

@Composable
fun TvSurface(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    container: Color = Panel,
    focusedContainer: Color = PanelBright,
    onFocusedChange: (Boolean) -> Unit = {},
    content: @Composable BoxScope.(focused: Boolean) -> Unit,
) {
    var focused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (focused) 1.045f else 1f, tween(140), label = "focusScale")
    val interaction = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .onFocusChanged {
                focused = it.isFocused
                onFocusedChange(it.isFocused)
            }
            .clickable(interaction, indication = null, onClick = onClick)
            .border(if (focused) 3.dp else 1.dp, if (focused) Amber else Line, shape)
            .background(if (focused) focusedContainer else container, shape),
        content = { content(focused) },
    )
}

@Composable
fun TvButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    primary: Boolean = false,
    requester: FocusRequester? = null,
) {
    var focused by remember { mutableStateOf(false) }
    val interaction = remember { MutableInteractionSource() }
    val background = when {
        focused && primary -> Amber
        focused -> Ivory
        primary -> Amber.copy(alpha = 0.16f)
        else -> Color.Transparent
    }
    val foreground = if (focused) Ink else if (primary) Amber else Ivory
    Text(
        text = label,
        color = foreground,
        style = BType.label.copy(letterSpacing = 0.8.spSafe()),
        modifier = modifier
            .then(if (requester != null) Modifier.focusRequester(requester) else Modifier)
            .defaultMinSize(minHeight = 48.dp)
            .onFocusChanged { focused = it.isFocused }
            .clickable(interaction, indication = null, onClick = onClick)
            .border(
                width = 1.5.dp,
                color = if (focused || primary) Amber else Line,
                shape = RoundedCornerShape(999.dp),
            )
            .background(background, RoundedCornerShape(999.dp))
            .padding(horizontal = 18.dp, vertical = 12.dp),
    )
}

@Composable
fun ModeChip(configured: Boolean, modelLabel: String, modifier: Modifier = Modifier) {
    val text = if (configured) "Bedrock · $modelLabel" else "Offline curated"
    Text(
        text = text.uppercase(),
        style = BType.label,
        color = if (configured) Sea else Amber,
        modifier = modifier
            .border(1.dp, if (configured) Sea.copy(alpha = 0.7f) else Amber.copy(alpha = 0.7f), RoundedCornerShape(999.dp))
            .padding(horizontal = 14.dp, vertical = 8.dp),
    )
}

@Composable
fun TitleCard(title: Title, onClick: () -> Unit, modifier: Modifier = Modifier) {
    TvSurface(onClick = onClick, modifier = modifier, shape = RoundedCornerShape(18.dp)) {
        Box(Modifier.fillMaxSize()) {
            MotifArt(title.motif, Modifier.fillMaxSize())
            Column(
                Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(Color(0xCC07080C))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            ) {
                Text(title.name, style = BType.cardTitle, maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 16.spSafe())
                Text(
                    text = listOfNotNull(title.year.toString(), title.runtimeLabel, if (title.playable) "Plays here" else "Bridge").joinToString(" · "),
                    style = BType.meta,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
fun SuggestionCard(
    suggestion: Suggestion,
    onClick: () -> Unit,
    onFocused: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TvSurface(onClick = onClick, onFocusedChange = { if (it) onFocused() }, modifier = modifier) { focused ->
        Column(Modifier.padding(16.dp)) {
            Text("WHY THIS", style = BType.label, color = if (focused) Amber else IvoryDim)
            Spacer(Modifier.height(8.dp))
            Text(suggestion.name, style = BType.cardTitle, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(8.dp))
            Text(suggestion.why, style = BType.body, maxLines = 4, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
fun CategoryChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TvSurface(
        onClick = onClick,
        onFocusedChange = { if (it) onClick() },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
    ) { focused ->
        Row(Modifier.padding(horizontal = 12.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .width(3.dp)
                    .height(18.dp)
                    .background(if (selected || focused) Amber else Color.Transparent),
            )
            Spacer(Modifier.width(10.dp))
            Text(label, style = BType.body, maxLines = 1, overflow = TextOverflow.Ellipsis, color = if (focused) Amber else Ivory)
        }
    }
}

private fun Int.spSafe() = androidx.compose.ui.unit.TextUnit(this.toFloat(), androidx.compose.ui.unit.TextUnitType.Sp)

private fun Double.spSafe() = androidx.compose.ui.unit.TextUnit(this.toFloat(), androidx.compose.ui.unit.TextUnitType.Sp)
