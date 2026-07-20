package com.omnipulse.app.ui.chat

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.AddComment
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Videocam
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class Conversation(
    val id: String,
    val name: String,
    val handle: String,
    val initials: String,
    val preview: String,
    val timestamp: String,
    val unreadCount: Int,
    val isOnline: Boolean,
    val colors: List<Color>,
    val messages: List<ChatMessage>,
)

private data class ChatMessage(
    val id: String,
    val body: String,
    val timestamp: String,
    val isMine: Boolean,
)

private data class LocalSentMessage(
    val conversationId: String,
    val body: String,
)

private const val StateSeparator = "\u001F"

private val sentMessagesSaver = Saver<List<LocalSentMessage>, ArrayList<String>>(
    save = { messages ->
        ArrayList(
            messages.map { message ->
                "${message.conversationId}$StateSeparator${message.body}"
            },
        )
    },
    restore = { saved ->
        saved.mapNotNull { value ->
            val separatorIndex = value.indexOf(StateSeparator)
            if (separatorIndex < 0) {
                null
            } else {
                LocalSentMessage(
                    conversationId = value.substring(0, separatorIndex),
                    body = value.substring(separatorIndex + StateSeparator.length),
                )
            }
        }
    },
)

private val draftsSaver = Saver<Map<String, String>, ArrayList<String>>(
    save = { drafts ->
        ArrayList(drafts.map { (conversationId, draft) -> "$conversationId$StateSeparator$draft" })
    },
    restore = { saved ->
        saved.mapNotNull { value ->
            val separatorIndex = value.indexOf(StateSeparator)
            if (separatorIndex < 0) null else {
                value.substring(0, separatorIndex) to
                    value.substring(separatorIndex + StateSeparator.length)
            }
        }.toMap()
    },
)

private val openedConversationIdsSaver = Saver<Set<String>, ArrayList<String>>(
    save = { ArrayList(it) },
    restore = { it.toSet() },
)

private val conversations = listOf(
    Conversation(
        id = "maya",
        name = "Maya Chen",
        handle = "@mayamakes",
        initials = "MC",
        preview = "The rooftop view was unreal 🌆",
        timestamp = "2m",
        unreadCount = 2,
        isOnline = true,
        colors = listOf(Color(0xFFFF7A59), Color(0xFFA855F7)),
        messages = listOf(
            ChatMessage("maya-1", "Hey! Are you still coming to the night market?", "7:42 PM", false),
            ChatMessage("maya-2", "Absolutely. I should be there around eight.", "7:44 PM", true),
            ChatMessage("maya-3", "Perfect — I found the mango buns you mentioned.", "7:45 PM", false),
            ChatMessage("maya-4", "The rooftop view was unreal 🌆", "7:46 PM", false),
        ),
    ),
    Conversation(
        id = "leo",
        name = "Leo Martins",
        handle = "@leomoves",
        initials = "LM",
        preview = "Saturday morning ride?",
        timestamp = "18m",
        unreadCount = 1,
        isOnline = true,
        colors = listOf(Color(0xFF00A8E8), Color(0xFF00C9A7)),
        messages = listOf(
            ChatMessage("leo-1", "That coastal route looked amazing.", "6:18 PM", true),
            ChatMessage("leo-2", "It was! Saturday morning ride?", "6:22 PM", false),
        ),
    ),
    Conversation(
        id = "nora",
        name = "Nora Ellis",
        handle = "@noranoise",
        initials = "NE",
        preview = "Sent you the new mix 🎧",
        timestamp = "1h",
        unreadCount = 0,
        isOnline = false,
        colors = listOf(Color(0xFFFF4D8D), Color(0xFF5B4DFF)),
        messages = listOf(
            ChatMessage("nora-1", "Sent you the new mix 🎧", "5:11 PM", false),
            ChatMessage("nora-2", "Listening now. The second drop is so good.", "5:20 PM", true),
        ),
    ),
    Conversation(
        id = "kai",
        name = "Kai Brooks",
        handle = "@kaiexplores",
        initials = "KB",
        preview = "Thanks for the trail recommendation!",
        timestamp = "3h",
        unreadCount = 0,
        isOnline = false,
        colors = listOf(Color(0xFF22C55E), Color(0xFF84CC16)),
        messages = listOf(
            ChatMessage("kai-1", "Thanks for the trail recommendation!", "2:34 PM", false),
            ChatMessage("kai-2", "Anytime! Go early for the best view.", "2:40 PM", true),
        ),
    ),
    Conversation(
        id = "pulse-team",
        name = "OmniPulse Team",
        handle = "@omnipulse",
        initials = "OP",
        preview = "Welcome! Your profile is ready.",
        timestamp = "Mon",
        unreadCount = 0,
        isOnline = true,
        colors = listOf(Color(0xFF5B4DFF), Color(0xFF9B8CFF)),
        messages = listOf(
            ChatMessage("team-1", "Welcome! Your OmniPulse profile is ready.", "Monday", false),
            ChatMessage("team-2", "Explore your feed, clips, chats, and AI space anytime.", "Monday", false),
        ),
    ),
)

