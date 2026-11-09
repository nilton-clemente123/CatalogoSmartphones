# Catálogo de Smartphones 📱

Aplicación móvil Android desarrollada con **Kotlin y Jetpack Compose** que permite consultar un catálogo de smartphones, visualizar sus detalles, gestionar favoritos y conservar información localmente. Incluye autenticación mediante Firebase y control de acceso según el rol del usuario.

## 1. Información del proyecto

| Característica | Descripción |
|---|---|
| Nombre | Catálogo de Smartphones |
| Tema | Productos |
| Variante | Smartphones |
| Plataforma | Android |
| Lenguaje | Kotlin |
| Interfaz | Jetpack Compose + Material 3 |
| Arquitectura | MVVM |
| API | DummyJSON |
| Base de datos local | Room |
| Autenticación | Firebase Authentication |
| Base de datos en la nube | Cloud Firestore |

## 2. Funcionalidades

### Catálogo de smartphones
- Consulta de smartphones mediante DummyJSON.
- Visualización de información y detalles de cada producto.
- Estados de carga, error y lista vacía.
- Opción para volver a intentar la consulta.

### Favoritos
- Marcar y desmarcar smartphones como favoritos.
- Consultar los smartphones favoritos en una pantalla independiente.
- Persistencia de favoritos mediante Room.

### Funcionamiento sin conexión
- Almacena localmente los smartphones descargados.
- Permite consultar los datos guardados cuando no hay conexión a Internet.
- Muestra un aviso cuando no es posible actualizar el catálogo.

### Autenticación y roles
- Inicio de sesión mediante correo electrónico y contraseña.
- Autenticación con Firebase Authentication.
- Consulta del rol del usuario desde Cloud Firestore.
- Dos roles: administrador y usuario.
- Persistencia de sesión y opción para cerrar sesión.

### Panel de administración
Disponible únicamente para usuarios con rol `admin`.

Incluye:
- Total de smartphones guardados.
- Total de smartphones favoritos.
- Botón para sincronizar el catálogo.
- Botón para limpiar la caché local.

### Notificaciones locales
- Notifica cuando termina correctamente una sincronización.
- Informa la cantidad de elementos guardados.
- Utiliza canales de notificación de Android.
- Solicita el permiso de notificaciones cuando corresponde.

## 3. Tecnologías utilizadas

| Tecnología | Utilización |
|---|---|
| Kotlin | Lenguaje de programación. |
| Jetpack Compose | Construcción de interfaces. |
| Material 3 | Componentes visuales. |
| MVVM | Separación de responsabilidades. |
| Retrofit | Consumo de servicios REST. |
| DummyJSON | Fuente de información de smartphones. |
| Room | Persistencia local con SQLite. |
| Firebase Authentication | Autenticación de usuarios. |
| Cloud Firestore | Almacenamiento de roles y perfiles. |
| Kotlin Coroutines | Operaciones asíncronas. |
| Navigation Compose | Navegación entre pantallas. |
| KSP | Generación de código para Room. |

## 4. API utilizada

**DummyJSON**

URL base:

`https://dummyjson.com/`

Endpoints utilizados:

| Método HTTP | Endpoint | Descripción |
|---|---|---|
| GET | `/products/category/smartphones` | Obtiene la lista de smartphones. |
| GET | `/products/{id}` | Obtiene los detalles de un smartphone. |

La API proporciona los datos que Retrofit obtiene y que posteriormente se almacenan en Room.

## 5. Arquitectura del proyecto

Se utiliza el patrón arquitectónico MVVM junto con el patrón Repository.

```text
com.tecsup.smartphones/
│
├── MainActivity.kt
├── AppContainer.kt
│
├── data/
│   ├── auth/
│   │   ├── AuthRepository.kt
│   │   └── FirebaseAuthRepository.kt
│   │
│   ├── local/
│   │   ├── ItemEntity.kt
│   │   ├── ItemDao.kt
│   │   └── AppDatabase.kt
│   │
│   ├── remote/
│   │   ├── ApiService.kt
│   │   ├── RetrofitClient.kt
│   │   └── dto/
│   │
│   ├── repository/
│   │   └── ItemRepositoryImpl.kt
│   │
│   └── mapper/
│       └── ItemMapper.kt
│
├── domain/
│   └── model/
│       ├── Item.kt
│       └── Sesion.kt
│
├── notification/
│   ├── Notificador.kt
│   └── NotificadorAndroid.kt
│
└── ui/
    ├── navigation/
    ├── login/
    ├── lista/
    ├── detalle/
    ├── favoritos/
    ├── admin/
    ├── components/
    └── theme/
```

### Responsabilidades principales

- **Data:** gestiona el acceso a la API, la base de datos local y Firebase.
- **Domain:** contiene los modelos principales de la aplicación.
- **UI:** contiene las pantallas, ViewModels y navegación.
- **Notification:** define e implementa el sistema de notificaciones.
- **AppContainer:** proporciona las dependencias necesarias a los repositorios y ViewModels.

## 6. Requisitos previos

Para ejecutar el proyecto se necesita:

