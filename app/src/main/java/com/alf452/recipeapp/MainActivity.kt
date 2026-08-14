package com.alf452.recipeapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.alf452.recipeapp.navigation.RecipeNavGraph
import com.alf452.recipeapp.ui.RecipeViewModel
import com.alf452.recipeapp.ui.theme.LocalRecipeTextColor
import com.alf452.recipeapp.ui.theme.RecipeAppTheme

class MainActivity : ComponentActivity() {

    private val viewModel: RecipeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIncomingIntent(intent)
        setContent {
            RecipeAppRoot(viewModel)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIncomingIntent(intent)
    }

    private fun handleIncomingIntent(intent: Intent?) {
        if (intent?.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
            if (!sharedText.isNullOrBlank()) {
                viewModel.setPendingSharedText(sharedText)
            }
        }
    }
}

@Composable
private fun RecipeAppRoot(viewModel: RecipeViewModel) {
    val textColor by viewModel.textColor
    RecipeAppTheme {
        CompositionLocalProvider(LocalRecipeTextColor provides textColor) {
            Surface(modifier = Modifier.fillMaxSize()) {
                RecipeNavGraph(viewModel = viewModel)
            }
        }
    }
}
