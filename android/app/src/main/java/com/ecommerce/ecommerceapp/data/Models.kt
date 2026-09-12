// Models.kt — clases de datos que representan lo que ENVIAMOS y RECIBIMOS del backend.
// Una "data class" en Kotlin es una clase pensada para guardar datos: el compilador
// le genera solo los métodos útiles (equals, toString, copy) a partir de sus campos.
package com.ecommerce.ecommerceapp.data

// ============================================================================
//  GUÍA 3 — LOGIN
// ============================================================================

// -------- Lo que ENVIAMOS al hacer login --------
// Los nombres deben coincidir EXACTAMENTE con los que espera el backend:
// "Email" y "PasswoRDkey" (respeta mayúsculas y minúsculas). Gson (el traductor
// de JSON) usa estos nombres tal cual para armar el cuerpo de la petición.
data class LoginRequest(
    val Email: String,       // correo que escribe el usuario
    val PasswoRDkey: String  // contraseña que escribe el usuario
)

// -------- Lo que RECIBIMOS si el login es correcto --------
// El backend responde un JSON con tres campos. El signo "?" indica que ese valor
// PODRÍA llegar nulo, así evitamos que la app se caiga si falta algo.
data class LoginResponse(
    val message: String?,  // mensaje del servidor, ej. "Login exitoso"
    val token: String?,    // token de sesión (lo usaremos en las siguientes guías)
    val user: User?        // datos del usuario que inició sesión
)

// -------- Datos del usuario que vienen dentro de la respuesta --------
// OJO: el campo se llama "iD_User" (i minúscula, D mayúscula) en el JSON del backend.
// Si el nombre de esta propiedad no coincide EXACTO con esa mezcla de mayúsculas y
// minúsculas, Gson no lo reconoce y el valor llega siempre en null.
data class User(
    val iD_User: Int,      // identificador único del usuario en la base de datos
    val UserName: String,  // nombre de usuario
    val Email: String      // correo del usuario
)

// ============================================================================
//  GUÍA 4 — LISTA DE PRODUCTOS
// ============================================================================
//
// El backend, en GET http://10.0.2.2:5050/api/products/, responde un JSON así:
//
// {
//   "products": [
//     {
//       "id_Product": 1,
//       "ProductName": "iPhone 15 Pro",
//       "Price": 1099.99,
//       "Stock": 10,
//       "categories": [ { "id_Category": 1, "CategoryName": "Celulares" } ],
//       "images":     [ { "id_image": 1, "pathimage": "/images/iphone15.jpg", ... } ]
//     }
//   ],
//   "pagination": { "page": 1, "pages": 3, "per_page": 10, "total": 25, ... }
// }
//
// Por eso creamos UNA clase por cada "nivel" de ese JSON.

// -------- Lo que RECIBIMOS al pedir la lista de productos --------
// Es el objeto de MÁS AFUERA: contiene la lista y los datos de paginación.
data class ProductsResponse(
    val products: List<Product>?,   // la lista de productos de esta página
    val pagination: Pagination?     // en qué página vamos, cuántas hay, etc.
)

// -------- Un producto --------
// Los nombres van EXACTAMENTE como en el JSON del backend (ProductName con P y N
// mayúsculas, Price, Stock, id_Product con "id" en minúscula y "P" mayúscula).
data class Product(
    val id_Product: Int,                     // identificador del producto
    val ProductName: String?,                // nombre, ej. "iPhone 15 Pro"
    val Price: Double?,                      // precio, ej. 1099.99
    val Stock: Int?,                         // unidades disponibles
    val categories: List<Category>? = null,  // categorías a las que pertenece
    val images: List<ProductImage>? = null   // imágenes asociadas
)

// -------- Una categoría del producto --------
data class Category(
    val id_Category: Int,       // identificador de la categoría
    val CategoryName: String?   // nombre, ej. "Celulares"
)

// -------- Una imagen del producto --------
data class ProductImage(
    val id_image: Int,             // identificador de la imagen
    val pathimage: String?,        // ruta del archivo, ej. "/images/iphone15.jpg"
    val alt_text: String?,         // texto alternativo (accesibilidad)
    val is_main_image: Boolean?    // true si es la imagen principal
)

// -------- Datos de paginación --------
// El backend no devuelve TODOS los productos de golpe: los manda por páginas.
data class Pagination(
    val page: Int?,          // página actual
    val pages: Int?,         // cuántas páginas hay en total
    val per_page: Int?,      // cuántos productos trae cada página
    val total: Int?,         // total de productos en la base de datos
    val has_next: Boolean?,  // ¿existe una página siguiente?
    val has_prev: Boolean?   // ¿existe una página anterior?
)
