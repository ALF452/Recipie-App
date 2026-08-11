package com.alf452.recipeapp.data

import kotlinx.serialization.Serializable

/**
 * Portable representation of a recipe sent between users. Deliberately excludes
 * id, isFavorite, and photoUri (a local content:// Uri isn't valid on another
 * device) — importing always creates a brand new local recipe and never
 * touches the recipient's pantry.
 */
@Serializable
data class SharedRecipe(
    val title: String,
    val category: String = "",
    val ingredients: String,
    val instructions: String,
    val notes: String = ""
)

fun Recipe.toSharedRecipe(): SharedRecipe = SharedRecipe(
    title = title,
    category = category,
    ingredients = ingredients,
    instructions = instructions,
    notes = notes
)

fun SharedRecipe.toRecipe(): Recipe = Recipe(
    title = title,
    category = category,
    ingredients = ingredients,
    instructions = instructions,
    notes = notes
)
