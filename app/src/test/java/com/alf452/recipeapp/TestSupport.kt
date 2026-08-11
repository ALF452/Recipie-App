package com.alf452.recipeapp

import com.alf452.recipeapp.data.RecipeDatabase
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

/**
 * Robolectric gives every test method a fresh sandboxed Application, but
 * RecipeDatabase.getInstance() caches its instance in a companion-object
 * field that lives for the whole JVM fork. Without a reset, whichever test
 * launches MainActivity first "wins" the cache, and every later test that
 * also launches MainActivity inherits a database bound to a previous test's
 * defunct Application/file sandbox.
 *
 * A same-class @Before is too late to fix this: createAndroidComposeRule
 * launches the Activity (and so populates the cache) as part of the Rule's
 * own setup, which JUnit4 runs before any @Before method in the same class.
 * Wrapping the Compose rule in this one via RuleChain.outerRule(...) runs
 * the reset ahead of that Activity launch instead.
 */
class ResetRecipeDatabaseRule : TestRule {
    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                resetRecipeDatabaseInstance()
                base.evaluate()
            }
        }
    }
}

private fun resetRecipeDatabaseInstance() {
    val companionField = RecipeDatabase::class.java.getDeclaredField("Companion")
    companionField.isAccessible = true
    val companion = companionField.get(null)
    val instanceField = companion.javaClass.getDeclaredField("INSTANCE")
    instanceField.isAccessible = true
    instanceField.set(companion, null)
}
