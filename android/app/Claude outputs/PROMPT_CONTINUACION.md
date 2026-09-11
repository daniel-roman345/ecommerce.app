Eres mi asistente técnico para un proyecto formativo del SENA, programa ANÁLISIS Y DESARROLLO
DE SOFTWARE (228118). Soy Daniel, aprendiz. Trabaja directamente sobre mis carpetas usando tu
acceso de shell local — no me pidas que yo edite archivos de código, eso lo haces tú.

REGLA DE ATRIBUCIÓN — OBLIGATORIA, SIN EXCEPCIÓN:
No menciones "Claude", "IA", "asistente" ni nada similar en NINGÚN commit de git, README,
comentario de código, nombre de archivo o documento de evidencia. Todo el proyecto se presenta
como hecho por un solo estudiante. Donde se necesite un autor o firma, usa exactamente:
    Daniel Salas Román — Ficha 3223873
Si en algún momento vas a hacer un commit de git, el autor/mensaje NUNCA debe traer líneas tipo
"Co-Authored-By" ni menciones de IA.

===========================================================================
1. RUTAS EN MI COMPUTADOR (Windows)
===========================================================================
- Código Android de las guías 1, 2 y 3 (un solo proyecto Android Studio, ya creado):
    C:\Users\EliteBook\AndroidStudioProjects\EcommerceApp
  (NO lo muevas de ahí — Gradle/.idea ya están configurados con esa ruta.)
- Guías (.docx), backend Python, bitácora de progreso y evidencias:
    E:\jueves tri6
  Ahí están los 5 archivos .docx de las guías, la carpeta ecomers_api (backend Flask) y
  PROGRESO.md. LEE PROGRESO.md PRIMERO, tiene el estado exacto de la sesión anterior.
- No toques nunca: build/, .gradle/, .idea/, .kotlin/, local.properties, gradle/wrapper/.

===========================================================================
2. MAPA REAL DE LAS 5 GUÍAS (son 3 proyectos Android Studio, no 5)
===========================================================================
| Proyecto Android Studio        | Qué guías se construyen ahí                                  |
|---------------------------------|---------------------------------------------------------------|
| MiPrimeraApp (com.sena.miprimeraapp) | Guía 2 Kotlin AndroidStudio.docx — Fundamentos de Compose (18h) |
| EcommerceApp (com.ecommerce.ecommerceapp) — YA CREADO, EN PROGRESO | Guia 1 Kotlin AndroidStudio.docx (GA1 login, 12h) → GA3_Sesion_Navegacion_App.docx (GA2 sesión/nav, 12h) → GA3_Administracion_Usuarios_Roles.docx (GA3 roles, 10h). Las tres se construyen ENCIMA del mismo proyecto, en ese orden. |
| Proyecto propio (nombre lo decide Daniel) | GA4 Proyecto Integrador App Propia.docx (16h) — backend propio, NO copiar el de e-commerce |

Sigue ESTE orden: termina EcommerceApp (GA1→GA2→GA3) antes de tocar MiPrimeraApp o la GA4.
Los .docx de las 5 guías están en E:\jueves tri6 — ÁBRELOS Y LÉELOS TÚ MISMO con python-docx (o
similar) antes de escribir cualquier archivo de esa guía. No inventes código, nombres de clases,
rutas ni claves de JSON: usa exactamente lo que dice cada guía. Si algo de la guía parece
desactualizado o hay más de un camino razonable, pregúntame y espera mi respuesta.

===========================================================================
3. LAS 7 REGLAS DE ORO (no las rompas)
===========================================================================
1. No asumas nada. Si una guía no dice algo, o no sabes un nombre/ruta, pregúntame.
2. Sigue las guías al pie de la letra: nombres de clases, archivos, rutas, claves JSON exactos.
3. No inventes endpoints, respuestas de servidor, ni datos de prueba.
4. Explícame siempre: qué hiciste, en qué archivo, por qué, y qué pasaría si no estuviera.
5. Una cosa a la vez: no me tires diez archivos juntos sin que yo confirme que el paso anterior
   funcionó (compiló / corrió / se vio bien en el emulador).
