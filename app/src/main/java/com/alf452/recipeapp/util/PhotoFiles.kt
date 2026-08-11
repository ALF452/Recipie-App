package com.alf452.recipeapp.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

/**
 * Creates a fresh, empty file under the app's private files dir and returns a
 * content:// Uri for it via FileProvider, suitable as the destination for
 * ActivityResultContracts.TakePicture().
 */
fun createRecipePhotoUri(context: Context): Uri {
    val photosDir = File(context.filesDir, "recipe_photos").apply { mkdirs() }
    val photoFile = File(photosDir, "recipe_${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", photoFile)
}
