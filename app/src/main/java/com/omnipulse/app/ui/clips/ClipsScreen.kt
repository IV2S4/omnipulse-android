package com.omnipulse.app.ui.clips

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.ChatBubble
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.VolumeOff
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Stable
class ClipsUiState internal constructor() {
    private val likedById = mutableStateMapOf<String, Boolean>()
    private val mutedById = mutableStateMapOf<String, Boolean>()
    private val pausedById = mutableStateMapOf<String, Boolean>()

    fun isLiked(id: String): Boolean = likedById[id] == true

    fun isMuted(id: String): Boolean = mutedById[id] ?: true

    fun isPaused(id: String): Boolean = pausedById[id] == true

    fun toggleLike(id: String) {
        likedById[id] = !isLiked(id)
    }

    fun toggleMute(id: String) {
        mutedById[id] = !isMuted(id)
    }

    fun togglePlayback(id: String) {
        pausedById[id] = !isPaused(id)
    }
}

@Composable
fun rememberClipsUiState(): ClipsUiState = remember { ClipsUiState() }

private data class ClipFixture(
    val id: String,
    val creator: String,
    val handle: String,
    val avatar: String,
    val caption: String,
    val audio: String,
    val scene: String,
    val sceneLabel: String,
    val colors: List<Color>,
    val likes: Int,
    val comments: Int,
    val reposts: Int,
)

private val clipFixtures = listOf(
    ClipFixture(
        id = "night-market",
        creator = "Maya Chen",
        handle = "@mayamakes",
        avatar = "MC",
        caption = "POV: you followed the music and found the best night market in the city. 🌙",
        audio = "Neon Walk · Luma",
        scene = "🏮",
        sceneLabel = "Night market glow",
        colors = listOf(Color(0xFF160A3A), Color(0xFFDA2CFF), Color(0xFFFF7A18)),
        likes = 12_400,
        comments = 386,
        reposts = 1_208,
    ),
    ClipFixture(
        id = "coastal-run",
        creator = "Leo Martins",
        handle = "@leomoves",
        avatar = "LM",
        caption = "Blue skies, salt air, and a route worth waking up early for.",
        audio = "Sunday Pace · Coastline",
        scene = "🌊",
        sceneLabel = "Morning on the coast",
        colors = listOf(Color(0xFF062A5E), Color(0xFF0077B6), Color(0xFF48CAE4)),
        likes = 8_921,
        comments = 214,
        reposts = 742,
    ),
    ClipFixture(
        id = "studio-loop",
        creator = "Nora Ellis",
        handle = "@noranoise",
        avatar = "NE",
        caption = "Building a beat from one tiny sound. Wait for the bass to arrive. 🎧",
        audio = "Original audio · Nora Ellis",
        scene = "🎚️",
        sceneLabel = "From sample to song",
        colors = listOf(Color(0xFF080912), Color(0xFF5B21B6), Color(0xFFEC4899)),
        likes = 21_700,
        comments = 903,
        reposts = 2_105,
    ),
    ClipFixture(
        id = "ramen",
        creator = "Kai Ito",
        handle = "@kaicooks",
        avatar = "KI",
        caption = "The five-minute bowl I make when comfort food cannot wait.",
        audio = "Kitchen Radio · Paper Lanterns",
        scene = "🍜",
        sceneLabel = "A perfect comfort bowl",
        colors = listOf(Color(0xFF3A0D0D), Color(0xFFB45309), Color(0xFFFBBF24)),
        likes = 16_300,
        comments = 527,
        reposts = 1_664,
    ),
)

@Composable
fun ClipsScreen(
    uiState: ClipsUiState,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState(pageCount = { clipFixtures.size })

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            key = { page -> clipFixtures[page].id },
        ) { page ->
            ClipPage(
                clip = clipFixtures[page],
                isActive = pagerState.currentPage == page,
                uiState = uiState,
            )
        }

        ClipsHeader(
            muted = uiState.isMuted(clipFixtures[pagerState.currentPage].id),
            onMuteClick = {
                uiState.toggleMute(clipFixtures[pagerState.currentPage].id)
            },
            modifier = Modifier.align(Alignment.TopCenter),
        )
    }
}

@Composable
private fun ClipPage(
    clip: ClipFixture,
    isActive: Boolean,
    uiState: ClipsUiState,
) {
    val liked = uiState.isLiked(clip.id)
    val muted = uiState.isMuted(clip.id)
    val isPlaying = isActive && !uiState.isPaused(clip.id)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(clip.colors))
            .clickable(
                role = Role.Button,
                onClickLabel = if (isPlaying) "Pause clip" else "Play clip",
            ) {
                uiState.togglePlayback(clip.id)
            },
    ) {
        DecorativeScene(
            emoji = clip.scene,
            label = clip.sceneLabel,
            isPlaying = isPlaying,
            modifier = Modifier.align(Alignment.Center),
        )

        PlaybackButton(
            isPlaying = isPlaying,
            onClick = { uiState.togglePlayback(clip.id) },
            modifier = Modifier.align(Alignment.Center),
        )

        ClipMetadata(
            clip = clip,
            muted = muted,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, end = 82.dp, bottom = 22.dp),
        )

        ActionRail(
            clip = clip,
            liked = liked,
            onLikeClick = { uiState.toggleLike(clip.id) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 10.dp, bottom = 16.dp),
        )
    }
}

