package com.alf452.recipeapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.alf452.recipeapp.data.Recipe
import com.alf452.recipeapp.ui.components.WindowHerbGardenBackground
import com.alf452.recipeapp.ui.components.WoodenCuttingBoardBackground
import com.alf452.recipeapp.ui.theme.RecipeCream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeListScreen(
    recipesFlow: kotlinx.coroutines.flow.Flow<List<Recipe>>,
    onAddClick: () -> Unit,
    onRecipeClick: (Long) -> Unit,
    onPantryClick: () -> Unit,
    onImportClick: () -> Unit
) {
    val recipes by recipesFlow.collectAsState(initial = emptyList())

    Box(modifier = Modifier.fillMaxSize()) {
        WoodenCuttingBoardBackground(modifier = Modifier.fillMaxSize())

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                Box {
                    WindowHerbGardenBackground(modifier = Modifier.matchParentSize())
                    LargeTopAppBar(
                        title = { Text("My Cookbook") },
                        colors = TopAppBarDefaults.largeTopAppBarColors(
                            containerColor = Color.Transparent,
                            scrolledContainerColor = Color.Transparent,
                            titleContentColor = RecipeCream
                        ),
                        actions = {
                            TopBarScrimIconButton(onClick = onImportClick) {
                                Icon(Icons.Filled.Inbox, contentDescription = "Import Recipe", tint = Color.White)
                            }
                            TopBarScrimIconButton(onClick = onPantryClick) {
                                Icon(Icons.Filled.Kitchen, contentDescription = "My Pantry", tint = Color.White)
                            }
                        }
                    )
                }
            },
            floatingActionButton = {
                FloatingActionButton(onClick = onAddClick) {
                    Icon(Icons.Filled.Add, contentDescription = "Add recipe")
                }
            }
        ) { padding ->
            if (recipes.isEmpty()) {
                EmptyState(padding)
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(recipes, key = { it.id }) { recipe ->
                        RecipeCard(recipe = recipe, onClick = { onRecipeClick(recipe.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun TopBarScrimIconButton(onClick: () -> Unit, content: @Composable () -> Unit) {
    IconButton(onClick = onClick) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.28f)),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}

@Composable
private fun EmptyState(padding: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentAlignment = Alignment.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = RecipeCream.copy(alpha = 0.92f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                Icon(
                    Icons.Filled.MenuBook,
                    contentDescription = null,
                    modifier = Modifier.padding(bottom = 12.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "No recipes yet",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Tap + to add your first recipe",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun RecipeCard(recipe: Recipe, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = RecipeCream),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RecipeThumbnail(photoUri = recipe.photoUri)

            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(text = recipe.title, style = MaterialTheme.typography.titleMedium)
                if (recipe.category.isNotBlank()) {
                    Text(
                        text = recipe.category,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                Text(
                    text = recipe.ingredients,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun RecipeThumbnail(photoUri: String?) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center
    ) {
        if (photoUri != null) {
            AsyncImage(
                model = photoUri,
                contentDescription = "Last time you made this",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Icon(
                Icons.Filled.RestaurantMenu,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}
