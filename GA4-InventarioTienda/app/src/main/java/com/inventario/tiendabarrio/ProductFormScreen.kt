// ProductFormScreen.kt — PANTALLA 4: el formulario que ESCRIBE en el servidor.
// Sirve para dos cosas con el mismo código:
//   - productId = null  -> crear un producto nuevo  (POST /api/products/)
//   - productId = 7     -> editar el producto 7     (PUT  /api/products/7)
// Es la pantalla que convierte la app en una herramienta y no en un catálogo.
package com.inventario.tiendabarrio

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inventario.tiendabarrio.data.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductFormScreen(
    productId: Int? = null,     // null = crear, con valor = editar
    onAtras: () -> Unit = {},
    onGuardado: () -> Unit = {}
) {
    val editando = productId != null

    // --- Campos del formulario ---
    // Precio y stock se manejan como TEXTO mientras se escriben: si fueran Double
    // no se podría borrar el último dígito ni escribir "12." a medias.
    var nombre by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var categoriasElegidas by remember { mutableStateOf(setOf<Int>()) }

    // --- Datos auxiliares y estados ---
    var categorias by remember { mutableStateOf<List<Category>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }
    var guardando by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

    // --- Errores de validación, uno por campo ---
    var errorNombre by remember { mutableStateOf("") }
    var errorPrecio by remember { mutableStateOf("") }
    var errorStock by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    // Al aparecer la pantalla: traemos las categorías y, si estamos editando,
    // también el producto para rellenar los campos con sus valores actuales.
    LaunchedEffect(productId) {
        cargando = true
        error = ""
        try {
            val rCat = RetrofitClient.api.getCategories()
            if (rCat.isSuccessful) categorias = rCat.body()?.categories ?: emptyList()

            if (productId != null) {
                val rProd = RetrofitClient.api.getProduct(productId)
                if (rProd.isSuccessful) {
                    val p = rProd.body()?.product
                    nombre = p?.ProductName.orEmpty()
                    precio = p?.Price?.toString().orEmpty()
                    stock = p?.Stock?.toString().orEmpty()
                    categoriasElegidas = p?.categories?.map { it.id_Category }?.toSet() ?: emptySet()
                } else {
                    error = mensajeDeError(rProd.code())
                }
            }
        } catch (e: Exception) {
            error = mensajeSinConexion(e.message)
        } finally {
            cargando = false
        }
    }

    // VALIDACIÓN EN LA APP: no mandamos al servidor lo que ya sabemos que está mal.
    fun datosValidos(): Boolean {
        errorNombre = when {
            nombre.isBlank() -> "El nombre es obligatorio"
            nombre.trim().length < 3 -> "Escribe al menos 3 caracteres"
            else -> ""
        }
        val precioNum = precio.replace(",", ".").toDoubleOrNull()
        errorPrecio = when {
            precio.isBlank() -> "El precio es obligatorio"
            precioNum == null -> "El precio debe ser un número"
            precioNum <= 0 -> "El precio debe ser mayor que cero"
            else -> ""
        }
        val stockNum = stock.toIntOrNull()
        errorStock = when {
            stock.isBlank() -> "Las existencias son obligatorias"
            stockNum == null -> "Las existencias deben ser un número entero"
            stockNum < 0 -> "Las existencias no pueden ser negativas"
            else -> ""
        }
        return errorNombre.isEmpty() && errorPrecio.isEmpty() && errorStock.isEmpty()
    }

    fun guardar() {
        if (!datosValidos()) return
        guardando = true
        error = ""
        scope.launch {
            try {
                val cuerpo = ProductRequest(
                    ProductName = nombre.trim(),
                    Price = precio.replace(",", ".").toDouble(),
                    Stock = stock.toInt(),
                    categories = categoriasElegidas.toList()
                )
                // Mismo cuerpo, distinto verbo según estemos creando o editando.
                val respuesta = if (productId == null)
                    RetrofitClient.api.createProduct(Session.bearer(), cuerpo)
                else
                    RetrofitClient.api.updateProduct(Session.bearer(), productId, cuerpo)

                if (respuesta.isSuccessful) {
                    onGuardado()   // volvemos a la lista, que se recarga sola
                } else {
                    error = mensajeDeError(respuesta.code())  // 401, 403, 404, 400...
                }
            } catch (e: Exception) {
                error = mensajeSinConexion(e.message)
            } finally {
                guardando = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (editando) "Editar producto" else "Nuevo producto") },
                navigationIcon = { TextButton(onClick = onAtras) { Text("Atrás") } }
            )
        }
    ) { innerPadding ->

        // Doble control de permisos dentro de la propia app: aunque alguien llegue
        // a esta ruta sin ser administrador, no ve el formulario.
        if (!Session.esAdministrador()) {
            Column(
                Modifier.fillMaxSize().padding(innerPadding).padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(mensajeDeError(403))
                Spacer(Modifier.height(16.dp))
                Button(onClick = onAtras) { Text("Volver") }
            }
            return@Scaffold
        }

        if (cargando) {
            Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())  // el teclado no tapa los campos
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it; errorNombre = "" },
                label = { Text("Nombre del producto") },
                singleLine = true,
                isError = errorNombre.isNotEmpty(),
                supportingText = { if (errorNombre.isNotEmpty()) Text(errorNombre) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = precio,
                onValueChange = { precio = it; errorPrecio = "" },
                label = { Text("Precio") },
                singleLine = true,
                isError = errorPrecio.isNotEmpty(),
                supportingText = { if (errorPrecio.isNotEmpty()) Text(errorPrecio) },
                // Teclado numérico con decimales: menos errores de digitación.
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = stock,
                onValueChange = { stock = it; errorStock = "" },
                label = { Text("Existencias") },
                singleLine = true,
                isError = errorStock.isNotEmpty(),
                supportingText = { if (errorStock.isNotEmpty()) Text(errorStock) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))
            Text("Categorías", fontSize = 16.sp)
            Spacer(Modifier.height(4.dp))

            if (categorias.isEmpty()) {
                Text("No se pudieron cargar las categorías.", fontSize = 13.sp)
            } else {
                categorias.forEach { categoria ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = categoriasElegidas.contains(categoria.id_Category),
                            onCheckedChange = { marcado ->
                                // Un Set no admite repetidos: sumamos o restamos el id.
                                categoriasElegidas = if (marcado)
                                    categoriasElegidas + categoria.id_Category
                                else
                                    categoriasElegidas - categoria.id_Category
                            }
                        )
                        Text(categoria.CategoryName ?: "Sin nombre")
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { guardar() },
                enabled = !guardando,   // evita envíos duplicados por doble toque
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    when {
                        guardando -> "Guardando..."
                        editando -> "Guardar cambios"
                        else -> "Crear producto"
                    }
                )
            }

            if (error.isNotEmpty()) {
                Spacer(Modifier.height(16.dp))
                Card(Modifier.fillMaxWidth()) { Text(error, Modifier.padding(16.dp)) }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
