package com.omnipulse.app.ui.navigation

object Routes {
    const val AUTH = "auth"
    const val FEED = "feed"
    const val CLIPS = "clips"
    const val MESSAGES = "messages"
    const val AI = "ai"
    const val CONVERSATION = "conversation/{chatId}"

    fun conversation(chatId: String) = "conversation/$chatId"
}
