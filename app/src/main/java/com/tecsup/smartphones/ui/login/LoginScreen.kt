
package com.tecsup.smartphones.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VerifiedUser

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.lifecycle.compose.collectAsStateWithLifecycle

import com.tecsup.smartphones.ui.UiState

// Paleta de colores de Stitch.
private val LoginBackground = Color(0xFFF8F9FF)
private val LoginPrimary = Color(0xFF2563EB)
private val LoginInputBackground = Color(0xFFEFF4FF)
private val LoginInfoBackground = Color(0xFFE5EEFF)
private val LoginText = Color(0xFF0B1C30)
private val LoginSecondary = Color(0xFF575E70)

@Composable
fun LoginScreen(
    viewModel: LoginViewModel
) {

    var correo by rememberSaveable {
        mutableStateOf("")
    }

    var clave by rememberSaveable {
        mutableStateOf("")
    }

    var mostrarClave by rememberSaveable {
        mutableStateOf(false)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val error = (uiState as? UiState.Error)?.mensaje

    val cargando = uiState is UiState.Cargando

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LoginBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 100.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Encabezado.
            Text(
                text = "Bienvenido",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = LoginText,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Descubre y explora los mejores smartphones",
                fontSize = 18.sp,
                lineHeight = 25.sp,
                color = LoginSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.height(76.dp))

            // Tarjeta principal.
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
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    // Correo electrónico.
                    Text(
                        text = "Correo electrónico",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = LoginSecondary
                    )

                    OutlinedTextField(
                        value = correo,
                        onValueChange = {
                            correo = it
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text("ejemplo@catalogo.com")
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Correo electrónico",
                                tint = LoginSecondary
                            )
                        },
                        singleLine = true,
                        enabled = !cargando,
                        shape = RoundedCornerShape(16.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = LoginInputBackground,
                            unfocusedContainerColor = LoginInputBackground,
                            disabledContainerColor = LoginInputBackground,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            disabledBorderColor = Color.Transparent
                        )
                    )

                    // Contraseña.
                    Text(
                        text = "Contraseña",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = LoginSecondary
                    )

                    OutlinedTextField(
                        value = clave,
                        onValueChange = {
                            clave = it
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text("Ingresa tu contraseña")
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Contraseña",
                                tint = LoginSecondary
                            )
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    mostrarClave = !mostrarClave
                                }
                            ) {
                                Icon(
                                    imageVector = if (mostrarClave) {
                                        Icons.Default.Visibility
                                    } else {
                                        Icons.Default.VisibilityOff
                                    },
                                    contentDescription = if (mostrarClave) {
                                        "Ocultar contraseña"
                                    } else {
                                        "Mostrar contraseña"
                                    },
                                    tint = LoginSecondary
                                )
                            }
                        },
                        visualTransformation = if (mostrarClave) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        singleLine = true,
                        enabled = !cargando,
                        shape = RoundedCornerShape(16.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = LoginInputBackground,
                            unfocusedContainerColor = LoginInputBackground,
                            disabledContainerColor = LoginInputBackground,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            disabledBorderColor = Color.Transparent
                        )
                    )

                    // Mensaje de error.
                    if (error != null) {

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFFFEDEC)
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 0.dp
                            )
                        ) {
                            Text(
                                text = error,
                                modifier = Modifier.padding(16.dp),
                                color = Color(0xFFBA1A1A),
                                fontSize = 14.sp
                            )
                        }

                    } else {

                        // Panel informativo.
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = LoginInfoBackground
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 0.dp
                            )
                        ) {

                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Icon(
                                    imageVector = Icons.Default.VerifiedUser,
                                    contentDescription = null,
                                    tint = LoginPrimary
                                )

                                Column(
                                    modifier = Modifier.padding(start = 12.dp)
                                ) {

                                    Text(
                                        text = "Acceso seguro",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = LoginText
                                    )

                                    Text(
                                        text = "Inicia sesión con las credenciales proporcionadas para tu cuenta.",
                                        fontSize = 13.sp,
                                        lineHeight = 19.sp,
                                        color = LoginSecondary
                                    )
                                }
                            }
                        }
                    }

                    // Botón de inicio de sesión.
                    Button(
                        onClick = {
                            viewModel.iniciarSesion(
                                correo = correo.trim(),
                                clave = clave
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = !cargando,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LoginPrimary,
                            disabledContainerColor = LoginPrimary.copy(
                                alpha = 0.6f
                            )
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 4.dp
                        )
                    ) {

                        if (cargando) {

                            CircularProgressIndicator(
                                modifier = Modifier.height(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )

                        } else {

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {

                                Text(
                                    text = "Iniciar sesión",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(
                                    modifier = Modifier.padding(horizontal = 5.dp)
                                )

                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
