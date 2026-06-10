package com.jiro.inventorytracker.ui.add

import android.app.DatePickerDialog
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.jiro.inventorytracker.media.PhotoStorage
import com.jiro.inventorytracker.persona.Condition
import com.jiro.inventorytracker.persona.Persona
import com.jiro.inventorytracker.persona.PersonaCategories
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditScreen(
    onBack: () -> Unit,
    onSaved: (Long) -> Unit,
    onScanClick: () -> Unit,
    viewModel: AddEditViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val persona by viewModel.persona.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    var showPhotoChooser by remember { mutableStateOf(false) }
    var pendingCameraFile by remember { mutableStateOf<File?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val path = PhotoStorage.importFromUri(context, uri)
            if (path != null) viewModel.addPhoto(path)
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        val file = pendingCameraFile
        if (success && file != null && file.exists() && file.length() > 0) {
            viewModel.addPhoto(file.absolutePath)
        }
        pendingCameraFile = null
    }

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (viewModel.isEditMode) "Edit item" else "Add item") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (viewModel.isEditMode) {
                        IconButton(onClick = { viewModel.delete(onBack) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.save(onSaved) }) {
                Icon(Icons.Default.Add, contentDescription = "Save")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) { Snackbar(it) } }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Mode: ${persona.displayName}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = state.name,
                onValueChange = { v -> viewModel.update { it.copy(name = v) } },
                label = { Text("Name *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Category with chip suggestions
            CategoryField(
                value = state.category,
                onValueChange = { v -> viewModel.update { it.copy(category = v) } },
                suggestions = PersonaCategories.forPersona(persona),
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = state.quantity.toString(),
                    onValueChange = { v ->
                        val n = v.toIntOrNull() ?: 1
                        viewModel.update { it.copy(quantity = n.coerceAtLeast(1)) }
                    },
                    label = { Text("Qty") },
                    modifier = Modifier.width(96.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = state.location,
                    onValueChange = { v -> viewModel.update { it.copy(location = v) } },
                    label = { Text("Location") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            OutlinedTextField(
                value = state.barcode,
                onValueChange = { v -> viewModel.update { it.copy(barcode = v) } },
                label = { Text("Barcode") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = onScanClick) {
                        Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan")
                    }
                }
            )

            // Manufacturer / model / serial — useful for all personas
            OutlinedTextField(
                value = state.manufacturer,
                onValueChange = { v -> viewModel.update { it.copy(manufacturer = v) } },
                label = { Text("Manufacturer / Brand") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = state.model,
                    onValueChange = { v -> viewModel.update { it.copy(model = v) } },
                    label = { Text("Model") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = state.serialNumber,
                    onValueChange = { v -> viewModel.update { it.copy(serialNumber = v) } },
                    label = { Text("Serial #") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            // Persona-specific block
            when (persona) {
                Persona.HOME -> Unit // home: nothing extra; warranty/expiry covered below
                Persona.BUSINESS -> {
                    OutlinedTextField(
                        value = state.assetTag,
                        onValueChange = { v -> viewModel.update { it.copy(assetTag = v) } },
                        label = { Text("Asset tag / Inventory ID") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = state.assignedTo,
                        onValueChange = { v -> viewModel.update { it.copy(assignedTo = v) } },
                        label = { Text("Assigned to (employee / department)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
                Persona.COLLECTOR -> {
                    ConditionDropdown(
                        value = state.condition,
                        onPick = { c -> viewModel.update { it.copy(condition = c) } }
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = state.grade,
                            onValueChange = { v -> viewModel.update { it.copy(grade = v) } },
                            label = { Text("Grade (e.g. PSA 10)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = state.era,
                            onValueChange = { v -> viewModel.update { it.copy(era = v) } },
                            label = { Text("Era / Year") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }
            }

            // Money + dates
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = state.purchasePrice?.toString().orEmpty(),
                    onValueChange = { v ->
                        val d = v.toDoubleOrNull()
                        viewModel.update { it.copy(purchasePrice = d) }
                    },
                    label = { Text("Purchase price") },
                    modifier = Modifier.weight(1.5f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                OutlinedTextField(
                    value = state.purchaseCurrency,
                    onValueChange = { v -> viewModel.update { it.copy(purchaseCurrency = v.uppercase().take(3)) } },
                    label = { Text("Cur") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            if (persona == Persona.COLLECTOR) {
                OutlinedTextField(
                    value = state.currentValue?.toString().orEmpty(),
                    onValueChange = { v ->
                        val d = v.toDoubleOrNull()
                        viewModel.update { it.copy(currentValue = d) }
                    },
                    label = { Text("Current estimated value (${state.purchaseCurrency})") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
            }

            DateRow(
                label = "Purchase date",
                timestamp = state.purchaseDate,
                onPick = { ts -> viewModel.update { it.copy(purchaseDate = ts) } },
                onClear = { viewModel.update { it.copy(purchaseDate = null) } }
            )

            if (persona == Persona.HOME || persona == Persona.BUSINESS) {
                DateRow(
                    label = "Warranty expires",
                    timestamp = state.warrantyExpiresAt,
                    onPick = { ts -> viewModel.update { it.copy(warrantyExpiresAt = ts) } },
                    onClear = { viewModel.update { it.copy(warrantyExpiresAt = null) } }
                )
            }

            if (persona == Persona.HOME) {
                DateRow(
                    label = "Expiry date",
                    timestamp = state.expiryDate,
                    onPick = { ts -> viewModel.update { it.copy(expiryDate = ts) } },
                    onClear = { viewModel.update { it.copy(expiryDate = null) } }
                )
            }

            Text(
                "Photos",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            PhotoStrip(
                photoPaths = state.photoPaths,
                onAdd = { showPhotoChooser = true },
                onRemove = { uri -> viewModel.removePhoto(uri) }
            )

            OutlinedTextField(
                value = state.notes,
                onValueChange = { v -> viewModel.update { it.copy(notes = v) } },
                label = { Text("Notes") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                minLines = 3
            )

            Button(
                onClick = { viewModel.save(onSaved) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (viewModel.isEditMode) "Update item" else "Save item")
            }

            Box(modifier = Modifier.size(72.dp)) // FAB spacing
        }
    }

    if (showPhotoChooser) {
        AlertDialog(
            onDismissRequest = { showPhotoChooser = false },
            title = { Text("Add photo") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    TextButton(
                        onClick = {
                            showPhotoChooser = false
                            val file = PhotoStorage.newCameraFile(context)
                            pendingCameraFile = file
                            val uri = PhotoStorage.fileProviderUri(context, file)
                            cameraLauncher.launch(uri)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.PhotoCamera, contentDescription = null)
                        Text("  Take photo")
                    }
                    TextButton(
                        onClick = {
                            showPhotoChooser = false
                            galleryLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                        Text("  Pick from gallery")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showPhotoChooser = false }) { Text("Cancel") }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryField(
    value: String,
    onValueChange: (String) -> Unit,
    suggestions: List<String>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text("Category") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        if (suggestions.isNotEmpty()) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(suggestions) { s ->
                    FilterChip(
                        selected = value.equals(s, ignoreCase = true),
                        onClick = { onValueChange(s) },
                        label = { Text(s) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConditionDropdown(
    value: Condition?,
    onPick: (Condition?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = value?.displayName.orEmpty(),
            onValueChange = {},
            readOnly = true,
            label = { Text("Condition") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text("None") },
                onClick = { onPick(null); expanded = false }
            )
            Condition.entries.forEach { c ->
                DropdownMenuItem(
                    text = { Text(c.displayName) },
                    onClick = { onPick(c); expanded = false }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateRow(
    label: String,
    timestamp: Long?,
    onPick: (Long) -> Unit,
    onClear: () -> Unit
) {
    val context = LocalContext.current
    val formatter = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilledTonalButton(
            onClick = {
                val cal = Calendar.getInstance().apply { timestamp?.let { timeInMillis = it } }
                DatePickerDialog(
                    context,
                    { _, year, month, day ->
                        val picked = Calendar.getInstance().apply {
                            set(year, month, day, 0, 0, 0)
                            set(Calendar.MILLISECOND, 0)
                        }
                        onPick(picked.timeInMillis)
                    },
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            },
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = timestamp?.let { formatter.format(Date(it)) } ?: "Set $label"
            )
        }
        if (timestamp != null) {
            OutlinedButton(onClick = onClear) { Text("Clear") }
        }
    }
}

@Composable
private fun PhotoStrip(
    photoPaths: List<String>,
    onAdd: () -> Unit,
    onRemove: (String) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        item {
            FilledTonalButton(onClick = onAdd) {
                Icon(Icons.Default.Add, contentDescription = null)
                Text("  Add photo")
            }
        }
        items(photoPaths) { path ->
            Card(modifier = Modifier.size(96.dp)) {
                Box(modifier = Modifier.fillMaxSize()) {
                    AsyncImage(
                        model = path,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                    IconButton(
                        onClick = { onRemove(path) },
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Remove",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}
