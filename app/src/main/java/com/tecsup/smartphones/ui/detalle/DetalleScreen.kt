
package com.tecsup.smartphones.ui.detalle

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Smartphone

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

import coil3.compose.AsyncImage

import com.tecsup.smartphones.AppContainer
import com.tecsup.smartphones.domain.model.Item
import com.tecsup.smartphones.ui.UiState
import com.tecsup.smartphones.ui.components.Cargando
import com.tecsup.smartphones.ui.components.ErrorView

import java.util.Locale

// Paleta Apex Mobile Catalog.
private val DetalleFondo = Color(0xFFF8F9FF)
private val DetalleAzul = Color(0xFF004AC6)
private val DetalleAzulSuave = Color(0xFFEFF4FF)
private val DetalleTexto = Color(0xFF0B1C30)
private val DetalleSecundario = Color(0xFF575E70)
private val DetalleBorde = Color(0xFFE2E8F0)
private val DetalleVerde = Color(0xFF006329)
private val DetalleVerdeFondo = Color(0xFFE5F5E9)
private val DetalleRojo = Color(0xFFBA1A1A)
private val DetalleRojoFondo = Color(0xFFFFDAD6)
private val DetalleEstrella = Color(0xFFF59E0B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleScreen(
    smartphoneId: Int,
    onVolver: () -> Unit
) {

    val factory = remember(smartphoneId) {
        DetalleViewModelFactory(
            repository = AppContainer.itemRepository,
            smartphoneId = smartphoneId
        )
    }

    val detalleViewModel: DetalleViewModel = viewModel(
        key = "detalle-$smartphoneId",
        factory = factory
    )

    val uiState by detalleViewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = DetalleFondo,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detalle del smartphone",
                        color = DetalleTexto,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 19.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = DetalleTexto
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DetalleFondo
                )
            )
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            when (val estado = uiState) {

                is UiState.Cargando -> {
                    Cargando()
                }

                is UiState.Error -> {
                    ErrorView(
                        mensaje = estado.mensaje,
                        onReintentar = {
                            detalleViewModel.cargarDetalle()
                        }
                    )
                }

                is UiState.Exito -> {
                    DetalleContenido(
                        smartphone = estado.datos,
                        onFavoritoClick = {
                            detalleViewModel.alternarFavorito()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun DetalleContenido(
    smartphone: Item,
    onFavoritoClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // 1. Imagen principal del smartphone.
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(DetalleAzulSuave),
                contentAlignment = Alignment.Center
            ) {

                if (!smartphone.imagenUrl.isNullOrBlank()) {

                    AsyncImage(
                        model = smartphone.imagenUrl,
                        contentDescription = smartphone.titulo,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        contentScale = ContentScale.Fit
                    )

                } else {

                    Icon(
                        imageVector = Icons.Default.Smartphone,
                        contentDescription = "Imagen no disponible",
                        modifier = Modifier.size(100.dp),
                        tint = DetalleSecundario
                    )
                }
            }
        }

        // 2. Información principal del producto.
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
        ) {

            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // Marca y disponibilidad.
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = smartphone.marca
                            ?.uppercase()
                            ?: "MARCA NO DISPONIBLE",
                        color = DetalleAzul,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Surface(
                        shape = CircleShape,
                        color = if (smartphone.stock > 0) {
                            DetalleVerdeFondo
                        } else {
                            DetalleRojoFondo
                        }
                    ) {

                        Row(
                            modifier = Modifier.padding(
                                horizontal = 10.dp,
                                vertical = 7.dp
                            ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {

                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        color = if (smartphone.stock > 0) {
                                            DetalleVerde
                                        } else {
                                            DetalleRojo
                                        },
                                        shape = CircleShape
                                    )
                            )

                            Text(
                                text = if (smartphone.stock > 0) {
                                    "En stock (${smartphone.stock})"
                                } else {
                                    "Sin stock"
                                },
                                color = if (smartphone.stock > 0) {
                                    DetalleVerde
                                } else {
                                    DetalleRojo
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Nombre del smartphone.
                Text(
                    text = smartphone.titulo,
                    style = MaterialTheme.typography.headlineMedium,
                    color = DetalleTexto,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(2.dp))


                Text(
                    text = String.format(
                        Locale.US,
                        "$%,.2f",
                        smartphone.precio
                    ),
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = DetalleTexto
                )


                Spacer(modifier = Modifier.height(4.dp))

                // Calificación.
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {

                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Calificación",
                        tint = DetalleEstrella,
                        modifier = Modifier.size(23.dp)
                    )

                    Text(
                        text = String.format(
                            Locale.US,
                            "%.1f",
                            smartphone.calificacion
                        ),
                        fontSize = 16.sp,
                        color = DetalleTexto,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "/ 5",
                        fontSize = 13.sp,
                        color = DetalleSecundario
                    )
                }

                // Barra visual de calificación.
                val progreso = (
                        smartphone.calificacion.coerceIn(0.0, 5.0) / 5.0
                        ).toFloat()

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(7.dp)
                        .clip(CircleShape)
                        .background(DetalleAzulSuave)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progreso)
                            .height(7.dp)
                            .clip(CircleShape)
                            .background(DetalleVerde)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Acción de favoritos.
                Button(
                    onClick = onFavoritoClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DetalleAzulSuave,
                        contentColor = DetalleTexto
                    )
                ) {

                    Icon(
                        imageVector = if (smartphone.esFavorito) {
                            Icons.Default.Favorite
                        } else {
                            Icons.Default.FavoriteBorder
                        },
                        contentDescription = null,
                        tint = DetalleRojo
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = if (smartphone.esFavorito) {
                            "Guardado en favoritos"
                        } else {
                            "Añadir a favoritos"
                        },
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // 3. Información disponible del catálogo.
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 1.dp
            )
        ) {

            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                Text(
                    text = "Información del producto",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DetalleTexto
                )

                FilaInformacion(
                    etiqueta = "Marca",
                    valor = smartphone.marca ?: "No especificada"
                )

                FilaInformacion(
                    etiqueta = "Unidades disponibles",
                    valor = smartphone.stock.toString()
                )

                FilaInformacion(
                    etiqueta = "Identificador",
                    valor = smartphone.id.toString()
                )
            }
        }

        // 4. Descripción tecnológica.
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 1.dp
            )
        ) {

            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = DetalleAzul
                    )

                    Text(
                        text = "Descripción tecnológica",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = DetalleTexto
                    )
                }

                Text(
                    text = smartphone.subtitulo,
                    fontSize = 15.sp,
                    lineHeight = 24.sp,
                    color = DetalleSecundario
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun FilaInformacion(
    etiqueta: String,
    valor: String
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = etiqueta,
            fontSize = 14.sp,
            color = DetalleSecundario
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = valor,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = DetalleTexto
        )
    }
}

