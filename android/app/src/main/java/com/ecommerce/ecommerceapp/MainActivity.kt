// MainActivity.kt — la primera pantalla que abre la app. Aquí "montamos" Compose.
package com.ecommerce.ecommerceapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent   // puente entre Android y Compose
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*             // remember, mutableStateOf, Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost    // el "mapa" de pantallas de la app
import androidx.navigation.compose.composable // cada parada de ese mapa
import androidx.navigation.compose.rememberNavController
import com.ecommerce.ecommerceapp.ui.theme.EcommerceAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContent define la interfaz de la Activity USANDO Compose.
        setContent {
            EcommerceAppTheme {  // aplica los colores/estilos del proyecto
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()  // GUÍA 4: ya no una sola pantalla, sino varias
                }
            }
        }
    }
}

// GUÍA 4 — NAVEGACIÓN
// Antes mostrábamos LoginScreen directamente. Ahora la app tiene DOS pantallas y
// necesitamos movernos entre ellas: login -> productos.
@Composable
fun AppNavigation() {
    // navController: el "control remoto" que manda a cambiar de pantalla.
    val navController = rememberNavController()

    // Guardamos aquí el nombre del usuario para mostrarlo en la pantalla siguiente.
    var userName by remember { mutableStateOf("") }

    // NavHost = el contenedor donde se dibuja la pantalla actual.
    // startDestination = la ruta con la que arranca la app.
    NavHost(navController = navController, startDestination = "login") {

        // ----- Ruta "login" -----
        composable("login") {
            LoginScreen(
                onLoginSuccess = { nombre ->
                    userName = nombre
                    // navigate: ir a la ruta "products".
                    // popUpTo("login") { inclusive = true } borra el login del historial,
                    // así al pulsar "atrás" el usuario NO regresa al formulario ya usado.
                    navController.navigate("products") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        // ----- Ruta "products" -----
        composable("products") {
            ProductsScreen(
                userName = userName,
                onLogout = {
                    userName = ""
                    // Volvemos al login y limpiamos el historial de productos.
                    navController.navigate("login") {
                        popUpTo("products") { inclusive = true }
                    }
                }
            )
        }
    }
}
