package com.tecsup.smartphones.ui.lista
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Star

import androidx.compose.material3.*

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.navigation.NavController

import coil3.compose.AsyncImage
import com.tecsup.smartphones.AppContainer
import com.tecsup.smartphones.domain.model.Item
import com.tecsup.smartphones.ui.UiState
import com.tecsup.smartphones.ui.components.Cargando
import com.tecsup.smartphones.ui.components.ErrorView

import java.util.Locale


private val Fondo = Color(0xFFF8F9FF)
private val AzulPrincipal = Color(0xFF004AC6)
private val AzulSuave = Color(0xFFEFF4FF)
private val TextoPrincipal = Color(0xFF0B1C30)
private val TextoSecundario = Color(0xFF575E70)
private val RojoFavorito = Color(0xFFBA1A1A)
private val Borde = Color(0xFFE2E8F0)
private val AmarilloEstrella = Color(0xFFF59E0B)
private val VerdeStock = Color(0xFF006329)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaScreen(
    onSmartphoneClick: (Int) -> Unit,
    navController: NavController,
    isAdmin: Boolean,
    onAdminClick: () -> Unit,
    onCerrarSesion: () -> Unit
) {

    val factory = remember {
        ListaViewModelFactory(
            repository = AppContainer.itemRepository,
            notificador = AppContainer.notificador
        )
    }

    val listaViewModel: ListaViewModel = viewModel(
        factory = factory
    )

    val uiState by listaViewModel.uiState.collectAsStateWithLifecycle()
    val aviso by listaViewModel.aviso.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = Fondo,

        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Catálogo de Smartphones",
                        color = TextoPrincipal,
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Fondo
                ),
                actions = {
                    IconButton(
                        onClick = {
                            navController.navigate("favoritos") {
                                launchSingleTop = true
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Ver favoritos",
                            tint = RojoFavorito
                        )
                    }

                    IconButton(
                        onClick = onCerrarSesion
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Cerrar sesión",
                            tint = TextoSecundario
                        )
                    }
                }
            )
        },

        // Navegación inferior inspirada en Stitch.
        bottomBar = {

            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 2.dp
            ) {

                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = {
                        Icon(
                            Icons.Default.Smartphone,
                            contentDescription = "Catálogo"
                        )
                    },
                    label = {
                        Text("Catálogo")
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AzulPrincipal,
                        selectedTextColor = AzulPrincipal,
                        indicatorColor = AzulSuave
                    )
                )

                NavigationBarItem(
                    selected = false,
                    onClick = {
                        navController.navigate("favoritos") {
                            launchSingleTop = true
                        }
                    },
                    icon = {
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = "Favoritos"
                        )
                    },
                    label = {
                        Text("Favoritos")
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AzulPrincipal,
                        selectedTextColor = AzulPrincipal
                    )
                )

                if (isAdmin) {
                    NavigationBarItem(
                        selected = false,
                        onClick = onAdminClick,
                        icon = {
                            Icon(
                                Icons.Default.AdminPanelSettings,
                                contentDescription = "Administración"
                            )
                        },
                        label = {
                            Text("Admin")
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AzulPrincipal,
                            selectedTextColor = AzulPrincipal
                        )
                    )
                }
            }
        }

    ) { innerPadding ->

        when (val estado = uiState) {

            is UiState.Cargando -> {
                Cargando()
            }

            is UiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    ErrorView(
                        mensaje = estado.mensaje,
                        onReintentar = {
                            listaViewModel.refrescar()
                        }
                    )
                }
            }

            is UiState.Exito -> {

                if (estado.datos.isEmpty()) {

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Smartphone,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = TextoSecundario
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "No se encontraron smartphones",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextoPrincipal
                        )
                    }

                } else {

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {

                        // Encabezado del catálogo.
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    start = 16.dp,
                                    end = 16.dp,
                                    top = 12.dp,
                                    bottom = 12.dp
                                )
                        ) {

                            Text(
                                text = "Smartphones",
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextoPrincipal
                            )

                            Text(
                                text = "Explora nuestro catálogo",
                                fontSize = 15.sp,
                                color = TextoSecundario
                            )
                        }

                        // Aviso de conexión.
                        aviso?.let { mensaje ->

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFFFF3E0)
                                ),
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 0.dp
                                )
                            ) {
                                Text(
                                    text = mensaje,
                                    modifier = Modifier.padding(12.dp),
                                    color = Color(0xFF8A4B08),
                                    fontSize = 13.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        // Catálogo de smartphones.
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                start = 16.dp,
                                end = 16.dp,
                                top = 4.dp,
                                bottom = 16.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {

                            items(
                                items = estado.datos,
                                key = { smartphone -> smartphone.id }
                            ) { smartphone ->

                                SmartphoneCard(
                                    smartphone = smartphone,
                                    onClick = {
                                        onSmartphoneClick(smartphone.id)
                                    },
                                    esFavorito = smartphone.esFavorito,
                                    onFavoritoClick = {
                                        listaViewModel.alternarFavorito(
                                            smartphone.id
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SmartphoneCard(
    smartphone: Item,
    onClick: () -> Unit,
    onFavoritoClick: () -> Unit,
    esFavorito: Boolean
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Borde
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // Fotografía real obtenida desde DummyJSON.
                Box(
                    modifier = Modifier
                        .size(width = 112.dp, height = 128.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(AzulSuave),
                    contentAlignment = Alignment.Center
                ) {

                    if (!smartphone.imagenUrl.isNullOrBlank()) {

                        AsyncImage(
                            model = smartphone.imagenUrl,
                            contentDescription = smartphone.titulo,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            contentScale = ContentScale.Fit
                        )

                    } else {

                        Icon(
                            imageVector = Icons.Default.Smartphone,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = TextoSecundario
                        )
                    }
                }

                // Información real del smartphone.
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 34.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {

                    Text(
                        text = smartphone.titulo,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextoPrincipal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = smartphone.marca ?: "Marca no disponible",
                        fontSize = 13.sp,
                        color = TextoSecundario,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Calificación.
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(AzulSuave)
                            .padding(
                                horizontal = 7.dp,
                                vertical = 4.dp
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {

                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = AmarilloEstrella,
                            modifier = Modifier.size(16.dp)
                        )

                        Text(
                            text = String.format(
                                Locale.US,
                                "%.1f",
                                smartphone.calificacion
                            ),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextoPrincipal
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = smartphone.subtitulo,
                        fontSize = 12.sp,
                        color = TextoSecundario,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = "PRECIO DE REFERENCIA",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextoSecundario
                    )

                    Text(
                        text = String.format(
                            Locale.US,
                            "\$%,.2f",
                            smartphone.precio
                        ),
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Bold,
                        color = AzulPrincipal
                    )

                    Text(
                        text = if (smartphone.stock > 0) {
                            "Stock: ${smartphone.stock} unidades"
                        } else {
                            "Stock no disponible"
                        },
                        fontSize = 11.sp,
                        color = if (smartphone.stock > 0) {
                            VerdeStock
                        } else {
                            RojoFavorito
                        }
                    )
                }
            }

            // Botón de favoritos.
            IconButton(
                onClick = onFavoritoClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(40.dp)
                    .background(
                        color = AzulSuave,
                        shape = CircleShape
                    )
            ) {

                Icon(
                    imageVector = if (esFavorito) {
                        Icons.Default.Favorite
                    } else {
                        Icons.Default.FavoriteBorder
                    },
                    contentDescription = if (esFavorito) {
                        "Quitar de favoritos"
                    } else {
                        "Agregar a favoritos"
                    },
                    tint = if (esFavorito) {
                        RojoFavorito
                    } else {
                        TextoSecundario
                    }
                )
            }
        }
    }
}
