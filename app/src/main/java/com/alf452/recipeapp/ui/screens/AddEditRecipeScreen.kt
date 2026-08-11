package com.alf452.recipeapp.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PhotoCamera
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.alf452.recipeapp.data.Recipe
import com.alf452.recipeapp.util.createRecipePhotoUri
import com.alf452.recipeapp.util.deletePhotoUri
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

    val context = LocalContext.current

    var id by remember { mutableStateOf(0L) }
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var ingredients by remember { mutableStateOf("") }
    var instructions by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<String?>(null) }
    var pendingCameraUri by remember { mutableStateOf<android.net.Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        val newUri = pendingCameraUri
        if (success && newUri != null) {
            // Only delete the photo currently shown if it's an unsaved capture from
            // *this* editing session — never the original persisted photo, since the
            // user might still back out of this screen without saving.
            val current = photoUri
            if (current != null && current != existingRecipe?.photoUri) {
                deletePhotoUri(context, current)
            }
            photoUri = newUri.toString()
        } else if (newUri != null) {
            deletePhotoUri(context, newUri.toString())
        }
    }

    fun launchCamera() {
        val uri = createRecipePhotoUri(context)
        pendingCameraUri = uri
        cameraLauncher.launch(uri)
    }

    LaunchedEffect(existingRecipe) {
        existingRecipe?.let {
            id = it.id
            title = it.title
            category = it.category
            ingredients = it.ingredients
            instructions = it.instructions
            notes = it.notes
            photoUri = it.photoUri
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
                                val originalPhotoUri = existingRecipe?.photoUri
                                if (originalPhotoUri != null && originalPhotoUri != photoUri) {
                                    deletePhotoUri(context, originalPhotoUri)
                                }
                                onSave(
                                    Recipe(
                                        id = id,
                                        title = title.trim(),
                                        category = category.trim(),
                                        ingredients = ingredients.trim(),
                                        instructions = instructions.trim(),
                                        notes = notes.trim(),
                                        photoUri = photoUri
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
            PhotoPicker(
                photoUri = photoUri,
                onTakePhotoClick = { launchCamera() }
            )

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
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

@Composable
private fun PhotoPicker(photoUri: String?, onTakePhotoClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { onTakePhotoClick() },
        contentAlignment = Alignment.Center
    ) {
        if (photoUri != null) {
            AsyncImage(
                model = photoUri,
                contentDescription = "Recipe photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(8.dp)
            ) {
                Icon(
                    Icons.Filled.PhotoCamera,
                    contentDescription = "Retake photo",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Filled.PhotoCamera,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Take a photo of the dish",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
