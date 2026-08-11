package com.alf452.recipeapp

import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Regression test for a real bug found during review: AddEditRecipeScreen's
 * Save button rebuilt the Recipe from only the fields shown on that screen,
 * silently dropping timesMade and isFavorite back to their defaults (0 /
 * false) on every edit. Drives the actual flow that exposed it — add a
 * recipe, log it as made a few times, edit it, save — through the real
 * MainActivity/NavGraph/ViewModel/Room stack, not a stand-in for it.
 */
@RunWith(RobolectricTestRunner::class)
class TimesMadeSurvivesEditTest {

    private val composeRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val ruleChain: RuleChain = RuleChain.outerRule(ResetRecipeDatabaseRule()).around(composeRule)

    @Test
    fun `editing a recipe does not reset its times-made count`() {
        // Add a recipe.
        composeRule.onNodeWithContentDescription("Add recipe").performClick()
        composeRule.onNodeWithTag("recipe_title_field").performTextInput("Chili")
        composeRule.onNodeWithContentDescription("Save").performClick()
        composeRule.waitForIdle()

        // Open it and log it as made 3 times, checking the count after each
        // individual tap so a CI failure here pinpoints exactly which
        // increment broke instead of only "somewhere in these three taps".
        composeRule.onNodeWithText("Chili").performClick()
        composeRule.waitForIdle()
        for (expected in 1..3) {
            composeRule.onNodeWithTag("times_made_fab").performClick()
            composeRule.waitForIdle()
            assertFabShows("Made it ${expected}×")
        }

        // Edit it (change the title) and save.
        composeRule.onNodeWithContentDescription("Edit").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("recipe_title_field").performTextReplacement("Chili Verde")
        composeRule.onNodeWithContentDescription("Save").performClick()
        composeRule.waitForIdle()

        // Leave the detail screen entirely and reopen the recipe from the
        // list, so this reads the value actually persisted to the database
        // rather than in-session UI state that survived the edit's screen
        // navigation regardless of what got saved.
        composeRule.onNodeWithContentDescription("Back").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Chili Verde").performClick()
        composeRule.waitForIdle()

        assertFabShows("Made it 3×")
    }

    // ExtendedFloatingActionButton wraps its text label two semantics levels
    // below the tagged node (FAB -> internal animation container -> Text),
    // and Compose's semantics merging doesn't bridge that gap here even
    // though the FAB reports MergeDescendants = true — confirmed by dumping
    // the unmerged tree, which showed the label's real text sitting on that
    // untagged grandchild. assertTextContains against the tagged parent node
    // can't see it, so match the visible label directly instead.
    private fun assertFabShows(label: String) {
        composeRule.onNodeWithText(label, substring = true, useUnmergedTree = true)
            .assertTextContains(label, substring = true)
    }
}
