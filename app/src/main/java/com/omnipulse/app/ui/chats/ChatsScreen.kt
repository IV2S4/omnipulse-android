package com.omnipulse.app.ui.chats

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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Videocam
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

internal data class ChatMessage(
    val id: String,
    val text: String,
    val timestamp: String,
    val isOutgoing: Boolean,
)

internal data class Conversation(
    val id: String,
    val name: String,
    val handle: String,
    val initials: String,
    val status: String,
    val isOnline: Boolean,
    val accentColors: List<Color>,
    val initialUnreadCount: Int,
    val messages: List<ChatMessage>,
)

private val conversationFixtures = listOf(
    Conversation(
        id = "maya",
        name = "Maya Chen",
        handle = "@mayamakes",
        initials = "MC",
        status = "Active now",
        isOnline = true,
        accentColors = listOf(Color(0xFFFF7A59), Color(0xFFFFC857)),
        initialUnreadCount = 2,
        messages = listOf(
            ChatMessage("maya-1", "That rooftop view was unreal ✨", "6:38 PM", false),
            ChatMessage("maya-2", "Right? I almost missed the sunset.", "6:40 PM", true),
            ChatMessage("maya-3", "Sending you the full set now.", "6:41 PM", true),
            ChatMessage("maya-4", "These are perfect. Coffee this week?", "6:44 PM", false),
            ChatMessage("maya-5", "I found a new place near the studio.", "6:44 PM", false),
        ),
    ),
    Conversation(
        id = "leo",
        name = "Leo Martins",
        handle = "@leomoves",
        initials = "LM",
        status = "Active 12m ago",
        isOnline = false,
        accentColors = listOf(Color(0xFF0077B6), Color(0xFF48CAE4)),
        initialUnreadCount = 0,
        messages = listOf(
            ChatMessage("leo-1", "Saturday ride is officially on.", "4:12 PM", false),
            ChatMessage("leo-2", "Same trail as last time?", "4:16 PM", true),
            ChatMessage("leo-3", "New route. Easier hills, better coffee.", "4:18 PM", false),
        ),
    ),
    Conversation(
        id = "nora",
        name = "Nora Ellis",
        handle = "@noranoise",
        initials = "NE",
        status = "Active now",
        isOnline = true,
        accentColors = listOf(Color(0xFF5B21B6), Color(0xFFEC4899)),
        initialUnreadCount = 1,
        messages = listOf(
            ChatMessage("nora-1", "I finally finished that studio loop.", "Yesterday", false),
            ChatMessage("nora-2", "Headphones ready. Send it over!", "Yesterday", true),
            ChatMessage("nora-3", "You heard it here first 🎧", "Yesterday", false),
        ),
    ),
    Conversation(
        id = "kai",
        name = "Kai Ito",
        handle = "@kaicooks",
        initials = "KI",
        status = "Active 1h ago",
        isOnline = false,
        accentColors = listOf(Color(0xFFB45309), Color(0xFFFBBF24)),
        initialUnreadCount = 0,
        messages = listOf(
            ChatMessage("kai-1", "Can you share that ramen recipe?", "Mon", true),
            ChatMessage("kai-2", "Absolutely — it is only five steps.", "Mon", false),
        ),
    ),
)

@Stable
internal class ChatsUiState internal constructor(
    internal val conversations: List<Conversation> = conversationFixtures,
) {
    private val messagesByConversation = mutableStateMapOf<String, List<ChatMessage>>()
    private val unreadByConversation = mutableStateMapOf<String, Int>()
    private val draftsByConversation = mutableStateMapOf<String, String>()
    private var sentMessageCount by mutableIntStateOf(0)

    var activeConversationId by mutableStateOf<String?>(null)
        private set

    var searchQuery by mutableStateOf("")
        private set

    init {
        conversations.forEach { conversation ->
            messagesByConversation[conversation.id] = conversation.messages
            unreadByConversation[conversation.id] = conversation.initialUnreadCount
            draftsByConversation[conversation.id] = ""
        }
    }

    val activeConversation: Conversation?
        get() = conversations.firstOrNull { it.id == activeConversationId }

    fun visibleConversations(): List<Conversation> {
        val normalizedQuery = searchQuery.trim()
        if (normalizedQuery.isEmpty()) return conversations
        return conversations.filter { conversation ->
            conversation.name.contains(normalizedQuery, ignoreCase = true) ||
                conversation.handle.contains(normalizedQuery, ignoreCase = true) ||
                messagesFor(conversation.id).any { message ->
                    message.text.contains(normalizedQuery, ignoreCase = true)
                }
        }
    }

    fun updateSearchQuery(value: String) {
        searchQuery = value
    }

    fun openConversation(id: String) {
        if (conversations.none { it.id == id }) return
        activeConversationId = id
        unreadByConversation[id] = 0
    }

    fun closeConversation() {
        activeConversationId = null
    }

    fun messagesFor(id: String): List<ChatMessage> = messagesByConversation[id].orEmpty()

    fun unreadCountFor(id: String): Int = unreadByConversation[id] ?: 0

    fun draftFor(id: String): String = draftsByConversation[id].orEmpty()

    fun updateDraft(id: String, value: String) {
        if (messagesByConversation.containsKey(id)) {
            draftsByConversation[id] = value
        }
    }

    fun sendDraft(id: String) {
        val messageText = draftFor(id).trim()
        if (messageText.isEmpty() || !messagesByConversation.containsKey(id)) return
        sentMessageCount += 1
        val newMessage = ChatMessage(
            id = "local-$sentMessageCount",
            text = messageText,
            timestamp = "Now",
            isOutgoing = true,
        )
        messagesByConversation[id] = messagesFor(id) + newMessage
        draftsByConversation[id] = ""
    }
}

