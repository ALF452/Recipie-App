package com.alf452.recipeapp.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.util.UUID

/**
 * Creates a fresh, empty file under the app's private files dir and returns a
 * content:// Uri for it via FileProvider, suitable as the destination for
 * ActivityResultContracts.TakePicture(). Includes a random suffix alongside
 * the timestamp so rapid repeated calls (e.g. quickly retaking a photo, or
 * bulk testing) can't collide on the same filename.
 */
fun createRecipePhotoUri(context: Context): Uri {
    val photosDir = File(context.filesDir, "recipe_photos").apply { mkdirs() }
    val uniqueSuffix = UUID.randomUUID().toString().take(8)
    val photoFile = File(photosDir, "recipe_${System.currentTimeMillis()}_$uniqueSuffix.jpg")
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", photoFile)
}

/**
 * Deletes a previously created recipe photo (an old photo replaced by a retake,
 * or an unused destination file left behind by a cancelled camera capture), so
 * private storage doesn't accumulate orphaned files over time.
 */
fun deletePhotoUri(context: Context, uriString: String) {
    runCatching { context.contentResolver.delete(Uri.parse(uriString), null, null) }
}