@Composable
private fun ClipsHeader(
    muted: Boolean,
    onMuteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "Following",
            color = Color.White.copy(alpha = 0.68f),
            style = MaterialTheme.typography.titleSmall,
        )
        Box(
            modifier = Modifier
                .padding(horizontal = 13.dp)
                .size(width = 1.dp, height = 18.dp)
                .background(Color.White.copy(alpha = 0.4f)),
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "For you",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Box(
                modifier = Modifier
                    .padding(top = 3.dp)
                    .size(width = 24.dp, height = 2.dp)
                    .background(Color.White, CircleShape),
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            onClick = onMuteClick,
            modifier = Modifier
                .size(42.dp)
                .background(Color.Black.copy(alpha = 0.3f), CircleShape),
        ) {
            Icon(
                imageVector = if (muted) Icons.Rounded.VolumeOff else Icons.Rounded.VolumeUp,
                contentDescription = if (muted) "Unmute clip" else "Mute clip",
                tint = Color.White,
            )
        }
    }
}

@Composable
private fun DecorativeScene(
    emoji: String,
    label: String,
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.alpha(if (isPlaying) 1f else 0.72f),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(190.dp)
                .background(Color.White.copy(alpha = 0.08f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(145.dp)
                    .background(Color.Black.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = emoji, fontSize = 82.sp)
            }
        }
        Text(
            text = label,
            modifier = Modifier.padding(top = 14.dp),
            color = Color.White.copy(alpha = 0.82f),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun PlaybackButton(
    isPlaying: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(72.dp)
            .alpha(if (isPlaying) 0.42f else 1f)
            .background(Color.Black.copy(alpha = 0.42f), CircleShape),
    ) {
        Icon(
            imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
            contentDescription = if (isPlaying) "Pause clip" else "Play clip",
            modifier = Modifier.size(42.dp),
            tint = Color.White,
        )
    }
}

@Composable
private fun ClipMetadata(
    clip: ClipFixture,
    muted: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = clip.creator,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            OutlinedButton(
                onClick = {},
                modifier = Modifier.padding(start = 10.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.85f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    horizontal = 13.dp,
                    vertical = 2.dp,
                ),
            ) {
                Text(text = "Follow", style = MaterialTheme.typography.labelMedium)
            }
        }
        Text(
            text = clip.handle,
            modifier = Modifier.padding(top = 2.dp),
            color = Color.White.copy(alpha = 0.78f),
            style = MaterialTheme.typography.labelMedium,
        )
        Text(
            text = clip.caption,
            modifier = Modifier.padding(top = 8.dp),
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )
        Row(
            modifier = Modifier.padding(top = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Rounded.MusicNote,
                contentDescription = null,
                modifier = Modifier.size(17.dp),
                tint = Color.White,
            )
            Text(
                text = if (muted) "${clip.audio} · muted" else clip.audio,
                modifier = Modifier.padding(start = 6.dp),
                color = Color.White.copy(alpha = 0.9f),
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun ActionRail(
    clip: ClipFixture,
    liked: Boolean,
    onLikeClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.width(62.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(11.dp),
    ) {
        Surface(
            modifier = Modifier.size(48.dp),
            shape = CircleShape,
            color = Color.White.copy(alpha = 0.94f),
            border = BorderStroke(2.dp, Color.White),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = clip.avatar,
                    color = clip.colors.first(),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.ExtraBold,
                )
            }
        }
        ActionButton(
            icon = if (liked) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
            label = compactCount(clip.likes + if (liked) 1 else 0),
            contentDescription = if (liked) "Unlike clip" else "Like clip",
            tint = if (liked) Color(0xFFFF4D78) else Color.White,
            onClick = onLikeClick,
        )
        ActionButton(
            icon = Icons.Rounded.ChatBubble,
            label = compactCount(clip.comments),
            contentDescription = "View comments",
            onClick = {},
        )
        ActionButton(
            icon = Icons.Rounded.Repeat,
            label = compactCount(clip.reposts),
            contentDescription = "Repost clip",
            onClick = {},
        )
        ActionButton(
            icon = Icons.AutoMirrored.Rounded.Send,
            label = "Share",
            contentDescription = "Share clip",
            onClick = {},
        )
    }
}

@Composable
private fun ActionButton(
    icon: ImageVector,
    label: String,
    contentDescription: String,
    onClick: () -> Unit,
    tint: Color = Color.White,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(44.dp)
                .background(Color.Black.copy(alpha = 0.25f), CircleShape),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                modifier = Modifier.size(27.dp),
                tint = tint,
            )
        }
        Text(
            text = label,
            color = Color.White,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

private fun compactCount(value: Int): String = when {
    value >= 1_000_000 -> "${value / 1_000_000}M"
    value >= 10_000 -> "${value / 1_000}K"
    value >= 1_000 -> "${value / 1_000}.${value % 1_000 / 100}K"
    else -> value.toString()
}
