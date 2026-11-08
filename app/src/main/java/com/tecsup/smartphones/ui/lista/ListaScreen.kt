
package com.tecsup.smartphones.ui.lista
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tecsup.smartphones.AppContainer
import com.tecsup.smartphones.domain.model.Item
import com.tecsup.smartphones.ui.UiState
import com.tecsup.smartphones.ui.components.Cargando
import com.tecsup.smartphones.ui.components.ErrorView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaScreen(
    onSmartphoneClick: (Int) -> Unit,
    navController: NavController
) {

    val factory = remember {
        ListaViewModelFactory(AppContainer.itemRepository)
    }

    val listaViewModel: ListaViewModel = viewModel(
        factory = factory
    )

    val uiState by listaViewModel.uiState.collectAsStateWithLifecycle()
    val aviso by listaViewModel.aviso.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Catálogo de Smartphones")

                        IconButton(
                            onClick = {
                                navController.navigate("favoritos")
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Ver favoritos"
                            )
                        }
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
                            listaViewModel.refrescar()
                        }
                    )
                }

                is UiState.Exito -> {

                    if (estado.datos.isEmpty()) {

                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No se encontraron smartphones")
                        }

                    } else {

                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {

                            // Aviso cuando no hay conexión
                            aviso?.let { mensaje ->

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            horizontal = 16.dp,
                                            vertical = 8.dp
                                        ),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme
                                            .colorScheme.errorContainer
                                    )
                                ) {
                                    Text(
                                        text = mensaje,
                                        modifier = Modifier.padding(12.dp),
                                        color = MaterialTheme
                                            .colorScheme.onErrorContainer
                                    )
                                }
                            }

                            // Lista de smartphones
                            LazyColumn(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
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
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Imagen provisional del smartphone
            Box(
                modifier = Modifier
                    .size(85.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "📱",
                    fontSize = 38.sp
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = smartphone.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.size(6.dp))

                Text(
                    text = smartphone.subtitulo,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(
                onClick = onFavoritoClick
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
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }
    }
}
