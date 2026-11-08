package com.tecsup.smartphones.ui.admin


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tecsup.smartphones.AppContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    onVolver: () -> Unit
) {

    val adminViewModel: AdminViewModel = viewModel(
        factory = AdminViewModelFactory(
            AppContainer.itemRepository,
            AppContainer.notificador
        )
    )

    val totalGuardado by adminViewModel.totalGuardado.collectAsStateWithLifecycle()
    val totalFavoritos by adminViewModel.totalFavoritos.collectAsStateWithLifecycle()
    val ocupado by adminViewModel.ocupado.collectAsStateWithLifecycle()
    val mensaje by adminViewModel.mensaje.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Panel de administración")
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
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "Resumen del catálogo",
                style = MaterialTheme.typography.titleLarge
            )

            // Total de smartphones guardados.
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Smartphones guardados",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = totalGuardado.toString(),
                        style = MaterialTheme.typography.headlineLarge
                    )
                }
            }

            // Total de favoritos.
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Smartphones favoritos",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = totalFavoritos.toString(),
                        style = MaterialTheme.typography.headlineLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Botón de sincronización.
            Button(
                onClick = {
                    adminViewModel.sincronizar()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !ocupado
            ) {
                if (ocupado) {
                    CircularProgressIndicator()
                } else {
                    Text("Sincronizar catálogo")
                }
            }

            // Botón para limpiar la caché.
            OutlinedButton(
                onClick = {
                    adminViewModel.limpiarCache()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !ocupado
            ) {
                Text("Limpiar caché")
            }

            // Resultado de las operaciones.
            mensaje?.let { texto ->
                Text(
                    text = texto,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
