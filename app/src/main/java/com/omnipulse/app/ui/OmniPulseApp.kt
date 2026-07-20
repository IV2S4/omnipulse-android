package com.omnipulse.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.omnipulse.app.feature.feed.FeedScreen

private enum class MainDestination(
    val label: String,
    val icon: ImageVector,
) {
    Feed("Feed", Icons.Default.Home),
    Clips("Clips", Icons.Default.PlayCircleOutline),
    Messages("Messages", Icons.Default.ChatBubbleOutline),
    Assistant("Pulse AI", Icons.Default.AutoAwesome),
}

@Composable
fun OmniPulseApp() {
    var selectedName by rememberSaveable { mutableStateOf(MainDestination.Feed.name) }
    val selected = MainDestination.entries.first { it.name == selectedName }

    Scaffold(
        bottomBar = {
            NavigationBar {
                MainDestination.entries.forEach { destination ->
                    NavigationBarItem(
                        selected = selected == destination,
                        onClick = { selectedName = destination.name },
                        icon = {
                            Icon(
                                imageVector = destination.icon,
                                contentDescription = destination.label,
                            )
                        },
                        label = { Text(destination.label) },
                    )
                }
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when (selected) {
                MainDestination.Feed -> FeedScreen()
                MainDestination.Clips -> ClipsScreen()
                MainDestination.Messages -> MessagesScreen()
                MainDestination.Assistant -> AssistantScreen()
            }
        }
    }
}

private data class Clip(
    val creator: String,
    val caption: String,
    val accent: List<Color>,
)

private val clips = listOf(
    Clip(
        creator = "@rileyruns",
        caption = "The city before everyone wakes up",
        accent = listOf(Color(0xFF153B5B), Color(0xFFDC6E4B)),
    ),
    Clip(
        creator = "@studiohana",
        caption = "Turning a sketch into a tiny clay world",
        accent = listOf(Color(0xFF5A3A69), Color(0xFFE3A35B)),
    ),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ClipsScreen() {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text("Clips", fontWeight = FontWeight.Bold)
            },
            actions = {
                IconButton(onClick = {}) {
                    Icon(Icons.Default.Add, contentDescription = "Create clip")
                }
            },
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(clips) { clip ->
                ClipCard(clip)
            }
        }
    }
}

@Composable
private fun ClipCard(clip: Clip) {
    var liked by rememberSaveable(clip.creator) { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(480.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(Brush.verticalGradient(clip.accent)),
    ) {
        Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = "Play ${clip.creator}'s clip",
            modifier = Modifier
                .size(72.dp)
                .align(Alignment.Center),
            tint = Color.White.copy(alpha = 0.9f),
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(20.dp)
                .padding(end = 56.dp),
        ) {
            Text(clip.creator, color = Color.White, fontWeight = FontWeight.Bold)
            Text(clip.caption, color = Color.White, style = MaterialTheme.typography.titleMedium)
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            IconButton(onClick = { liked = !liked }) {
                Icon(
                    if (liked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (liked) "Unlike clip" else "Like clip",
                    tint = if (liked) Color(0xFFFF8A80) else Color.White,
                )
            }
            IconButton(onClick = {}) {
                Icon(Icons.Default.ChatBubbleOutline, contentDescription = "Clip comments", tint = Color.White)
            }
            IconButton(onClick = {}) {
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Share clip",
                    tint = Color.White,
                )
            }
        }
    }
}

private data class Conversation(
    val id: String,
    val name: String,
    val preview: String,
    val time: String,
    val unread: Int = 0,
)

