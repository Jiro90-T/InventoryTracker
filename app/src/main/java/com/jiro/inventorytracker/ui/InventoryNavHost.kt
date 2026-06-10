package com.jiro.inventorytracker.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jiro.inventorytracker.ui.add.AddEditScreen
import com.jiro.inventorytracker.ui.detail.ItemDetailScreen
import com.jiro.inventorytracker.ui.home.HomeScreen
import com.jiro.inventorytracker.ui.scan.ScanScreen

object Routes {
    const val HOME = "home"
    const val SCAN = "scan"

    // Optional barcode argument: ?barcode=xxxx  (used when returning from scanner)
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
    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                onAddClick = { navController.navigate(Routes.add()) },
                onScanClick = { navController.navigate(Routes.SCAN) },
                onItemClick = { id -> navController.navigate(Routes.detail(id)) }
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
