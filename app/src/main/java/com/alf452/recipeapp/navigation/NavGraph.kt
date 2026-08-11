package com.alf452.recipeapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.alf452.recipeapp.ui.RecipeViewModel
import com.alf452.recipeapp.ui.screens.AddEditRecipeScreen
import com.alf452.recipeapp.ui.screens.PantryScreen
import com.alf452.recipeapp.ui.screens.RecipeDetailScreen
import com.alf452.recipeapp.ui.screens.RecipeListScreen

private object Routes {
    const val LIST = "recipe_list"
    const val DETAIL = "recipe_detail/{recipeId}"
    const val ADD = "recipe_add"
    const val EDIT = "recipe_edit/{recipeId}"
    const val PANTRY = "pantry"

    fun detail(id: Long) = "recipe_detail/$id"
    fun edit(id: Long) = "recipe_edit/$id"
}

@Composable
fun RecipeNavGraph(viewModel: RecipeViewModel) {
    val navController: NavHostController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.LIST) {
        composable(Routes.LIST) {
            RecipeListScreen(
                recipesFlow = viewModel.allRecipes,
                onAddClick = { navController.navigate(Routes.ADD) },
                onRecipeClick = { id -> navController.navigate(Routes.detail(id)) },
                onPantryClick = { navController.navigate(Routes.PANTRY) }
            )
        }

        composable(
            route = Routes.DETAIL,
            arguments = listOf(navArgument("recipeId") { type = NavType.LongType })
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getLong("recipeId") ?: 0L
            RecipeDetailScreen(
                recipeFlow = viewModel.getRecipeById(recipeId),
                pantryItemsFlow = viewModel.allPantryItems,
                onBack = { navController.popBackStack() },
                onEdit = { id -> navController.navigate(Routes.edit(id)) },
                onDelete = { recipe ->
                    viewModel.deleteRecipe(recipe)
                    navController.popBackStack()
                },
                onPhotoUpdated = { updated -> viewModel.updateRecipe(updated) }
            )
        }

        composable(Routes.ADD) {
            AddEditRecipeScreen(
                existingRecipeFlow = null,
                onBack = { navController.popBackStack() },
                onSave = { recipe ->
                    viewModel.saveRecipe(recipe)
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Routes.EDIT,
            arguments = listOf(navArgument("recipeId") { type = NavType.LongType })
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getLong("recipeId") ?: 0L
            AddEditRecipeScreen(
                existingRecipeFlow = viewModel.getRecipeById(recipeId),
                onBack = { navController.popBackStack() },
                onSave = { recipe ->
                    viewModel.updateRecipe(recipe)
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.PANTRY) {
            PantryScreen(
                pantryItemsFlow = viewModel.allPantryItems,
                onBack = { navController.popBackStack() },
                onAddItem = { name -> viewModel.addPantryItem(name) },
                onDeleteItem = { item -> viewModel.deletePantryItem(item) }
            )
        }
    }
}
