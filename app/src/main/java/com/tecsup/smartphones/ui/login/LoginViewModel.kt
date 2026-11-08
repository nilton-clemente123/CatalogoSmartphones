package com.tecsup.smartphones.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.FirebaseNetworkException
import com.tecsup.smartphones.data.auth.AuthRepository
import com.tecsup.smartphones.domain.model.Sesion
import com.tecsup.smartphones.ui.UiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<UiState<Sesion?>>(UiState.Cargando)

    val uiState = _uiState.asStateFlow()

    init {
        verificarSesion()
    }

    // Comprueba si existe una sesión al abrir la aplicación.
    fun verificarSesion() {
        viewModelScope.launch {
            _uiState.value = UiState.Cargando

            try {
                val sesion = repository.sesionActual()
                _uiState.value = UiState.Exito(sesion)

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = UiState.Error(
                    mensajeError(e)
                )
            }
        }
    }

    // Inicia sesión con correo y contraseña.
    fun iniciarSesion(correo: String, clave: String) {

        if (correo.isBlank() || clave.isBlank()) {
            _uiState.value = UiState.Error(
                "Completa el correo y la contraseña."
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = UiState.Cargando

            val resultado = repository.iniciarSesion(
                correo = correo,
                clave = clave
            )

            _uiState.value = resultado.fold(
                onSuccess = { sesion ->
                    UiState.Exito(sesion)
                },
                onFailure = { error ->
                    UiState.Error(mensajeError(error))
                }
            )
        }
    }

    // Cierra la sesión del usuario.
    fun cerrarSesion() {
        repository.cerrarSesion()
        _uiState.value = UiState.Exito(null)
    }

    private fun mensajeError(error: Throwable): String {
        return when (error) {
            is FirebaseAuthInvalidCredentialsException,
            is FirebaseAuthInvalidUserException ->
                "El correo o la contraseña son incorrectos."

            is FirebaseNetworkException ->
                "No se pudo conectar con Firebase. Comprueba tu conexión."

            else ->
                error.message ?: "Ocurrió un error inesperado."
        }
    }
}