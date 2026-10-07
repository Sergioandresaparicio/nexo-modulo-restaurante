# NEXO · Plantilla de gestión y seguridad

Proyecto completo con **backend Java 21 / Spring Boot 4.1.1 / PostgreSQL** y
**frontend HTML, CSS y JavaScript**. Incluye autenticación JWT, roles y permisos,
y módulos de clientes, productos y ventas.

Esta carpeta reúne ambas aplicaciones. El backend valida las reglas de negocio
y autoriza cada operación; el frontend ofrece una interfaz acorde a los permisos
de la sesión.

## Estructura

```text
sguridad java 21/
├── README.md                            Guía general
├── spring-security-user-role-management/
│   ├── README.md                        Guía del backend
│   ├── build.gradle                     Dependencias y compilación
│   ├── gradlew.bat                      Gradle Wrapper para Windows
│   ├── src/main/java/                   Seguridad y módulos comerciales
│   ├── src/main/resources/              Perfiles, banner y migración SQL
│   ├── src/test/                        Pruebas del backend
│   └── docs/PLANTILLA_MODULOS.md         Guía para ampliar módulos
└── Frontend/
    ├── README.md                        Guía de la interfaz
    ├── index.html                       Página de entrada
    ├── styles.css                       Diseño responsive
    ├── app.js                           Cliente REST y flujos
    └── vista-previa.png                  Captura con datos de prueba
```

## Guías

- [Backend: configuración, endpoints y permisos](spring-security-user-role-management/README.md).
- [Frontend: ejecución, pantallas y sesión](Frontend/README.md).
- [Módulos: ejemplos y extensión de la plantilla](spring-security-user-role-management/docs/PLANTILLA_MODULOS.md).

## Requisitos

- JDK 21 para ejecutar y compilar el backend.
- PostgreSQL con una base de datos disponible.
- Navegador moderno para el frontend.
- Gradle Wrapper incluido; no necesitas instalar Gradle.
- Opcional: Python o Live Server para servir el frontend por HTTP.

El frontend no necesita Node.js, paquetes npm ni compilación.

## Inicio rápido

### 1. Preparar PostgreSQL

Inicia PostgreSQL y crea la base desde pgAdmin o tu cliente SQL:

```sql
CREATE DATABASE sistema_gestion_usuarios;
```

La aplicación crea o actualiza las tablas dentro de esa base con
`spring.jpa.hibernate.ddl-auto=update` en desarrollo. No crea el servidor ni
importa automáticamente datos de MySQL.

### 2. Iniciar el backend

Abre una terminal PowerShell:

```powershell
cd 'D:\Spring Boot Proyectos\sguridad java 21\spring-security-user-role-management'
$env:DB_URL = 'jdbc:postgresql://localhost:5432/sistema_gestion_usuarios'
$env:DB_USERNAME = 'postgres'
$env:DB_PASSWORD = '<contraseña de tu PostgreSQL>'
.\gradlew.bat bootRun
```

Por defecto usa el perfil `dev` y escucha en `http://localhost:8050`.
El banner NEXO muestra las versiones de Java, Spring Boot y el perfil activo.

### 3. Abrir el frontend

Abre [Frontend/index.html](Frontend/index.html) en el navegador o sirve la carpeta.
Por ejemplo, si tienes Python instalado, abre otra terminal:

```powershell
cd 'D:\Spring Boot Proyectos\sguridad java 21\Frontend'
python -m http.server 5500 --bind 127.0.0.1
```

