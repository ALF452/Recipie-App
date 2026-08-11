package com.alf452.recipeapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.alf452.recipeapp.data.PantryItem
import com.alf452.recipeapp.data.Recipe
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    recipeFlow: Flow<Recipe?>,
    pantryItemsFlow: Flow<List<PantryItem>>,
    onBack: () -> Unit,
    onEdit: (Long) -> Unit,
    onDelete: (Recipe) -> Unit
) {
    val recipe by recipeFlow.collectAsState(initial = null)
    val pantryItems by pantryItemsFlow.collectAsState(initial = emptyList())
    var showDeleteDialog by remember { mutableStateOf(false) }

    val current = recipe ?: return

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(current.title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onEdit(current.id) }) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            if (current.category.isNotBlank()) {
                Text(current.category, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
            }

            Text("Ingredients", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp))

            val ingredientLines = remember(current.ingredients) {
                current.ingredients.lines().map { it.trim() }.filter { it.isNotEmpty() }
            }

            if (ingredientLines.isEmpty()) {
                Text(current.ingredients, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 4.dp))
            } else {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    ingredientLines.forEach { line ->
                        val inStock = pantryItems.any { item -> line.contains(item.name, ignoreCase = true) }
                        IngredientRow(text = line, inStock = inStock)
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            Text("Instructions", style = MaterialTheme.typography.titleMedium)
            Text(current.instructions, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 4.dp))

            if (current.notes.isNotBlank()) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                Text("Notes", style = MaterialTheme.typography.titleMedium)
                Text(current.notes, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 4.dp))
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete recipe?") },
            text = { Text("This will permanently delete \"${current.title}\".") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    onDelete(current)
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }
}

private val InStockGreen = Color(0xFF2E7D32)
private val OutOfStockRed = Color(0xFFB3261E)

@Composable
private fun IngredientRow(text: String, inStock: Boolean) {
    val backgroundColor = if (inStock) InStockGreen.copy(alpha = 0.16f) else OutOfStockRed.copy(alpha = 0.12f)
    val icon = if (inStock) Icons.Filled.CheckCircle else Icons.Filled.Cancel
    val iconTint = if (inStock) InStockGreen else OutOfStockRed

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = if (inStock) "In stock" else "Out of stock",
            tint = iconTint,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, style = MaterialTheme.typography.bodyLarge)
    }
}