@Composable
fun ChatsScreen() {
    var selectedConversationId by rememberSaveable { mutableStateOf<String?>(null) }
    var sentMessages by rememberSaveable(stateSaver = sentMessagesSaver) {
        mutableStateOf(emptyList())
    }
    var drafts by rememberSaveable(stateSaver = draftsSaver) {
        mutableStateOf(emptyMap())
    }
    var openedConversationIds by rememberSaveable(stateSaver = openedConversationIdsSaver) {
        mutableStateOf(emptySet())
    }
    val selectedConversation = conversations.firstOrNull { it.id == selectedConversationId }

    BackHandler(enabled = selectedConversation != null) {
        selectedConversationId = null
    }

    if (selectedConversation == null) {
        ConversationInbox(
            conversations = conversations,
            openedConversationIds = openedConversationIds,
            sentMessages = sentMessages,
            onConversationClick = { conversationId ->
                openedConversationIds = openedConversationIds + conversationId
                selectedConversationId = conversationId
            },
        )
    } else {
        val localMessages = sentMessages
            .filter { it.conversationId == selectedConversation.id }
            .mapIndexed { index, message ->
                ChatMessage(
                    id = "local-${selectedConversation.id}-$index",
                    body = message.body,
                    timestamp = "Now",
                    isMine = true,
                )
            }
        ChatThread(
            conversation = selectedConversation,
            messages = selectedConversation.messages + localMessages,
            draft = drafts[selectedConversation.id].orEmpty(),
            onDraftChange = { draft ->
                drafts = if (draft.isEmpty()) {
                    drafts - selectedConversation.id
                } else {
                    drafts + (selectedConversation.id to draft)
                }
            },
            onSend = {
                val message = drafts[selectedConversation.id].orEmpty().trim()
                if (message.isNotEmpty()) {
                    sentMessages = sentMessages + LocalSentMessage(selectedConversation.id, message)
                    drafts = drafts - selectedConversation.id
                }
            },
            onBack = { selectedConversationId = null },
        )
    }
}

@Composable
private fun ConversationInbox(
    conversations: List<Conversation>,
    openedConversationIds: Set<String>,
    sentMessages: List<LocalSentMessage>,
    onConversationClick: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        InboxHeader()
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 6.dp),
        ) {
            items(conversations, key = Conversation::id) { conversation ->
                val localPreview = sentMessages.lastOrNull {
                    it.conversationId == conversation.id
                }?.body
                ConversationRow(
                    conversation = conversation,
                    preview = localPreview?.let { "You: $it" } ?: conversation.preview,
                    timestamp = if (localPreview == null) conversation.timestamp else "Now",
                    unreadCount = if (conversation.id in openedConversationIds) {
                        0
                    } else {
                        conversation.unreadCount
                    },
                    onClick = { onConversationClick(conversation.id) },
                )
            }
        }
    }
}

@Composable
private fun InboxHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, top = 12.dp, end = 8.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Chats",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
            )
            Text(
                text = "Your private conversations",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
            )
        }
        IconButton(onClick = {}) {
            Icon(Icons.Rounded.Search, contentDescription = "Search conversations")
        }
        IconButton(onClick = {}) {
            Icon(Icons.Rounded.AddComment, contentDescription = "Start a new conversation")
        }
    }
}