Abre [http://127.0.0.1:5500](http://127.0.0.1:5500). En el login, despliega
**Conexión al servidor** y usa `http://localhost:8050`, sin `/api` al final.
El backend debe seguir ejecutándose mientras utilizas la interfaz.

### 4. Iniciar sesión

Si la cuenta inicial de desarrollo no fue modificada:

| Campo | Valor |
| --- | --- |
| Usuario | `superadmin` |
| Contraseña | `SuperAdmin123!` |
| Rol | `ADMIN` |

Las credenciales de la aplicación son diferentes de las de PostgreSQL. El
bootstrap no reemplaza datos de una cuenta inicial que ya existe.

En Swagger también puedes ejecutar `POST /api/auth/login`. La respuesta es un
JWT como texto; pégalo en **Authorize** sin escribir Bearer.

| Recurso | Dirección de desarrollo |
| --- | --- |
| Backend | http://localhost:8050 |
| Swagger UI | http://localhost:8050/swagger-ui.html |
| OpenAPI JSON | http://localhost:8050/v3/api-docs |
| Frontend con el servidor de ejemplo | http://127.0.0.1:5500 |

## Funcionalidades

| Módulo | Alcance |
| --- | --- |
| Autenticación | Login, JWT y consulta de sesión actual |
| Usuarios | Alta, edición, habilitación, bloqueo, eliminación y accesos |
| Roles | Catálogo dinámico y permisos compartidos por rol |
| Permisos | Catálogo y concesiones individuales |
| Clientes | Gestión de contactos y desactivación |
| Productos | Catálogo, SKU único, precios y existencias |
| Ventas | Registro, detalle histórico, total calculado y anulación |
| Inicio | Métricas y accesos rápidos según la cuenta |

Las ventas usan precios de la base, calculan importes con BigDecimal y descuentan
inventario en una transacción. Si fallan, los cambios se revierten. Anular una
venta repone existencias una sola vez y conserva el detalle original. Desactivar
clientes o productos no elimina el historial de ventas.

## Cómo funciona la seguridad

Cada usuario tiene **un solo rol**. Sus permisos efectivos son la unión de los
permisos del rol y los permisos individuales. Así, dos usuarios VENDEDOR pueden
compartir las operaciones habituales, mientras solo uno recibe SALE_CANCEL.

```text
Login → JWT → Sesión actual → Menús disponibles → Operación protegida
                                                    ↓
                                      Validación de permisos en backend
```

El frontend conserva el JWT en `sessionStorage` y la URL de la API en
`localStorage`; no guarda contraseñas. La navegación se adapta a los accesos
recibidos de `/api/auth/me`. El servidor vuelve a verificar permisos y estado
de cuenta en cada petición: ocultar botones no sustituye la seguridad.

ADMIN recibe los permisos base al arrancar. CUSTOMER recibe `random_order`;
no tiene acceso comercial automático. Roles personalizados y permisos adicionales
se gestionan desde la API o la interfaz. Cambiar el rol conserva los permisos
individuales. Bloquear, deshabilitar o eliminar una cuenta impide usar sus tokens.
No hay renovación automática ni lista de revocación de JWT.

## Configuración por entorno

La configuración del backend se encuentra en:

- `application.properties`: selecciona `dev` por defecto.
- `application-dev.properties`: conexión, puerto, bootstrap y JWT de desarrollo.
- `application-test.properties`: configuración independiente para pruebas.
- `application-prod.properties`: archivo que puedes crear para producción;
  actualmente no se incluye.

JWT en **application-dev.properties**:

```properties
security.jwt.secret=${JWT_SECRET:User_M4n4gement}
security.jwt.issuer=${JWT_ISSUER:User_R0les_M4n4gement}
security.jwt.expiration=${JWT_EXPIRATION:15d}
```

La duración admite `30m`, `8h`, `15d` o ISO-8601. Puedes reemplazar los valores
con JWT_SECRET, JWT_ISSUER y JWT_EXPIRATION. Reinicia el backend después de
modificarlos. Cambiar clave o emisor invalida tokens anteriores; cambiar duración
solo afecta a los emitidos después del cambio.

Para usar un futuro perfil prod, define sus propiedades y ejecuta con
`SPRING_PROFILES_ACTIVE=prod`. Ese perfil no hereda application-dev.properties.
Utiliza claves, credenciales y orígenes CORS propios del entorno. La configuración
CORS actual permite cualquier origen. Un frontend servido por HTTPS necesita
una API HTTPS.

## Flujo para probar el proyecto

1. Entra con el administrador.
2. Crea un cliente y un producto con existencias.
3. Registra una venta y consulta su detalle.
4. Anula la venta y verifica la reposición del inventario.
5. Crea un rol VENDEDOR con permisos comerciales de consulta y creación.
6. Crea una cuenta y asígnale ese rol.
7. Dale SALE_CANCEL como permiso individual si debe anular ventas.
8. Inicia sesión con esa cuenta y comprueba sus menús y acciones.

Encontrarás cuerpos JSON y la tabla completa de endpoints en el README del backend.
Las cuentas de seguridad y los clientes comerciales son entidades independientes.

## Compilación y verificación

Desde la carpeta del backend:

```powershell
.\gradlew.bat test
.\gradlew.bat clean test bootJar
java -jar build/libs/usermanagement-0.0.1-SNAPSHOT.jar
```

Las pruebas usan H2 en modo PostgreSQL y una configuración JWT propia; no necesitan
modificar la base de desarrollo. Cubren seguridad, catálogos, Swagger, sesión,
JWT configurable y operaciones comerciales, incluidas ventas concurrentes,
rollback y anulación.

Desde la carpeta del frontend, si tienes Node.js instalado:

```powershell
node --check app.js
```

Node.js solo sirve para esa comprobación opcional. La interfaz se verificó en el
navegador contra Spring Boot/H2 temporal. La seguridad se probó anteriormente
contra esquemas PostgreSQL aislados; las últimas ampliaciones comerciales y JWT
se verificaron con H2 porque PostgreSQL local estaba apagado. No se incluye una
suite automatizada de pruebas del navegador.

## Ampliar la plantilla

Los módulos comerciales del backend están en `modules/customer`, `modules/product`
y `modules/sale`, separados en entidades, DTO, repositorios, servicios y
controladores. Usa su estructura para añadir funcionalidades y protege cada
operación con `@PreAuthorize` y el permiso correspondiente.

En el frontend, registra el módulo en `sections` de `app.js`, sus condiciones de
visibilidad y sus formularios/listados. Mantén las validaciones del servidor y
el escape de datos al renderizar HTML. Crear una entrada en el catálogo de
permisos no crea automáticamente un endpoint ni una pantalla.

La plantilla no incluye pagos, impuestos, facturación fiscal, múltiples monedas,
devoluciones parciales ni aislamiento por empresa/propietario. Estos comportamientos
se implementan según las necesidades del proyecto que la utilice.

## Problemas frecuentes

| Problema | Revisión |
| --- | --- |
| Backend no inicia | JDK 21, PostgreSQL activo, base creada y credenciales correctas |
| Frontend no conecta | URL de API, puerto, backend activo, CORS y HTTP/HTTPS |
| Nuevas tablas no aparecen | Arrancar el backend contra la base correcta; Hibernate actualiza al iniciar |
| No aparece un módulo | Permisos efectivos en Mi espacio; actualizar la interfaz |
| Login rechazado | Contraseña y estados de cuenta; no usar la contraseña de PostgreSQL |
| Token rechazado tras cambiar configuración | Volver a iniciar sesión |
| SKU repetido o stock insuficiente | Revisar catálogo y existencias; no repetir una venta sin consultar su historial |

![NEXO: vista previa con datos de prueba](Frontend/vista-previa.png)

---
## 👨‍💻 Autores

Jhon Reyes
Sebastian Ayala
Sergio Aparicio

**Ing. Cristian Díaz**

<p align="center">
  <img width="300" src="https://i.imgur.com/a7YBcsp.png">
</p>
