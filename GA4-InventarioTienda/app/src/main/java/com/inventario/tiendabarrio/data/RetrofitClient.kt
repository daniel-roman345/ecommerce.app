// RetrofitClient.kt — crea y configura UNA sola instancia de Retrofit para toda la app.
// "object" en Kotlin = singleton: existe una única copia, accesible como RetrofitClient.api
package com.inventario.tiendabarrio.data

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // 10.0.2.2 es la dirección que, DESDE EL EMULADOR, apunta al "localhost" de tu
    // computador (donde corre el backend Flask). Con "localhost" la app fallaría,
    // porque para el emulador localhost es él mismo. La URL debe terminar en "/".
    //
    // Si pruebas en un CELULAR FÍSICO, cámbiala por la IP de tu computador en la red,
    // por ejemplo "http://192.168.1.15:5050/" (la ves con ipconfig / ifconfig).
    private const val BASE_URL = "http://10.0.2.2:5050/"

    // Interceptor: "espía" cada petición y su respuesta y las escribe en el Logcat.
    // Es la herramienta con la que vas a depurar: ahí se ve el JSON real.
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Cliente HTTP. Los timeouts evitan que la app se quede esperando para siempre
    // cuando el servidor está apagado: a los 15 segundos lanza excepción y la
    // atrapamos con try/catch para mostrar "sin conexión".
    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    val api: ApiService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())  // traduce JSON <-> Kotlin
        .build()
        .create(ApiService::class.java)
}
