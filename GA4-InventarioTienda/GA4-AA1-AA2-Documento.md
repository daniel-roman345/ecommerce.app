# GA4 · Documento de las actividades 1 y 2
**Aprendiz:** _(tu nombre)_ · **Ficha:** _(número)_ · **Programa:** ADSO 228118

> ⚠️ Este documento es una **base**. Revísalo, cámbialo por el problema que tú
> conozcas de cerca y preséntalo al instructor para el visto bueno del alcance antes
> de seguir programando (lo exige la evidencia GA4-AA1-EV01).

---

# GA4-AA1-EV01 — Reflexión: qué app voy a construir y para quién

## 1. Cinco situaciones observadas del entorno

1. El inventario de la tienda del barrio, que se lleva en un cuaderno.
2. Los turnos de la barbería de la esquina, apuntados en una hoja pegada al espejo.
3. Los préstamos de libros de la biblioteca de la ficha, en un grupo de WhatsApp.
4. La asistencia del equipo de microfútbol, que se pierde entre mensajes.
5. Los pedidos del negocio de comidas, anotados en servilletas durante el almuerzo.

## 2. La situación escogida y por qué

**El inventario de la tienda del barrio.** La escojo porque la conozco de cerca y
porque el problema se repite todos los días: cuando un cliente pregunta el precio o
si queda un producto, hay que ir hasta la estantería o buscar en el cuaderno.

- **¿Quién usaría la app?** La dueña de la tienda, que administra los productos, y
  quien atiende el mostrador, que solo necesita consultar.
- **¿Cómo lo resuelve hoy?** Con un cuaderno de inventario y con la memoria.
- **¿Qué gana al tenerla en el celular?** Consultar precio y existencias mientras
  atiende, sin moverse del mostrador, y corregir un precio en el momento en que cambia.

## 3. El backend que voy a consumir

**Camino elegido:** otro módulo del backend de e-commerce que ya conozco
(productos y categorías).

- **URL base:** `http://10.0.2.2:5050/` desde el emulador
  (`http://localhost:5050/` desde el computador).
- **Repositorio:** <https://github.com/matiussw/Ecommerce-Api-Python>

**Endpoints que ya comprobé que existen:**

1. `POST /api/auth/login` — iniciar sesión y obtener el token.
2. `GET /api/products/` — listar los productos del inventario.
3. `GET /api/products/{id}` — consultar un producto puntual.
4. `POST /api/products/` — crear un producto.
5. `PUT /api/products/{id}` — editar un producto.
6. `GET /api/categories/` — listar las categorías.

## 4. Mis pantallas

| # | Pantalla | Qué hace |
|---|---|---|
| 1 | Acceso | Pide correo y contraseña, guarda el token y deja entrar. También permite entrar solo a consultar. |
| 2 | Lista de inventario | Muestra todos los productos con su precio y existencias, y permite buscar por nombre. |
| 3 | Detalle del producto | Muestra los datos completos de un producto y da acceso a editarlo o eliminarlo. |
| 4 | Formulario de producto | Crea un producto nuevo o edita uno existente, validando los datos antes de enviarlos. |
| 5 | Categorías | Lista las categorías disponibles en el inventario. |

Mínimos cumplidos: hay una pantalla de **acceso** (1), una de **lista** (2) y una de
**detalle y formulario** (3 y 4).

## 5. Matriz de permisos

| Operación | Sin sesión | Autenticado | Administrador |
|---|---|---|---|
| Ver la lista de productos | Sí | Sí | Sí |
| Ver el detalle | Sí | Sí | Sí |
| Ver categorías | Sí | Sí | Sí |
| Crear producto | No | No | Sí |
| Editar producto | No | No | Sí |
| Eliminar producto | No | No | Sí |

**Razón de negocio:** consultar no hace daño y agiliza la atención. Escribir sí:
un precio o un stock mal digitado desordena la contabilidad de la tienda, así que
esa responsabilidad queda en quien responde por el negocio.

## 6. Frase de alcance

> «Mi app permite a **la encargada de una tienda de barrio** consultar y gestionar
> **los productos de su inventario** desde el celular, para que pueda **saber precio
> y existencias en cualquier momento y corregirlos al instante**».

