// LoginScreen.kt — PANTALLA 1: acceso a la aplicación.
// Envía las credenciales al backend, guarda el token en Session y avisa que el
// ingreso fue correcto. Si algo falla, muestra un mensaje claro y NO cierra la app.
package com.inventario.tiendabarrio

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inventario.tiendabarrio.data.*
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginOk: () -> Unit = {},      // se dispara cuando el ingreso es correcto
    onEntrarSinSesion: () -> Unit = {}  // ver el inventario sin iniciar sesión (solo lectura)
) {
    // --- ESTADO de la pantalla ---
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

    // Errores de VALIDACIÓN, uno por campo: se muestran debajo del campo que falla.
    var errorEmail by remember { mutableStateOf("") }
    var errorPassword by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    // Validación en la app: no gastamos una petición si ya sabemos que está mal.
    fun datosValidos(): Boolean {
        errorEmail = when {
            email.isBlank() -> "Escribe tu correo"
            !email.contains("@") || !email.contains(".") -> "El correo no tiene un formato válido"
            else -> ""
        }
        errorPassword = if (password.isBlank()) "Escribe tu contraseña" else ""
        return errorEmail.isEmpty() && errorPassword.isEmpty()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Inventario de la tienda", fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        Text("Ingresa para administrar los productos", fontSize = 14.sp)
        Spacer(Modifier.height(28.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it; errorEmail = "" },
            label = { Text("Correo") },
            singleLine = true,
            isError = errorEmail.isNotEmpty(),          // pinta el campo de rojo
            supportingText = { if (errorEmail.isNotEmpty()) Text(errorEmail) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it; errorPassword = "" },
            label = { Text("Contraseña") },
            singleLine = true,
            isError = errorPassword.isNotEmpty(),
            supportingText = { if (errorPassword.isNotEmpty()) Text(errorPassword) },
            visualTransformation = PasswordVisualTransformation(),  // oculta el texto
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                if (!datosValidos()) return@Button
                loading = true
                error = ""
                // La llamada de red va dentro de una corrutina: no congela la pantalla.
                scope.launch {
                    try {
                        val respuesta = RetrofitClient.api.login(
                            LoginRequest(Email = email.trim(), PasswoRDkey = password)
                        )
                        if (respuesta.isSuccessful) {
                            val cuerpo = respuesta.body()
                            // Guardamos la sesión para que el resto de pantallas
                            // pueda usar el token y conocer el rol.
                            Session.token = cuerpo?.token
                            Session.user = cuerpo?.user
                            onLoginOk()
                        } else {
                            // 401 = credenciales inválidas. Traducido a lenguaje humano.
                            error = if (respuesta.code() == 401)
                                "Correo o contraseña incorrectos."
                            else
                                mensajeDeError(respuesta.code())
                        }
                    } catch (e: Exception) {
                        // No hubo ni respuesta: servidor apagado, sin red, URL mal.
                        error = mensajeSinConexion(e.message)
                    } finally {
                        loading = false  // se apague como se apague, quitamos el "cargando"
                    }
                }
            },
            // Deshabilitado mientras carga: evita que un doble toque mande dos peticiones.
            enabled = !loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (loading) "Ingresando..." else "Ingresar")
        }

        Spacer(Modifier.height(8.dp))

        // Matriz de permisos: un visitante SÍ puede consultar el inventario,
        // pero no podrá crear ni editar (esas opciones ni siquiera se le muestran).
        TextButton(onClick = { Session.cerrar(); onEntrarSinSesion() }) {
            Text("Entrar sin iniciar sesión (solo consultar)")
        }

        if (error.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            Card(Modifier.fillMaxWidth()) {
                Text(error, Modifier.padding(16.dp))
            }
        }
    }
}
