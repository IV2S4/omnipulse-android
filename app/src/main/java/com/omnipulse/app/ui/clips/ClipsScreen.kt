package com.omnipulse.app.ui.clips

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.ChatBubbleOutline
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.VolumeOff
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class Clip(
    val id: String,
    val creator: String,
    val handle: String,
    val avatar: String,
    val caption: String,
    val audio: String,
    val visualEmoji: String,
    val visualDescription: String,
    val colors: List<Color>,
    val likes: Int,
    val comments: Int,
    val shares: Int,
)

private val clips = listOf(
    Clip(
        id = "night-market",
        creator = "Maya Chen",
        handle = "@mayamakes",
        avatar = "MC",
        caption = "A tiny night market with a huge personality. Save room for the mango buns. 🌙",
        audio = "City Glow · Nora Ellis",
        visualEmoji = "🏮",
        visualDescription = "Lanterns glowing above a lively night market",
        colors = listOf(Color(0xFF130F40), Color(0xFFB53471), Color(0xFFFF793F)),
        likes = 12_480,
        comments = 642,
        shares = 318,
    ),
    Clip(
        id = "coastal-ride",
        creator = "Leo Martins",
        handle = "@leomoves",
        avatar = "LM",
        caption = "The coastal road was showing off today. Headphones on, horizon ahead.",
        audio = "Open Roads · Pulse Library",
        visualEmoji = "🚲",
        visualDescription = "A bicycle ride beside a bright blue ocean",
        colors = listOf(Color(0xFF005C97), Color(0xFF00B4DB), Color(0xFFFBD786)),
        likes = 8_921,
        comments = 407,
        shares = 256,
    ),
    Clip(
        id = "studio-loop",
        creator = "Nora Ellis",
        handle = "@noranoise",
        avatar = "NE",
        caption = "Built this loop from train doors, coffee cups, and one very patient synth.",
        audio = "Platform 6 (original sound) · Nora Ellis",
        visualEmoji = "🎛️",
        visualDescription = "Colorful controls in a late-night music studio",
        colors = listOf(Color(0xFF0F0C29), Color(0xFF302B63), Color(0xFFC33764)),
        likes = 24_300,
        comments = 1_204,
        shares = 781,
    ),
)

@Composable
fun ClipsScreen() {
    val pagerState = rememberPagerState(pageCount = clips::size)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        VerticalPager(
            state = pagerState,
            key = { page -> clips[page].id },
            modifier = Modifier.fillMaxSize(),
        ) { page ->
            ClipPage(
                clip = clips[page],
                isActive = page == pagerState.currentPage,
            )
        }

        ClipsHeader(
            currentPage = pagerState.currentPage,
            pageCount = clips.size,
            modifier = Modifier.align(Alignment.TopCenter),
        )
    }
}

@Composable
private fun ClipPage(
    clip: Clip,
    isActive: Boolean,
) {
    var isPlaying by rememberSaveable(clip.id, "playing") { mutableStateOf(true) }
    var isLiked by rememberSaveable(clip.id, "liked") { mutableStateOf(false) }
    var isMuted by rememberSaveable(clip.id, "muted") { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(clip.colors))
            .semantics { contentDescription = clip.visualDescription },
    ) {
        Text(
            text = clip.visualEmoji,
            modifier = Modifier.align(Alignment.Center),
            fontSize = 112.sp,
        )

        Surface(
            modifier = Modifier
                .align(Alignment.Center)
                .size(68.dp),
            shape = CircleShape,
            color = Color.Black.copy(alpha = 0.42f),
        ) {
            IconButton(onClick = { isPlaying = !isPlaying }) {
                Icon(
                    imageVector = if (isActive && isPlaying) {
                        Icons.Rounded.Pause
                    } else {
                        Icons.Rounded.PlayArrow
                    },
                    contentDescription = if (isActive && isPlaying) {
                        "Pause clip"
                    } else {
                        "Play clip"
                    },
                    modifier = Modifier.size(36.dp),
                    tint = Color.White,
                )
            }
        }

        Surface(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 16.dp, top = 72.dp),
            shape = RoundedCornerShape(100),
            color = Color.Black.copy(alpha = 0.36f),
        ) {
            Text(
                text = if (isActive && isPlaying) "Playing preview" else "Paused",
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                color = Color.White,
                style = MaterialTheme.typography.labelMedium,
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.86f)),
                    ),
                )
                .padding(start = 16.dp, top = 72.dp, end = 10.dp, bottom = 18.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            ClipDetails(
                clip = clip,
                modifier = Modifier.weight(1f),
            )
            Spacer(modifier = Modifier.width(8.dp))
            ClipActionRail(
                clip = clip,
                isLiked = isLiked,
                isMuted = isMuted,
                onLike = { isLiked = !isLiked },
                onMute = { isMuted = !isMuted },
            )
        }
    }
}

@Composable
private fun ClipsHeader(
    currentPage: Int,
    pageCount: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(Color.Black.copy(alpha = 0.55f), Color.Transparent),
                ),
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Clips",
            color = Color.White,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
        )
        Text(
            text = "${currentPage + 1} / $pageCount",
            color = Color.White.copy(alpha = 0.84f),
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

@Composable
private fun ClipDetails(
    clip: Clip,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = clip.avatar,
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
            Column(
                modifier = Modifier
                    .padding(start = 10.dp)
                    .weight(1f),
            ) {
                Text(
                    text = clip.creator,
                    color = Color.White,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = clip.handle,
                    color = Color.White.copy(alpha = 0.82f),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black,
                ),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    horizontal = 14.dp,
                    vertical = 6.dp,
                ),
            ) {
                Text("Follow", fontWeight = FontWeight.Bold)
            }
        }

        Text(
            text = clip.caption,
            modifier = Modifier.padding(top = 12.dp),
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
                text = clip.audio,
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
private fun ClipActionRail(
    clip: Clip,
    isLiked: Boolean,
    isMuted: Boolean,
    onLike: () -> Unit,
    onMute: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        ClipAction(
            icon = if (isLiked) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
            label = compactCount(clip.likes + if (isLiked) 1 else 0),
            contentDescription = if (isLiked) "Unlike clip" else "Like clip",
            onClick = onLike,
            tint = if (isLiked) Color(0xFFFF4D78) else Color.White,
        )
        ClipAction(
            icon = Icons.Rounded.ChatBubbleOutline,
            label = compactCount(clip.comments),
            contentDescription = "Open comments",
            onClick = {},
        )
        ClipAction(
            icon = Icons.AutoMirrored.Rounded.Send,
            label = compactCount(clip.shares),
            contentDescription = "Share clip",
            onClick = {},
        )
        ClipAction(
            icon = if (isMuted) Icons.Rounded.VolumeOff else Icons.Rounded.VolumeUp,
            label = if (isMuted) "Muted" else "Sound",
            contentDescription = if (isMuted) "Unmute clip" else "Mute clip",
            onClick = onMute,
        )
        ClipAction(
            icon = Icons.Rounded.MoreHoriz,
            label = "More",
            contentDescription = "More clip options",
            onClick = {},
        )
    }
}

@Composable
private fun ClipAction(
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
                .size(46.dp)
                .background(Color.Black.copy(alpha = 0.32f), CircleShape),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
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
    value >= 1_000 -> "${value / 1_000}K"
    else -> value.toString()
}
