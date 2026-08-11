package com.alf452.recipeapp.util

import com.alf452.recipeapp.data.Recipe
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class RecipeSharingTest {

    @Test
    fun `round-trips a recipe whose text contains literal braces`() {
        // Regression test: the bracket-matcher used to count every '{' and
        // '}' in the raw text, including ones inside JSON string values, so
        // a recipe with literal braces in its instructions would unbalance
        // the match and fail to decode.
        val recipe = Recipe(
            title = "Weeknight Stir-fry",
            ingredients = "chicken\nsoy sauce",
            instructions = "Marinate the chicken {30 minutes}, then stir-fry on high heat {do not overcrowd the pan}."
        )

        val decoded = decodeSharedRecipe(buildShareText(recipe))

        assertNotNull(decoded)
        assertEquals(recipe.title, decoded?.title)
        assertEquals(recipe.instructions, decoded?.instructions)
    }

    @Test
    fun `returns null when the text has no share payload`() {
        assertNull(decodeSharedRecipe("just a regular message, no recipe here"))
    }

    @Test
    fun `ignores trailing text appended by messaging apps`() {
        val recipe = Recipe(title = "Pancakes", ingredients = "flour", instructions = "mix and cook")

        val decoded = decodeSharedRecipe(buildShareText(recipe) + "\n\nSent from my iPhone")

        assertEquals(recipe.title, decoded?.title)
    }
}
