package com.alf452.recipeapp.ui

import android.app.Application
import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.alf452.recipeapp.data.PantryItem
import com.alf452.recipeapp.data.PantryRepository
import com.alf452.recipeapp.data.Recipe
import com.alf452.recipeapp.data.RecipeDatabase
import com.alf452.recipeapp.data.RecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

private const val PREFS_NAME = "app_settings"
private const val KEY_TEXT_COLOR = "text_color_argb"

class RecipeViewModel(application: Application) : AndroidViewModel(application) {

    private val database = RecipeDatabase.getInstance(application)
    private val repository: RecipeRepository = RecipeRepository(database.recipeDao())
    private val pantryRepository: PantryRepository = PantryRepository(database.pantryDao())
    private val prefs = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    val allRecipes: Flow<List<Recipe>> = repository.allRecipes
    val allPantryItems: Flow<List<PantryItem>> = pantryRepository.allItems

    /** Text handed off from an incoming share intent, waiting to be reviewed on the Import screen. */
    val pendingSharedText = mutableStateOf<String?>(null)

    /** User-selected text/icon color for the menu screens, persisted across launches. */
    val textColor = mutableStateOf(
        if (prefs.contains(KEY_TEXT_COLOR)) {
            Color(prefs.getInt(KEY_TEXT_COLOR, Color.White.toArgb()))
        } else {
            Color.White
        }
    )

    fun setTextColor(color: Color) {
        textColor.value = color
        prefs.edit().putInt(KEY_TEXT_COLOR, color.toArgb()).apply()
    }

    fun setPendingSharedText(text: String?) {
        pendingSharedText.value = text
    }

    fun consumePendingSharedText() {
        pendingSharedText.value = null
    }

    fun getRecipeById(id: Long): Flow<Recipe?> = repository.getRecipeById(id)

    fun saveRecipe(recipe: Recipe, onSaved: (Long) -> Unit = {}) {
        viewModelScope.launch {
            val id = repository.saveRecipe(recipe)
            onSaved(if (recipe.id != 0L) recipe.id else id)
        }
    }

    fun updateRecipe(recipe: Recipe) {
        viewModelScope.launch {
            repository.updateRecipe(recipe)
        }
    }

    fun deleteRecipe(recipe: Recipe) {
        viewModelScope.launch {
            repository.deleteRecipe(recipe)
        }
    }

    fun addPantryItem(name: String) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return
        viewModelScope.launch {
            pantryRepository.addItem(trimmed)
        }
    }

    fun deletePantryItem(item: PantryItem) {
        viewModelScope.launch {
            pantryRepository.deleteItem(item)
        }
    }
}
