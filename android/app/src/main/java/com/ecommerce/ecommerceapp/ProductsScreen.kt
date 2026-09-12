// ProductsScreen.kt — GUÍA 4: la pantalla que muestra la LISTA DE PRODUCTOS.
// Pide los productos al backend (GET /api/products/) y los dibuja en una lista
// que se puede desplazar. Todo con Jetpack Compose, sin XML.
package com.ecommerce.ecommerceapp

import androidx.compose.foundation.layout.*      // Column, Row, Spacer, padding...
import androidx.compose.foundation.lazy.LazyColumn  // lista larga y eficiente
import androidx.compose.foundation.lazy.items      // para recorrer la lista de datos
import androidx.compose.material3.*               // Card, Text, Button, TopAppBar...
import androidx.compose.runtime.*                 // remember, mutableStateOf, LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecommerce.ecommerceapp.data.Product
import com.ecommerce.ecommerceapp.data.RetrofitClient
import kotlinx.coroutines.launch

// El parámetro onLogout es una FUNCIÓN que recibimos desde afuera: la pantalla no
// sabe (ni le importa) qué pasa al cerrar sesión, solo avisa. Quien la dibuja decide.
@OptIn(ExperimentalMaterial3Api::class)  // TopAppBar todavía está marcada como experimental
@Composable
fun ProductsScreen(
    userName: String = "",      // nombre del usuario que inició sesión (para el saludo)
    onLogout: () -> Unit = {}   // qué hacer cuando pulsen "Salir"
) {
    // --- ESTADO de la pantalla ---
    // Igual que en el login: remember + mutableStateOf = valores que, al cambiar,
    // hacen que Compose vuelva a dibujar SOLO lo que depende de ellos.
    var products by remember { mutableStateOf<List<Product>>(emptyList()) } // lista recibida
    var search by remember { mutableStateOf("") }      // texto del buscador
    var loading by remember { mutableStateOf(false) }  // true mientras esperamos al servidor
    var error by remember { mutableStateOf("") }       // mensaje de error, si lo hay

    val scope = rememberCoroutineScope()  // ámbito para lanzar las llamadas de red

    // Función que hace la petición al backend. La definimos una vez y la reutilizamos
    // (al entrar a la pantalla, al buscar y al pulsar "Reintentar").
    fun cargarProductos() {
        loading = true
        error = ""
        scope.launch {
            try {
                // Si el buscador está vacío mandamos null => Retrofit no agrega ?search=
                val response = RetrofitClient.api.getProducts(
                    page = 1,
                    perPage = 20,
                    search = search.ifBlank { null }
                )
                if (response.isSuccessful) {                 // ¿código HTTP 2xx?
                    products = response.body()?.products ?: emptyList()
                    if (products.isEmpty()) error = "No se encontraron productos"
                } else {
                    error = "El servidor respondió con el código ${response.code()}"
                }
            } catch (e: Exception) {
                // No se pudo ni conectar (backend apagado, URL mal, sin red...).
                error = "⚠️ Error de conexión: ${e.message}"
            } finally {
                loading = false  // pase lo que pase, apagamos el "cargando"
            }
        }
    }

    // LaunchedEffect(Unit): ejecuta este bloque UNA sola vez, cuando la pantalla
    // aparece por primera vez. Sirve para cargar datos al entrar.
    LaunchedEffect(Unit) { cargarProductos() }

    // Scaffold es el "esqueleto" de una pantalla Material: barra superior + contenido.
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (userName.isBlank()) "Productos" else "Productos — $userName")
                },
                actions = {
                    // Botón de texto a la derecha de la barra.
                    TextButton(onClick = onLogout) { Text("Salir") }
                }
            )
        }
    ) { innerPadding ->  // innerPadding = espacio que ocupa la barra; hay que respetarlo
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)  // para que el contenido no quede debajo de la barra
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(12.dp))

            // --- Buscador ---
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    label = { Text("Buscar producto") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)  // ocupa todo el ancho que sobre
                )
                Spacer(Modifier.width(8.dp))
                Button(onClick = { cargarProductos() }, enabled = !loading) {
                    Text("Buscar")
                }
            }

            Spacer(Modifier.height(12.dp))

            // --- Contenido: cargando / error / lista ---
            when {
                // 1) Mientras esperamos al servidor: rueda de carga centrada.
                loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                // 2) Si algo falló: mensaje + botón para volver a intentar.
                error.isNotEmpty() -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(error)
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = { cargarProductos() }) { Text("Reintentar") }
                    }
                }
                // 3) Todo bien: dibujamos la lista.
                else -> {
                    // LazyColumn solo crea en memoria los elementos VISIBLES: por eso
                    // aguanta listas largas sin ponerse lenta (es como un RecyclerView).
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(products) { product ->
                            ProductItem(product)  // una tarjeta por producto
                        }
                        // Espacio final para que la última tarjeta no quede pegada abajo.
                        item { Spacer(Modifier.height(16.dp)) }
                    }
                }
            }
        }
    }
}

// Componente pequeño y reutilizable: cómo se ve UN producto en la lista.
// Partir la interfaz en funciones @Composable pequeñas la hace más fácil de leer.
@Composable
fun ProductItem(product: Product) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = product.ProductName ?: "Sin nombre",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))

            // "%.2f" formatea el precio con dos decimales: 1099.9 -> 1099.90
            Text("Precio: $${String.format("%.2f", product.Price ?: 0.0)}")

            val stock = product.Stock ?: 0
            Text(if (stock > 0) "Stock: $stock unidades" else "Agotado")

            // Las categorías llegan como una lista; las unimos separadas por comas.
            val categorias = product.categories
                ?.mapNotNull { it.CategoryName }
                ?.joinToString(", ")
                .orEmpty()
            if (categorias.isNotEmpty()) {
                Spacer(Modifier.height(4.dp))
                Text("Categorías: $categorias", fontSize = 13.sp)
            }
        }
    }
}