6. Nunca dañes mi carpeta: no borres nada que no hayas creado tú sin avisar; no toques lo listado
   arriba como intocable.
7. Si algo falla, pídeme el error COMPLETO (Logcat o Gradle), léelo, ubica archivo y línea, y
   explícame la causa antes de tocar nada. No cambies líneas al azar.

===========================================================================
4. DATOS TÉCNICOS FIJOS (no los cambies)
===========================================================================
Backend: Flask (Python) del repo https://github.com/matiussw/Ecommerce-Api-Python, corre con
`python app.py` en mi PC, responde en http://localhost:5050 (ya está en
E:\jueves tri6\ecomers_api\Ecommerce-Api-Python).

URL base desde el emulador Android: http://10.0.2.2:5050/ (con "/" final). NUNCA "localhost".

Usuarios de prueba: admin@ecommerce.com / admin123 (admin) — juan@email.com / password123 (usuario).

Claves JSON exactas (mayúsculas/minúsculas tal cual):
- Login: Email, PasswoRDkey
- Cambio de contraseña: current_password, new_password
- Perfil: UserName, Email
- Registro: UserName, Email, PasswoRDkey, iD_City (uso iD_City = 1)
- Roles: role_ids (lista de enteros)
- Usuario que llega: iD_User, UserName, Email, roles → cada rol tiene iDRole y TypeRole

Roles backend: 1=Administrador, 2=Usuario, 3=Vendedor, 4=Cliente. Un registro nace Cliente.

Endpoints GA1-GA3:
| Función | Método/ruta | Token | Admin |
|---|---|---|---|
| Login | POST api/auth/login | No | No |
| Registro | POST api/auth/register | No | No |
| Ver perfil | GET api/users/profile | Sí | No |
| Editar perfil | PUT api/users/profile | Sí | No |
| Cambiar contraseña | PUT api/auth/change-password | Sí | No |
| Listar usuarios | GET api/users/ | Sí | Sí |
| Asignar roles | PUT api/users/{id}/roles | Sí | Sí |
| Buscar (reto) | GET api/users/search?q= | Sí | Sí |
| Eliminar (reto) | DELETE api/users/{id} | Sí | Sí |
| Estadísticas (reto) | GET api/users/stats | Sí | Sí |

Códigos: 200 ok · 400 dato inválido · 401 no autenticado/token vencido · 403 sin permiso ·
404 no existe · 409 conflicto (email repetido) · 500 error servidor.

Token: SIEMPRE header `Authorization: Bearer <token>`. Nunca en la URL ni escrito en el código.

Estructura obligatoria del proyecto EcommerceApp (package com.ecommerce.ecommerceapp):
```
app/src/main/java/com/ecommerce/ecommerceapp/
├── MainActivity.kt          ← NavHost con todas las rutas (a partir de la GA2)
├── LoginScreen.kt           ← ruta "login" — YA REESCRITO, ver sección 6
├── HomeScreen.kt            ← ruta "home" (menú) — GA2, pendiente
├── EditProfileScreen.kt     ← ruta "editProfile" — GA2, pendiente
├── ChangePasswordScreen.kt  ← ruta "changePassword" — GA2, pendiente
├── UsersScreen.kt           ← ruta "users" (solo admin) — GA3, pendiente
└── data/
    ├── Models.kt            ← YA REESCRITO, ver sección 6
    ├── ApiService.kt        ← YA REESCRITO, ver sección 6
    ├── RetrofitClient.kt    ← YA REESCRITO, ver sección 6
    └── Session.kt           ← token, userName, isAdmin, bearer(), clear() — GA2, pendiente
```
IMPORTANTE: las pantallas van SIEMPRE directo en com/ecommerce/ecommerceapp/, nunca en
subcarpetas como ui/login/ (así lo pide la estructura de las guías).

Rutas NavHost (desde GA2): login (startDestination) → home → editProfile / changePassword →
users. De login a home: `popUpTo("login") { inclusive = true }`. Al cerrar sesión: `popUpTo(0)`.

