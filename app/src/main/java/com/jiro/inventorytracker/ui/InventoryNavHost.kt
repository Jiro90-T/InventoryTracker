package com.jiro.inventorytracker.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jiro.inventorytracker.ui.add.AddEditScreen
import com.jiro.inventorytracker.ui.detail.ItemDetailScreen
import com.jiro.inventorytracker.ui.home.HomeScreen
import com.jiro.inventorytracker.ui.onboarding.OnboardingScreen
import com.jiro.inventorytracker.ui.scan.ScanScreen
import com.jiro.inventorytracker.ui.settings.SettingsScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Routes {
    const val ONBOARDING = "onboarding"
    const val HOME = "home"
    const val SCAN = "scan"
    const val SETTINGS = "settings"

    const val ADD = "add?itemId={itemId}&barcode={barcode}"
    fun add(itemId: Long? = null, barcode: String? = null): String {
        val id = itemId ?: 0L
        val b = barcode?.takeIf { it.isNotBlank() } ?: ""
        return "add?itemId=$id&barcode=$b"
    }

    const val DETAIL = "detail/{itemId}"
    fun detail(itemId: Long) = "detail/$itemId"
}

@Composable
fun InventoryNavHost() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // CSV file-picker launcher. We pass a filename suggestion; the actual bytes are
    // written on a background thread once the URI is granted.
    val pendingCsv = remember { mutableStateOf<String?>(null) }
    val csvLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        val data = pendingCsv.value
        if (uri != null && data != null) {
            scope.launch {
                withContext(Dispatchers.IO) {
                    context.contentResolver.openOutputStream(uri)?.use {
                        it.write(data.toByteArray())
                    }
                }
            }
        }
        pendingCsv.value = null
    }

    NavHost(navController = navController, startDestination = Routes.ONBOARDING) {
        composable(Routes.ONBOARDING) {
            OnboardingScreen(onDone = {
                navController.navigate(Routes.HOME) {
                    popUpTo(Routes.ONBOARDING) { inclusive = true }
                }
            })
        }
        composable(Routes.HOME) {
            HomeScreen(
                onAddClick = { navController.navigate(Routes.add()) },
                onScanClick = { navController.navigate(Routes.SCAN) },
                onItemClick = { id -> navController.navigate(Routes.detail(id)) },
                onSettingsClick = { navController.navigate(Routes.SETTINGS) }
            )
        }
        composable(Routes.SCAN) {
            ScanScreen(
                onBarcodeDetected = { code ->
                    navController.navigate(Routes.add(barcode = code)) {
                        popUpTo(Routes.HOME)
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onExportCsv = { csv ->
                    pendingCsv.value = csv
                    val stamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
                    csvLauncher.launch("inventory_$stamp.csv")
                }
            )
        }
        composable(
            route = Routes.ADD,
            arguments = listOf(
                navArgument("itemId") {
                    type = NavType.LongType
                    defaultValue = 0L
                },
                navArgument("barcode") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true
                }
            )
        ) {
            AddEditScreen(
                onBack = { navController.popBackStack() },
                onSaved = { savedId ->
                    navController.popBackStack(Routes.HOME, inclusive = false)
                    navController.navigate(Routes.detail(savedId))
                },
                onScanClick = { navController.navigate(Routes.SCAN) }
            )
        }
        composable(
            route = Routes.DETAIL,
            arguments = listOf(navArgument("itemId") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("itemId") ?: 0L
            ItemDetailScreen(
                itemId = id,
                onBack = { navController.popBackStack() },
                onEdit = { navController.navigate(Routes.add(itemId = id)) }
            )
        }
    }
}
