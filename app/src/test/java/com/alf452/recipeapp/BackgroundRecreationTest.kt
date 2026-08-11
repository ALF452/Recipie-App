package com.alf452.recipeapp

import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Simulates the app being idle in the background long enough for Android to
 * reclaim the process and recreate the Activity when the user returns.
 * ActivityScenario#recreate() drives MainActivity through the real
 * onSaveInstanceState -> onCreate(Bundle) lifecycle Android uses for exactly
 * that case, so this exercises the actual production code path
 * rememberSaveable relies on rather than a stand-in for it.
 */
@RunWith(RobolectricTestRunner::class)
class BackgroundRecreationTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun `unsaved new-recipe title survives the process being recreated in the background`() {
        composeRule.onNodeWithContentDescription("Add recipe").performClick()
        composeRule.onNodeWithTag("recipe_title_field").performTextInput("Grandma's Chili")

        composeRule.activityRule.scenario.recreate()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("recipe_title_field").assertTextContains("Grandma's Chili")
    }
}
