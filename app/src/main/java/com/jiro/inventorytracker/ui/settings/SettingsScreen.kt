package com.jiro.inventorytracker.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
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
import com.jiro.inventorytracker.persona.Persona
import com.jiro.inventorytracker.persona.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onExportCsv: (String) -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val persona by viewModel.persona.collectAsState()
    val currency by viewModel.currency.collectAsState()
    val warrantyLeadDays by viewModel.warrantyLeadDays.collectAsState()
    val expiryLeadDays by viewModel.expiryLeadDays.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Section("User mode") {
                Persona.entries.forEach { p ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        androidx.compose.material3.RadioButton(
                            selected = persona == p,
                            onClick = { viewModel.setPersona(p) }
                        )
                        Column(modifier = Modifier.padding(start = 8.dp)) {
                            Text(p.displayName, fontWeight = FontWeight.SemiBold)
                            Text(
                                p.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            HorizontalDivider()

            Section("Currency") {
                OutlinedTextField(
                    value = currency,
                    onValueChange = { v -> viewModel.setCurrency(v.uppercase().take(3)) },
                    label = { Text("3-letter code (e.g. USD, MYR, EUR)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            HorizontalDivider()

            Section("Theme") {
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    ThemeMode.entries.forEachIndexed { idx, mode ->
                        SegmentedButton(
                            selected = themeMode == mode,
                            onClick = { viewModel.setThemeMode(mode) },
                            shape = SegmentedButtonDefaults.itemShape(idx, ThemeMode.entries.size)
                        ) { Text(mode.displayName) }
                    }
                }
            }

            HorizontalDivider()

            Section("Reminder lead time") {
                Text("Warranty reminders: $warrantyLeadDays days before expiry")
                Slider(
                    value = warrantyLeadDays.toFloat(),
                    onValueChange = { viewModel.setWarrantyLeadDays(it.toInt()) },
                    valueRange = 0f..90f,
                    steps = 17
                )
                Text("Expiry reminders: $expiryLeadDays days before expiry")
                Slider(
                    value = expiryLeadDays.toFloat(),
                    onValueChange = { viewModel.setExpiryLeadDays(it.toInt()) },
                    valueRange = 0f..30f,
                    steps = 5
                )
            }

            HorizontalDivider()

            Section("Data") {
                Button(
                    onClick = { viewModel.buildCsvAsync(onExportCsv) },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Export to CSV") }
                Text(
                    "Save your inventory as a CSV file using the system file picker.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun Section(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        content()
    }
}
