package com.alf452.recipeapp.ui

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.alf452.recipeapp.data.PantryItem
import com.alf452.recipeapp.data.PantryRepository
import com.alf452.recipeapp.data.Recipe
import com.alf452.recipeapp.data.RecipeDatabase
import com.alf452.recipeapp.data.RecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class RecipeViewModel(application: Application) : AndroidViewModel(application) {

    private val database = RecipeDatabase.getInstance(application)
    private val repository: RecipeRepository = RecipeRepository(database.recipeDao())
    private val pantryRepository: PantryRepository = PantryRepository(database.pantryDao())

    val allRecipes: Flow<List<Recipe>> = repository.allRecipes
    val allPantryItems: Flow<List<PantryItem>> = pantryRepository.allItems

    /** Text handed off from an incoming share intent, waiting to be reviewed on the Import screen. */
    val pendingSharedText = mutableStateOf<String?>(null)

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
