package com.omnipulse.app.data

data class FeedPost(
    val id: String,
    val author: String,
    val handle: String,
    val body: String,
    val minutesAgo: Int,
    val pulseCount: Int,
    val replyCount: Int
)

data class ClipItem(
    val id: String,
    val creator: String,
    val caption: String,
    val durationSec: Int,
    val vibeColor: Long
)

data class ChatThread(
    val id: String,
    val peerName: String,
    val lastMessage: String,
    val minutesAgo: Int,
    val unread: Int
)

data class ChatMessage(
    val id: String,
    val text: String,
    val fromMe: Boolean,
    val timestamp: String
)

data class AiMessage(
    val id: String,
    val text: String,
    val fromUser: Boolean
)

object MockData {
    val feedPosts = listOf(
        FeedPost(
            id = "p1",
            author = "Nova Chen",
            handle = "@novawave",
            body = "Morning pulse check: shipped the outdoor photo walk reel. Who else is capturing city light before 7am?",
            minutesAgo = 12,
            pulseCount = 128,
            replyCount = 14
        ),
        FeedPost(
            id = "p2",
            author = "River Okonkwo",
            handle = "@riverok",
            body = "Looking for a weekend workout buddy near the river trail. Bring playlists — leave excuses.",
            minutesAgo = 41,
            pulseCount = 56,
            replyCount = 9
        ),
        FeedPost(
            id = "p3",
            author = "Mira Soto",
            handle = "@mirasoto",
            body = "OmniPulse tip: pin your favorite clips so friends land on the good stuff first.",
            minutesAgo = 95,
            pulseCount = 203,
            replyCount = 31
        ),
        FeedPost(
            id = "p4",
            author = "Jules Hart",
            handle = "@juleshart",
            body = "Sketching a cabin kitchen layout. Drop your favorite small-space storage hacks.",
            minutesAgo = 180,
            pulseCount = 77,
            replyCount = 22
        )
    )

    val clips = listOf(
        ClipItem("c1", "Kai Rivers", "Sunset skate line through the pier", 18, 0xFF0F766E),
        ClipItem("c2", "Ada Bloom", "60-second latte art fail → win", 24, 0xFFBE123C),
        ClipItem("c3", "Theo Park", "Night market neon walk", 15, 0xFF1D4ED8),
        ClipItem("c4", "Sam Vale", "Balcony herb garden tour", 21, 0xFFCA8A04)
    )

    val chats = listOf(
        ChatThread("t1", "Lena Brooks", "See you at the gallery at 6?", 8, 2),
        ChatThread("t2", "Omar Diaz", "Sent the clip draft — thoughts?", 35, 0),
        ChatThread("t3", "Priya Nair", "That AI prompt tip was gold.", 120, 0),
        ChatThread("t4", "Chris Lang", "Coffee tomorrow?", 400, 1)
    )

    val conversationMessages = mapOf(
        "t1" to listOf(
            ChatMessage("m1", "Hey! Still free for the gallery?", false, "5:42 PM"),
            ChatMessage("m2", "Yes — see you at the gallery at 6?", true, "5:44 PM"),
            ChatMessage("m3", "Perfect. I'll grab tickets.", false, "5:45 PM")
        ),
        "t2" to listOf(
            ChatMessage("m1", "Clip draft is in your inbox.", false, "4:10 PM"),
            ChatMessage("m2", "Sent the clip draft — thoughts?", false, "4:11 PM"),
            ChatMessage("m3", "Watching now!", true, "4:20 PM")
        ),
        "t3" to listOf(
            ChatMessage("m1", "That AI prompt tip was gold.", false, "Yesterday"),
            ChatMessage("m2", "Glad it helped — try the travel planner next.", true, "Yesterday")
        ),
        "t4" to listOf(
            ChatMessage("m1", "Coffee tomorrow?", false, "Mon"),
            ChatMessage("m2", "I'm free after 10.", true, "Mon")
        )
    )

    val starterAiMessages = listOf(
        AiMessage(
            id = "a0",
            text = "Hi — I'm PulseBot, your OmniPulse assistant. Ask me to draft a post, summarize a chat, or suggest clip ideas.",
            fromUser = false
        )
    )

    fun replyToAi(prompt: String): String {
        val trimmed = prompt.trim()
        return when {
            trimmed.isEmpty() -> "Tell me what you need — posts, clips, or message drafts."
            trimmed.contains("post", ignoreCase = true) ->
                "Draft: \"Quick pulse from the trail — crisp air, clear head, and a playlist that actually keeps pace. Who's outside today?\""
            trimmed.contains("clip", ignoreCase = true) ->
                "Clip idea: 12-second before/after of your desk reset, captioned \"reset in a breath.\""
            trimmed.contains("message", ignoreCase = true) || trimmed.contains("chat", ignoreCase = true) ->
                "Message draft: \"Free later for a quick catch-up? I can do 15 minutes after 5.\""
            else ->
                "Got it. Here's a short take: keep it warm, specific, and under two sentences — then add one clear ask."
        }
    }
}