## 7. Lista "versión 2" (fuera de alcance)

Fotos de los productos · lector de código de barras · reporte de ventas del día ·
alertas de stock bajo · funcionamiento sin conexión · varias tiendas en una misma cuenta.

---

# GA4-AA2-EV02 — Diseño de pantallas, navegación y contrato con la API

## Paso 1 · Wireframes

> Dibuja cada pantalla en Excalidraw o a mano y **adjunta la imagen**. Esta tabla
> es el guion de lo que debe aparecer en cada dibujo.

**Pantalla 1 — Acceso (`login`)**
- Título "Inventario de la tienda"
- Campo *Correo* · Campo *Contraseña* (oculta)
- Botón **Ingresar** → valida y llama a `POST /api/auth/login`
- Enlace **Entrar sin iniciar sesión** → va a la lista en modo consulta
- Zona de mensaje de error bajo el botón

**Pantalla 2 — Lista de inventario (`productos`)**
- Barra superior: "Inventario — {nombre}" + acciones *Categorías* y *Salir*
- Campo *Buscar por nombre* + botón **Buscar** → recarga con `?search=`
- Lista de tarjetas: nombre, existencias, precio → al tocar va al detalle
- Botón flotante **+ Nuevo** → solo visible para el administrador

**Pantalla 3 — Detalle (`detalle/{id}`)**
- Barra superior con botón **Atrás** → `popBackStack()`
- Nombre, código, precio, existencias, categorías
- Botones **Editar** y **Eliminar** → solo para el administrador
- Diálogo de confirmación antes de eliminar

**Pantalla 4 — Formulario (`formulario?id={id}`)**
- Barra superior: "Nuevo producto" o "Editar producto"
- Campos *Nombre*, *Precio* (teclado decimal), *Existencias* (teclado numérico)
- Casillas de las categorías
- Botón **Crear / Guardar cambios** → se deshabilita mientras envía

**Pantalla 5 — Categorías (`categorias`)**
- Barra superior con botón **Atrás**
- Lista de tarjetas con id y nombre de cada categoría

## Paso 2 · Mapa de navegación

```
                      ┌──────────────────────┐
                      │   login  (inicio)    │
                      └──────────┬───────────┘
                    navigate()   │   popUpTo(login, inclusive)
                                 ▼
                      ┌──────────────────────┐
              ┌───────│      productos       │───────┐
   navigate() │       └──────────┬───────────┘       │ navigate()
              ▼                  │ navigate()        ▼
      ┌───────────────┐          ▼           ┌───────────────┐
      │  categorias   │   ┌──────────────┐   │ formulario    │
      └───────┬───────┘   │ detalle/{id} │   │  (crear)      │
              │           └──────┬───────┘   └───────┬───────┘
              │ popBackStack()   │ navigate()        │ popBackStack(productos)
              │                  ▼                   │
              │           ┌──────────────────┐       │
              └──────────>│ formulario?id={} │<──────┘
                          │    (editar)      │
                          └──────────────────┘
```

- **startDestination:** `login`, porque el rol del usuario decide qué podrá hacer
  en todas las demás pantallas; hay que resolverlo antes que nada.
- **Flechas `navigate()`:** login → productos, productos → detalle, productos →
  formulario, productos → categorías, detalle → formulario.
- **Flechas `popBackStack()`:** los botones *Atrás* de detalle, formulario y
  categorías; y el regreso a la lista después de guardar o eliminar.
- **Rutas con argumento:** `detalle/{id}` (obligatorio, `NavType.IntType`) y
  `formulario?id={id}` (opcional; si no viene, se está creando).

## Paso 3 · Contrato con la API

