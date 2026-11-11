package com.tecsup.smartphones.ui.lista

import androidx.lifecycle.ViewModelStore
import com.tecsup.smartphones.data.repository.FakeItemRepository
import com.tecsup.smartphones.domain.model.Item
import com.tecsup.smartphones.notification.FakeNotificador
import com.tecsup.smartphones.ui.UiState
import com.tecsup.smartphones.util.MainDispatcherRule
import java.io.IOException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class ListaViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val viewModelStore = ViewModelStore()
    private val notificador = FakeNotificador()
    private val smartphone = Item(
        id = 1,
        titulo = "Smartphone de prueba",
        subtitulo = "Producto para pruebas unitarias",
        imagenUrl = null
    )

    @After
    fun limpiarViewModels() {
        viewModelStore.clear()
    }

    @Test
    fun mantieneCargandoMientrasEsperaLaPrimeraSincronizacion() = runTest {
        val respuesta = CompletableDeferred<Result<List<Item>>>()
        val repository = FakeItemRepository().apply {
            alRefrescar = { respuesta.await() }
        }
        val viewModel = crearViewModel(repository)

        assertEquals(UiState.Cargando, viewModel.uiState.value)
        runCurrent()

        assertEquals(1, repository.llamadasARefrescar)
        assertEquals(UiState.Cargando, viewModel.uiState.value)
        assertTrue(notificador.notificaciones.isEmpty())

        respuesta.complete(Result.success(listOf(smartphone)))
        advanceUntilIdle()

        assertEquals(UiState.Exito(listOf(smartphone)), viewModel.uiState.value)
    }

    @Test
    fun sincronizacionExitosaPublicaLosDatosYNotificaLaCantidad() = runTest {
        val smartphones = listOf(smartphone, smartphone.copy(id = 2))
        val repository = FakeItemRepository().apply {
            alRefrescar = { Result.success(smartphones) }
        }
        val viewModel = crearViewModel(repository)

        advanceUntilIdle()

        assertEquals(UiState.Exito(smartphones), viewModel.uiState.value)
        assertNull(viewModel.aviso.value)
        assertEquals(
            listOf(FakeNotificador.Notificacion("Datos actualizados", "Se guardaron 2 elementos")),
            notificador.notificaciones
        )
    }

    @Test
    fun sincronizacionSinProductosMuestraListaVacia() = runTest {
        val viewModel = crearViewModel(FakeItemRepository())

        advanceUntilIdle()

        assertEquals(UiState.Exito(emptyList<Item>()), viewModel.uiState.value)
        assertNull(viewModel.aviso.value)
        assertEquals("Se guardaron 0 elementos", notificador.notificaciones.single().texto)
    }

    @Test
    fun sinConexionNiDatosLocalesMuestraErrorYNoNotifica() = runTest {
        val repository = FakeItemRepository().apply {
            alRefrescar = { Result.failure(IOException("Sin red")) }
        }
        val viewModel = crearViewModel(repository)

        advanceUntilIdle()

        assertEquals(UiState.Error("No hay conexión a internet."), viewModel.uiState.value)
        assertNull(viewModel.aviso.value)
        assertTrue(notificador.notificaciones.isEmpty())
    }

    @Test
    fun sinConexionConDatosLocalesConservaListaYMuestraAviso() = runTest {
        val guardados = listOf(smartphone.copy(esFavorito = true))
        val repository = FakeItemRepository(guardados).apply {
            alRefrescar = { Result.failure(IOException("Sin red")) }
        }
        val viewModel = crearViewModel(repository)

        advanceUntilIdle()

        assertEquals(UiState.Exito(guardados), viewModel.uiState.value)
        assertEquals("Sin conexión: mostrando smartphones guardados.", viewModel.aviso.value)
        assertTrue(notificador.notificaciones.isEmpty())
    }

    @Test
    fun errorHttpSinDatosLocalesMuestraElCodigoDelServidor() = runTest {
        val error = HttpException(Response.error<Unit>(503, "No disponible".toResponseBody()))
        val repository = FakeItemRepository().apply {
            alRefrescar = { Result.failure(error) }
        }
        val viewModel = crearViewModel(repository)

        advanceUntilIdle()

        assertEquals(UiState.Error("Error del servidor: 503"), viewModel.uiState.value)
        assertTrue(notificador.notificaciones.isEmpty())
    }

    @Test
    fun reintentarTrasErrorCargaLosDatos() = runTest {
        val repository = FakeItemRepository().apply {
            alRefrescar = { Result.failure(IOException("Sin red")) }
        }
        val viewModel = crearViewModel(repository)
        advanceUntilIdle()
        assertEquals(UiState.Error("No hay conexión a internet."), viewModel.uiState.value)

        repository.alRefrescar = { Result.success(listOf(smartphone)) }
        viewModel.refrescar()
        advanceUntilIdle()

        assertEquals(2, repository.llamadasARefrescar)
        assertEquals(UiState.Exito(listOf(smartphone)), viewModel.uiState.value)
        assertNull(viewModel.aviso.value)
        assertEquals(1, notificador.notificaciones.size)
    }

    @Test
    fun alternarFavoritoDelegaAlRepositorioYActualizaLaLista() = runTest {
        val repository = FakeItemRepository(listOf(smartphone))
        val viewModel = crearViewModel(repository)
        advanceUntilIdle()

        viewModel.alternarFavorito(smartphone.id)
        advanceUntilIdle()

        assertEquals(listOf(smartphone.id), repository.idsAlternados)
        assertEquals(
            UiState.Exito(listOf(smartphone.copy(esFavorito = true))),
            viewModel.uiState.value
        )
    }

    @Test
    fun ignoraRefrescosSimultaneosYPermiteSincronizarAlTerminar() = runTest {
        val respuesta = CompletableDeferred<Result<List<Item>>>()
        val repository = FakeItemRepository().apply {
            alRefrescar = { respuesta.await() }
        }
        val viewModel = crearViewModel(repository)

        // Incluso antes de ejecutar la corrutina inicial, no debe duplicarse el trabajo.
        viewModel.refrescar()
        viewModel.refrescar()
        runCurrent()
        assertEquals(1, repository.llamadasARefrescar)

        // Tampoco se duplica mientras la petición está suspendida.
        viewModel.refrescar()
        runCurrent()
        assertEquals(1, repository.llamadasARefrescar)

        respuesta.complete(Result.success(listOf(smartphone)))
        advanceUntilIdle()
        assertEquals(1, notificador.notificaciones.size)

        repository.alRefrescar = { Result.success(listOf(smartphone)) }
        viewModel.refrescar()
        advanceUntilIdle()

        assertEquals(2, repository.llamadasARefrescar)
        assertEquals(2, notificador.notificaciones.size)
        assertEquals(UiState.Exito(listOf(smartphone)), viewModel.uiState.value)
    }

    private fun crearViewModel(repository: FakeItemRepository): ListaViewModel {
        return ListaViewModel(repository, notificador).also { viewModel ->
            viewModelStore.put("lista", viewModel)
        }
    }
}