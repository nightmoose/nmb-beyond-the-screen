package com.beyondthescreen.ui

import android.view.ViewGroup
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.beyondthescreen.recommendation.Catalog
import com.beyondthescreen.recommendation.RecommendationSet
import com.beyondthescreen.recommendation.Suggestion
import com.beyondthescreen.recommendation.Title
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun PlayerScreen(
    title: Title,
    result: RecommendationSet?,
    loading: Boolean,
) {
    val context = LocalContext.current
    val beyondFocus = remember { FocusRequester() }
    var sheet by remember { mutableStateOf(false) }
    var resume by remember { mutableStateOf(false) }
    var playing by remember { mutableStateOf(false) }
    var buffering by remember { mutableStateOf(true) }
    var errorText by remember { mutableStateOf<String?>(null) }
    var position by remember { mutableLongStateOf(0L) }
    var duration by remember { mutableLongStateOf(0L) }
    var focused by remember(title.id) { mutableStateOf<Suggestion?>(null) }
    val exo = remember(title.id) {
        ExoPlayer.Builder(context).build().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
                    .build(),
                true,
            )
            val uri = title.playbackUrl
            when {
                uri == Catalog.BUNDLED_SINTEL -> setMediaItem(
                    MediaItem.fromUri("android.resource://${context.packageName}/raw/sintel_trailer"),
                )
                !uri.isNullOrBlank() -> setMediaItem(MediaItem.fromUri(uri))
            }
            prepare()
            playWhenReady = true
        }
    }
    DisposableEffect(exo) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                playing = isPlaying
            }
            override fun onPlaybackStateChanged(playbackState: Int) {
                buffering = playbackState == Player.STATE_BUFFERING
                if (playbackState == Player.STATE_READY) errorText = null
            }
            override fun onPlayerError(error: PlaybackException) {
                errorText = "Playback didn't start. You can still open recommendations."
            }
        }
        exo.addListener(listener)
        onDispose {
            exo.removeListener(listener)
            exo.release()
        }
    }
    LaunchedEffect(exo) {
        while (isActive) {
            position = exo.currentPosition
            duration = exo.duration.coerceAtLeast(0L)
            delay(400)
        }
    }
    LaunchedEffect(Unit) { runCatching { beyondFocus.requestFocus() } }
    BackHandler(enabled = sheet) {
        sheet = false
        if (resume) exo.play()
    }
    Row(Modifier.fillMaxSize().background(Color.Black)) {
        Box(Modifier.weight(1f).fillMaxHeight()) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        useController = false
                        player = exo
                        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                        setShutterBackgroundColor(android.graphics.Color.parseColor("#07080C"))
                        isFocusable = false
                        descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS
                        keepScreenOn = true
                    }
                },
                update = { it.player = exo },
            )
            Column(
                Modifier
                    .align(Alignment.TopStart)
                    .padding(20.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.55f))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            ) {
                Text(title.name, style = BType.cardTitle)
                val credit = title.attribution?.let { "${it.credit} · ${it.license}" } ?: "Recommendations only"
                Text(credit, style = BType.meta, color = Sea)
            }
            if (errorText != null || buffering) {
                Text(
                    errorText ?: "Buffering...",
                    style = BType.body,
                    modifier = Modifier.align(Alignment.Center).padding(24.dp),
                )
            }
            Column(
                Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.82f))))
                    .padding(20.dp),
            ) {
                val fraction = if (duration > 0) position.toFloat() / duration.toFloat() else 0f
                Box(Modifier.fillMaxWidth().height(3.dp).background(Line)) {
                    Box(Modifier.fillMaxHeight().fillMaxWidth(fraction.coerceIn(0f, 1f)).background(Amber))
                }
                Spacer(Modifier.height(6.dp))
                Text("${clock(position)}  /  ${clock(duration)}", style = BType.meta)
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    TvButton(if (playing) "Pause" else "Play", {
                        if (exo.isPlaying) exo.pause() else exo.play()
                    })
                    if (errorText != null) {
                        TvButton("Try again", {
                            errorText = null
                            exo.prepare()
                            exo.play()
                        })
                    }
                    TvButton("Beyond the screen", {
                        resume = exo.isPlaying
                        exo.pause()
                        sheet = true
                    }, primary = true, requester = beyondFocus)
                }
            }
        }
        if (sheet) {
            BeyondSheet(
                result = result,
                loading = loading,
                focused = focused,
                onFocused = { focused = it },
                modifier = Modifier.width(430.dp).fillMaxHeight(),
            )
        }
    }
}

@Composable
private fun BeyondSheet(
    result: RecommendationSet?,
    loading: Boolean,
    focused: Suggestion?,
    onFocused: (Suggestion) -> Unit,
    modifier: Modifier,
) {
    val context = LocalContext.current
    val shown = focused ?: result?.categories?.firstOrNull()?.suggestions?.firstOrNull()
    val opener = remember { FocusRequester() }
    val firstKey = result?.categories?.firstOrNull()?.suggestions?.firstOrNull()?.let { suggestion ->
        "${result.categories.first().id}-${suggestion.name}"
    }
    LaunchedEffect(firstKey) {
        if (firstKey != null) runCatching { opener.requestFocus() }
    }
    Column(modifier.background(Panel).padding(18.dp)) {
        Text("BEYOND THE SCREEN", style = BType.label)
        Text("Back closes this. The film stays where you paused it.", style = BType.meta)
        Spacer(Modifier.height(10.dp))
        if (result == null) {
            Text(if (loading) "Pairing this film with the rest of the catalog..." else "No pairings yet.", style = BType.body)
            Spacer(Modifier.weight(1f))
        } else {
            LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                result.categories.forEach { category ->
                    item(key = "label-${category.id}") {
                        Text(category.label.uppercase(), style = BType.label, modifier = Modifier.padding(top = 8.dp))
                    }
                    items(category.suggestions, key = { "${category.id}-${it.name}" }) { suggestion ->
                        val key = "${category.id}-${suggestion.name}"
                        TvSurface(
                            onClick = { openAmazonSearch(context, suggestion.url) },
                            onFocusedChange = { if (it) onFocused(suggestion) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .then(if (key == firstKey) Modifier.focusRequester(opener) else Modifier),
                            shape = RoundedCornerShape(12.dp),
                        ) {
                            Column(Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                                Text(suggestion.name, style = BType.cardTitle, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(suggestion.why, style = BType.meta, maxLines = 2, overflow = TextOverflow.Ellipsis)
                            }
                        }
                    }
                }
            }
        }
        if (shown != null) {
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                QrImage(shown.url, Modifier.size(132.dp).clip(RoundedCornerShape(8.dp)))
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(shown.name, style = BType.cardTitle, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Text("Scan to search on Amazon.", style = BType.meta)
                }
            }
        }
    }
}

private fun clock(ms: Long): String {
    if (ms <= 0L) return "0:00"
    val total = ms / 1000
    return "%d:%02d".format(total / 60, total % 60)
}