private val conversations = listOf(
    Conversation("alex", "Alex Morgan", "That sounds perfect — see you then!", "2m", 2),
    Conversation("sam", "Sam Rivera", "Sent a clip", "18m"),
    Conversation("design", "Design circle", "Mina: New color pass is up", "1h", 4),
    Conversation("devon", "Devon Brooks", "Thanks for checking in", "Yesterday"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MessagesScreen() {
    var selectedId by rememberSaveable { mutableStateOf<String?>(null) }
    val selected = conversations.firstOrNull { it.id == selectedId }
    if (selected != null) {
        ConversationScreen(selected, onBack = { selectedId = null })
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Messages", fontWeight = FontWeight.Bold) },
            actions = {
                IconButton(onClick = {}) {
                    Icon(Icons.Default.Search, contentDescription = "Search messages")
                }
            },
        )
        LazyColumn(contentPadding = PaddingValues(vertical = 8.dp)) {
            items(conversations) { conversation ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedId = conversation.id }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Avatar(conversation.name.toInitials(), size = 52)
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(conversation.name, fontWeight = FontWeight.SemiBold)
                        Text(
                            conversation.preview,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            conversation.time,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.labelSmall,
                        )
                        if (conversation.unread > 0) {
                            Spacer(Modifier.height(6.dp))
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary,
                            ) {
                                Text(
                                    "${conversation.unread}",
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    style = MaterialTheme.typography.labelSmall,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private data class ChatMessage(
    val text: String,
    val fromMe: Boolean,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConversationScreen(
    conversation: Conversation,
    onBack: () -> Unit,
) {
    val messages = remember(conversation.id) {
        mutableStateListOf(
            ChatMessage("Hey! Are we still on for tomorrow?", false),
            ChatMessage("Absolutely. How does 10:30 sound?", true),
            ChatMessage(conversation.preview, false),
        )
    }
    var draft by rememberSaveable(conversation.id) { mutableStateOf("") }
    val send = {
        val message = draft.trim()
        if (message.isNotEmpty()) {
            messages += ChatMessage(message, true)
            draft = ""
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Avatar(conversation.name.toInitials(), size = 36)
                    Spacer(Modifier.width(10.dp))
                    Text(conversation.name, style = MaterialTheme.typography.titleMedium)
                }
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
        )
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(messages) { message ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (message.fromMe) Arrangement.End else Arrangement.Start,
                ) {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = if (message.fromMe) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.surfaceContainerHighest
                        },
                    ) {
                        Text(
                            message.text,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            color = if (message.fromMe) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            },
                        )
                    }
                }
            }
        }
        MessageComposer(
            value = draft,
            placeholder = "Message ${conversation.name}",
            onValueChange = { draft = it },
            onSend = send,
        )
    }
}

private data class AssistantMessage(
    val text: String,
    val fromUser: Boolean,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AssistantScreen() {
    val messages = remember {
        mutableStateListOf(
            AssistantMessage(
                "Hi, I’m Pulse AI. I can help you catch up, draft a message, or brainstorm something new.",
                false,
            ),
        )
    }
    var draft by rememberSaveable { mutableStateOf("") }
    val submit: (String) -> Unit = { prompt ->
        val cleanPrompt = prompt.trim()
        if (cleanPrompt.isNotEmpty()) {
            messages += AssistantMessage(cleanPrompt, true)
            messages += AssistantMessage(
                "I’m ready to help with that. Live AI responses will connect in a coming MVP slice.",
                false,
            )
            draft = ""
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Pulse AI", fontWeight = FontWeight.Bold)
                }
            },
        )
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(messages) { message ->
                AssistantBubble(message)
            }
            if (messages.size == 1) {
                item {
                    Text(
                        "Try asking",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(
                            "Catch me up on today",
                            "Draft a friendly check-in",
                            "Give me three dinner ideas",
                        ).forEach { prompt ->
                            AssistChip(
                                onClick = { submit(prompt) },
                                label = { Text(prompt) },
                            )
                        }
                    }
                }
            }
        }
        MessageComposer(
            value = draft,
            placeholder = "Ask Pulse AI",
            onValueChange = { draft = it },
            onSend = { submit(draft) },
        )
    }
}

@Composable
private fun AssistantBubble(message: AssistantMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.fromUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom,
    ) {
        if (!message.fromUser) {
            Surface(
                modifier = Modifier.size(32.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
            ) {
                Icon(
                    Icons.Default.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.padding(7.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
            Spacer(Modifier.width(8.dp))
        }
        Surface(
            modifier = Modifier.fillMaxWidth(0.82f),
            shape = RoundedCornerShape(20.dp),
            color = if (message.fromUser) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.surfaceContainerHighest
            },
        ) {
            Text(
                message.text,
                modifier = Modifier.padding(14.dp),
                color = if (message.fromUser) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
            )
        }
    }
}

@Composable
private fun MessageComposer(
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
) {
    Surface(shadowElevation = 4.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text(placeholder) },
                shape = RoundedCornerShape(24.dp),
                maxLines = 4,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { onSend() }),
            )
            FilledIconButton(
                onClick = onSend,
                enabled = value.isNotBlank(),
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
            }
        }
    }
}

@Composable
private fun Avatar(
    initials: String,
    size: Int = 44,
) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.secondaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            initials,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

private fun String.toInitials(): String =
    split(" ")
        .mapNotNull { it.firstOrNull()?.uppercase() }
        .take(2)
        .joinToString("")
