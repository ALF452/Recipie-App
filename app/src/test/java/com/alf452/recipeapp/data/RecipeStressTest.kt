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

    /**
     * Heavier version of the above: 1000 recipes per cycle, each with a real
     * photo file, across 3 full add-then-delete-everything cycles. Records
     * used-heap after each cycle (post-GC) to catch the classic leak signature
     * — memory that keeps climbing cycle over cycle instead of returning to
     * baseline once everything from that cycle has been deleted.
     */
    @Test
    fun `1000 recipes with photos across repeated cycles show no memory growth or orphaned files`() = runBlocking {
        val photosDir = File(context.filesDir, "recipe_photos")
        val cycles = 3
        val perCycle = 1000
        val usedMemoryMbAfterCycle = mutableListOf<Double>()

        repeat(cycles) { cycleIndex ->
            val cycleNumber = cycleIndex + 1
            val photoUris = mutableListOf<String>()
            val recipeIds = mutableListOf<Long>()

            for (i in 1..perCycle) {
                val uri = createRecipePhotoUri(context)
                context.contentResolver.openOutputStream(uri)?.use { out ->
                    out.write(ByteArray(2048) { b -> (b % 256).toByte() })
                }
                photoUris += uri.toString()

                val id = recipeDao.insertRecipe(
                    Recipe(
                        title = "Stress Recipe c$cycleNumber-$i",
                        category = if (i % 3 == 0) "Dessert" else "",
                        ingredients = "ingredient a\ningredient b $i",
                        instructions = "Step 1\nStep 2 for recipe $i",
                        notes = if (i % 5 == 0) "note $i" else "",
                        photoUri = uri.toString()
                    )
                )
                recipeIds += id

                if (i % 100 == 0) {
                    println("STRESS_PROGRESS cycle=$cycleNumber/$cycles phase=insert recipe=$i/$perCycle")
                }
            }

            assertEquals("cycle $cycleNumber: insert should produce $perCycle unique ids", perCycle, recipeIds.toSet().size)
            assertEquals("cycle $cycleNumber: createRecipePhotoUri should produce $perCycle unique filenames", perCycle, photoUris.toSet().size)

            val afterInsert = recipeDao.getAllRecipes().first()
            assertEquals("cycle $cycleNumber: all recipes present after insert", perCycle, afterInsert.size)

            val filesAfterCreate = photosDir.listFiles()?.size ?: 0
            assertEquals("cycle $cycleNumber: all photo files present after insert", perCycle, filesAfterCreate)

            afterInsert.forEachIndexed { i, recipe ->
                recipe.photoUri?.let { deletePhotoUri(context, it) }
                recipeDao.deleteRecipe(recipe)
                if ((i + 1) % 100 == 0) {
                    println("STRESS_PROGRESS cycle=$cycleNumber/$cycles phase=delete recipe=${i + 1}/$perCycle")
                }
            }

            val afterDelete = recipeDao.getAllRecipes().first()
            assertTrue("cycle $cycleNumber: expected no recipes left, found ${afterDelete.size}", afterDelete.isEmpty())

            val filesAfterDelete = photosDir.listFiles()?.size ?: 0
            assertEquals("cycle $cycleNumber: expected no orphaned photo files, found $filesAfterDelete", 0, filesAfterDelete)

            val usedMb = usedMemoryMb()
            usedMemoryMbAfterCycle += usedMb
            println("STRESS_PROGRESS cycle=$cycleNumber/$cycles complete usedMemoryMb=${"%.1f".format(usedMb)}")
        }

        println("STRESS_MEMORY_SAMPLES_MB ${usedMemoryMbAfterCycle.joinToString { "%.1f".format(it) }}")

        val baselineMb = usedMemoryMbAfterCycle.first()
        val finalMb = usedMemoryMbAfterCycle.last()
        val growthMb = finalMb - baselineMb
        assertTrue(
            "used heap grew by ${"%.1f".format(growthMb)}MB across $cycles cycles of $perCycle recipes+photos " +
                "each (baseline ${"%.1f".format(baselineMb)}MB -> final ${"%.1f".format(finalMb)}MB); a real leak " +
                "would show unbounded growth here since every recipe and photo from each cycle was fully deleted " +
                "before the next cycle started",
            growthMb < 40.0
        )
    }

    private fun usedMemoryMb(): Double {
        // Force a full collection before sampling so we're measuring live
        // retained objects, not garbage that just hasn't been swept yet.
        System.gc()
        Thread.sleep(50)
        System.gc()
        val runtime = Runtime.getRuntime()
        return (runtime.totalMemory() - runtime.freeMemory()) / (1024.0 * 1024.0)
    }
}
