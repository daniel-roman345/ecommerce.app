// ProductDetailScreen.kt — PANTALLA 3: el detalle de un producto.
// Recibe el id por la RUTA ("detalle/{id}") y lo consulta con GET /api/products/{id}.
// Desde aquí el administrador puede editar o eliminar.
package com.inventario.tiendabarrio

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inventario.tiendabarrio.data.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: Int,
    onAtras: () -> Unit = {},
    onEditar: (Int) -> Unit = {},
    onEliminado: () -> Unit = {}
) {
    var producto by remember { mutableStateOf<Product?>(null) }
    var cargando by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }
    var confirmarBorrado by remember { mutableStateOf(false) }
    var borrando by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    fun cargar() {
        cargando = true
        error = ""
        scope.launch {
            try {
                val respuesta = RetrofitClient.api.getProduct(productId)
                if (respuesta.isSuccessful) {
                    producto = respuesta.body()?.product
                    if (producto == null) error = mensajeDeError(404)
                } else {
                    error = mensajeDeError(respuesta.code())   // 404 si ya no existe
                }
            } catch (e: Exception) {
                error = mensajeSinConexion(e.message)
            } finally {
                cargando = false
            }
        }
    }

    // La llave es el id: si se navega al detalle de otro producto, vuelve a cargar.
    LaunchedEffect(productId) { cargar() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del producto") },
                navigationIcon = { TextButton(onClick = onAtras) { Text("Atrás") } }
            )
        }
    ) { innerPadding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
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
                    Spacer(Modifier.height(8.dp))
                    TextButton(onClick = onAtras) { Text("Volver a la lista") }
                }

                producto != null -> {
                    val p = producto!!
                    Column(Modifier.fillMaxSize()) {
                        Text(p.ProductName ?: "Sin nombre", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(16.dp))

                        FilaDato("Código interno", "#${p.id_Product}")
                        FilaDato("Precio", "$${String.format("%.2f", p.Price ?: 0.0)}")
                        FilaDato("Existencias", "${p.Stock ?: 0} unidades")
                        FilaDato(
                            "Categorías",
                            p.categories?.mapNotNull { it.CategoryName }?.joinToString(", ")
                                ?.ifBlank { "Sin categoría" } ?: "Sin categoría"
                        )

                        Spacer(Modifier.height(32.dp))

                        // PERMISOS POR ROL: editar y eliminar solo para el administrador.
                        if (Session.esAdministrador()) {
                            Button(
                                onClick = { onEditar(p.id_Product) },
                                modifier = Modifier.fillMaxWidth()
                            ) { Text("Editar producto") }

                            Spacer(Modifier.height(8.dp))

                            OutlinedButton(
                                onClick = { confirmarBorrado = true },
                                enabled = !borrando,
                                modifier = Modifier.fillMaxWidth()
                            ) { Text(if (borrando) "Eliminando..." else "Eliminar producto") }
                        } else {
                            Text(
                                "Solo un administrador puede modificar este producto.",
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            // Diálogo de confirmación: eliminar es irreversible, se pregunta antes.
            if (confirmarBorrado) {
                AlertDialog(
                    onDismissRequest = { confirmarBorrado = false },
                    title = { Text("¿Eliminar el producto?") },
                    text = { Text("Esta acción no se puede deshacer.") },
                    confirmButton = {
                        TextButton(onClick = {
                            confirmarBorrado = false
                            borrando = true
                            scope.launch {
                                try {
                                    val r = RetrofitClient.api.deleteProduct(
                                        token = Session.bearer(),   // token en el ENCABEZADO
                                        id = productId
                                    )
                                    if (r.isSuccessful) {
                                        onEliminado()   // vuelve a la lista y la recarga
                                    } else {
                                        // 400 lo devuelve el backend si el producto
                                        // tiene ventas asociadas.
                                        error = r.body()?.error ?: mensajeDeError(r.code())
                                    }
                                } catch (e: Exception) {
                                    error = mensajeSinConexion(e.message)
                                } finally {
                                    borrando = false
                                }
                            }
                        }) { Text("Sí, eliminar") }
                    },
                    dismissButton = {
                        TextButton(onClick = { confirmarBorrado = false }) { Text("Cancelar") }
                    }
                )
            }
        }
    }
}

// Fila "etiqueta: valor", reutilizada cuatro veces arriba.
@Composable
private fun FilaDato(etiqueta: String, valor: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Text("$etiqueta:", Modifier.weight(1f), fontWeight = FontWeight.Bold)
        Text(valor, Modifier.weight(1f))
    }
}
