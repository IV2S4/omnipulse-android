package com.omnipulse.app.ui.chats

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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
internal fun ChatThreadScreen(
    conversation: Conversation,
    uiState: ChatsUiState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val messages = uiState.messages(conversation.id)
    val groups = groupMessages(messages)
    val listState = rememberLazyListState()

    BackHandler(onBack = onBack)

    LaunchedEffect(messages.size) {
        if (groups.isNotEmpty()) {
            listState.animateScrollToItem(groups.lastIndex)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        ThreadHeader(
            conversation = conversation,
            onBack = onBack,
        )
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item(key = "today") {
                Text(
                    text = "Today",
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                )
            }
            groups.forEach { group ->
                item(key = group.messages.first().id) {
                    MessageGroupRow(
                        group = group,
                        senderInitials = conversation.initials,
                    )
                }
            }
        }

        MessageComposer(
            value = uiState.draft(conversation.id),
            onValueChange = { uiState.updateDraft(conversation.id, it) },
            onSend = { uiState.sendMessage(conversation.id) },
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
            .padding(horizontal = 4.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = "Back to conversations",
            )
        }
        ChatAvatar(
            initials = conversation.initials,
            isOnline = conversation.status == "Active now",
            modifier = Modifier.size(42.dp),
        )
        Column(
            modifier = Modifier
                .padding(start = 10.dp)
                .weight(1f),
        ) {
            Text(
                text = conversation.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = conversation.status,
                color = if (conversation.status == "Active now") {
                    Color(0xFF15803D)
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
            )
        }
        IconButton(onClick = {}) {
            Icon(
                imageVector = Icons.Rounded.MoreVert,
                contentDescription = "Conversation options",
            )
        }
    }
}

@Composable
private fun MessageGroupRow(
    group: MessageGroup,
    senderInitials: String,
) {
    if (group.isFromMe) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            GroupedBubbles(group = group)
        }
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
        ) {
            MiniAvatar(initials = senderInitials)
            Spacer(modifier = Modifier.width(8.dp))
            GroupedBubbles(group = group)
        }
    }
}

@Composable
private fun GroupedBubbles(group: MessageGroup) {
    Column(
        horizontalAlignment = if (group.isFromMe) Alignment.End else Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        group.messages.forEachIndexed { index, message ->
            val isLast = index == group.messages.lastIndex
            Surface(
                modifier = Modifier.widthIn(max = 304.dp),
                shape = messageBubbleShape(
                    isFromMe = group.isFromMe,
                    isFirst = index == 0,
                    isLast = isLast,
                ),
                color = if (group.isFromMe) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                },
            ) {
                Text(
                    text = message.text,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    color = if (group.isFromMe) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
            if (isLast) {
                Text(
                    text = if (group.isFromMe && message.sentAt == "Now") {
                        "Sent now"
                    } else {
                        message.sentAt
                    },
                    modifier = Modifier.padding(start = 5.dp, top = 2.dp, end = 5.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
    }
}

private fun messageBubbleShape(
    isFromMe: Boolean,
    isFirst: Boolean,
    isLast: Boolean,
): RoundedCornerShape {
    val large = 20.dp
    val small = 7.dp
    return if (isFromMe) {
        RoundedCornerShape(
            topStart = large,
            topEnd = if (isFirst) large else small,
            bottomStart = large,
            bottomEnd = if (isLast) large else small,
        )
    } else {
        RoundedCornerShape(
            topStart = if (isFirst) large else small,
            topEnd = large,
            bottomStart = if (isLast) large else small,
            bottomEnd = large,
        )
    }
}

@Composable
private fun MiniAvatar(initials: String) {
    Box(
        modifier = Modifier
            .size(28.dp)
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
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun MessageComposer(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
) {
    Surface(
        tonalElevation = 3.dp,
        shadowElevation = 4.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .padding(horizontal = 12.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Message") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { onSend() }),
                maxLines = 4,
                shape = RoundedCornerShape(24.dp),
            )
            FilledIconButton(
                onClick = onSend,
                enabled = value.isNotBlank(),
                modifier = Modifier.padding(start = 8.dp),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.Send,
                    contentDescription = "Send message",
                )
            }
        }
    }
}
