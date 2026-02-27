package com.example.exchoice.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.exchoice.ui.viewmodel.MainViewModel

@Composable
fun CheckScreen(vm: MainViewModel, modifier: Modifier = Modifier) {
    val input = remember { mutableStateOf("") }
    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text("Проверка Ex-маркировки")
        OutlinedTextField(
            value = input.value,
            onValueChange = { input.value = it },
            label = { Text("Ex маркировка") }
        )
        Button(onClick = { vm.checkManual(input.value) }, modifier = Modifier.padding(top = 8.dp)) {
            Text("Проверить")
        }
        vm.evaluation?.let { eval ->
            Text("Вердикт: ${eval.result}")
            eval.reasons.forEach { Text("${it.code}: ${it.message}") }
        }
        Text(vm.disclaimer, modifier = Modifier.padding(top = 8.dp))
    }
}
