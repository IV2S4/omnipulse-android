package com.omnipulse.app.ui.clips

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ChatBubble
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.VolumeOff
import androidx.compose.material.icons.rounded.VolumeUp
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class Clip(
    val id: String,
    val creator: String,
    val handle: String,
    val initials: String,
    val caption: String,
    val audio: String,
    val scene: String,
    val sceneLabel: String,
    val colors: List<Color>,
    val likes: Int,
    val comments: Int,
    val reposts: Int,
)

private val clips = listOf(
    Clip(
        id = "night-market",
        creator = "Maya Chen",
        handle = "@mayamakes",
        initials = "MC",
        caption = "POV: you followed the lanterns and found the best noodles in the city. 🌶️ #nightmarket",
        audio = "Midnight Walk · Luma",
        scene = "🏮",
        sceneLabel = "Night market glow",
        colors = listOf(Color(0xFF180B3A), Color(0xFF7D2AE8), Color(0xFFFF7043)),
        likes = 18_400,
        comments = 642,
        reposts = 318,
    ),
    Clip(
        id = "ocean-run",
        creator = "Leo Martins",
        handle = "@leomoves",
        initials = "LM",
        caption = "The kind of finish line that makes the early alarm worth it. 🌊 #morningrun",
        audio = "Open Air · Northbound",
        scene = "🏃",
        sceneLabel = "Sunrise coast run",
        colors = listOf(Color(0xFF003B73), Color(0xFF00A8A8), Color(0xFFFFC857)),
        likes = 9_820,
        comments = 271,
        reposts = 144,
    ),
    Clip(
        id = "studio-loop",
        creator = "Nora Ellis",
        handle = "@noranoise",
        initials = "NE",
        caption = "Building a beat from three sounds I recorded on the train. Headphones on. 🎧 #studiotake",
        audio = "Original audio · Nora Ellis",
        scene = "🎛️",
        sceneLabel = "One-minute studio loop",
        colors = listOf(Color(0xFF120C24), Color(0xFFB02EEC), Color(0xFFFF4D8D)),
        likes = 31_200,
        comments = 1_108,
        reposts = 896,
    ),
)

@Composable
fun ClipsScreen() {
    val pagerState = rememberPagerState(pageCount = clips::size)
    var likedIds by rememberSaveable { mutableStateOf(emptyList<String>()) }
    var mutedIds by rememberSaveable { mutableStateOf(clips.map(Clip::id)) }
    var pausedIds by rememberSaveable { mutableStateOf(emptyList<String>()) }

    VerticalPager(
        state = pagerState,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        key = { page -> clips[page].id },
    ) { page ->
        val clip = clips[page]
        ClipPage(
            clip = clip,
            page = page,
            pageCount = clips.size,
            isLiked = clip.id in likedIds,
            isMuted = clip.id in mutedIds,
            isPlaying = page == pagerState.currentPage && clip.id !in pausedIds,
            onLike = { likedIds = likedIds.toggled(clip.id) },
            onMute = { mutedIds = mutedIds.toggled(clip.id) },
            onPlayPause = { pausedIds = pausedIds.toggled(clip.id) },
        )
    }
}

@Composable
private fun ClipPage(
    clip: Clip,
    page: Int,
    pageCount: Int,
    isLiked: Boolean,
    isMuted: Boolean,
    isPlaying: Boolean,
    onLike: () -> Unit,
    onMute: () -> Unit,
    onPlayPause: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(clip.colors))
            .clickable(
                onClickLabel = if (isPlaying) "Pause clip" else "Play clip",
                onClick = onPlayPause,
            ),
    ) {
        ClipArtwork(
            emoji = clip.scene,
            label = clip.sceneLabel,
            modifier = Modifier.align(Alignment.Center),
        )

        ClipTopBar(
            page = page,
            pageCount = pageCount,
            isMuted = isMuted,
            onMute = onMute,
            modifier = Modifier.align(Alignment.TopCenter),
        )

        if (!isPlaying) {
            Surface(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(74.dp),
                shape = CircleShape,
                color = Color.Black.copy(alpha = 0.45f),
            ) {
                Icon(
                    imageVector = Icons.Rounded.PlayArrow,
                    contentDescription = "Play clip",
                    modifier = Modifier.padding(18.dp),
                    tint = Color.White,
                )
            }
        }

        ClipDetails(
            clip = clip,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 18.dp, end = 88.dp, bottom = 24.dp),
        )

        ClipActionRail(
            clip = clip,
            isLiked = isLiked,
            onLike = onLike,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 10.dp, bottom = 20.dp),
        )
    }
}

