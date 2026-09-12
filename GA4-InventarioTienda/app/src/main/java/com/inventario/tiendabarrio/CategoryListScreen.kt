// CategoryListScreen.kt — PANTALLA 5: las categorías del inventario.
// Es la pantalla más simple de la app y sirve para demostrar que se consume un
// SEGUNDO endpoint (GET /api/categories/) con los mismos cuatro estados.
package com.inventario.tiendabarrio

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.inventario.tiendabarrio.data.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryListScreen(onAtras: () -> Unit = {}) {

    var categorias by remember { mutableStateOf<List<Category>>(emptyList()) }
    var cargando by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    fun cargar() {
        cargando = true
        error = ""
        scope.launch {
            try {
                val respuesta = RetrofitClient.api.getCategories()
                if (respuesta.isSuccessful) {
                    categorias = respuesta.body()?.categories ?: emptyList()
                } else {
                    error = mensajeDeError(respuesta.code())
                }
            } catch (e: Exception) {
                error = mensajeSinConexion(e.message)
            } finally {
                cargando = false
            }
        }
    }

    LaunchedEffect(Unit) { cargar() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Categorías") },
                navigationIcon = { TextButton(onClick = onAtras) { Text("Atrás") } }
            )
        }
    ) { innerPadding ->
        Box(Modifier.fillMaxSize().padding(innerPadding).padding(16.dp)) {
            when {
                cargando -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }

                error.isNotEmpty() -> Column(
                    Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(error)
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { cargar() }) { Text("Reintentar") }
                }

                categorias.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Todavía no hay categorías registradas.")
                }

                else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categorias) { categoria ->
                        Card(Modifier.fillMaxWidth()) {
                            Text(
                                "${categoria.id_Category} · ${categoria.CategoryName ?: "Sin nombre"}",
                                Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
