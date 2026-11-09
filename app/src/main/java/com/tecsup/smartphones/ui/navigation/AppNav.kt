package com.tecsup.smartphones.ui.navigation
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tecsup.smartphones.AppContainer
import com.tecsup.smartphones.domain.model.Rol
import com.tecsup.smartphones.domain.model.Sesion
import com.tecsup.smartphones.ui.UiState
import com.tecsup.smartphones.ui.components.Cargando
import com.tecsup.smartphones.ui.login.LoginScreen
import com.tecsup.smartphones.ui.login.LoginViewModel
import com.tecsup.smartphones.ui.login.LoginViewModelFactory
import com.tecsup.smartphones.ui.lista.ListaScreen
import com.tecsup.smartphones.ui.detalle.DetalleScreen
import com.tecsup.smartphones.ui.favoritos.FavoritosScreen
import com.tecsup.smartphones.ui.admin.AdminScreen

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

                // Existe una sesión.
                // Solicitamos el permiso de notificaciones.
                CatalogoConPermiso(
                    sesion = resultado.datos,
                    onCerrarSesion = {
                        authViewModel.cerrarSesion()
                    }
                )
            }
        }
    }
}

// Gestiona el permiso de notificaciones antes de mostrar el catálogo.
@Composable
private fun CatalogoConPermiso(
    sesion: Sesion,
    onCerrarSesion: () -> Unit
) {

    val context = LocalContext.current

    // Comprueba si el permiso ya está concedido.
    var permisoResuelto by rememberSaveable {
        mutableStateOf(
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Lanzador para solicitar el permiso.
    val permisoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        // Aunque el usuario rechace el permiso,
        // podrá continuar utilizando la aplicación.
        permisoResuelto = true
    }

    LaunchedEffect(Unit) {

        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            !permisoResuelto
        ) {

            permisoLauncher.launch(
                Manifest.permission.POST_NOTIFICATIONS
            )
        }
    }

    if (permisoResuelto) {

        CatalogoNavHost(
            sesion = sesion,
            onCerrarSesion = onCerrarSesion
        )

    } else {

        Cargando()
    }
}

// Navegación interna del catálogo.
@Composable
private fun CatalogoNavHost(
    sesion: Sesion,
    onCerrarSesion: () -> Unit
) {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "lista"
    ) {

        // Catálogo principal.
        composable("lista") {

            ListaScreen(
                onSmartphoneClick = { id ->
                    navController.navigate("detalle/$id")
                },
                navController = navController,
                isAdmin = sesion.rol == Rol.ADMIN,
                onAdminClick = {
                    if (sesion.rol == Rol.ADMIN) {
                        navController.navigate("admin")
                    }
                },
                onCerrarSesion = onCerrarSesion
            )
        }

        // Detalle del smartphone.
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

        // Favoritos: accesible para ambos roles.
        composable("favoritos") {

            FavoritosScreen(
                navController = navController,
                isAdmin = sesion.rol == Rol.ADMIN
            )
        }

        // Panel Admin: exclusivo para administradores.
        if (sesion.rol == Rol.ADMIN) {

            composable("admin") {

                AdminScreen(
                    navController = navController,
                    onVolver = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
