package com.omnipulse.app.ui.chats

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList

data class ChatMessage(
    val id: String,
    val text: String,
    val sentAt: String,
    val isFromMe: Boolean,
)

data class Conversation(
    val id: String,
    val name: String,
    val handle: String,
    val initials: String,
    val status: String,
    val lastActivity: String,
    val initialUnreadCount: Int,
    val initialMessages: List<ChatMessage>,
)

data class MessageGroup(
    val isFromMe: Boolean,
    val messages: List<ChatMessage>,
)

internal val conversationFixtures = listOf(
    Conversation(
        id = "maya",
        name = "Maya Chen",
        handle = "@mayamakes",
        initials = "MC",
        status = "Active now",
        lastActivity = "2m",
        initialUnreadCount = 2,
        initialMessages = listOf(
            ChatMessage("maya-1", "That rooftop photo is incredible.", "6:42 PM", isFromMe = true),
            ChatMessage("maya-2", "Thank you! The light changed every minute.", "6:44 PM", isFromMe = false),
            ChatMessage("maya-3", "I saved a spot for our next photo walk.", "6:44 PM", isFromMe = false),
            ChatMessage("maya-4", "Tomorrow after work?", "6:46 PM", isFromMe = true),
            ChatMessage("maya-5", "Perfect. I know exactly where we should start.", "6:48 PM", isFromMe = false),
        ),
    ),
    Conversation(
        id = "leo",
        name = "Leo Martins",
        handle = "@leomoves",
        initials = "LM",
        status = "Active 12m ago",
        lastActivity = "18m",
        initialUnreadCount = 0,
        initialMessages = listOf(
            ChatMessage("leo-1", "Are you joining the Saturday ride?", "9:14 AM", isFromMe = false),
            ChatMessage("leo-2", "Absolutely. Is the coastal route still the plan?", "9:18 AM", isFromMe = true),
            ChatMessage("leo-3", "Yep! Meet at the north gate at eight.", "9:21 AM", isFromMe = false),
            ChatMessage("leo-4", "I will bring an extra repair kit.", "9:24 AM", isFromMe = true),
        ),
    ),
    Conversation(
        id = "nora",
        name = "Nora Ellis",
        handle = "@noranoise",
        initials = "NE",
        status = "Active now",
        lastActivity = "1h",
        initialUnreadCount = 1,
        initialMessages = listOf(
            ChatMessage("nora-1", "I finally finished the bridge.", "4:03 PM", isFromMe = false),
            ChatMessage("nora-2", "It completely changes the track.", "4:03 PM", isFromMe = false),
            ChatMessage("nora-3", "Send it over when you are ready!", "4:08 PM", isFromMe = true),
            ChatMessage("nora-4", "Uploading a private preview now 🎧", "4:12 PM", isFromMe = false),
        ),
    ),
    Conversation(
        id = "kai",
        name = "Kai Ito",
        handle = "@kaicooks",
        initials = "KI",
        status = "Active yesterday",
        lastActivity = "Yesterday",
        initialUnreadCount = 0,
        initialMessages = listOf(
            ChatMessage("kai-1", "The five-minute ramen passed the test.", "Yesterday", isFromMe = true),
            ChatMessage("kai-2", "Glad it worked! Did you add the chili crisp?", "Yesterday", isFromMe = false),
            ChatMessage("kai-3", "A heroic amount.", "Yesterday", isFromMe = true),
            ChatMessage("kai-4", "That is the correct amount 😂", "Yesterday", isFromMe = false),
        ),
    ),
    Conversation(
        id = "zara",
        name = "Zara Okafor",
        handle = "@zarabuilds",
        initials = "ZO",
        status = "Active Monday",
        lastActivity = "Mon",
        initialUnreadCount = 0,
        initialMessages = listOf(
            ChatMessage("zara-1", "The prototype is ready for a second look.", "Monday", isFromMe = false),
            ChatMessage("zara-2", "Nice. I will send notes before lunch.", "Monday", isFromMe = true),
            ChatMessage("zara-3", "No rush—tomorrow works too.", "Monday", isFromMe = false),
        ),
    ),
)

fun groupMessages(messages: List<ChatMessage>): List<MessageGroup> =
    messages.fold(emptyList()) { groups, message ->
        val lastGroup = groups.lastOrNull()
        if (lastGroup?.isFromMe == message.isFromMe) {
            groups.dropLast(1) + lastGroup.copy(messages = lastGroup.messages + message)
        } else {
            groups + MessageGroup(message.isFromMe, listOf(message))
        }
    }

@Stable
class ChatsUiState internal constructor(
    private val conversations: List<Conversation> = conversationFixtures,
) {
    private val messagesByConversation = mutableStateMapOf<String, SnapshotStateList<ChatMessage>>()
    private val unreadByConversation = mutableStateMapOf<String, Int>()
    private val draftsByConversation = mutableStateMapOf<String, String>()
    private var nextLocalMessageId = 1

    var searchQuery by mutableStateOf("")
        private set

    var selectedConversationId by mutableStateOf<String?>(null)
        private set

    fun updateSearchQuery(query: String) {
        searchQuery = query
    }

    fun filteredConversations(): List<Conversation> {
        val normalizedQuery = searchQuery.trim().lowercase()
        if (normalizedQuery.isEmpty()) return conversations

        return conversations.filter { conversation ->
            conversation.name.lowercase().contains(normalizedQuery) ||
                conversation.handle.lowercase().contains(normalizedQuery) ||
                lastMessage(conversation.id).text.lowercase().contains(normalizedQuery)
        }
    }

    fun conversation(id: String): Conversation? = conversations.firstOrNull { it.id == id }

    fun messages(id: String): SnapshotStateList<ChatMessage> =
        messagesByConversation.getOrPut(id) {
            conversation(id)?.initialMessages.orEmpty().toMutableStateList()
        }

    fun lastMessage(id: String): ChatMessage = messages(id).lastOrNull()
        ?: ChatMessage("$id-empty", "Start a conversation", "", isFromMe = false)

    fun unreadCount(id: String): Int = unreadByConversation[id]
        ?: conversation(id)?.initialUnreadCount
        ?: 0

    fun openConversation(id: String) {
        if (conversation(id) == null) return
        selectedConversationId = id
        unreadByConversation[id] = 0
    }

    fun closeConversation() {
        selectedConversationId = null
    }

    fun draft(id: String): String = draftsByConversation[id].orEmpty()

    fun updateDraft(id: String, draft: String) {
        draftsByConversation[id] = draft
    }

    fun sendMessage(id: String): Boolean {
        val text = draft(id).trim()
        if (text.isEmpty() || conversation(id) == null) return false

        messages(id).add(
            ChatMessage(
                id = "$id-local-${nextLocalMessageId++}",
                text = text,
                sentAt = "Now",
                isFromMe = true,
            ),
        )
        draftsByConversation[id] = ""
        return true
    }
}

@Composable
fun rememberChatsUiState(): ChatsUiState = remember { ChatsUiState() }
