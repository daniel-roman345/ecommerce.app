// RetrofitClient.kt — crea y configura UNA sola instancia de Retrofit para toda la app.
// "object" en Kotlin = singleton: existe una única copia, accesible como RetrofitClient.api
package com.ecommerce.ecommerceapp.data

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor         // imprime en Logcat lo que se envía/recibe
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory  // traduce JSON <-> objetos Kotlin

object RetrofitClient {

    // 10.0.2.2 es la dirección que, DESDE EL EMULADOR, apunta a "localhost" de tu
    // computador (donde corre el backend de Python). La URL debe terminar en "/".
    private const val BASE_URL = "http://10.0.2.2:5050/"

    // Interceptor: "espía" cada petición y su respuesta y las escribe en Logcat.
    // Level.BODY = muestra encabezados y cuerpo completos (muy útil para depurar).
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Cliente HTTP con el interceptor conectado.
    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    // Construcción de Retrofit y creación del ApiService listo para usar.
    // Nombre EXACTO que usan todas las guías: RetrofitClient.api
    val api: ApiService = Retrofit.Builder()
        .baseUrl(BASE_URL)                                   // a dónde apuntan las peticiones
        .client(client)                                      // usar el cliente con logging
        .addConverterFactory(GsonConverterFactory.create())  // activar traducción JSON
        .build()                                             // arma el objeto Retrofit
        .create(ApiService::class.java)                      // genera la implementación
}
