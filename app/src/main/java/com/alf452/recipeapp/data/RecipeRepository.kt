package com.alf452.recipeapp.data

import kotlinx.coroutines.flow.Flow

class RecipeRepository(private val dao: RecipeDao) {

    val allRecipes: Flow<List<Recipe>> = dao.getAllRecipes()

    fun getRecipeById(id: Long): Flow<Recipe?> = dao.getRecipeById(id)

    suspend fun saveRecipe(recipe: Recipe): Long = dao.insertRecipe(recipe)

    suspend fun updateRecipe(recipe: Recipe) = dao.updateRecipe(recipe)

    suspend fun deleteRecipe(recipe: Recipe) = dao.deleteRecipe(recipe)
}
