package com.omnipulse.app.feature.feed

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FeedRepositoryTest {
    private val seedPost = FeedPost(
        id = "seed",
        author = "Taylor",
        handle = "@taylor",
        time = "1m",
        body = "Seed post",
        topic = "Testing",
        baseLikes = 4,
        baseCommentCount = 2,
    )

    @Test
    fun createPost_trimsInputAndPrependsDeterministicPost() {
        val repository = InMemoryFeedRepository(listOf(seedPost))

        assertTrue(repository.createPost("  A local update  ", "  News  "))

        val created = repository.posts().first()
        assertEquals("local-post-2", created.id)
        assertEquals("A local update", created.body)
        assertEquals("News", created.topic)
        assertEquals("seed", repository.posts()[1].id)
    }

    @Test
    fun blankPostIsRejectedWithoutChangingFeed() {
        val repository = InMemoryFeedRepository(listOf(seedPost))

        assertFalse(repository.createPost("   ", "News"))

        assertEquals(listOf(seedPost), repository.posts())
    }

    @Test
    fun likeAndCommentUpdateOnlyTheSelectedPost() {
        val otherPost = seedPost.copy(id = "other", author = "Morgan")
        val repository = InMemoryFeedRepository(listOf(seedPost, otherPost))

        repository.toggleLike("seed")
        assertTrue(repository.addComment("seed", "  Looks great  "))

        val updated = repository.posts().first()
        assertTrue(updated.isLiked)
        assertEquals(5, updated.likeCount)
        assertEquals(3, updated.commentCount)
        assertEquals("Looks great", updated.comments.single().body)
        assertEquals(otherPost, repository.posts()[1])
    }
}
