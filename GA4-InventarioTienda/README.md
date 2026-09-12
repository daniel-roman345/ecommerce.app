# Inventario Tienda — App Android (GA4 · Proyecto Integrador)

Aplicación Android que permite a la persona encargada de una tienda de barrio
**consultar y gestionar el inventario de productos desde el celular**, para que no
dependa del cuaderno ni del computador del local.

Consume el backend REST de e-commerce en Python/Flask
(<https://github.com/matiussw/Ecommerce-Api-Python>), específicamente sus módulos
de **productos** y **categorías**.

---

## 1. El problema

Hoy la tienda lleva el inventario en un cuaderno: para saber si queda stock de un
producto hay que ir físicamente a la estantería o buscar página por página. No hay
forma de consultar precios mientras se atiende a un cliente, y los cambios de precio
se anotan encima de los anteriores hasta que la hoja es ilegible.

| Pregunta | Respuesta |
|---|---|
| ¿Quién usa la app? | La dueña de la tienda (administradora) y quien atiende el mostrador (consulta) |
| ¿Cómo lo resuelve hoy? | Un cuaderno de inventario y la memoria |
| ¿Qué gana en el celular? | Consultar precio y existencias en el mostrador, sin ir al computador |

**Frase de alcance:** *«Mi app permite a la encargada de una tienda de barrio
consultar y gestionar los productos de su inventario desde el celular, para que
pueda saber precio y existencias en cualquier momento y corregirlos al instante».*

**Versión 2 (fuera de alcance, escrito y quieto):** fotos de producto, lectura de
código de barras, reporte de ventas del día, alertas de stock bajo, modo sin conexión.

---

## 2. Requisitos previos

- Android Studio (Ladybug o superior) con un emulador creado.
- Python 3 y el backend de e-commerce corriendo:
  ```bash
  pip install -r requirement.txt
  python app.py          # queda escuchando en http://localhost:5050
  ```
- Base de datos con al menos un usuario con rol **Administrador**.

## 3. Configurar la URL base

La dirección del servidor se define en un solo lugar:
`app/src/main/java/com/inventario/tiendabarrio/data/RetrofitClient.kt`

| Dónde pruebas | Valor de `BASE_URL` |
|---|---|
| Emulador de Android Studio | `http://10.0.2.2:5050/` |
| Celular físico en la misma red wifi | `http://<IP-de-tu-PC>:5050/` (ej. `http://192.168.1.15:5050/`) |

`10.0.2.2` es el alias con el que el emulador alcanza el `localhost` del computador.
Usar `localhost` **no funciona**: para el emulador, `localhost` es él mismo.

## 4. Endpoints consumidos (contrato con la API)

| Funcionalidad | Método y ruta | ¿Token? | ¿Rol? | Envía | Recibe | Error |
|---|---|---|---|---|---|---|
| Iniciar sesión | `POST /api/auth/login` | No | — | `{Email, PasswoRDkey}` | `{message, token, user{roles}}` | 401 credenciales inválidas |
| Listar productos | `GET /api/products/?page&per_page&search` | No | — | — | `{products[], pagination}` | 500 |
| Ver un producto | `GET /api/products/{id}` | No | — | — | `{product}` | 404 no existe |
| Listar categorías | `GET /api/categories/` | No | — | — | `{categories[], count}` | 500 |
| Crear producto | `POST /api/products/` | Sí | Administrador | `{ProductName, Price, Stock, categories[]}` | `{message, product}` | 400 datos incompletos |
| Editar producto | `PUT /api/products/{id}` | Sí | Administrador | `{ProductName, Price, Stock, categories[]}` | `{message, product}` | 404 no existe |
| Eliminar producto | `DELETE /api/products/{id}` | Sí | Administrador | — | `{message}` | 400 tiene ventas asociadas |

El token viaja **siempre** en el encabezado `Authorization: Bearer <token>`,
nunca en la URL y nunca escrito dentro del código.

## 5. Pantallas y navegación

| Ruta | Pantalla | Qué hace |
|---|---|---|
| `login` | `LoginScreen` | Acceso. Guarda el token en `Session`. Permite entrar solo a consultar. |
| `productos` | `ProductListScreen` | Lista del inventario con búsqueda. **startDestination tras el acceso.** |
| `detalle/{id}` | `ProductDetailScreen` | Detalle de un producto. Editar y eliminar si es administrador. |
| `formulario?id={id}` | `ProductFormScreen` | Crea (sin id) o edita (con id) un producto. |
| `categorias` | `CategoryListScreen` | Lista de categorías. |

```
login ──navigate──> productos ──navigate──> detalle/{id} ──navigate──> formulario?id={id}
  ▲                     │  │                      │                          │
  └──── Salir ──────────┘  └──> categorias        └── popBackStack ──────────┘
```

`startDestination = "login"`: el rol del usuario decide qué puede hacer en el resto
de la app, así que hay que resolverlo antes que nada.

## 6. Matriz de permisos

| Operación | Visitante (sin sesión) | Usuario autenticado | Administrador |
|---|---|---|---|
| Ver la lista de productos | Sí | Sí | Sí |
| Ver el detalle de un producto | Sí | Sí | Sí |
| Ver categorías | Sí | Sí | Sí |
| Crear un producto | No | No | Sí |
| Editar un producto | No | No | Sí |
| Eliminar un producto | No | No | Sí |

**Razón de negocio:** consultar el inventario no causa daño y agiliza la atención;
en cambio, un precio o un stock equivocado desordena la contabilidad de la tienda,
así que escribir queda reservado a quien responde por ella.

### Por qué ocultar los botones NO es seguridad

En la app las opciones de crear, editar y eliminar se ocultan con un `if
(Session.esAdministrador())`. Eso mejora la experiencia, pero **no protege nada**:
cualquiera puede tomar la URL del servidor y llamar `POST /api/products/` desde
Postman o desde `curl`, sin pasar por la app.

La seguridad real tiene que estar **en el servidor**. Hoy este backend genera el
token en el login, pero los endpoints de escritura de productos **no lo verifican**:
aceptan la petición aunque llegue sin `Authorization`. Lo que le agregaría para
lograr las dos capas:

1. Un decorador `@token_requerido` que lea el encabezado `Authorization`, decodifique
   el JWT con la `SECRET_KEY` y responda **401** si falta, está vencido o es inválido.
2. Un decorador `@rol_requerido('Administrador')` que, con el usuario ya identificado,
   revise sus roles y responda **403** si no le corresponde la operación.
3. Aplicar ambos a `POST`, `PUT` y `DELETE` de `/api/products/`.

Con eso la app oculta lo que el usuario no debe usar, y el servidor **rechaza** lo
que el usuario no debe hacer.

## 7. Manejo de errores

Toda llamada de red está dentro de `try / catch` y revisa `isSuccessful`. Los
mensajes se traducen en `data/ErrorMessages.kt`:

| Escenario | Qué ve el usuario |
|---|---|
| Servidor apagado o sin red | "No se pudo conectar con el servidor. Verifica que esté encendido..." |
| 401 sesión vencida | "Tu sesión venció. Vuelve a iniciar sesión." |
| 403 sin permisos | "No tienes permisos para realizar esta operación." |
| 404 no existe | "El registro que buscas ya no existe." |
| 400 datos inválidos | "Los datos enviados no son válidos. Revisa el formulario." |
| Lista vacía | "No hay productos que coincidan con la búsqueda." (no es un error) |

Ningún camino termina en un cierre inesperado de la aplicación.

## 8. Usuarios de prueba

| Rol | Correo | Contraseña |
|---|---|---|
| Administrador | _(completar con el de tu base de datos)_ | _(completar)_ |
| Cliente | _(completar)_ | _(completar)_ |

## 9. Generar el APK de depuración

En Android Studio: **Build → Build Bundle(s) / APK(s) → Build APK(s)**.
El archivo queda en `app/build/outputs/apk/debug/app-debug.apk`.

## 10. Limitaciones conocidas y siguientes pasos

- La sesión vive en memoria (`Session`): al cerrar la app se pierde. Siguiente paso:
  guardarla con **DataStore**.
- Las llamadas de red se hacen desde la propia pantalla. Siguiente paso: moverlas a
  un **ViewModel**.
- No hay caché local: sin servidor no hay datos. Siguiente paso: **Room**.
- El APK no está firmado para publicación.
