package com.example.exchoice.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.exchoice.domain.repo.ExRepository
import com.example.exchoice.ui.screens.CatalogScreen
import com.example.exchoice.ui.screens.CheckScreen
import com.example.exchoice.ui.screens.WizardScreen
import com.example.exchoice.ui.viewmodel.MainViewModel

enum class Tab { Wizard, Check, Catalog }

@Composable
fun ExApp(repository: ExRepository) {
    var tab by remember { mutableStateOf(Tab.Wizard) }
    val vm: MainViewModel = viewModel(factory = object : androidx.lifecycle.ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T = MainViewModel(repository) as T
    })

    Scaffold(
        bottomBar = {
            NavigationBar {
                Tab.entries.forEach {
                    NavigationBarItem(
                        selected = tab == it,
                        onClick = { tab = it },
                        icon = { Text(it.name.take(1)) },
                        label = { Text(it.name) }
                    )
                }
            }
        }
    ) { p ->
        when (tab) {
            Tab.Wizard -> WizardScreen(vm, Modifier.padding(p))
            Tab.Check -> CheckScreen(vm, Modifier.padding(p))
            Tab.Catalog -> CatalogScreen(vm, Modifier.padding(p))
        }
    }
}
