package com.alf452.recipeapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val category: String = "",
    val ingredients: String,
    val instructions: String,
    val notes: String = "",
    val isFavorite: Boolean = false,
    val photoUri: String? = null
)
