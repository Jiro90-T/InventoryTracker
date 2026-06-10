package com.jiro.inventorytracker.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailScreen(
    itemId: Long,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    viewModel: ItemDetailViewModel = hiltViewModel()
) {
    val item by viewModel.item.collectAsState()
    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Item details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                }
            )
        }
    ) { padding ->
        val current = item
        if (current == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                current.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )

            if (current.photoPaths.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(current.photoPaths) { path ->
                        AsyncImage(
                            model = path,
                            contentDescription = null,
                            modifier = Modifier
                                .height(160.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                    }
                }
            }

            DetailRow("Category", current.category)
            DetailRow("Barcode", current.barcode)
            DetailRow("Quantity", current.quantity.toString())
            DetailRow("Location", current.location)
            DetailRow(
                "Purchase price",
                current.purchasePrice?.let { "%.2f".format(it) }
            )
            DetailRow(
                "Purchase date",
                current.purchaseDate?.let { dateFormatter.format(Date(it)) }
            )
            DetailRow(
                "Warranty expires",
                current.warrantyExpiresAt?.let { dateFormatter.format(Date(it)) }
            )
            DetailRow(
                "Expiry date",
                current.expiryDate?.let { dateFormatter.format(Date(it)) }
            )

            if (!current.notes.isNullOrBlank()) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Notes",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(current.notes, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String?) {
    if (value.isNullOrBlank()) return
    Column {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(value, style = MaterialTheme.typography.bodyLarge)
    }
}
