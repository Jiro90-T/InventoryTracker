package com.jiro.inventorytracker.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jiro.inventorytracker.ui.detail.ItemDetailScreen
import com.jiro.inventorytracker.ui.home.HomeScreen
import com.jiro.inventorytracker.ui.scan.ScanScreen

object Routes {
    const val HOME = "home"
    const val SCAN = "scan"
    const val DETAIL = "detail/{itemId}"
    fun detail(itemId: Long) = "detail/$itemId"
}

@Composable
fun InventoryNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                onScanClick = { navController.navigate(Routes.SCAN) },
                onItemClick = { id -> navController.navigate(Routes.detail(id)) }
            )
        }
        composable(Routes.SCAN) {
            ScanScreen(
                onBarcodeDetected = { code ->
                    // TODO: navigate to add screen with barcode prefilled
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Routes.DETAIL,
            arguments = listOf(navArgument("itemId") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("itemId") ?: 0L
            ItemDetailScreen(itemId = id, onBack = { navController.popBackStack() })
        }
    }
}
