// Models.kt — las clases de datos de la app.
// Cada data class representa UNA estructura del JSON que el servidor envía o recibe.
// Los nombres de las propiedades deben coincidir EXACTO con las llaves del JSON,
// porque Gson (el traductor) se guía por ellos. El "?" significa "podría llegar null".
package com.inventario.tiendabarrio.data

// ============================================================
//  ACCESO (POST /api/auth/login)
// ============================================================

// Lo que ENVIAMOS al iniciar sesión.
// El backend espera estas llaves con esta mezcla de mayúsculas: "Email" y "PasswoRDkey".
data class LoginRequest(
    val Email: String,
    val PasswoRDkey: String
)

// Lo que RECIBIMOS si las credenciales son correctas.
data class LoginResponse(
    val message: String?,  // "Login exitoso"
    val token: String?,    // token JWT, válido 24 horas
    val user: User?        // datos del usuario, con sus roles
)

// El usuario que inició sesión. Ojo: "iD_User" con i minúscula y D mayúscula.
data class User(
    val iD_User: Int,
    val UserName: String?,
    val Email: String?,
    val roles: List<Role>? = null   // un usuario puede tener varios roles
)

// Un rol del sistema: Administrador, Vendedor, Cliente...
data class Role(
    val iDRole: Int,
    val TypeRole: String?
)

// ============================================================
//  PRODUCTOS (el módulo que consume esta app)
// ============================================================

// GET /api/products/  ->  { "products": [...], "pagination": {...} }
data class ProductsResponse(
    val products: List<Product>?,
    val pagination: Pagination?
)

// GET /api/products/{id}  ->  { "product": {...} }
// POST y PUT /api/products/  ->  { "message": "...", "product": {...} }
data class ProductResponse(
    val message: String?,
    val product: Product?
)

// Un producto del inventario.
data class Product(
    val id_Product: Int,
    val ProductName: String?,
    val Price: Double?,
    val Stock: Int?,
    val categories: List<Category>? = null
)

// Lo que ENVIAMOS al crear (POST) o editar (PUT) un producto.
// "categories" es la lista de ids de categoría, ej. [1, 6].
data class ProductRequest(
    val ProductName: String,
    val Price: Double,
    val Stock: Int,
    val categories: List<Int> = emptyList()
)

// El servidor entrega los productos por páginas, no todos de golpe.
data class Pagination(
    val page: Int?,
    val pages: Int?,
    val per_page: Int?,
    val total: Int?,
    val has_next: Boolean?,
    val has_prev: Boolean?
)

// ============================================================
//  CATEGORÍAS (GET /api/categories/)
// ============================================================

data class CategoriesResponse(
    val categories: List<Category>?,
    val count: Int?
)

data class Category(
    val id_Category: Int,
    val CategoryName: String?
)

// ============================================================
//  ERRORES
// ============================================================

// Cuando algo sale mal, el backend responde { "error": "texto del problema" }.
data class ErrorResponse(
    val error: String?
)