| Funcionalidad | Método y ruta | ¿Token? | ¿Rol? | Cuerpo que envía | Respuesta esperada | Error |
|---|---|---|---|---|---|---|
| Iniciar sesión | `POST /api/auth/login` | No | — | `{"Email":"...","PasswoRDkey":"..."}` | `200 {message, token, user}` | `401` credenciales inválidas |
| Listar productos | `GET /api/products/` | No | — | — | `200 {products[], pagination}` | `500` error del servidor |
| Ver producto | `GET /api/products/{id}` | No | — | — | `200 {product}` | `404` no existe |
| Listar categorías | `GET /api/categories/` | No | — | — | `200 {categories[], count}` | `500` |
| Crear producto | `POST /api/products/` | Sí | Administrador | `{"ProductName","Price","Stock","categories":[]}` | `201 {message, product}` | `400` faltan datos |
| Editar producto | `PUT /api/products/{id}` | Sí | Administrador | igual que crear | `200 {message, product}` | `404` no existe |
| Eliminar producto | `DELETE /api/products/{id}` | Sí | Administrador | — | `200 {message}` | `400` tiene ventas asociadas |

> **Adjunta las capturas de Postman** de cada llamada con la respuesta JSON real.
> No diseñes contra lo que crees que devuelve el servidor: diseña contra lo que
> devuelve de verdad.

### Los `data class` que se derivan del JSON real

| Clase | Para qué | Campos |
|---|---|---|
| `LoginRequest` | cuerpo que envía el login | `Email`, `PasswoRDkey` |
| `LoginResponse` | respuesta del login | `message`, `token`, `user` |
| `User` | el usuario autenticado | `iD_User`, `UserName`, `Email`, `roles` |
| `Role` | un rol del usuario | `iDRole`, `TypeRole` |
| `ProductsResponse` | respuesta de la lista | `products`, `pagination` |
| `ProductResponse` | respuesta de uno solo / de crear / de editar | `message`, `product` |
| `Product` | un producto | `id_Product`, `ProductName`, `Price`, `Stock`, `categories` |
| `ProductRequest` | cuerpo que se envía al crear o editar | `ProductName`, `Price`, `Stock`, `categories[]` |
| `Pagination` | datos de paginación | `page`, `pages`, `per_page`, `total`, `has_next`, `has_prev` |
| `CategoriesResponse` | respuesta de categorías | `categories`, `count` |
| `Category` | una categoría | `id_Category`, `CategoryName` |
| `ErrorResponse` | respuesta de error | `error` |

### Anotaciones de Retrofit por método

| Método del `ApiService` | Anotaciones |
|---|---|
| `login` | `@POST("api/auth/login")` + `@Body` |
| `getProducts` | `@GET("api/products/")` + `@Query("page")`, `@Query("per_page")`, `@Query("search")` |
| `getProduct` | `@GET("api/products/{id}")` + `@Path("id")` |
| `getCategories` | `@GET("api/categories/")` |
| `createProduct` | `@POST("api/products/")` + `@Header("Authorization")` + `@Body` |
| `updateProduct` | `@PUT("api/products/{id}")` + `@Header("Authorization")` + `@Path("id")` + `@Body` |
| `deleteProduct` | `@DELETE("api/products/{id}")` + `@Header("Authorization")` + `@Path("id")` |

## Paso 4 · Los cuatro estados de cada pantalla

| Pantalla | Cargando | Con datos | Vacía | Error |
|---|---|---|---|---|
| Acceso | Botón dice "Ingresando..." y queda deshabilitado | Navega a la lista | — | "Correo o contraseña incorrectos" / "No se pudo conectar con el servidor" |
| Lista | Rueda + "Consultando el inventario..." | Tarjetas de productos | "No hay productos que coincidan con la búsqueda" + enlace para limpiar el filtro | Mensaje traducido + botón **Reintentar** |
| Detalle | Rueda centrada | Datos del producto | — (o existe o no existe) | "El registro que buscas ya no existe" + **Reintentar** / **Volver** |
| Formulario | Rueda mientras trae categorías y el producto a editar | Campos rellenos | Aviso "No se pudieron cargar las categorías" | Tarjeta con el mensaje bajo el botón |
| Categorías | Rueda centrada | Lista de categorías | "Todavía no hay categorías registradas" | Mensaje + **Reintentar** |

## Registro de cambios sobre el diseño

| Fecha | Qué cambió | Por qué |
|---|---|---|
| _(dd/mm)_ | _(ej.: se agregó el botón "Entrar sin iniciar sesión")_ | _(ej.: la matriz de permisos da acceso de consulta a un visitante)_ |
