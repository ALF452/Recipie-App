package com.alf452.recipeapp.util

import com.alf452.recipeapp.data.Recipe
import com.alf452.recipeapp.data.SharedRecipe
import com.alf452.recipeapp.data.toSharedRecipe
import kotlinx.serialization.json.Json

private const val SHARE_PREFIX = "MYCOOKBOOK-RECIPE-V1:"

private val json = Json { ignoreUnknownKeys = true }

/** Builds the message sent through the system share sheet. */
fun buildShareText(recipe: Recipe): String {
    val payload = json.encodeToString(SharedRecipe.serializer(), recipe.toSharedRecipe())
    return "${recipe.title} — a recipe from My Cookbook. Open this in My Cookbook, or paste the whole " +
        "message into the app's Import Recipe screen, to save it.\n\n$SHARE_PREFIX$payload"
}

/**
 * Extracts and decodes a shared recipe from arbitrary pasted/forwarded text.
 * Locates the marker, then bracket-matches the JSON object that follows so
 * trailing text added by messaging/email apps (quoting, signatures) doesn't
 * break parsing. Returns null if no valid payload is found.
 */
fun decodeSharedRecipe(text: String): SharedRecipe? {
    val prefixIndex = text.indexOf(SHARE_PREFIX)
    if (prefixIndex == -1) return null

    val jsonStart = text.indexOf('{', prefixIndex)
    if (jsonStart == -1) return null

    var depth = 0
    var jsonEnd = -1
    for (i in jsonStart until text.length) {
        when (text[i]) {
            '{' -> depth++
            '}' -> {
                depth--
                if (depth == 0) {
                    jsonEnd = i
                    break
                }
            }
        }
    }
    if (jsonEnd == -1) return null

    val jsonText = text.substring(jsonStart, jsonEnd + 1)
    return try {
        json.decodeFromString(SharedRecipe.serializer(), jsonText)
    } catch (e: Exception) {
        null
    }
}
