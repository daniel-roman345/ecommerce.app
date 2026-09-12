// Session.kt — guarda EN MEMORIA los datos de la sesión activa.
// Es un "object" (singleton): cualquier pantalla puede leer Session.token sin
// tener que ir pasándolo de una a otra por parámetros.
//
// Limitación conocida y a sustentar: al cerrar la app la sesión se pierde, porque
// esto vive en memoria RAM. El paso siguiente sería guardarlo con DataStore.
package com.inventario.tiendabarrio.data

object Session {

    var token: String? = null   // el JWT que devolvió el login
    var user: User? = null      // el usuario que inició sesión, con sus roles

    // ¿Hay alguien con sesión iniciada?
    fun estaAutenticado(): Boolean = !token.isNullOrBlank()

    // Arma el valor del encabezado Authorization tal como lo espera el servidor:
    //   Authorization: Bearer eyJhbGciOi...
    // "Bearer" (portador) es el esquema estándar para tokens JWT.
    fun bearer(): String = "Bearer ${token.orEmpty()}"

    // ¿El usuario tiene el rol de Administrador?
    // Los roles llegan dentro de la respuesta del login: [{ "TypeRole": "Administrador" }]
    fun esAdministrador(): Boolean =
        user?.roles?.any { it.TypeRole.equals("Administrador", ignoreCase = true) } == true

    // Nombre para saludar en la barra superior.
    fun nombre(): String = user?.UserName.orEmpty()

    // Borra todo al cerrar sesión.
    fun cerrar() {
        token = null
        user = null
    }
}
