package com.alf452.recipeapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alf452.recipeapp.data.Recipe
import com.alf452.recipeapp.data.toRecipe
import com.alf452.recipeapp.util.decodeSharedRecipe

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportRecipeScreen(
    initialSharedText: String?,
    onBack: () -> Unit,
    onImport: (Recipe) -> Unit
) {
    var rawText by remember { mutableStateOf(initialSharedText.orEmpty()) }
    val parsed = remember(rawText) { decodeSharedRecipe(rawText) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Import Recipe") },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = "Got a recipe from another My Cookbook user? Paste the message they sent you below, " +
                    "or share it into the app directly from your messaging or email app.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = rawText,
                onValueChange = { rawText = it },
                label = { Text("Shared recipe message") },
                minLines = 6,
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
            )

            if (rawText.isNotBlank()) {
                if (parsed != null) {
                    Card(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(parsed.title, style = MaterialTheme.typography.titleMedium)
                            if (parsed.category.isNotBlank()) {
                                Text(
                                    parsed.category,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                            Text(
                                "Ingredients",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(top = 12.dp)
                            )
                            Text(parsed.ingredients, style = MaterialTheme.typography.bodyMedium)
                            Text(
                                "Instructions",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(top = 12.dp)
                            )
                            Text(parsed.instructions, style = MaterialTheme.typography.bodyMedium)
                        }
                    }

                    Button(
                        onClick = { onImport(parsed.toRecipe()) },
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                    ) {
                        Text("Save to My Cookbook")
                    }
                } else {
                    Text(
                        text = "This doesn't look like a My Cookbook recipe. Make sure you pasted the whole shared message.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
            }
        }
    }
}
