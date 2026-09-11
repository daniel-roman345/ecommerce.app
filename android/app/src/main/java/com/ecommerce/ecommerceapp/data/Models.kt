// Models.kt — clases de datos que representan lo que ENVIAMOS y RECIBIMOS del backend.
// Una "data class" en Kotlin es una clase pensada para guardar datos: el compilador
// le genera solo los métodos útiles (equals, toString, copy) a partir de sus campos.
package com.ecommerce.ecommerceapp.data

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