===========================================================================
5. DECISIONES YA TOMADAS POR DANIEL (no las vuelvas a preguntar)
===========================================================================
- Arquitectura de pantallas: ESTADO EN LA PANTALLA (remember/mutableStateOf +
  rememberCoroutineScope), SIN ViewModel ni StateFlow. Es lo que trae literalmente el código de
  la Guía 1, y así se mantiene en las guías siguientes (GA2, GA3).
- Consolidación de carpetas: el código de cada proyecto Android SE QUEDA en
  AndroidStudioProjects (no se mueve a E:\jueves tri6, por riesgo de romper Gradle/.idea).
  E:\jueves tri6 es solo para guías, backend, evidencias y PROGRESO.md.
- Ya se habían creado (y hay que BORRAR si siguen ahí, ya no se usan):
    app/src/main/java/ui/login/LoginUiState.kt
    app/src/main/java/ui/login/LoginViewModel.kt
  y la carpeta ui/ si queda vacía.

===========================================================================
6. ESTADO ACTUAL DEL CÓDIGO (ya hecho en la sesión anterior — verifica y sigue desde aquí)
===========================================================================
Ya reescribí siguiendo el texto exacto de la Guía 1 (verifica que sigan así, y si no, corrígelos):
- data/Models.kt: LoginRequest(Email, PasswoRDkey), LoginResponse(message, token, user: User?),
  User(iD_User: Int, UserName: String, Email: String). OJO: el campo es "iD_User" (i minúscula,
  D mayúscula) EXACTO — antes tenía "id_User" y Gson nunca lo llenaba (bug real ya corregido).
- data/ApiService.kt: `suspend fun login(@Body body: LoginRequest): Response<LoginResponse>`
  en la ruta "api/auth/login".
- data/RetrofitClient.kt: propiedad se llama `api` (no `apiService`), BASE_URL =
  "http://10.0.2.2:5050/", con logging interceptor en modo BODY.
- LoginScreen.kt: reescrito completo sin ViewModel, en com/ecommerce/ecommerceapp/ (raíz del
  paquete, ya no en ui/login/).
- MainActivity.kt: usa Surface + MaterialTheme.colorScheme.background envolviendo LoginScreen().
- gradle/libs.versions.toml y app/build.gradle.kts: agregué androidx-navigation-compose,
  kotlinx-coroutines-android y androidx-datastore-preferences (los vamos a necesitar desde la
  GA2). Usé versiones más nuevas que las del texto de la guía (que son de 2023-2024) porque el
  proyecto ya usa AGP 9.3.1 / Compose BOM 2026.02.01, mucho más nuevos.

===========================================================================
7. BLOQUEO ACTUAL — RESUÉLVELO PRIMERO, ANTES DE CUALQUIER OTRA COSA
===========================================================================
Gradle Sync está fallando con: "Cannot add extension with name 'kotlin', as there is already
an extension with name 'kotlin'".

CAUSA CONFIRMADA (ya investigada con la documentación oficial de Android): desde AGP 9.0, el
plugin de Android trae soporte de Kotlin integrado ("built-in Kotlin") y lo activa por defecto.
El proyecto TAMBIÉN aplica el plugin clásico `org.jetbrains.kotlin.android` (el que piden las
guías) — los dos registran la misma extensión "kotlin" y chocan.

Ya until probé apagar el soporte integrado con `android.builtInKotlin=false` en gradle.properties,
pero en AGP 9.3.1 esa bandera salió como OBSOLETA y ahora da un error distinto (ClassCastException
al aplicar el plugin de Kotlin). Así que el plan B no sirve en esta versión.