@Composable
private fun ConversationRow(
    conversation: Conversation,
    preview: String,
    timestamp: String,
    unreadCount: Int,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        color = if (unreadCount > 0) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.26f)
        } else {
            Color.Transparent
        },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ConversationAvatar(conversation = conversation, size = 54)
            Column(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .weight(1f),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = conversation.name,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (unreadCount > 0) FontWeight.Bold else FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = timestamp,
                        color = if (unreadCount > 0) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
                Row(
                    modifier = Modifier.padding(top = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = preview,
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (unreadCount > 0) FontWeight.SemiBold else FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (unreadCount > 0) {
                        Spacer(modifier = Modifier.width(8.dp))
                        BadgedBox(
                            badge = {
                                Badge {
                                    Text(unreadCount.toString())
                                }
                            },
                        ) {}
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatThread(
    conversation: Conversation,
    messages: List<ChatMessage>,
    draft: String,
    onDraftChange: (String) -> Unit,
    onSend: () -> Unit,
    onBack: () -> Unit,
) {
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .imePadding(),
    ) {
        ThreadHeader(conversation = conversation, onBack = onBack)
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                Text(
                    text = "Today",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                )
            }
            items(messages, key = ChatMessage::id) { message ->
                MessageBubble(message)
            }
        }
        MessageComposer(
            draft = draft,
            onDraftChange = onDraftChange,
            onSend = onSend,
        )
    }
}

@Composable
private fun ThreadHeader(
    conversation: Conversation,
    onBack: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back to conversations")
        }
        ConversationAvatar(conversation = conversation, size = 42)
        Column(
            modifier = Modifier
                .padding(start = 10.dp)
                .weight(1f),
        ) {
            Text(
                text = conversation.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = if (conversation.isOnline) "Active now" else conversation.handle,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
            )
        }
        IconButton(onClick = {}) {
            Icon(Icons.Rounded.Call, contentDescription = "Voice call ${conversation.name}")
        }
        IconButton(onClick = {}) {
            Icon(Icons.Rounded.Videocam, contentDescription = "Video call ${conversation.name}")
        }
        IconButton(onClick = {}) {
            Icon(Icons.Rounded.MoreVert, contentDescription = "Conversation options")
        }
    }
}

@Composable
private fun MessageBubble(message: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isMine) Arrangement.End else Arrangement.Start,
    ) {
        Column(
            horizontalAlignment = if (message.isMine) Alignment.End else Alignment.Start,
        ) {
            Surface(
                shape = RoundedCornerShape(
                    topStart = 20.dp,
                    topEnd = 20.dp,
                    bottomStart = if (message.isMine) 20.dp else 5.dp,
                    bottomEnd = if (message.isMine) 5.dp else 20.dp,
                ),
                color = if (message.isMine) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                },
            ) {
                Text(
                    text = message.body,
                    modifier = Modifier
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                        .widthIn(max = 272.dp),
                    color = if (message.isMine) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
            Text(
                text = message.timestamp,
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}

@Composable
private fun MessageComposer(
    draft: String,
    onDraftChange: (String) -> Unit,
    onSend: () -> Unit,
) {
    val focusManager = LocalFocusManager.current

    Surface(
        tonalElevation = 2.dp,
        shadowElevation = 4.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            OutlinedTextField(
                value = draft,
                onValueChange = onDraftChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Message") },
                shape = RoundedCornerShape(24.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(
                    onSend = {
                        onSend()
                        focusManager.clearFocus()
                    },
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                ),
                maxLines = 4,
            )
            Spacer(modifier = Modifier.width(8.dp))
            FilledIconButton(
                onClick = {
                    onSend()
                    focusManager.clearFocus()
                },
                enabled = draft.isNotBlank(),
                modifier = Modifier.size(48.dp),
            ) {
                Icon(Icons.AutoMirrored.Rounded.Send, contentDescription = "Send message")
            }
        }
    }
}

@Composable
private fun ConversationAvatar(
    conversation: Conversation,
    size: Int,
) {
    BadgedBox(
        badge = {
            if (conversation.isOnline) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(2.dp)
                        .background(Color(0xFF22C55E), CircleShape),
                )
            }
        },
    ) {
        Box(
            modifier = Modifier
                .size(size.dp)
                .background(Brush.linearGradient(conversation.colors), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = conversation.initials,
                color = Color.White,
                fontSize = (size * 0.28f).sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
