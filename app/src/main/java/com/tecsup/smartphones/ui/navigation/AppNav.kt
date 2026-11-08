
package com.tecsup.smartphones.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.tecsup.smartphones.AppContainer
import com.tecsup.smartphones.ui.UiState
import com.tecsup.smartphones.ui.components.Cargando
import com.tecsup.smartphones.ui.login.LoginScreen
import com.tecsup.smartphones.ui.login.LoginViewModel
import com.tecsup.smartphones.ui.login.LoginViewModelFactory
import com.tecsup.smartphones.ui.lista.ListaScreen
import com.tecsup.smartphones.ui.detalle.DetalleScreen
import com.tecsup.smartphones.ui.favoritos.FavoritosScreen

@Composable
fun AppNav() {

    // ViewModel encargado de la autenticación.
    val authViewModel: LoginViewModel = viewModel(
        factory = LoginViewModelFactory(
            AppContainer.authRepository
        )
    )

    // Observamos el estado de autenticación.
    val estado by authViewModel.uiState.collectAsStateWithLifecycle()

    when (val resultado = estado) {

        // Comprobando si existe una sesión.
        is UiState.Cargando -> {
            Cargando()
        }

        // Si ocurre un error, mostramos el Login.
        is UiState.Error -> {
            LoginScreen(
                viewModel = authViewModel
            )
        }

        // Comprobamos si existe un usuario autenticado.
        is UiState.Exito -> {

            if (resultado.datos == null) {

                // No existe una sesión.
                LoginScreen(
                    viewModel = authViewModel
                )

            } else {

                // Existe una sesión: mostramos el catálogo.
                CatalogoNavHost(
                    onCerrarSesion = {
                        authViewModel.cerrarSesion()
                    }
                )
            }
        }
    }
}

// Navegación interna del catálogo.
@Composable
private fun CatalogoNavHost(
    onCerrarSesion: () -> Unit
) {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "lista"
    ) {

        // Pantalla principal: catálogo.
        composable("lista") {

            ListaScreen(
                onSmartphoneClick = { id ->
                    navController.navigate("detalle/$id")
                },
                navController = navController,
                onCerrarSesion = onCerrarSesion
            )
        }

        // Pantalla de detalle.
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

        // Pantalla de favoritos.
        composable("favoritos") {

            FavoritosScreen(
                navController = navController
            )
        }
    }
}
