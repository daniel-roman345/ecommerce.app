// ApiService.kt — la "lista de servicios" que ofrece el backend.
// Es una INTERFAZ: solo declara QUÉ se puede pedir, no CÓMO. Retrofit genera solo
// el código que arma la petición HTTP a partir de estas anotaciones (las @).
package com.ecommerce.ecommerceapp.data

import retrofit2.Response      // envoltura con el resultado + el código de estado HTTP
import retrofit2.http.Body     // marca el objeto que viaja en el CUERPO de la petición
import retrofit2.http.POST     // indica que el método es una petición HTTP POST

interface ApiService {

    // @POST le dice a Retrofit: "esto es una petición POST a esta ruta".
    // La ruta se une a la BASE_URL del RetrofitClient:
    //   http://10.0.2.2:5050/  +  api/auth/login
    @POST("api/auth/login")
    // "suspend": función que puede PAUSARSE mientras espera la red sin congelar la app;
    //            solo se puede llamar desde una corrutina.
    // @Body: toma el LoginRequest y lo convierte en el JSON del cuerpo.
    // Devuelve Response<LoginResponse>: la respuesta ya traducida + el estado HTTP.
    suspend fun login(@Body body: LoginRequest): Response<LoginResponse>
}
