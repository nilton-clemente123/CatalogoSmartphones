package com.tecsup.smartphones.ui.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tecsup.smartphones.ui.lista.ListaScreen
import com.tecsup.smartphones.ui.detalle.DetalleScreen
import com.tecsup.smartphones.ui.favoritos.FavoritosScreen

@Composable
fun AppNav() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "lista"
    ) {

        // Pantalla principal: catálogo
        composable("lista") {

            ListaScreen(
                onSmartphoneClick = { id ->
                    navController.navigate("detalle/$id")
                }
                , navController= navController

            )
        }

        // Pantalla de detalle
        composable(
            route = "detalle/{smartphoneId}",
            arguments = listOf(
                navArgument("smartphoneId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->

            val smartphoneId = backStackEntry.arguments
                ?.getInt("smartphoneId")

            if (smartphoneId != null) {
                DetalleScreen(
                    smartphoneId = smartphoneId,
                    onVolver = {
                        navController.popBackStack()
                    }
                )
            }
        }

        composable("favoritos") {
            FavoritosScreen(navController = navController)
        }
    }
}
