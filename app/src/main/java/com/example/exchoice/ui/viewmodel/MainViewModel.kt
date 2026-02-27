package com.example.exchoice.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.exchoice.domain.model.EnvironmentMode
import com.example.exchoice.domain.model.ExEquipment
import com.example.exchoice.domain.model.RuleEvaluation
import com.example.exchoice.domain.model.SelectionConditions
import com.example.exchoice.domain.repo.ExRepository
import kotlinx.coroutines.launch

class MainViewModel(private val repo: ExRepository) : ViewModel() {
    var conditions by mutableStateOf(SelectionConditions(mode = EnvironmentMode.GAS, zone = 1))
    var catalog by mutableStateOf<List<ExEquipment>>(emptyList())
    var evaluation by mutableStateOf<RuleEvaluation?>(null)
    var disclaimer = "Результат — предварительная проверка по RuleSet v1.0.0"

    init {
        viewModelScope.launch {
            repo.seedCatalogIfEmpty()
            runSelection()
        }
    }

    fun runSelection() {
        viewModelScope.launch { catalog = repo.filterCatalog(conditions) }
    }

    fun checkManual(raw: String) {
        viewModelScope.launch {
            evaluation = repo.evaluateManual(raw, conditions)
        }
    }

    fun updateConditions(next: SelectionConditions) {
        conditions = next
        runSelection()
    }
}
