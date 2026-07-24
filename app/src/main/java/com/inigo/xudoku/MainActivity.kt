package com.inigo.xudoku

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.inigo.xudoku.ui.XudokuNavGraph
import com.inigo.xudoku.ui.theme.XudokuTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            XudokuTheme {
                XudokuNavGraph()
            }
        }
    }
}