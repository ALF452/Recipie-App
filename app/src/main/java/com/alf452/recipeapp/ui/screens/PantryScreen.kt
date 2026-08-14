package com.alf452.recipeapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.alf452.recipeapp.data.PantryItem
import com.alf452.recipeapp.ui.components.slateTextFieldColors
import com.alf452.recipeapp.ui.theme.LocalRecipeTextColor
import com.alf452.recipeapp.ui.theme.RecipeSlate
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantryScreen(
    pantryItemsFlow: Flow<List<PantryItem>>,
    onBack: () -> Unit,
    onAddItem: (String) -> Unit,
    onDeleteItem: (PantryItem) -> Unit
) {
    val pantryItems by pantryItemsFlow.collectAsState(initial = emptyList())
    var newItemName by rememberSaveable { mutableStateOf("") }
    val textColor = LocalRecipeTextColor.current

    fun submitNewItem() {
        if (newItemName.isNotBlank()) {
            onAddItem(newItemName)
            newItemName = ""
        }
    }

    Scaffold(
        containerColor = RecipeSlate,
        topBar = {
            TopAppBar(
                title = { Text("My Pantry") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = RecipeSlate,
                    titleContentColor = textColor,
                    navigationIconContentColor = textColor,
                    actionIconContentColor = textColor
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Add the food, spices, and herbs you have on hand. Recipe ingredients will be highlighted green when you already have them, red when you don't.",
                style = MaterialTheme.typography.bodyMedium,
                color = textColor
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = newItemName,
                    onValueChange = { newItemName = it },
                    label = { Text("Item name") },
                    colors = slateTextFieldColors(),
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { submitNewItem() })
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = { submitNewItem() }) {
                    Icon(Icons.Filled.Add, contentDescription = "Add item", tint = textColor)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()

            if (pantryItems.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Your pantry is empty.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = textColor.copy(alpha = 0.7f)
                    )
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    items(pantryItems, key = { it.id }) { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item.name,
                                style = MaterialTheme.typography.bodyLarge,
                                color = textColor,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = { onDeleteItem(item) }) {
                                Icon(Icons.Filled.Delete, contentDescription = "Remove ${item.name}", tint = textColor)
                            }
                        }
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}
