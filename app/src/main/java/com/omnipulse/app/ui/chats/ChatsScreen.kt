package com.omnipulse.app.ui.chats

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun ChatsScreen(
    uiState: ChatsUiState,
    modifier: Modifier = Modifier,
) {
    val selectedConversation = uiState.selectedConversationId?.let(uiState::conversation)

    if (selectedConversation != null) {
        ChatThreadScreen(
            conversation = selectedConversation,
            uiState = uiState,
            onBack = uiState::closeConversation,
            modifier = modifier,
        )
    } else {
        ChatsInbox(
            uiState = uiState,
            modifier = modifier,
        )
    }
}

@Composable
private fun ChatsInbox(
    uiState: ChatsUiState,
    modifier: Modifier = Modifier,
) {
    val filteredConversations = uiState.filteredConversations()

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
            placeholder = { Text("Search conversations") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = null,
                )
            },
            singleLine = true,
            shape = CircleShape,
        )

        Text(
            text = if (uiState.searchQuery.isBlank()) {
                "Recent conversations"
            } else {
                "${filteredConversations.size} matching conversations"
            },
            modifier = Modifier.padding(start = 20.dp, top = 8.dp, end = 20.dp, bottom = 4.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelLarge,
        )

        if (filteredConversations.isEmpty()) {
            EmptySearchState(query = uiState.searchQuery)
        } else {
            LazyColumn(
                contentPadding = PaddingValues(bottom = 16.dp),
            ) {
                items(filteredConversations, key = Conversation::id) { conversation ->
                    ConversationRow(
                        conversation = conversation,
                        lastMessage = uiState.lastMessage(conversation.id),
                        unreadCount = uiState.unreadCount(conversation.id),
                        onClick = { uiState.openConversation(conversation.id) },
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(start = 86.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f),
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
            .padding(start = 20.dp, top = 14.dp, end = 8.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Chats",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
            )
            Text(
                text = "Your private conversations",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
            )
        }
        IconButton(onClick = {}) {
            Icon(
                imageVector = Icons.Rounded.Edit,
                contentDescription = "Start a new conversation",
            )
        }
    }
}

@Composable
private fun ConversationRow(
    conversation: Conversation,
    lastMessage: ChatMessage,
    unreadCount: Int,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClickLabel = "Open conversation with ${conversation.name}",
                onClick = onClick,
            )
            .padding(horizontal = 16.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ChatAvatar(
            initials = conversation.initials,
            isOnline = conversation.status == "Active now",
        )
        Column(
            modifier = Modifier
                .padding(start = 14.dp)
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
                    text = conversation.lastActivity,
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
                    text = if (lastMessage.isFromMe) {
                        "You: ${lastMessage.text}"
                    } else {
                        lastMessage.text
                    },
                    modifier = Modifier.weight(1f),
                    color = if (unreadCount > 0) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (unreadCount > 0) FontWeight.SemiBold else FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (unreadCount > 0) {
                    Badge(
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .semantics {
                                contentDescription = "$unreadCount unread messages"
                            },
                    ) {
                        Text(unreadCount.toString())
                    }
                }
            }
        }
    }
}

@Composable
internal fun ChatAvatar(
    initials: String,
    isOnline: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.size(56.dp)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
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
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
            )
        }
        if (isOnline) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(16.dp),
                shape = CircleShape,
                color = Color(0xFF22C55E),
                border = androidx.compose.foundation.BorderStroke(
                    2.dp,
                    MaterialTheme.colorScheme.background,
                ),
            ) {}
        }
    }
}

@Composable
private fun EmptySearchState(query: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 72.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Surface(
            modifier = Modifier.size(58.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.secondaryContainer,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            }
        }
        Text(
            text = "No conversations found",
            modifier = Modifier.padding(top = 16.dp),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "No names or messages match “$query”.",
            modifier = Modifier.padding(top = 6.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}
