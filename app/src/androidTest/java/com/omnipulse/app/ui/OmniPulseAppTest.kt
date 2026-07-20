package com.omnipulse.app.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.omnipulse.app.MainActivity
import org.junit.Rule
import org.junit.Test

class OmniPulseAppTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun primaryNavigationShowsEveryDestination() {
        composeRule.onNodeWithText("Clips").performClick()
        composeRule.onNodeWithContentDescription("Create clip").assertIsDisplayed()

        composeRule.onNodeWithText("Messages").performClick()
        composeRule.onNodeWithText("Alex Morgan").assertIsDisplayed()

        composeRule.onNodeWithText("Pulse AI").performClick()
        composeRule.onNodeWithText("Try asking").assertIsDisplayed()

        composeRule.onNodeWithText("Feed").performClick().assertIsSelected()
        composeRule.onNodeWithText("Your pulse").assertIsDisplayed()
    }

    @Test
    fun userCanCreateLikeAndCommentOnPost() {
        composeRule.onNodeWithContentDescription("Create post").performClick()
        composeRule.onNodeWithText("What's happening?").performTextInput("My first OmniPulse post")
        composeRule.onNodeWithText("Topic (optional)").performTextInput("Community")
        composeRule.onNodeWithText("Publish").assertIsEnabled().performClick()

        composeRule.onNodeWithText("My first OmniPulse post").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Like post by You").performClick()
        composeRule.onNodeWithContentDescription("Unlike post by You").assertIsDisplayed()

        composeRule.onNodeWithContentDescription("View comments on You's post").performClick()
        composeRule.onNodeWithText("Add a comment").performTextInput("Glad to be here")
        composeRule.onNodeWithContentDescription("Post comment").performClick()
        composeRule.onNodeWithText("Glad to be here").assertIsDisplayed()
    }
}
