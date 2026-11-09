package com.tecsup.smartphones.ui.favoritos

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Star

import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue

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

import java.util.Locale

// Paleta Apex Mobile Catalog.
private val FavoritoFondo = Color(0xFFF8F9FF)
private val FavoritoAzul = Color(0xFF004AC6)
private val FavoritoAzulClaro = Color(0xFFE5EEFF)
private val FavoritoAzulSuave = Color(0xFFEFF4FF)
private val FavoritoTexto = Color(0xFF0B1C30)
private val FavoritoSecundario = Color(0xFF575E70)
private val FavoritoBorde = Color(0xFFE2E8F0)
private val FavoritoRojo = Color(0xFFEF4444)
private val FavoritoEstrella = Color(0xFFF59E0B)

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun FavoritosScreen(
    navController: NavController,
    isAdmin: Boolean
) {

    val viewModel: FavoritosViewModel = viewModel(
        factory = FavoritosViewModelFactory(
            AppContainer.itemRepository
        )
    )

    val favoritos by viewModel.favoritos.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = FavoritoFondo,

        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Favoritos",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = FavoritoTexto
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = FavoritoFondo
                )
            )
        },

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
                        selectedIconColor = FavoritoAzul,
                        selectedTextColor = FavoritoAzul
                    )
                )

                // Favoritos: pantalla actual.
                NavigationBarItem(
                    selected = true,
                    onClick = {},
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
                        selectedIconColor = FavoritoAzul,
                        selectedTextColor = FavoritoAzul,
                        indicatorColor = FavoritoAzulClaro
                    )
                )

                // Admin solamente para administradores.
                if (isAdmin) {

                    NavigationBarItem(
                        selected = false,
                        onClick = {
                            navController.navigate("admin") {
                                launchSingleTop = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = "Administración"
                            )
                        },
                        label = {
                            Text("Admin")
                        }
                    )
                }
            }
        }

    ) { innerPadding ->

        if (favoritos.isEmpty()) {

            // Estado vacío.
            FavoritosVacio(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                onIrCatalogo = {
                    val regreso = navController.popBackStack(
                        "lista",
                        false
                    )

                    if (!regreso) {
                        navController.navigate("lista")
                    }
                }
            )

        } else {

            // Estado con productos favoritos.
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 16.dp,
                            vertical = 12.dp
                        )
                ) {

                    Text(
                        text = "Tus smartphones guardados " +
                                "(${favoritos.size} " +
                                if (favoritos.size == 1) {
                                    "dispositivo)"
                                } else {
                                    "dispositivos)"
                                },
                        fontSize = 14.sp,
                        color = FavoritoSecundario
                    )
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        horizontal = 16.dp,
                        vertical = 8.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    items(
                        items = favoritos,
                        key = { smartphone -> smartphone.id }
                    ) { smartphone ->

                        FavoritoCard(
                            smartphone = smartphone,
                            onClick = {
                                navController.navigate(
                                    "detalle/${smartphone.id}"
                                )
                            },
                            onQuitarFavorito = {
                                viewModel.alternarFavorito(
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

// Tarjeta de smartphone favorito.
@Composable
private fun FavoritoCard(
    smartphone: Item,
    onClick: () -> Unit,
    onQuitarFavorito: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            FavoritoBorde
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Fotografía del producto.
            Box(
                modifier = Modifier
                    .size(width = 112.dp, height = 128.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(FavoritoAzulSuave),
                contentAlignment = Alignment.Center
            ) {

                if (!smartphone.imagenUrl.isNullOrBlank()) {

                    AsyncImage(
                        model = smartphone.imagenUrl,
                        contentDescription = smartphone.titulo,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(6.dp),
                        contentScale = ContentScale.Fit
                    )

                } else {

                    Icon(
                        imageVector = Icons.Default.Smartphone,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = FavoritoSecundario
                    )
                }

                // Etiqueta de marca.
                smartphone.marca?.let { marca ->

                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(5.dp),
                        shape = RoundedCornerShape(5.dp),
                        color = Color.White.copy(alpha = 0.90f)
                    ) {
                        Text(
                            text = marca,
                            modifier = Modifier.padding(
                                horizontal = 6.dp,
                                vertical = 3.dp
                            ),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = FavoritoTexto,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Información del smartphone.
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {

                    Text(
                        text = smartphone.titulo,
                        modifier = Modifier.weight(1f),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = FavoritoTexto,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    IconButton(
                        onClick = onQuitarFavorito,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Quitar de favoritos",
                            tint = FavoritoRojo
                        )
                    }
                }

                // Calificación.
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {

                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Calificación",
                        modifier = Modifier.size(16.dp),
                        tint = FavoritoEstrella
                    )

                    Text(
                        text = String.format(
                            Locale.US,
                            "%.1f",
                            smartphone.calificacion
                        ),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = FavoritoTexto
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Precio real de DummyJSON.
                Text(
                    text = String.format(
                        Locale.US,
                        "\$ %,.2f USD",
                        smartphone.precio
                    ),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = FavoritoAzul
                )

                Text(
                    text = smartphone.stock.toString() +
                            " unidades disponibles",
                    fontSize = 11.sp,
                    color = FavoritoSecundario
                )
            }
        }
    }
}

// Pantalla cuando todavía no hay favoritos.
@Composable
private fun FavoritosVacio(
    modifier: Modifier = Modifier,
    onIrCatalogo: () -> Unit
) {

    Column(
        modifier = modifier
            .padding(horizontal = 24.dp)
            .padding(bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // Ilustración del smartphone.
        Box(
            modifier = Modifier
                .size(184.dp)
                .background(
                    color = FavoritoAzulClaro,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {

            // Cuerpo del teléfono.
            Box(
                modifier = Modifier
                    .size(width = 76.dp, height = 132.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .border(
                        width = 3.dp,
                        color = Color(0xFFCBD5E1),
                        shape = RoundedCornerShape(20.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {

                Column(
                    modifier = Modifier.padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Box(
                        modifier = Modifier
                            .width(26.dp)
                            .height(5.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE2E8F0))
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp))
                            .background(FavoritoFondo),
                        contentAlignment = Alignment.Center
                    ) {

                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = "Sin favoritos",
                            modifier = Modifier.size(42.dp),
                            tint = FavoritoAzul
                        )
                    }
                }
            }

            // Detalles decorativos.
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 42.dp, bottom = 40.dp)
                    .size(9.dp)
                    .background(
                        Color(0xFFB4C5FF),
                        CircleShape
                    )
            )

            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 42.dp, top = 24.dp)
                    .size(8.dp)
                    .background(
                        Color(0xFFB4C5FF),
                        CircleShape
                    )
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Etiqueta.
        Surface(
            shape = CircleShape,
            color = FavoritoAzulClaro
        ) {
            Text(
                text = "Tu lista está en pausa",
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 8.dp
                ),
                color = FavoritoAzul,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Todavía no tienes favoritos",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = FavoritoTexto
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Explora el catálogo y guarda los smartphones " +
                    "que más te interesen para comparar " +
                    "especificaciones al instante.",
            fontSize = 15.sp,
            lineHeight = 23.sp,
            color = FavoritoSecundario,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Volver al catálogo.
        Button(
            onClick = onIrCatalogo,
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 340.dp)
                .height(56.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = FavoritoAzul,
                contentColor = Color.White
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 4.dp
            )
        ) {

            Icon(
                imageVector = Icons.Default.Smartphone,
                contentDescription = null
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = "Ir al catálogo",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
