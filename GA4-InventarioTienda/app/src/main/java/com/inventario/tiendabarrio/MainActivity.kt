// MainActivity.kt — el punto de entrada de la app y el MAPA DE NAVEGACIÓN.
// Aquí se registran todas las rutas del NavHost, tal como quedaron en el diseño
// de la actividad GA4-AA2-EV02.
package com.inventario.tiendabarrio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.inventario.tiendabarrio.ui.theme.InventarioTiendaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InventarioTiendaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

// Las rutas se declaran una sola vez como constantes: si mañana cambia un nombre,
// se cambia aquí y no hay que buscarlo repetido por todo el proyecto.
object Rutas {
    const val LOGIN = "login"
    const val LISTA = "productos"
    const val DETALLE = "detalle/{id}"        // {id} = trozo variable de la ruta
    const val FORMULARIO = "formulario?id={id}"  // ?id= = argumento opcional
    const val CATEGORIAS = "categorias"

    // Funciones de ayuda para construir la ruta con el valor real.
    fun detalle(id: Int) = "detalle/$id"
    fun formularioNuevo() = "formulario"
    fun formularioEditar(id: Int) = "formulario?id=$id"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // Contador que cambia cada vez que se crea, edita o elimina un producto.
    // La lista lo usa como llave de su LaunchedEffect, así se refresca sola.
    var versionDatos by remember { mutableStateOf(0) }

    NavHost(
        navController = navController,
        // startDestination: la app arranca en el acceso, porque el rol del usuario
        // decide qué podrá hacer en el resto de las pantallas.
        startDestination = Rutas.LOGIN
    ) {

        // ---------- 1. ACCESO ----------
        composable(Rutas.LOGIN) {
            LoginScreen(
                onLoginOk = {
                    navController.navigate(Rutas.LISTA) {
                        // Borra el login del historial: "atrás" no vuelve al formulario.
                        popUpTo(Rutas.LOGIN) { inclusive = true }
                    }
                },
                onEntrarSinSesion = {
                    navController.navigate(Rutas.LISTA) {
                        popUpTo(Rutas.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // ---------- 2. LISTA ----------
        composable(Rutas.LISTA) {
            ProductListScreen(
                recargar = versionDatos,   // al cambiar, la lista se refresca sola
                onAbrirDetalle = { id -> navController.navigate(Rutas.detalle(id)) },
                onNuevoProducto = { navController.navigate(Rutas.formularioNuevo()) },
                onVerCategorias = { navController.navigate(Rutas.CATEGORIAS) },
                onSalir = {
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(Rutas.LISTA) { inclusive = true }
                    }
                }
            )
        }

        // ---------- 3. DETALLE ----------
        composable(
            route = Rutas.DETALLE,
            // Declaramos el argumento y su tipo para poder leerlo como Int.
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            ProductDetailScreen(
                productId = id,
                // popBackStack() = volver a la pantalla anterior del historial.
                onAtras = { navController.popBackStack() },
                onEditar = { idEditar -> navController.navigate(Rutas.formularioEditar(idEditar)) },
                onEliminado = {
                    versionDatos++            // marca que los datos cambiaron
                    navController.popBackStack(Rutas.LISTA, inclusive = false)
                }
            )
        }

        // ---------- 4. FORMULARIO (crear / editar) ----------
        composable(
            route = Rutas.FORMULARIO,
            // defaultValue = -1 significa "no vino id", es decir, estamos creando.
            arguments = listOf(navArgument("id") {
                type = NavType.IntType
                defaultValue = -1
            })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: -1
            ProductFormScreen(
                productId = if (id > 0) id else null,
                onAtras = { navController.popBackStack() },
                onGuardado = {
                    versionDatos++
                    navController.popBackStack(Rutas.LISTA, inclusive = false)
                }
            )
        }

        // ---------- 5. CATEGORÍAS ----------
        composable(Rutas.CATEGORIAS) {
            CategoryListScreen(onAtras = { navController.popBackStack() })
        }
    }
}
