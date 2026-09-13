// ErrorMessages.kt — traduce los códigos HTTP a frases que entiende una persona.
// Criterio de calidad de la guía: el usuario nunca debe ver un volcado de excepción
// de Kotlin; debe ver una explicación en lenguaje humano.
package com.inventario.tiendabarrio.data

// Los cuatro escenarios que exige la GA4, más un caso por defecto.
fun mensajeDeError(codigo: Int): String = when (codigo) {
    400 -> "Los datos enviados no son válidos. Revisa el formulario."
    401 -> "Tu sesión venció. Vuelve a iniciar sesión."          // token inválido o expirado
    403 -> "No tienes permisos para realizar esta operación."     // rol insuficiente
    404 -> "El registro que buscas ya no existe."                 // recurso inexistente
    409 -> "Ese registro ya existe."
    in 500..599 -> "El servidor tuvo un problema. Intenta más tarde."
    else -> "Ocurrió un error inesperado (código $codigo)."
}

// Para el caso "no hubo ni respuesta": servidor apagado, sin wifi, URL mal escrita.
fun mensajeSinConexion(detalle: String?): String =
    "No se pudo conectar con el servidor. Verifica que esté encendido y que la " +
    "URL base sea correcta.\n\nDetalle técnico: ${detalle ?: "desconocido"}"
