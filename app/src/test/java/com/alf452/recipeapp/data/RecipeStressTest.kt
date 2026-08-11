package com.alf452.recipeapp.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.alf452.recipeapp.util.createRecipePhotoUri
import com.alf452.recipeapp.util.deletePhotoUri
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File

/**
 * Exercises the same DAO/repository-level code the UI calls, at bulk scale,
 * to check that add/delete cycles don't corrupt the database or leave
 * orphaned data/files behind — the concrete meaning of "doesn't break the
 * app or build up junk storage" for this app's data layer.
 */
@RunWith(RobolectricTestRunner::class)
class RecipeStressTest {

    private lateinit var context: Context
    private lateinit var db: RecipeDatabase
    private lateinit var recipeDao: RecipeDao
    private lateinit var pantryDao: PantryDao

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(context, RecipeDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        recipeDao = db.recipeDao()
        pantryDao = db.pantryDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `insert and delete 100 recipes leaves no residue`() = runBlocking {
        val insertedIds = (1..100).map { i ->
            recipeDao.insertRecipe(
                Recipe(
                    title = "Stress Recipe $i",
                    category = if (i % 3 == 0) "Dessert" else "",
                    ingredients = "ingredient a\ningredient b $i",
                    instructions = "Step 1\nStep 2 for recipe $i",
                    notes = if (i % 5 == 0) "note $i" else ""
                )
            )
        }
        assertEquals("insert should produce 100 unique ids", 100, insertedIds.toSet().size)

        val afterInsert = recipeDao.getAllRecipes().first()
        assertEquals(100, afterInsert.size)

        afterInsert.forEach { recipeDao.deleteRecipe(it) }

        val afterDelete = recipeDao.getAllRecipes().first()
        assertTrue("expected no recipes left, found ${afterDelete.size}", afterDelete.isEmpty())
    }

    @Test
    fun `insert and delete 100 pantry items leaves no residue`() = runBlocking {
        (1..100).forEach { i -> pantryDao.insertPantryItem(PantryItem(name = "Ingredient $i")) }

        val afterInsert = pantryDao.getAllPantryItems().first()
        assertEquals(100, afterInsert.size)

        afterInsert.forEach { pantryDao.deletePantryItem(it) }

        val afterDelete = pantryDao.getAllPantryItems().first()
        assertTrue("expected no pantry items left, found ${afterDelete.size}", afterDelete.isEmpty())
    }

    @Test
    fun `duplicate pantry item names are ignored, not duplicated`() = runBlocking {
        pantryDao.insertPantryItem(PantryItem(name = "Salt"))
        pantryDao.insertPantryItem(PantryItem(name = "salt"))
        pantryDao.insertPantryItem(PantryItem(name = "SALT"))

        val items = pantryDao.getAllPantryItems().first()
        assertEquals("case-insensitive duplicates should collapse to one row", 1, items.size)
    }

    @Test
    fun `creating and deleting 100 recipe photos leaves no orphaned files`() {
        val photosDir = File(context.filesDir, "recipe_photos")

        val uris = (1..100).map { createRecipePhotoUri(context) }
        assertEquals(
            "createRecipePhotoUri should produce 100 unique filenames even called rapidly in a loop",
            100,
            uris.map { it.toString() }.toSet().size
        )

        // TakePicture writes the actual photo bytes to the destination we hand it;
        // simulate that here so the files really exist on disk, same as production.
        uris.forEach { uri ->
            context.contentResolver.openOutputStream(uri)?.use { out ->
                out.write(byteArrayOf(1, 2, 3))
            }
        }

        val filesAfterCreate = photosDir.listFiles()?.size ?: 0
        assertEquals(100, filesAfterCreate)

        uris.forEach { deletePhotoUri(context, it.toString()) }

        val filesAfterDelete = photosDir.listFiles()?.size ?: 0
        assertEquals("expected no orphaned photo files, found $filesAfterDelete", 0, filesAfterDelete)
    }
}
