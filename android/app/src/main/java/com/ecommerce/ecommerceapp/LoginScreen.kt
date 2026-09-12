// LoginScreen.kt — la pantalla de inicio de sesión, dibujada con Jetpack Compose.
// En Compose la interfaz NO se hace con XML: se escribe con funciones @Composable.
package com.ecommerce.ecommerceapp

import androidx.compose.foundation.layout.*    // Column, Spacer, padding, fillMaxSize...
import androidx.compose.material3.*            // Button, OutlinedTextField, Text...
import androidx.compose.runtime.*              // remember, mutableStateOf, Composable...
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecommerce.ecommerceapp.data.LoginRequest
import com.ecommerce.ecommerceapp.data.RetrofitClient
import kotlinx.coroutines.launch

// @Composable convierte esta función en una "pieza de interfaz" que Compose puede
// dibujar y redibujar sola cuando cambian sus datos (eso se llama recomposición).
//
// GUÍA 4: ahora la pantalla recibe onLoginSuccess, una FUNCIÓN que avisa "el login
// salió bien" y entrega el nombre del usuario. Así esta pantalla NO decide a dónde
// ir después (eso es trabajo de MainActivity): solo informa. A ese patrón se le
// llama "elevar el estado" y evita que las pantallas queden amarradas entre sí.
@Composable
fun LoginScreen(
    onLoginSuccess: (String) -> Unit = {}
) {
    // --- ESTADO de la pantalla ---
    // remember: hace que el valor SOBREVIVA a las recomposiciones (no se reinicia
    //           cada vez que la pantalla se redibuja).
    // mutableStateOf: crea un valor OBSERVABLE; cuando cambia, Compose redibuja lo
    //                 que dependa de él.
    // by: nos deja usar la variable directamente (email) en vez de email.value.
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }      // texto con el resultado a mostrar
    var loading by remember { mutableStateOf(false) }  // true mientras esperamos al servidor

    // Ámbito de corrutinas atado a esta pantalla: aquí lanzamos la llamada de red.
    val scope = rememberCoroutineScope()

    // Column coloca sus elementos hijos uno debajo del otro (en columna).
    Column(
        modifier = Modifier
            .fillMaxSize()   // ocupa toda la pantalla
            .padding(24.dp), // margen interno de 24 dp
        verticalArrangement = Arrangement.Center,        // centra en vertical
        horizontalAlignment = Alignment.CenterHorizontally // centra en horizontal
    ) {
        Text("Iniciar sesión", fontSize = 26.sp)  // título
        Spacer(Modifier.height(24.dp))            // espacio en blanco

        // Campo de correo. value = lo que se ve; onValueChange = se dispara con cada
        // tecla y actualiza la variable email (lo que provoca la recomposición).
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))

        // Campo de contraseña. visualTransformation oculta el texto con puntos.
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(24.dp))

        // Botón "Entrar".
        Button(
            onClick = {
                // Validación mínima antes de llamar al servidor.
                if (email.isBlank() || password.isBlank()) {
                    result = "Escribe email y contraseña"
                    return@Button  // sale del onClick sin continuar
                }
                loading = true
                result = ""
                // Lanzamos la llamada de red en una corrutina (no bloquea la interfaz).
                scope.launch {
                    try {
                        // Al ser "suspend", el hilo no se bloquea: espera la respuesta
                        // y luego continúa en la línea siguiente.
                        val response = RetrofitClient.api.login(
                            LoginRequest(Email = email, PasswoRDkey = password)
                        )
                        if (response.isSuccessful) {  // ¿código HTTP 2xx (éxito)?
                            val body = response.body()  // cuerpo ya traducido a LoginResponse
                            // GUÍA 4: en vez de quedarnos mostrando el token, avisamos
                            // que el login funcionó para pasar a la lista de productos.
                            onLoginSuccess(body?.user?.UserName ?: "")
                        } else {
                            // El servidor respondió pero rechazó (ej. 401: credenciales malas).
                            result = "Credenciales inválidas (código ${response.code()})"
                        }
                    } catch (e: Exception) {
                        // No se pudo ni conectar (backend apagado, URL mal, sin red...).
                        result = "⚠️ Error de conexión: ${e.message}"
                    } finally {
                        loading = false  // pase lo que pase, apagamos el "cargando"
                    }
                }
            },
            enabled = !loading,  // deshabilita el botón mientras carga
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (loading) "Entrando..." else "Entrar")
        }

        // Mostramos el resultado solo si hay algo que mostrar.
        if (result.isNotEmpty()) {
            Spacer(Modifier.height(24.dp))
            Text(result)
        }
    }
}
