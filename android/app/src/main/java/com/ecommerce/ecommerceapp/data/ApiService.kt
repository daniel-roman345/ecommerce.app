// ApiService.kt — la "lista de servicios" que ofrece el backend.
// Es una INTERFAZ: solo declara QUÉ se puede pedir, no CÓMO. Retrofit genera solo
// el código que arma la petición HTTP a partir de estas anotaciones (las @).
package com.ecommerce.ecommerceapp.data

import retrofit2.Response      // envoltura con el resultado + el código de estado HTTP
import retrofit2.http.Body     // marca el objeto que viaja en el CUERPO de la petición
import retrofit2.http.GET      // indica que el método es una petición HTTP GET
import retrofit2.http.POST     // indica que el método es una petición HTTP POST
import retrofit2.http.Query    // agrega parámetros a la URL: ?page=1&per_page=10

interface ApiService {

    // ---------------------- GUÍA 3: LOGIN ----------------------

    // @POST le dice a Retrofit: "esto es una petición POST a esta ruta".
    // La ruta se une a la BASE_URL del RetrofitClient:
    //   http://10.0.2.2:5050/  +  api/auth/login
    @POST("api/auth/login")
    // "suspend": función que puede PAUSARSE mientras espera la red sin congelar la app;
    //            solo se puede llamar desde una corrutina.
    // @Body: toma el LoginRequest y lo convierte en el JSON del cuerpo.
    // Devuelve Response<LoginResponse>: la respuesta ya traducida + el estado HTTP.
    suspend fun login(@Body body: LoginRequest): Response<LoginResponse>

    // ------------------ GUÍA 4: LISTA DE PRODUCTOS ------------------

    // @GET: petición de LECTURA, no lleva cuerpo. La ruta final queda:
    //   http://10.0.2.2:5050/api/products/?page=1&per_page=10
    // OJO con la barra final ("api/products/"): el backend la define así.
    //
    // @Query("page"): cada parámetro con esta anotación se pega a la URL como
    // ?nombre=valor. Si el valor es null, Retrofit simplemente NO lo agrega,
    // por eso "search" es opcional (String? = null) y sirve para buscar por nombre.
    @GET("api/products/")
    suspend fun getProducts(
        @Query("page") page: Int = 1,          // página que queremos
        @Query("per_page") perPage: Int = 20,  // cuántos productos por página
        @Query("search") search: String? = null // texto a buscar en el nombre (opcional)
    ): Response<ProductsResponse>
}
