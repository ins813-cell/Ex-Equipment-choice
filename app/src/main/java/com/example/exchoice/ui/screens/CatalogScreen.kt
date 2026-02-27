package com.example.exchoice.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.exchoice.ui.viewmodel.MainViewModel

@Composable
fun CatalogScreen(vm: MainViewModel, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text("Каталог оборудования")
        LazyColumn {
            items(vm.catalog) { item ->
                Card(modifier = Modifier.padding(vertical = 4.dp)) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("${item.manufacturer} ${item.model}")
                        Text(item.type)
                        Text(item.exMarkingRaw)
                    }
                }
            }
        }
    }
}
