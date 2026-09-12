// ApiService.kt — el CONTRATO con la API, escrito como una interfaz de Kotlin.
// Aquí solo se declara QUÉ se le puede pedir al servidor; Retrofit genera el código
// que arma cada petición HTTP a partir de las anotaciones (las @).
package com.inventario.tiendabarrio.data

import retrofit2.Response
import retrofit2.http.Body      // el objeto viaja en el CUERPO de la petición
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header    // agrega un encabezado, aquí el del token
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path      // reemplaza un trozo variable de la ruta: /{id}
import retrofit2.http.Query     // agrega parámetros a la URL: ?page=1

interface ApiService {

    // ---------------- ACCESO ----------------

    // POST http://10.0.2.2:5050/api/auth/login
    // "suspend" = puede pausarse esperando la red sin congelar la interfaz.
    @POST("api/auth/login")
    suspend fun login(@Body body: LoginRequest): Response<LoginResponse>

    // ---------------- LECTURA (no exige token) ----------------

    // GET api/products/?page=1&per_page=20&search=texto
    // Si un @Query llega null, Retrofit NO lo agrega a la URL.
    @GET("api/products/")
    suspend fun getProducts(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20,
        @Query("search") search: String? = null
    ): Response<ProductsResponse>

    // GET api/products/5   ->  @Path("id") reemplaza el {id} de la ruta.
    @GET("api/products/{id}")
    suspend fun getProduct(@Path("id") id: Int): Response<ProductResponse>

    // GET api/categories/  -> para el selector de categorías del formulario.
    @GET("api/categories/")
    suspend fun getCategories(): Response<CategoriesResponse>

    // ---------------- ESCRITURA (exige sesión iniciada) ----------------

    // @Header("Authorization") manda el token en el ENCABEZADO, nunca en la URL:
    // así no queda guardado en historiales ni en los logs del servidor.
    @POST("api/products/")
    suspend fun createProduct(
        @Header("Authorization") token: String,
        @Body body: ProductRequest
    ): Response<ProductResponse>

    @PUT("api/products/{id}")
    suspend fun updateProduct(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body body: ProductRequest
    ): Response<ProductResponse>

    @DELETE("api/products/{id}")
    suspend fun deleteProduct(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<ErrorResponse>
}