@Composable
internal fun rememberChatsUiState(): ChatsUiState = remember { ChatsUiState() }

@Composable
internal fun ChatsScreen(
    uiState: ChatsUiState,
    modifier: Modifier = Modifier,
) {
    val conversation = uiState.activeConversation
    if (conversation == null) {
        ConversationInbox(
            uiState = uiState,
            modifier = modifier,
        )
    } else {
        ConversationThread(
            conversation = conversation,
            uiState = uiState,
            modifier = modifier,
        )
    }
}

@Composable
private fun ConversationInbox(
    uiState: ChatsUiState,
    modifier: Modifier = Modifier,
) {
    val visibleConversations = uiState.visibleConversations()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        InboxHeader()
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = uiState::updateSearchQuery,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            placeholder = { Text("Search messages") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = null,
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(18.dp),
        )
        if (uiState.searchQuery.isBlank()) {
            OnlineContacts(uiState.conversations.filter(Conversation::isOnline))
        }
        if (visibleConversations.isEmpty()) {
            EmptySearch(query = uiState.searchQuery)
        } else {
            Text(
                text = "Messages",
                modifier = Modifier.padding(start = 20.dp, top = 12.dp, bottom = 4.dp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 18.dp),
            ) {
                items(visibleConversations, key = Conversation::id) { conversation ->
                    ConversationRow(
                        conversation = conversation,
                        lastMessage = uiState.messagesFor(conversation.id).lastOrNull(),
                        unreadCount = uiState.unreadCountFor(conversation.id),
                        onClick = { uiState.openConversation(conversation.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun InboxHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, top = 14.dp, end = 8.dp, bottom = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Chats",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
            )
            Text(
                text = "Stay close to your people",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
            )
        }
        IconButton(onClick = {}) {
            Icon(
                imageVector = Icons.Rounded.AddComment,
                contentDescription = "Start a new conversation",
            )
        }
    }
}

@Composable
private fun OnlineContacts(conversations: List<Conversation>) {
    Column(modifier = Modifier.padding(top = 10.dp)) {
        Text(
            text = "Online now",
            modifier = Modifier.padding(horizontal = 20.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(conversations, key = Conversation::id) { conversation ->
                Column(
                    modifier = Modifier.width(66.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    ContactAvatar(
                        conversation = conversation,
                        size = 54,
                    )
                    Text(
                        text = conversation.name.substringBefore(" "),
                        modifier = Modifier.padding(top = 5.dp),
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
private fun ConversationRow(
    conversation: Conversation,
    lastMessage: ChatMessage?,
    unreadCount: Int,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                role = Role.Button,
                onClickLabel = "Open conversation with ${conversation.name}",
                onClick = onClick,
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ContactAvatar(conversation = conversation, size = 58)
        Column(
            modifier = Modifier
                .padding(start = 12.dp)
                .weight(1f),
        ) {
            Text(
                text = conversation.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = if (unreadCount > 0) FontWeight.Bold else FontWeight.SemiBold,
            )
            Text(
                text = lastMessage?.text.orEmpty(),
                modifier = Modifier.padding(top = 3.dp),
                color = if (unreadCount > 0) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (unreadCount > 0) FontWeight.Medium else FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Column(
            modifier = Modifier.padding(start = 8.dp),
            horizontalAlignment = Alignment.End,
        ) {
            Text(
                text = lastMessage?.timestamp.orEmpty(),
                color = if (unreadCount > 0) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                style = MaterialTheme.typography.labelSmall,
            )
            Spacer(modifier = Modifier.height(7.dp))
            if (unreadCount > 0) {
                BadgedBox(
                    badge = {
                        Badge {
                            Text(
                                text = unreadCount.toString(),
                                modifier = Modifier.padding(horizontal = 2.dp),
                            )
                        }
                    },
                ) {
                    Spacer(modifier = Modifier.size(14.dp))
                }
            } else {
                Spacer(modifier = Modifier.height(14.dp))
            }
        }
    }
}

@Composable
private fun ConversationThread(
    conversation: Conversation,
    uiState: ChatsUiState,
    modifier: Modifier = Modifier,
) {
    val messages = uiState.messagesFor(conversation.id)
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        ThreadHeader(
            conversation = conversation,
            onBack = uiState::closeConversation,
        )
        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 16.dp),
        ) {
            item {
                Text(
                    text = "Today",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                )
            }
            itemsIndexed(messages, key = { _, message -> message.id }) { index, message ->
                val groupedWithPrevious = index > 0 &&
                    messages[index - 1].isOutgoing == message.isOutgoing
                val groupedWithNext = index < messages.lastIndex &&
                    messages[index + 1].isOutgoing == message.isOutgoing
                MessageBubble(
                    message = message,
                    groupedWithPrevious = groupedWithPrevious,
                    groupedWithNext = groupedWithNext,
                )
            }
        }
        MessageComposer(
            value = uiState.draftFor(conversation.id),
            onValueChange = { uiState.updateDraft(conversation.id, it) },
            onSend = { uiState.sendDraft(conversation.id) },
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
            .padding(horizontal = 4.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = "Back to conversations",
            )
        }
        ContactAvatar(conversation = conversation, size = 42)
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
                text = conversation.status,
                color = if (conversation.isOnline) {
                    MaterialTheme.colorScheme.secondary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                style = MaterialTheme.typography.labelSmall,
            )
        }
        IconButton(onClick = {}) {
            Icon(
                imageVector = Icons.Rounded.Call,
                contentDescription = "Call ${conversation.name}",
                tint = MaterialTheme.colorScheme.primary,
            )
        }
        IconButton(onClick = {}) {
            Icon(
                imageVector = Icons.Rounded.Videocam,
                contentDescription = "Video call ${conversation.name}",
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun MessageBubble(
    message: ChatMessage,
    groupedWithPrevious: Boolean,
    groupedWithNext: Boolean,
) {
    val bubbleShape = if (message.isOutgoing) {
        RoundedCornerShape(
            topStart = 20.dp,
            topEnd = if (groupedWithPrevious) 7.dp else 20.dp,
            bottomStart = 20.dp,
            bottomEnd = if (groupedWithNext) 7.dp else 20.dp,
        )
    } else {
        RoundedCornerShape(
            topStart = if (groupedWithPrevious) 7.dp else 20.dp,
            topEnd = 20.dp,
            bottomStart = if (groupedWithNext) 7.dp else 20.dp,
            bottomEnd = 20.dp,
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = if (groupedWithPrevious) 3.dp else 10.dp),
        horizontalAlignment = if (message.isOutgoing) Alignment.End else Alignment.Start,
    ) {
        Surface(
            shape = bubbleShape,
            color = if (message.isOutgoing) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            },
        ) {
            Text(
                text = message.text,
                modifier = Modifier
                    .widthIn(max = 300.dp)
                    .padding(horizontal = 15.dp, vertical = 10.dp),
                color = if (message.isOutgoing) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                style = MaterialTheme.typography.bodyLarge,
            )
        }
        if (!groupedWithNext) {
            Text(
                text = message.timestamp,
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 4.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}

@Composable
private fun MessageComposer(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
) {
    val focusManager = LocalFocusManager.current

    Surface(
        tonalElevation = 3.dp,
        shadowElevation = 6.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Message") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(
                    onSend = {
                        onSend()
                        focusManager.clearFocus()
                    },
                ),
                maxLines = 4,
                shape = RoundedCornerShape(22.dp),
            )
            IconButton(
                onClick = onSend,
                enabled = value.isNotBlank(),
                modifier = Modifier
                    .padding(start = 7.dp)
                    .size(48.dp)
                    .background(
                        color = if (value.isNotBlank()) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        },
                        shape = CircleShape,
                    ),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.Send,
                    contentDescription = "Send message",
                    tint = if (value.isNotBlank()) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                )
            }
        }
    }
}

@Composable
private fun ContactAvatar(
    conversation: Conversation,
    size: Int,
) {
    BadgedBox(
        badge = {
            if (conversation.isOnline) {
                Box(
                    modifier = Modifier
                        .size(15.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(2.dp)
                        .background(Color(0xFF20C997), CircleShape),
                )
            }
        },
    ) {
        Box(
            modifier = Modifier
                .size(size.dp)
                .background(Brush.linearGradient(conversation.accentColors), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = conversation.initials,
                color = Color.White,
                style = if (size > 50) {
                    MaterialTheme.typography.titleMedium
                } else {
                    MaterialTheme.typography.labelLarge
                },
                fontWeight = FontWeight.ExtraBold,
            )
        }
    }
}

@Composable
private fun EmptySearch(query: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = "💬", fontSize = 46.sp)
        Text(
            text = "No messages found",
            modifier = Modifier.padding(top = 14.dp),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "Try another name or keyword for “$query”.",
            modifier = Modifier.padding(top = 6.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}
