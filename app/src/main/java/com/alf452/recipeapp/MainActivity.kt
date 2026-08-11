package com.alf452.recipeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.alf452.recipeapp.navigation.RecipeNavGraph
import com.alf452.recipeapp.ui.RecipeViewModel
import com.alf452.recipeapp.ui.theme.RecipeAppTheme

class MainActivity : ComponentActivity() {

    private val viewModel: RecipeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RecipeAppRoot(viewModel)
        }
    }
}

@Composable
private fun RecipeAppRoot(viewModel: RecipeViewModel) {
    RecipeAppTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            RecipeNavGraph(viewModel = viewModel)
        }
    }
}
