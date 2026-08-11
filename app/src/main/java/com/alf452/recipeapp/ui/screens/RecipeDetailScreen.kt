package com.alf452.recipeapp.ui.screens

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.alf452.recipeapp.data.PantryItem
import com.alf452.recipeapp.data.Recipe
import com.alf452.recipeapp.ui.components.OutlinedText
import com.alf452.recipeapp.ui.components.WoodenCuttingBoardBackground
import com.alf452.recipeapp.util.buildShareText
import com.alf452.recipeapp.util.createRecipePhotoUri
import com.alf452.recipeapp.util.deletePhotoUri
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    recipeFlow: Flow<Recipe?>,
    pantryItemsFlow: Flow<List<PantryItem>>,
    onBack: () -> Unit,
    onEdit: (Long) -> Unit,
    onDelete: (Recipe) -> Unit,
    onPhotoUpdated: (Recipe) -> Unit,
    onTimesMadeChanged: (Recipe) -> Unit
) {
    val recipe by recipeFlow.collectAsState(initial = null)
    val pantryItems by pantryItemsFlow.collectAsState(initial = emptyList())
    var showDeleteDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val current = recipe ?: return

    var pendingCameraUri by remember { mutableStateOf<android.net.Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        val newUri = pendingCameraUri
        if (success && newUri != null) {
            current.photoUri?.let { old -> deletePhotoUri(context, old) }
            onPhotoUpdated(current.copy(photoUri = newUri.toString()))
        } else if (newUri != null) {
            deletePhotoUri(context, newUri.toString())
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        WoodenCuttingBoardBackground(modifier = Modifier.fillMaxSize())

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text(current.title) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "Recipe: ${current.title}")
                                putExtra(Intent.EXTRA_TEXT, buildShareText(current))
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share recipe"))
                        }) {
                            Icon(Icons.Filled.Share, contentDescription = "Share recipe")
                        }
                        IconButton(onClick = {
                            val uri = createRecipePhotoUri(context)
                            pendingCameraUri = uri
                            cameraLauncher.launch(uri)
                        }) {
                            Icon(
                                Icons.Filled.PhotoCamera,
                                contentDescription = if (current.photoUri != null) "Retake photo" else "Add photo"
                            )
                        }
                        IconButton(onClick = { onEdit(current.id) }) {
                            Icon(Icons.Filled.Edit, contentDescription = "Edit")
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete")
                        }
                    }
                )
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = { onTimesMadeChanged(current.copy(timesMade = current.timesMade + 1)) },
                    icon = { Icon(Icons.Filled.Repeat, contentDescription = null) },
                    text = { Text("Made it ${current.timesMade}×") }
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
                if (current.photoUri != null) {
                    AsyncImage(
                        model = current.photoUri,
                        contentDescription = "Photo from the last time this was made",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (current.category.isNotBlank()) {
                    Text(current.category, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
                }

                OutlinedText(
                    text = "Ingredients",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 16.dp)
                )

                val ingredientLines = remember(current.ingredients) {
                    current.ingredients.lines().map { it.trim() }.filter { it.isNotEmpty() }
                }

                if (ingredientLines.isEmpty()) {
                    OutlinedText(
                        text = current.ingredients,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                } else {
                    Column(modifier = Modifier.padding(top = 8.dp)) {
                        ingredientLines.forEach { line ->
                            val inStock = pantryItems.any { item -> line.contains(item.name, ignoreCase = true) }
                            IngredientRow(text = line, inStock = inStock)
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                OutlinedText(text = "Instructions", style = MaterialTheme.typography.titleMedium)
                OutlinedText(
                    text = current.instructions,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 4.dp)
                )

                if (current.notes.isNotBlank()) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                    OutlinedText(text = "Notes", style = MaterialTheme.typography.titleMedium)
                    OutlinedText(
                        text = current.notes,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
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
        Text(text = text, style = MaterialTheme.typography.bodyLarge, color = Color.Black)
    }
}
