
package com.tecsup.smartphones.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Sync

import androidx.compose.material3.*

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.navigation.NavController

import com.tecsup.smartphones.AppContainer

// Paleta Apex Mobile Catalog.
private val AdminFondo = Color(0xFFF8F9FF)
private val AdminAzul = Color(0xFF004AC6)
private val AdminAzulSuave = Color(0xFFEFF4FF)
private val AdminAzulClaro = Color(0xFFE5EEFF)
private val AdminTexto = Color(0xFF0B1C30)
private val AdminSecundario = Color(0xFF575E70)
private val AdminBorde = Color(0xFFE2E8F0)
private val AdminRojo = Color(0xFFBA1A1A)
private val AdminRojoFondo = Color(0xFFFFEEEE)
private val AdminVerde = Color(0xFF006329)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    navController: NavController,
    onVolver: () -> Unit
) {

    // Factory del ViewModel.
    val factory = remember {
        AdminViewModelFactory(
            repository = AppContainer.itemRepository,
            notificador = AppContainer.notificador
        )
    }

    val adminViewModel: AdminViewModel = viewModel(
        factory = factory
    )

    // Estados del ViewModel.
    val totalGuardado by adminViewModel.totalGuardado.collectAsStateWithLifecycle()
    val totalFavoritos by adminViewModel.totalFavoritos.collectAsStateWithLifecycle()
    val ocupado by adminViewModel.ocupado.collectAsStateWithLifecycle()
    val mensaje by adminViewModel.mensaje.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = AdminFondo,

        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Panel de Administración",
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold,
                        color = AdminTexto
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onVolver
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = AdminTexto
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AdminFondo
                )
            )
        },

        // Navegación inferior.
        bottomBar = {

            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 2.dp
            ) {

                // Catálogo.
                NavigationBarItem(
                    selected = false,
                    onClick = {
                        val regreso = navController.popBackStack(
                            "lista",
                            false
                        )

                        if (!regreso) {
                            navController.navigate("lista")
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Smartphone,
                            contentDescription = "Catálogo"
                        )
                    },
                    label = {
                        Text("Catálogo")
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AdminAzul,
                        selectedTextColor = AdminAzul
                    )
                )

                // Favoritos.
                NavigationBarItem(
                    selected = false,
                    onClick = {
                        navController.navigate("favoritos") {
                            launchSingleTop = true
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Favoritos"
                        )
                    },
                    label = {
                        Text("Favoritos")
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AdminAzul,
                        selectedTextColor = AdminAzul
                    )
                )

                // Administración.
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = "Administración"
                        )
                    },
                    label = {
                        Text("Admin")
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AdminAzul,
                        selectedTextColor = AdminAzul,
                        indicatorColor = AdminAzulClaro
                    )
                )
            }
        }

    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(top = 12.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // Encabezado de estadísticas.
            Text(
                text = "Resumen del catálogo",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = AdminTexto
            )

            // Primera tarjeta: smartphones guardados.
            TarjetaEstadistica(
                titulo = "Smartphones guardados",
                cantidad = totalGuardado,
                descripcion = "Almacenamiento local en Room",
                icono = Icons.Default.Smartphone,
                colorIcono = AdminAzul,
                fondoIcono = AdminAzulClaro,
                unidad = "dispositivos"
            )

            // Segunda tarjeta: favoritos.
            TarjetaEstadistica(
                titulo = "Smartphones favoritos",
                cantidad = totalFavoritos,
                descripcion = "Guardados localmente en Room",
                icono = Icons.Default.Favorite,
                colorIcono = AdminRojo,
                fondoIcono = AdminRojoFondo,
                unidad = "favoritos"
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Sección de operaciones.
            Text(
                text = "OPERACIONES DEL REPOSITORIO",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = AdminSecundario,
                letterSpacing = 1.sp
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = AdminAzulSuave
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 1.dp
                )
            ) {

                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    // Botón de sincronización.
                    Button(
                        onClick = {
                            adminViewModel.sincronizar()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        enabled = !ocupado,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AdminAzul,
                            disabledContainerColor = AdminAzul.copy(
                                alpha = 0.6f
                            )
                        )
                    ) {

                        if (ocupado) {

                            CircularProgressIndicator(
                                modifier = Modifier.size(21.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )

                            Spacer(modifier = Modifier.size(10.dp))

                            Text("Procesando...")

                        } else {

                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = null
                            )

                            Spacer(modifier = Modifier.size(10.dp))

                            Text(
                                text = "Sincronizar catálogo",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }

                    // Separador.
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(AdminBorde)
                    )

                    // Botón de limpieza de caché.
                    OutlinedButton(
                        onClick = {
                            adminViewModel.limpiarCache()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        enabled = !ocupado,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White,
                            contentColor = AdminTexto,
                            disabledContentColor = AdminSecundario
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            AdminBorde
                        )
                    ) {

                        Icon(
                            imageVector = Icons.Default.CleaningServices,
                            contentDescription = null,
                            tint = AdminRojo
                        )

                        Spacer(modifier = Modifier.size(10.dp))

                        Text(
                            text = "Limpiar caché",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                    }

                    // Aviso informativo.
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(AdminAzulClaro)
                            .padding(12.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {

                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = AdminSecundario,
                            modifier = Modifier.size(20.dp)
                        )

                        Text(
                            text = "Al limpiar la caché se eliminarán " +
                                    "los smartphones y favoritos almacenados " +
                                    "localmente. Puedes sincronizar para " +
                                    "descargarlos nuevamente.",
                            fontSize = 13.sp,
                            lineHeight = 19.sp,
                            color = AdminSecundario
                        )
                    }
                }
            }

            // Resultado de las operaciones.
            mensaje?.let { texto ->

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = AdminAzulClaro
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 0.dp
                    )
                ) {

                    Text(
                        text = texto,
                        modifier = Modifier.padding(16.dp),
                        fontSize = 14.sp,
                        color = AdminTexto
                    )
                }
            }
        }
    }
}

// Tarjeta reutilizable para las estadísticas.
@Composable
private fun TarjetaEstadistica(
    titulo: String,
    cantidad: Int,
    descripcion: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    colorIcono: Color,
    fondoIcono: Color,
    unidad: String
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            AdminBorde
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            // Contenedor del icono.
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(fondoIcono),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = colorIcono,
                    modifier = Modifier.size(34.dp)
                )
            }

            // Información estadística.
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {

                Text(
                    text = titulo,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AdminSecundario
                )

                Text(
                    text = "$cantidad $unidad",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = AdminTexto
                )

                Text(
                    text = descripcion,
                    fontSize = 12.sp,
                    color = AdminSecundario
                )
            }
        }
    }
}

