package com.omnipulse.app.feature.feed

interface FeedRepository {
    fun posts(): List<FeedPost>

    fun createPost(body: String, topic: String): Boolean

    fun toggleLike(postId: String)

    fun addComment(postId: String, body: String): Boolean
}

class InMemoryFeedRepository(
    initialPosts: List<FeedPost> = sampleFeedPosts,
) : FeedRepository {
    private var storedPosts = initialPosts
    private var nextPostId = initialPosts.size + 1
    private var nextCommentId = 1

    override fun posts(): List<FeedPost> = storedPosts

    override fun createPost(body: String, topic: String): Boolean {
        val cleanBody = body.trim()
        if (cleanBody.isEmpty()) return false

        val post = FeedPost(
            id = "local-post-${nextPostId++}",
            author = "You",
            handle = "@you",
            time = "Now",
            body = cleanBody,
            topic = topic.trim().ifEmpty { "Community" },
            baseLikes = 0,
            baseCommentCount = 0,
        )
        storedPosts = listOf(post) + storedPosts
        return true
    }

    override fun toggleLike(postId: String) {
        storedPosts = storedPosts.map { post ->
            if (post.id == postId) post.copy(isLiked = !post.isLiked) else post
        }
    }

    override fun addComment(postId: String, body: String): Boolean {
        val cleanBody = body.trim()
        if (cleanBody.isEmpty() || storedPosts.none { it.id == postId }) return false

        val comment = FeedComment(
            id = "local-comment-${nextCommentId++}",
            author = "You",
            handle = "@you",
            body = cleanBody,
            time = "Now",
        )
        storedPosts = storedPosts.map { post ->
            if (post.id == postId) post.copy(comments = post.comments + comment) else post
        }
        return true
    }
}

val sampleFeedPosts = listOf(
    FeedPost(
        id = "maya-trail",
        author = "Maya Chen",
        handle = "@mayamakes",
        time = "12m",
        body = "Morning light, a quiet trail, and one very determined camera roll.",
        topic = "Outdoors",
        baseLikes = 248,
        baseCommentCount = 31,
    ),
    FeedPost(
        id = "jordan-prototype",
        author = "Jordan Ellis",
        handle = "@jordane",
        time = "38m",
        body = "Shipped the first prototype today. Small progress still changes the direction.",
        topic = "Building",
        baseLikes = 186,
        baseCommentCount = 22,
    ),
    FeedPost(
        id = "noor-dinner",
        author = "Noor Patel",
        handle = "@noorplates",
        time = "1h",
        body = "A fifteen-minute dinner that somehow tastes like a weekend project.",
        topic = "Food",
        baseLikes = 412,
        baseCommentCount = 47,
    ),
)
