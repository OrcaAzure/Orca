package com.orca.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.orca.app.navigation.OrcaNavGraph
import com.orca.app.ui.components.OrcaScaffold
import com.orca.app.ui.theme.OrcaTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OrcaTheme {
                val navController = rememberNavController()
                OrcaScaffold(navController = navController) { contentModifier ->
                    OrcaNavGraph(
                        navController = navController,
                        modifier = contentModifier.fillMaxSize(),
                    )
                }
            }
        }
    }
}