@Composable
private fun ClipTopBar(
    page: Int,
    pageCount: Int,
    isMuted: Boolean,
    onMute: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 18.dp, top = 10.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Clips",
            color = Color.White,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "${page + 1} / $pageCount",
            color = Color.White.copy(alpha = 0.82f),
            style = MaterialTheme.typography.labelLarge,
        )
        IconButton(onClick = onMute) {
            Icon(
                imageVector = if (isMuted) Icons.Rounded.VolumeOff else Icons.Rounded.VolumeUp,
                contentDescription = if (isMuted) "Unmute this clip" else "Mute this clip",
                tint = Color.White,
            )
        }
        IconButton(onClick = {}) {
            Icon(
                imageVector = Icons.Rounded.MoreVert,
                contentDescription = "More clip options",
                tint = Color.White,
            )
        }
    }
}

@Composable
private fun ClipArtwork(
    emoji: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.size(280.dp), contentAlignment = Alignment.Center) {
        Surface(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 8.dp, y = 20.dp)
                .size(94.dp),
            shape = CircleShape,
            color = Color.White.copy(alpha = 0.10f),
        ) {}
        Surface(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-2).dp, y = (-10).dp)
                .size(128.dp),
            shape = CircleShape,
            color = Color.Black.copy(alpha = 0.12f),
        ) {}
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = emoji, fontSize = 104.sp)
            Surface(
                modifier = Modifier.padding(top = 18.dp),
                shape = RoundedCornerShape(50),
                color = Color.Black.copy(alpha = 0.26f),
            ) {
                Text(
                    text = label,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                    color = Color.White,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun ClipDetails(
    clip: Clip,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(38.dp),
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.22f),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = clip.initials,
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            Column(modifier = Modifier.padding(start = 9.dp)) {
                Text(
                    text = clip.creator,
                    color = Color.White,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = clip.handle,
                    color = Color.White.copy(alpha = 0.76f),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            Surface(
                modifier = Modifier
                    .padding(start = 10.dp)
                    .clip(RoundedCornerShape(50))
                    .clickable(onClick = {}),
                shape = RoundedCornerShape(50),
                color = Color.White,
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp),
                        tint = Color.Black,
                    )
                    Text(
                        text = "Follow",
                        modifier = Modifier.padding(start = 3.dp),
                        color = Color.Black,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }
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
    onLike: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.width(66.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        ClipAction(
            icon = if (isLiked) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
            label = compactCount(clip.likes + if (isLiked) 1 else 0),
            contentDescription = if (isLiked) "Unlike clip" else "Like clip",
            tint = if (isLiked) Color(0xFFFF4D78) else Color.White,
            onClick = onLike,
        )
        ClipAction(
            icon = Icons.Rounded.ChatBubble,
            label = compactCount(clip.comments),
            contentDescription = "Open comments",
            onClick = {},
        )
        ClipAction(
            icon = Icons.Rounded.Repeat,
            label = compactCount(clip.reposts),
            contentDescription = "Repost clip",
            onClick = {},
        )
        ClipAction(
            icon = Icons.AutoMirrored.Rounded.Send,
            label = "Share",
            contentDescription = "Share clip",
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
        Surface(
            modifier = Modifier.size(46.dp),
            shape = CircleShape,
            color = Color.Black.copy(alpha = 0.32f),
        ) {
            IconButton(onClick = onClick) {
                Icon(
                    imageVector = icon,
                    contentDescription = contentDescription,
                    tint = tint,
                )
            }
        }
        Text(
            text = label,
            modifier = Modifier.padding(top = 3.dp),
            color = Color.White,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

private fun List<String>.toggled(id: String): List<String> =
    if (id in this) this - id else this + id

private fun compactCount(value: Int): String = when {
    value >= 1_000_000 -> "${value / 1_000_000}M"
    value >= 1_000 -> {
        val whole = value / 1_000
        val decimal = (value % 1_000) / 100
        if (decimal == 0) "${whole}K" else "$whole.${decimal}K"
    }
    else -> value.toString()
}
