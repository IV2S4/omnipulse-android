package com.omnipulse.app.feature.feed

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class FeedViewModel(
    private val repository: FeedRepository = InMemoryFeedRepository(),
) : ViewModel() {
    var uiState by mutableStateOf(FeedUiState(posts = repository.posts()))
        private set

    fun showComposer() {
        uiState = uiState.copy(isComposerVisible = true)
    }

    fun hideComposer() {
        uiState = uiState.copy(isComposerVisible = false)
    }

    fun createPost(body: String, topic: String): Boolean {
        val created = repository.createPost(body, topic)
        if (created) {
            refresh(isComposerVisible = false)
        }
        return created
    }

    fun toggleLike(postId: String) {
        repository.toggleLike(postId)
        refresh()
    }

    fun showComments(postId: String) {
        if (uiState.posts.any { it.id == postId }) {
            uiState = uiState.copy(commentsPostId = postId)
        }
    }

    fun hideComments() {
        uiState = uiState.copy(commentsPostId = null)
    }

    fun addComment(postId: String, body: String): Boolean {
        val added = repository.addComment(postId, body)
        if (added) refresh()
        return added
    }

    private fun refresh(
        isComposerVisible: Boolean = uiState.isComposerVisible,
    ) {
        uiState = uiState.copy(
            posts = repository.posts(),
            isComposerVisible = isComposerVisible,
        )
    }
}
