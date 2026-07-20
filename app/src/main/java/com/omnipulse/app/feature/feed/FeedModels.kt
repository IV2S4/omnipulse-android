package com.omnipulse.app.feature.feed

data class FeedComment(
    val id: String,
    val author: String,
    val handle: String,
    val body: String,
    val time: String,
)

data class FeedPost(
    val id: String,
    val author: String,
    val handle: String,
    val time: String,
    val body: String,
    val topic: String,
    val baseLikes: Int,
    val baseCommentCount: Int,
    val isLiked: Boolean = false,
    val comments: List<FeedComment> = emptyList(),
) {
    val likeCount: Int
        get() = baseLikes + if (isLiked) 1 else 0

    val commentCount: Int
        get() = baseCommentCount + comments.size
}

data class FeedUiState(
    val posts: List<FeedPost> = emptyList(),
    val isComposerVisible: Boolean = false,
    val commentsPostId: String? = null,
) {
    val commentsPost: FeedPost?
        get() = posts.firstOrNull { it.id == commentsPostId }
}
