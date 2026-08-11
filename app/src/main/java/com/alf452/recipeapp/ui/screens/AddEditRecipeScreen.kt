package com.alf452.recipeapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alf452.recipeapp.data.Recipe
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditRecipeScreen(
    existingRecipeFlow: Flow<Recipe?>?,
    onBack: () -> Unit,
    onSave: (Recipe) -> Unit
) {
    val existingRecipeState = if (existingRecipeFlow != null) {
        existingRecipeFlow.collectAsState(initial = null)
    } else {
        remember { mutableStateOf<Recipe?>(null) }
    }
    val existingRecipe = existingRecipeState.value

    var id by remember { mutableStateOf(0L) }
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var ingredients by remember { mutableStateOf("") }
    var instructions by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    LaunchedEffect(existingRecipe) {
        existingRecipe?.let {
            id = it.id
            title = it.title
            category = it.category
            ingredients = it.ingredients
            instructions = it.instructions
            notes = it.notes
        }
    }

    val isEditing = existingRecipeFlow != null

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Edit Recipe" else "New Recipe") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (title.isNotBlank()) {
                                onSave(
                                    Recipe(
                                        id = id,
                                        title = title.trim(),
                                        category = category.trim(),
                                        ingredients = ingredients.trim(),
                                        instructions = instructions.trim(),
                                        notes = notes.trim()
                                    )
                                )
                            }
                        }
                    ) {
                        Icon(Icons.Filled.Check, contentDescription = "Save")
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
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Category (optional)") },
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
            )
            OutlinedTextField(
                value = ingredients,
                onValueChange = { ingredients = it },
                label = { Text("Ingredients") },
                placeholder = { Text("One per line") },
                minLines = 4,
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
            )
            OutlinedTextField(
                value = instructions,
                onValueChange = { instructions = it },
                label = { Text("Instructions") },
                minLines = 6,
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
            )
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes (optional)") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
            )
            Text(
                text = "Title is required to save.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