- Android Studio.
- JDK compatible con la configuración Gradle del proyecto.
- Android SDK.
- Conexión a Internet para descargar dependencias y consultar DummyJSON.
- Un proyecto configurado en Firebase.

## 7. Instalación y ejecución

### 7.1 Clonar el repositorio

```bash
git clone https://github.com/nilton-clemente123/CatalogoSmartphones.git
```

Ingresar al directorio:

```bash
cd CatalogoSmartphones
```

### 7.2 Configurar Firebase

Para conectar Firebase:

1. Crear un proyecto en Firebase Console.
2. Registrar una aplicación Android con el identificador `com.tecsup.smartphones`.
3. Descargar el archivo `google-services.json`.
4. Colocar el archivo dentro del directorio `app/`.
5. Activar Email/Password en Firebase Authentication.
6. Crear la base de datos Cloud Firestore.

**Importante:** el archivo `google-services.json` no debe subirse al repositorio Git.

### 7.3 Sincronizar Gradle

Abrir el proyecto en Android Studio y ejecutar:

`Sync Project with Gradle Files`

### 7.4 Ejecutar la aplicación

Desde Android Studio, seleccionar un emulador o dispositivo Android y presionar Run.

También se puede compilar desde la terminal:

```bash
./gradlew assembleDebug
```

Para instalar la versión de depuración en un dispositivo conectado:

```bash
./gradlew installDebug
```

## 8. Configuración de usuarios de prueba

Los usuarios se crean desde Firebase Authentication. No se requiere implementar un registro dentro de la aplicación.

### Crear las cuentas

En Firebase Console:

1. Entrar a Authentication.
2. Seleccionar Usuarios.
3. Crear una cuenta para el administrador.
4. Crear otra cuenta para el usuario normal.

### Configurar los roles en Firestore

Crear una colección llamada:

`usuarios`

Cada documento debe utilizar como identificador el UID correspondiente al usuario de Firebase Authentication.

Estructura del administrador:

```text
usuarios/
└── UID_DEL_ADMIN/
    ├── rol: "admin"
    └── nombre: "Administrador"
```

Estructura del usuario:

```text
usuarios/
└── UID_DEL_USUARIO/
    ├── rol: "usuario"
    └── nombre: "Usuario"
```

Los campos `rol` y `nombre` deben ser de tipo String.

No se deben utilizar los textos de ejemplo como UID: hay que colocar los identificadores reales generados por Firebase.

### Roles y permisos de visualización

| Funcionalidad | Administrador | Usuario |
|---|---|---|
| Iniciar sesión | Sí | Sí |
| Consultar smartphones | Sí | Sí |
| Ver detalles | Sí | Sí |
| Gestionar favoritos | Sí | Sí |
| Consultar favoritos | Sí | Sí |
| Cerrar sesión | Sí | Sí |
| Acceder al panel Admin | Sí | No |
| Sincronizar desde el panel | Sí | No |
| Limpiar caché desde el panel | Sí | No |

## 9. Capturas de pantalla

Las siguientes capturas documentan las principales funcionalidades de la aplicación.

Guardar las imágenes dentro de `docs/capturas/`.

### Inicio de sesión

![Pantalla de inicio de sesión](docs/capturas/login.png)

### Catálogo de smartphones

![Listado de smartphones](docs/capturas/lista.png)

### Detalle de smartphone

![Detalle de smartphone](docs/capturas/detalle.png)

### Favoritos

![Pantalla de favoritos](docs/capturas/favoritos.png)

### Panel de administración

![Panel de administración](docs/capturas/admin.png)

### Notificación local

![Notificación de sincronización](docs/capturas/notificacion.png)

> Reemplazar las imágenes de ejemplo por capturas reales de la aplicación antes de entregar el proyecto.

## 10. Pruebas realizadas

Se deben verificar los siguientes escenarios:

- Inicio de sesión con administrador y usuario normal.
- Validación de credenciales incorrectas.
- Visualización del catálogo y detalle.
- Agregar y quitar favoritos.
- Persistencia de favoritos al cerrar la aplicación.
- Visualización de datos guardados sin conexión.
- Error y reintento cuando no hay datos locales.
- Sincronización y limpieza de caché desde el panel Admin.
- Persistencia de sesión.
- Notificación al terminar una sincronización.
- Funcionamiento de la aplicación cuando se rechaza el permiso de notificaciones.

## 11. Desarrollo del proyecto

Proyecto desarrollado individualmente como parte del curso **Programación en Móviles**, correspondiente a la carrera de Diseño y Desarrollo de Software de TECSUP.

Las actividades incluyeron el diseño de la arquitectura, implementación de la interfaz, consumo de API, persistencia local, autenticación, control de roles y notificaciones.

## 12. Autor

**Desarrollador:** Nilton Clemente  
**Institución:** TECSUP  
**Carrera:** Diseño y Desarrollo de Software  
**Curso:** Programación en Móviles

---

**Repositorio:** [CatalogoSmartphones - GitHub](https://github.com/nilton-clemente123/CatalogoSmartphones)