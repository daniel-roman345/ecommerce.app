# Paso a paso para armar el proyecto en Android Studio

Sigue el orden. Cada paso corresponde a lo que pide la actividad **GA4-AA3-EV03**.

---

## Paso 1 · Crear el proyecto (la guía pide crearlo tú, no copiar el de e-commerce)

1. Android Studio → **New Project → Empty Activity** (la de Compose).
2. Llénalo **exactamente así**, porque de estos nombres dependen el tema y el package:
   - **Name:** `InventarioTienda`
   - **Package name:** `com.inventario.tiendabarrio`
   - **Language:** Kotlin · **Minimum SDK:** API 24 · **Build configuration language:** Kotlin DSL
3. Espera a que termine el primer *Gradle Sync* y ejecuta la app tal cual, para
   confirmar que el proyecto base compila **antes** de tocar nada.

> Si le pones otro nombre al proyecto, Android Studio generará el tema con otro
> nombre (`TuNombreTheme` y `@style/Theme.TuNombre`). En ese caso ajusta esas dos
> referencias en `MainActivity.kt` y en el `AndroidManifest.xml`.

## Paso 2 · Dependencias

**No reemplaces los archivos completos**: tu Android Studio generó versiones de
`agp`, `kotlin` y del BOM de Compose que ya funcionan en tu máquina, y pisarlas es
la causa número uno de que el proyecto deje de compilar. Solo **agrega** lo que falta.

En `gradle/libs.versions.toml`, dentro de `[versions]`:

```toml
retrofit = "2.11.0"
okhttp = "4.12.0"
navigationCompose = "2.8.5"
coroutinesAndroid = "1.9.0"
```

y dentro de `[libraries]`:

```toml
retrofit = { group = "com.squareup.retrofit2", name = "retrofit", version.ref = "retrofit" }
retrofit-converter-gson = { group = "com.squareup.retrofit2", name = "converter-gson", version.ref = "retrofit" }
okhttp-logging-interceptor = { group = "com.squareup.okhttp3", name = "logging-interceptor", version.ref = "okhttp" }
androidx-navigation-compose = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigationCompose" }
kotlinx-coroutines-android = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-android", version.ref = "coroutinesAndroid" }
```

En `app/build.gradle.kts`, dentro del bloque `dependencies { }`:

```kotlin
// Consumo de la API REST
implementation(libs.retrofit)
implementation(libs.retrofit.converter.gson)
implementation(libs.okhttp.logging.interceptor)

// Navegación y corrutinas
implementation(libs.androidx.navigation.compose)
implementation(libs.kotlinx.coroutines.android)
```

Pulsa **Sync Now** y confirma que compila antes de seguir. Los archivos completos
`gradle/libs.versions.toml` y `app/build.gradle.kts` de esta carpeta quedan como
**referencia** por si necesitas comparar; úsalos solo si el tuyo se dañó.

## Paso 3 · Permiso de Internet

Reemplaza `app/src/main/AndroidManifest.xml` por el de esta carpeta. Trae dos cosas
imprescindibles: el permiso `INTERNET` y `usesCleartextTraffic="true"` (sin esto la
app compila pero ninguna petición HTTP funciona).

## Paso 4 · La capa de datos

Dentro de `app/src/main/java/com/inventario/tiendabarrio/` crea el package `data`
(clic derecho → New → Package) y dentro de él estos cinco archivos:

| Archivo | Qué contiene |
|---|---|
| `data/Models.kt` | los `data class` de todo lo que se envía y se recibe |
| `data/ApiService.kt` | la interfaz de Retrofit con los 7 endpoints |
| `data/RetrofitClient.kt` | la URL base y el cliente con el logging |
| `data/Session.kt` | el token, el usuario y la función `bearer()` |
| `data/ErrorMessages.kt` | la traducción de los códigos HTTP a lenguaje humano |

## Paso 5 · Las pantallas y la navegación

En el package raíz `com.inventario.tiendabarrio` (al lado de `MainActivity.kt`):

