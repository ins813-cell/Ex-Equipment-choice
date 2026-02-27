package com.example.exchoice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.exchoice.data.local.AppDatabase
import com.example.exchoice.data.remote.BackendClient
import com.example.exchoice.domain.repo.ExRepository
import com.example.exchoice.ui.ExApp
import com.example.exchoice.ui.theme.ExChoiceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = AppDatabase.get(this)
        val repository = ExRepository(db, BackendClient.api)
        setContent {
            ExChoiceTheme {
                ExApp(repository)
            }
        }
    }
}
