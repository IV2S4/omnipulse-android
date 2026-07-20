package com.omnipulse.app.ui.chats

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ChatsUiStateTest {
    @Test
    fun groupMessages_combinesOnlyConsecutiveMessagesFromSameSender() {
        val messages = listOf(
            ChatMessage("1", "First", "1:00", isFromMe = false),
            ChatMessage("2", "Second", "1:01", isFromMe = false),
            ChatMessage("3", "Reply", "1:02", isFromMe = true),
            ChatMessage("4", "Follow-up", "1:03", isFromMe = false),
        )

        val groups = groupMessages(messages)

        assertEquals(3, groups.size)
        assertEquals(listOf("1", "2"), groups[0].messages.map(ChatMessage::id))
        assertTrue(groups[1].isFromMe)
        assertEquals(listOf("4"), groups[2].messages.map(ChatMessage::id))
    }

    @Test
    fun sendMessage_appendsTrimmedOutgoingMessageAndClearsDraft() {
        val state = ChatsUiState()
        val initialCount = state.messages("maya").size
        state.updateDraft("maya", "  See you tomorrow!  ")

        assertTrue(state.sendMessage("maya"))

        assertEquals(initialCount + 1, state.messages("maya").size)
        assertEquals("See you tomorrow!", state.lastMessage("maya").text)
        assertTrue(state.lastMessage("maya").isFromMe)
        assertEquals("", state.draft("maya"))
    }

    @Test
    fun sendMessage_rejectsBlankOrUnknownConversation() {
        val state = ChatsUiState()
        state.updateDraft("maya", "   ")
        state.updateDraft("unknown", "Hello")

        assertFalse(state.sendMessage("maya"))
        assertFalse(state.sendMessage("unknown"))
    }

    @Test
    fun openConversation_marksItReadAndSelectionSurvivesScreenDisposal() {
        val state = ChatsUiState()
        assertEquals(2, state.unreadCount("maya"))

        state.openConversation("maya")

        assertEquals("maya", state.selectedConversationId)
        assertEquals(0, state.unreadCount("maya"))

        state.closeConversation()
        assertEquals(null, state.selectedConversationId)
        assertEquals(0, state.unreadCount("maya"))
    }

    @Test
    fun filteredConversations_matchesNamesHandlesAndUpdatedPreviews() {
        val state = ChatsUiState()

        state.updateSearchQuery("noranoise")
        assertEquals(listOf("nora"), state.filteredConversations().map(Conversation::id))

        state.updateDraft("leo", "Bring the bright orange helmet")
        state.sendMessage("leo")
        state.updateSearchQuery("orange helmet")

        assertEquals(listOf("leo"), state.filteredConversations().map(Conversation::id))
    }
}
