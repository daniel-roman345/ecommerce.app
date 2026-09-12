// ProductListScreen.kt — PANTALLA 2: la lista del inventario. Es el corazón de la app.
// Consume GET /api/products/ y contempla los CUATRO estados que exige la guía:
// cargando, con datos, vacía y con error.
package com.inventario.tiendabarrio

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inventario.tiendabarrio.data.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)   // TopAppBar sigue marcada como experimental
@Composable
fun ProductListScreen(
    onAbrirDetalle: (Int) -> Unit = {},   // el id del producto tocado
    onNuevoProducto: () -> Unit = {},
    onVerCategorias: () -> Unit = {},
    onSalir: () -> Unit = {},
    recargar: Int = 0                     // sube tras crear/editar/eliminar: fuerza recarga
) {
    // --- ESTADO ---
    var productos by remember { mutableStateOf<List<Product>>(emptyList()) }
    var busqueda by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }
    var vacia by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    fun cargar() {
        cargando = true
        error = ""
        vacia = false
        scope.launch {
            try {
                val respuesta = RetrofitClient.api.getProducts(
                    page = 1,
                    perPage = 30,
                    search = busqueda.ifBlank { null }  // null => Retrofit omite ?search=
                )
                if (respuesta.isSuccessful) {
                    productos = respuesta.body()?.products ?: emptyList()
                    vacia = productos.isEmpty()     // estado VACÍO, distinto de error
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

    // LaunchedEffect se ejecuta cuando la pantalla aparece y cada vez que cambia
    // su "llave". Al usar `recargar` como llave, la lista se refresca sola después
    // de crear o editar un producto, sin que el usuario tenga que hacer nada.
    LaunchedEffect(recargar) { cargar() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (Session.estaAutenticado()) "Inventario — ${Session.nombre()}"
                        else "Inventario (consulta)"
                    )
                },
                actions = {
                    TextButton(onClick = onVerCategorias) { Text("Categorías") }
                    TextButton(onClick = { Session.cerrar(); onSalir() }) {
                        Text(if (Session.estaAutenticado()) "Salir" else "Ingresar")
                    }
                }
            )
        },
        floatingActionButton = {
            // PERMISOS POR ROL: el botón de crear SOLO existe para el administrador.
            // (Ocultar no es seguridad de verdad: ver el README, sección de dos capas.)
            if (Session.esAdministrador()) {
                ExtendedFloatingActionButton(
                    onClick = onNuevoProducto,
                    text = { Text("Nuevo") },
                    icon = { Text("+") }
                )
            }
        }
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = busqueda,
                    onValueChange = { busqueda = it },
                    label = { Text("Buscar por nombre") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                Button(onClick = { cargar() }, enabled = !cargando) { Text("Buscar") }
            }

            Spacer(Modifier.height(12.dp))

            // --- LOS CUATRO ESTADOS ---
            when {
                // 1) CARGANDO
                cargando -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(Modifier.height(12.dp))
                        Text("Consultando el inventario...")
                    }
                }

                // 2) ERROR (de red o de permisos)
                error.isNotEmpty() -> Column(
                    Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(error)
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { cargar() }) { Text("Reintentar") }
                }

                // 3) VACÍA: no es un error, es que no hay nada que mostrar.
                vacia -> Column(
                    Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("No hay productos que coincidan con la búsqueda.")
                    if (busqueda.isNotBlank()) {
                        Spacer(Modifier.height(12.dp))
                        TextButton(onClick = { busqueda = ""; cargar() }) {
                            Text("Ver todo el inventario")
                        }
                    }
                }

                // 4) CON DATOS
                else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(productos) { producto ->
                        ProductoItem(producto) { onAbrirDetalle(producto.id_Product) }
                    }
                    item { Spacer(Modifier.height(80.dp)) }  // aire bajo el botón flotante
                }
            }
        }
    }
}

// Una fila de la lista. Separarlo en su propia función hace la pantalla legible.
@Composable
fun ProductoItem(producto: Product, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }   // toda la tarjeta es tocable
    ) {
        Row(
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    producto.ProductName ?: "Sin nombre",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                val stock = producto.Stock ?: 0
                Text(
                    if (stock > 0) "Disponibles: $stock" else "AGOTADO",
                    fontSize = 13.sp
                )
            }
            Text("$${String.format("%.2f", producto.Price ?: 0.0)}", fontWeight = FontWeight.Bold)
        }
    }
}
