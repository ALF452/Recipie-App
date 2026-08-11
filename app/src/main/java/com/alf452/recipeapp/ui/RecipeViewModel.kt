package com.alf452.recipeapp.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.alf452.recipeapp.data.Recipe
import com.alf452.recipeapp.data.RecipeDatabase
import com.alf452.recipeapp.data.RecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class RecipeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RecipeRepository = RecipeRepository(
        RecipeDatabase.getInstance(application).recipeDao()
    )

    val allRecipes: Flow<List<Recipe>> = repository.allRecipes

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
}
