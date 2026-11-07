package com.tecsup.smartphones.ui.detalle

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tecsup.smartphones.AppContainer
import com.tecsup.smartphones.domain.model.Item
import com.tecsup.smartphones.ui.UiState
import com.tecsup.smartphones.ui.components.Cargando
import com.tecsup.smartphones.ui.components.ErrorView
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack

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

    val uiState by detalleViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Detalle del smartphone")
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
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
                        smartphone = estado.datos
                    )
                }
            }
        }
    }
}

@Composable
private fun DetalleContenido(smartphone: Item) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // Imagen provisional
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .size(220.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "📱",
                fontSize = 80.sp
            )
        }

        // Nombre y descripción
        Text(
            text = smartphone.titulo,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = smartphone.subtitulo,
            style = MaterialTheme.typography.bodyLarge
        )

        // Información adicional
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
        ) {

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Text(
                    text = "Información del producto",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                FilaInformacion(
                    etiqueta = "Marca",
                    valor = smartphone.marca ?: "No especificada"
                )

                FilaInformacion(
                    etiqueta = "Precio",
                    valor = "$ ${"%.2f".format(smartphone.precio)}"
                )

                FilaInformacion(
                    etiqueta = "Calificación",
                    valor = "${smartphone.calificacion} / 5"
                )

                FilaInformacion(
                    etiqueta = "Stock disponible",
                    valor = "${smartphone.stock} unidades"
                )

                FilaInformacion(
                    etiqueta = "ID del producto",
                    valor = smartphone.id.toString()
                )
            }
        }
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
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.size(8.dp))

        Text(
            text = valor,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}
