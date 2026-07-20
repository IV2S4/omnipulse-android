package com.omnipulse.app.feature.feed

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FeedViewModelTest {
    @Test
    fun composerCreatesPostAndClosesAfterSuccess() {
        val viewModel = FeedViewModel(InMemoryFeedRepository(emptyList()))

        viewModel.showComposer()
        assertTrue(viewModel.uiState.isComposerVisible)

        assertTrue(viewModel.createPost("Hello OmniPulse", "Introductions"))

        assertFalse(viewModel.uiState.isComposerVisible)
        assertEquals("Hello OmniPulse", viewModel.uiState.posts.single().body)
    }

    @Test
    fun selectedCommentsStayOpenWhenCommentIsAdded() {
        val repository = InMemoryFeedRepository(listOf(sampleFeedPosts.first()))
        val viewModel = FeedViewModel(repository)
        val postId = viewModel.uiState.posts.single().id

        viewModel.showComments(postId)
        assertTrue(viewModel.addComment(postId, "Welcome!"))

        assertEquals(postId, viewModel.uiState.commentsPostId)
        assertEquals("Welcome!", viewModel.uiState.commentsPost?.comments?.single()?.body)

        viewModel.hideComments()
        assertNull(viewModel.uiState.commentsPostId)
    }
}
