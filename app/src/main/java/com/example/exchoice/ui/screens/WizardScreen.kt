package com.example.exchoice.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.exchoice.domain.model.SelectionConditions
import com.example.exchoice.ui.viewmodel.MainViewModel

@Composable
fun WizardScreen(vm: MainViewModel, modifier: Modifier = Modifier) {
    val c = vm.conditions
    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text("Мастер подбора")
        OutlinedTextField(
            value = c.zone.toString(),
            onValueChange = { vm.updateConditions(c.copy(zone = it.toIntOrNull() ?: 1)) },
            label = { Text("Зона") }
        )
        OutlinedTextField(
            value = c.gasSubgroup ?: "",
            onValueChange = { vm.updateConditions(c.copy(gasSubgroup = it.ifBlank { null })) },
            label = { Text("Подгруппа газа") }
        )
        OutlinedTextField(
            value = c.temperatureClass ?: "",
            onValueChange = { vm.updateConditions(c.copy(temperatureClass = it.ifBlank { null })) },
            label = { Text("Температурный класс") }
        )
        Button(onClick = vm::runSelection, modifier = Modifier.padding(top = 8.dp)) { Text("Подобрать") }
        Text("Найдено: ${vm.catalog.size}")
        Text(vm.disclaimer, modifier = Modifier.padding(top = 8.dp))
    }
}
