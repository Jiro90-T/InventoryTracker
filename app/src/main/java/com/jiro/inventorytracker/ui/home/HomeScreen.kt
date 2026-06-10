package com.jiro.inventorytracker.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAddClick: () -> Unit,
    onScanClick: () -> Unit,
    onItemClick: (Long) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val items by viewModel.items.collectAsState()
    val query by viewModel.query.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inventory") },
                actions = {
                    IconButton(onClick = onScanClick) {
                        Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan barcode")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(androidx.compose.material.icons.Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = viewModel::setQuery,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                placeholder = { Text("Search items, category, location, barcode") }
            )

            if (items.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "No items yet. Tap + to add your first item.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(items, key = { it.id }) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth(),
                            onClick = { onItemClick(item.id) }
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    item.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                item.category?.let {
                                    Text(it, style = MaterialTheme.typography.bodySmall)
                                }
                                item.location?.let {
                                    Text("Location: $it", style = MaterialTheme.typography.bodySmall)
                                }
                                item.barcode?.let {
                                    Text("Barcode: $it", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
