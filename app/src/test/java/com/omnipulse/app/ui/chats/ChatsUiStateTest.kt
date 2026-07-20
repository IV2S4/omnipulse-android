package com.omnipulse.app.ui.chats

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ChatsUiStateTest {
    @Test
    fun openingConversationClearsUnreadAndClosingReturnsToInbox() {
        val state = ChatsUiState()

        assertEquals(2, state.unreadCountFor("maya"))

        state.openConversation("maya")

        assertEquals("maya", state.activeConversationId)
        assertEquals(0, state.unreadCountFor("maya"))

        state.closeConversation()

        assertNull(state.activeConversationId)
    }

    @Test
    fun draftSurvivesNavigationAndSendAppendsTrimmedOutgoingMessage() {
        val state = ChatsUiState()
        val initialMessageCount = state.messagesFor("leo").size

        state.openConversation("leo")
        state.updateDraft("leo", "  See you Saturday!  ")
        state.closeConversation()
        state.openConversation("leo")

        assertEquals("  See you Saturday!  ", state.draftFor("leo"))

        state.sendDraft("leo")

        val sentMessage = state.messagesFor("leo").last()
        assertEquals(initialMessageCount + 1, state.messagesFor("leo").size)
        assertEquals("See you Saturday!", sentMessage.text)
        assertEquals("Now", sentMessage.timestamp)
        assertTrue(sentMessage.isOutgoing)
        assertEquals("", state.draftFor("leo"))
    }

    @Test
    fun blankDraftDoesNotCreateMessage() {
        val state = ChatsUiState()
        val initialMessages = state.messagesFor("nora")

        state.updateDraft("nora", "   ")
        state.sendDraft("nora")

        assertEquals(initialMessages, state.messagesFor("nora"))
    }

    @Test
    fun searchMatchesNamesHandlesAndMessageHistory() {
        val state = ChatsUiState()

        state.updateSearchQuery("noranoise")
        assertEquals(listOf("nora"), state.visibleConversations().map(Conversation::id))

        state.updateSearchQuery("full set")
        assertEquals(listOf("maya"), state.visibleConversations().map(Conversation::id))

        state.updateSearchQuery("not in any conversation")
        assertTrue(state.visibleConversations().isEmpty())

        state.updateSearchQuery("")
        assertFalse(state.visibleConversations().isEmpty())
    }
}