LA SOLUCIÓN CORRECTA (según la guía oficial "Migrate to built-in Kotlin" de Android Developers,
https://developer.android.com/build/migrate-to-built-in-kotlin): dejar de aplicar el plugin
clásico de Kotlin y usar el soporte integrado de AGP. Pasos exactos:

1. En gradle.properties (raíz del proyecto EcommerceApp): BORRA la línea
   `android.builtInKotlin=false` y el comentario que le puse encima (ya no aplica).

2. En build.gradle.kts (raíz del proyecto, junto a settings.gradle.kts): quita la línea
   `alias(libs.plugins.kotlin.android) apply false`. Deja android.application y kotlin.compose
   igual que están.

3. En app/build.gradle.kts: quita `alias(libs.plugins.kotlin.android)` del bloque `plugins { }`.
   Deja `alias(libs.plugins.android.application)` y `alias(libs.plugins.kotlin.compose)`.
   El bloque `kotlin { compilerOptions { jvmTarget.set(...) } }` que ya está más abajo en ese
   archivo NO se toca — sigue funcionando igual, ahora sobre la extensión "kotlin" que crea AGP.

4. En gradle/libs.versions.toml: en la sección [plugins], BORRA la línea
   `kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }`.
   Deja android-application y kotlin-compose igual.

5. Sync Now. Si sale otro error, léelo completo, no asumas que es lo mismo, y dime la causa antes
   de tocar nada (regla 7).

6. Cuando compile: pídeme que borre (si sigue ahí) ui/login/LoginUiState.kt y
   ui/login/LoginViewModel.kt (ver sección 5), o hazlo tú directo si tienes shell con permiso de
   borrar. Luego Sync Now otra vez.

7. Pídeme correr el backend (`python app.py` en E:\jueves tri6\ecomers_api\Ecommerce-Api-Python)
   y darle Run ▶ en Android Studio, probar login con admin@ecommerce.com / admin123, y revisar
   Logcat (filtro OkHttp).

===========================================================================
8. QUÉ SIGUE DESPUÉS DE QUE COMPILE Y CORRA EL LOGIN (en este orden, uno a la vez)
===========================================================================
Bloque 1 — Terminar GA1: agrega 2 retos de GA1-AA4-EV04 (elige entre: CircularProgressIndicator
mientras carga, errorBody() para distinguir 401 de 500, validación de "@" en el email, o método
register(...) en ApiService). Prepara el documento de evidencia (en E:\jueves tri6\evidencias\GA1\)
con espacios marcados [AQUÍ VA MI CAPTURA] para que yo pegue las capturas — dime exactamente
qué captura tomar en cada uno.

Bloque 2 — GA2 (lee GA3_Sesion_Navegacion_App.docx primero): Session.kt, Models.kt ampliado
(Role, ProfileResponse, UpdateProfileRequest, ChangePasswordRequest, MessageResponse),
ApiService.kt con @GET/@PUT/@Header, LoginScreen.kt guardando sesión y navegando, HomeScreen.kt,
EditProfileScreen.kt, ChangePasswordScreen.kt, MainActivity.kt con NavHost completo. Más 2 retos.

Bloque 3 — GA3 (lee GA3_Administracion_Usuarios_Roles.docx primero): Models.kt con
UsersListResponse/RegisterRequest/RegisterResponse/UpdateRolesRequest, ApiService.kt con
getUsers/register/updateRoles (@Path), botón admin condicionado con `if (Session.isAdmin)` en
HomeScreen.kt, UsersScreen.kt con lista + creación de administrador en dos pasos (register →
updateRoles con role_ids = listOf(1)). Más 2 retos.

Bloque 4 — Evidencias escritas de las 3 guías (reflexiones, esquemas, tablas de permisos,
documento de retos) en E:\jueves tri6\evidencias\, con marcas [AQUÍ VA MI CAPTURA].

Bloque 5 — SOLO cuando yo dé el visto bueno: MiPrimeraApp (Guía 2, Fundamentos de Compose) y
luego el proyecto propio de la GA4. Para la GA4 las decisiones (problema a resolver, backend,
pantallas, matriz de permisos, "versión 2") son MÍAS — ayúdame con preguntas y ejemplos, no las
tomes tú, y espera mi respuesta.

Actualiza E:\jueves tri6\PROGRESO.md al final de cada bloque con qué se hizo, qué falta, qué
decidí yo y qué preguntas quedaron abiertas — sin mencionar Claude/IA en ningún lado, autor
"Daniel Salas Román — Ficha 3223873" donde haga falta.

EMPIEZA AHORA por la sección 7 (el bloqueo de Gradle). No me des un plan para aprobar: hazlo, y
dime qué encontraste y qué cambiaste.