| Archivo | Pantalla |
|---|---|
| `LoginScreen.kt` | acceso |
| `ProductListScreen.kt` | lista del inventario (los 4 estados) |
| `ProductDetailScreen.kt` | detalle, con eliminar |
| `ProductFormScreen.kt` | crear y editar |
| `CategoryListScreen.kt` | categorías |
| `MainActivity.kt` | **reemplaza el que generó Android Studio**: trae el `NavHost` |

Borra la función `Greeting` y su `@Preview` que venían en el `MainActivity.kt`
original: ya no se usan y dejarlas genera advertencias.

## Paso 6 · Probar

1. Enciende el backend: `python app.py` (queda en `http://localhost:5050`).
2. Ejecuta la app **en el emulador** (en celular físico hay que cambiar la URL base,
   ver el README).
3. Prueba el camino completo: ingresar → ver la lista → abrir un detalle → volver.
4. Abre el **Logcat** y filtra por `okhttp`: ahí ves la petición y la respuesta reales.

### Los errores que hay que provocar a propósito (son evidencia de la entrega)

| Prueba | Cómo provocarla | Qué debe pasar |
|---|---|---|
| Sin conexión | Apaga el backend y toca **Reintentar** | Mensaje "No se pudo conectar..." y botón para reintentar |
| 401 | Ingresa con una contraseña equivocada | "Correo o contraseña incorrectos" |
| 403 | Entra sin iniciar sesión y mira la lista | El botón **+ Nuevo** no aparece |
| 404 | Abre un detalle y borra ese producto desde Postman, luego recarga | "El registro que buscas ya no existe" |
| Lista vacía | Busca "zzzzz" | "No hay productos que coincidan con la búsqueda" |
| Validación | Deja el nombre vacío o pon precio `-5` | Mensaje bajo el campo, sin enviar nada al servidor |

Toma **captura de cada una**: la evidencia GA4-AA4-EV04 las pide.

## Paso 7 · Subirlo a GitHub

```bash
git init
git add .
git commit -m "agrega capa de datos y pantalla de lista del inventario"
git branch -M main
git remote add origin https://github.com/TU-USUARIO/inventario-tienda-app.git
git push -u origin main
```

El `.gitignore` de esta carpeta ya excluye `build/`, `.gradle/` y `local.properties`,
que es justo lo que revisan en la entrega. Haz commits con mensajes que digan qué
cambió: *"agrega pantalla de detalle del producto"* dice mucho más que *"cambios"*.

---

## Lo que todavía te toca a ti (no lo puede hacer el código)

| Evidencia | Qué falta |
|---|---|
| GA4-AA1-EV01 | Personalizar `GA4-AA1-AA2-Documento.md` con **tu** problema, exportarlo a PDF (máx. 2 páginas) y pedir el visto bueno del instructor |
| GA4-AA2-EV02 | Dibujar los **wireframes** (Excalidraw o a mano) y tomar las **capturas de Postman** de cada endpoint |
| GA4-AA3-EV03 | Capturas del emulador y del Logcat: una petición exitosa y una fallida |
| GA4-AA4-EV04 | Completar los usuarios de prueba del README, generar el **APK** (Build → Build APK(s)) y preparar la sustentación de 10 minutos |

### Las cinco preguntas de la sustentación, con la respuesta que da este código

1. **¿Qué problema resuelve y para quién?** → README, sección 1.
2. **¿Cómo viajan los datos del servidor a la pantalla?** →
   `RetrofitClient` arma la petición → `ApiService` define la ruta y las anotaciones →
   Gson traduce el JSON a los `data class` de `Models.kt` → la pantalla guarda el
   resultado en un `mutableStateOf` → Compose se redibuja solo.
3. **¿Cómo protegiste las operaciones sensibles?** → `Session.esAdministrador()` oculta
   los botones y el token viaja en `@Header("Authorization")`; y explica por qué eso
   **no basta** (README, sección 6) y qué le agregarías al backend.
4. **¿Qué fue lo más difícil?** → respóndelo tú, con un ejemplo real de algo que se
   te rompió y cómo lo leíste en el Logcat.
5. **¿Qué le falta para publicarse?** → README, sección 10: DataStore, ViewModel,
   Room y firmar el APK.
