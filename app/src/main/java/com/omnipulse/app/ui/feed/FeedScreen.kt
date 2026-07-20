package com.omnipulse.app.ui.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.ChatBubbleOutline
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.NotificationsNone
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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

private data class Story(
    val id: String,
    val name: String,
    val emoji: String,
    val colors: List<Color>,
)

private data class FeedPost(
    val id: String,
    val author: String,
    val handle: String,
    val avatar: String,
    val time: String,
    val message: String,
    val mediaEmoji: String,
    val mediaLabel: String,
    val mediaColors: List<Color>,
    val likes: Int,
    val comments: Int,
    val reposts: Int,
)

private val stories = listOf(
    Story("you", "Your story", "✨", listOf(Color(0xFF5B4DFF), Color(0xFF9B8CFF))),
    Story("maya", "Maya", "🌻", listOf(Color(0xFFFF7A59), Color(0xFFFFC857))),
    Story("leo", "Leo", "🏄", listOf(Color(0xFF00A8E8), Color(0xFF00C9A7))),
    Story("nora", "Nora", "🎧", listOf(Color(0xFFFF4D8D), Color(0xFFA855F7))),
    Story("kai", "Kai", "🚲", listOf(Color(0xFF22C55E), Color(0xFF84CC16))),
)

private val posts = listOf(
    FeedPost(
        id = "city-lights",
        author = "Maya Chen",
        handle = "@mayamakes",
        avatar = "MC",
        time = "12m",
        message = "Found the quietest rooftop above the loudest block. Tiny moments like this reset everything. 🌆",
        mediaEmoji = "🌇",
        mediaLabel = "Golden hour over downtown",
        mediaColors = listOf(Color(0xFF5B4DFF), Color(0xFFFF7A59)),
        likes = 284,
        comments = 36,
        reposts = 18,
    ),
    FeedPost(
        id = "trail",
        author = "Leo Martins",
        handle = "@leomoves",
        avatar = "LM",
        time = "48m",
        message = "Saturday route unlocked. Eight kilometers, two surprise hills, and one very necessary coffee stop.",
        mediaEmoji = "🚴",
        mediaLabel = "Morning ride through the hills",
        mediaColors = listOf(Color(0xFF00796B), Color(0xFF8BC34A)),
        likes = 142,
        comments = 21,
        reposts = 9,
    ),
    FeedPost(
        id = "studio",
        author = "Nora Ellis",
        handle = "@noranoise",
        avatar = "NE",
        time = "2h",
        message = "A new loop escaped the studio today. Headphones recommended, dancing optional.",
        mediaEmoji = "🎛️",
        mediaLabel = "Late-night studio session",
        mediaColors = listOf(Color(0xFF17102F), Color(0xFFB43BFF)),
        likes = 517,
        comments = 64,
        reposts = 41,
    ),
)

@Composable
fun FeedScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            FeedHeader()
        }
        item {
            StoriesRow()
        }
        item {
            ComposerPrompt()
        }
        items(posts, key = FeedPost::id) { post ->
            PostCard(
                post = post,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
    }
}

@Composable
private fun FeedHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, top = 10.dp, end = 8.dp, bottom = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "OmniPulse",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
            )
            Text(
                text = "Good evening, Alex",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
            )
        }
        IconButton(onClick = {}) {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = "Search",
            )
        }
        IconButton(onClick = {}) {
            Icon(
                imageVector = Icons.Rounded.NotificationsNone,
                contentDescription = "Notifications",
            )
        }
    }
}

@Composable
private fun StoriesRow() {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        items(stories, key = Story::id) { story ->
            StoryItem(story)
        }
    }
}

@Composable
private fun StoryItem(story: Story) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(70.dp),
    ) {
        Box(
            modifier = Modifier
                .size(66.dp)
                .background(Brush.linearGradient(story.colors), CircleShape)
                .padding(3.dp)
                .background(MaterialTheme.colorScheme.surface, CircleShape)
                .padding(3.dp)
                .background(Brush.linearGradient(story.colors), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = story.emoji, fontSize = 28.sp)
            if (story.id == "you") {
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(21.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "Add to your story",
                        modifier = Modifier.padding(3.dp),
                        tint = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            }
        }
        Text(
            text = story.name,
            modifier = Modifier.padding(top = 5.dp),
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun ComposerPrompt() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Avatar(initials = "AP")
            OutlinedButton(
                onClick = {},
                modifier = Modifier
                    .padding(start = 10.dp)
                    .weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            ) {
                Text(
                    text = "Share a pulse...",
                    modifier = Modifier.fillMaxWidth(),
                    fontWeight = FontWeight.Normal,
                )
            }
        }
    }
}

@Composable
private fun PostCard(
    post: FeedPost,
    modifier: Modifier = Modifier,
) {
    var liked by rememberSaveable(post.id) { mutableStateOf(false) }
    var bookmarked by rememberSaveable(post.id) { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column {
            Row(
                modifier = Modifier.padding(start = 14.dp, top = 14.dp, end = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Avatar(initials = post.avatar)
                Column(
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .weight(1f),
                ) {
                    Text(
                        text = post.author,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "${post.handle} · ${post.time}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Rounded.MoreVert,
                        contentDescription = "More options",
                    )
                }
            }

            Text(
                text = post.message,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                style = MaterialTheme.typography.bodyLarge,
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(Brush.linearGradient(post.mediaColors)),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = post.mediaEmoji, fontSize = 62.sp)
                    Text(
                        text = post.mediaLabel,
                        modifier = Modifier.padding(top = 10.dp),
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PostAction(
                    icon = if (liked) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                    label = compactCount(post.likes + if (liked) 1 else 0),
                    contentDescription = if (liked) "Unlike" else "Like",
                    tint = if (liked) Color(0xFFE53962) else MaterialTheme.colorScheme.onSurfaceVariant,
                    onClick = { liked = !liked },
                )
                PostAction(
                    icon = Icons.Rounded.ChatBubbleOutline,
                    label = compactCount(post.comments),
                    contentDescription = "Comment",
                    onClick = {},
                )
                PostAction(
                    icon = Icons.Rounded.Repeat,
                    label = compactCount(post.reposts),
                    contentDescription = "Repost",
                    onClick = {},
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.Send,
                        contentDescription = "Share",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                IconButton(onClick = { bookmarked = !bookmarked }) {
                    Icon(
                        imageVector = if (bookmarked) {
                            Icons.Rounded.Bookmark
                        } else {
                            Icons.Rounded.BookmarkBorder
                        },
                        contentDescription = if (bookmarked) "Remove bookmark" else "Bookmark",
                        tint = if (bookmarked) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun Avatar(initials: String) {
    Box(
        modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.secondary,
                    ),
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = initials,
            color = Color.White,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun PostAction(
    icon: ImageVector,
    label: String,
    contentDescription: String,
    onClick: () -> Unit,
    tint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
    TextButton(
        onClick = onClick,
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
        colors = ButtonDefaults.textButtonColors(contentColor = tint),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(20.dp),
        )
        Text(
            text = label,
            modifier = Modifier.padding(start = 5.dp),
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

private fun compactCount(value: Int): String = when {
    value >= 1_000_000 -> "${value / 1_000_000}M"
    value >= 1_000 -> "${value / 1_000}K"
    else -> value.toString()
}
