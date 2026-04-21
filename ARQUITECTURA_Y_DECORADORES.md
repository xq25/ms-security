# 🏗️ Arquitectura y Decoradores - MS Security

## 📊 Flujo de Capas (Arquitectura en Capas)

```
┌─────────────────────────────────────────────────────────────────┐
│                        CLIENTE (Frontend)                        │
│                     (Angular en :4200)                           │
└────────────────────────────┬──────────────────────────────────────┘
                             │ HTTP Request
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                    CAPA 1: CORS FILTER                           │
│                   (Validación de origen)                         │
│  @Bean CorsFilter → Resuelve preflight OPTIONS automaticamente  │
└────────────────────────────┬──────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                CAPA 2: SECURITY INTERCEPTOR                      │
│               (Portería de la aplicación)                        │
│  ✅ Valida rutas públicas (/api/public/**)                       │
│  ✅ Valida Token JWT para rutas protegidas (/api/**)             │
│  ✅ Verifica permisos según Roles                                │
│  ✅ Retorna 401 (Token inválido) o 403 (Sin permisos)            │
└────────────────────────────┬──────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│              CAPA 3: CONTROLLERS (Presentación)                  │
│                                                                   │
│  • SecurityController  → Login, Register, Logout                 │
│  • UserController      → CRUD de usuarios                        │
│  • RoleController      → Gestión de roles                        │
│  • PermissionController→ Gestión de permisos                     │
│  • SessionController   → Manejo de sesiones                      │
│  • PhotoController     → Gestión de fotos                        │
│  • ProfileController   → Manejo de perfiles                      │
└────────────────────────────┬──────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│              CAPA 4: SERVICES (Lógica de Negocio)                │
│                                                                   │
│  • SecurityService      → Orquesta login/register                │
│  • UserService          → Lógica de usuarios                     │
│  • JwtService           → Generación y validación de tokens      │
│  • EncryptionService    → Encriptación SHA256                    │
│  • FirebaseAuthService  → Autenticación con Firebase             │
│  • CaptchaService       → Validación de CAPTCHA                  │
│  • SessionService       → Manejo de sesiones activas             │
│  • NotificationService  → Envío de emails                        │
│  • PhotoService         → Guardado de fotos localmente           │
│  • ValidatorsService    → Validación de roles y permisos         │
│  • RoleService          → Lógica de roles                        │
│  • PermissionService    → Lógica de permisos                     │
└────────────────────────────┬──────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│           CAPA 5: REPOSITORIES (Acceso a Datos)                  │
│                                                                   │
│  • UserRepository           → Consultas a Users                  │
│  • SessionRepository        → Consultas a Sessions               │
│  • RoleRepository           → Consultas a Roles                  │
│  • PermissionRepository     → Consultas a Permissions            │
│  • ProfileRepository        → Consultas a Profiles               │
│  • PhotoRepository          → Consultas a Photos                 │
│  • RolePermissionRepository → Consultas a RolePermission         │
│  • UserRoleRepository       → Consultas a UserRole               │
└────────────────────────────┬──────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│          CAPA 6: MODELOS & BASE DE DATOS (MongoDB)               │
│                                                                   │
│  • Colección: users                                              │
│  • Colección: sessions                                           │
│  • Colección: roles                                              │
│  • Colección: permissions                                        │
│  • Colección: profiles                                           │
│  • Colección: photos                                             │
│  • Colección: role_permissions                                   │
│  • Colección: user_roles                                         │
└──────────────────────────────────────────────────────────────────┘
                             │
                             ▼
                    MongoDB (Base de Datos)
                   (mongodb+srv://jacobo:...)
```

---

## 🔄 Flujo de un Request (Ejemplo: Login)

```
1️⃣  CLIENTE envia POST /api/public/security/login
    └─ Body: {email, password, captchaToken}

2️⃣  CORS FILTER intercepta
    └─ Valida origen (http://localhost:4200)

3️⃣  SECURITY INTERCEPTOR intercepta
    └─ Verifica ruta: /api/public/** → ✅ SKIP (es pública)

4️⃣  SECURITY CONTROLLER maneja la request
    └─ Método: login(@RequestBody HashMap)
       └─ Extrae: email, password, captchaToken

5️⃣  SECURITY SERVICE orquesta la lógica
    ├─ CaptchaService.validate(token) → Valida CAPTCHA
    ├─ UserService.findByEmail(email) → Busca el usuario
    ├─ EncryptionService.convertSHA256(password) → Encripta contraseña
    ├─ JwtService.generateToken(user) → Genera JWT
    └─ SessionService.create(session) → Guarda sesión

6️⃣  REPOSITORIES acceden a MongoDB
    ├─ UserRepository.getUserByEmail(email) → Query @Query
    └─ SessionRepository.save(session) → Persiste

7️⃣  RESPUESTA retorna al cliente
    └─ HashMap {session: {...token, expiryDate...}}

8️⃣  CLIENTE recibe sesión y guarda token en localStorage
    └─ Próximos requests incluirán header: Authorization: Bearer <token>
```

---

## 🎯 Flujo de un Request Protegido (Ejemplo: GET /api/users/{id})

```
1️⃣  CLIENTE envia GET /api/users/123
    └─ Header: Authorization: Bearer <jwt_token>

2️⃣  CORS FILTER → ✅ PASS

3️⃣  SECURITY INTERCEPTOR valida
    ├─ Verifica ruta: /api/users/123 → ❌ NO es /api/public/**
    ├─ Extrae token del header Authorization
    ├─ ValidatorsService.validationRolePermission()
    │  ├─ JwtService.validateToken(token) → ✅ Valida JWT
    │  ├─ Obtiene userId del token
    │  ├─ UserService.findById(userId) → Obtiene usuario
    │  ├─ UserService.getRoles(userId) → Obtiene roles
    │  ├─ RoleService.getPermissions(roleId) → Obtiene permisos
    │  └─ Verifica si el usuario tiene permiso para GET /api/users/{id}
    │
    └─ Resultado: SUCCESS ✅ o PERMISSION_DENIED ❌

4️⃣ Si SUCCESS → continúa al Controller
   Si no → Retorna 401/403 con mensaje de error

5️⃣  USER CONTROLLER procesa GET /api/users/{id}
    └─ UserService.findById(id)

6️⃣  USER REPOSITORY consulta MongoDB
    └─ mongoRepository.findById(id)

7️⃣  RESPUESTA: JSON del usuario
```

---

## 📚 Decoradores Principales por Capas

### **🔴 CONFIGURACIÓN (@Configuration, @Bean)**

| Decorador | Ubicación | Función |
|-----------|-----------|---------|
| `@Configuration` | [FirebaseConfig.java](src/main/java/backend/ms_security/configurations/FirebaseConfig.java#L7) | Marca clase como configuración | 
| `@PostConstruct` | [FirebaseConfig.java](src/main/java/backend/ms_security/configurations/FirebaseConfig.java#L11) | Ejecuta método DESPUÉS de crear bean (inicializa Firebase) |
| `@Bean` | [WebConfig.java](src/main/java/backend/ms_security/configurations/WebConfig.java#L21) | Define bean `CorsFilter` para CORS |

**Ejemplo Firebase:**
```java
@Configuration
public class FirebaseConfig {
    @PostConstruct  // ✅ Se ejecuta cuando Spring crea el bean
    public void initFirebase() {
        // Inicializa Firebase ANTES de que los Controllers lo usen
    }
}
```

---

### **🟠 INYECCIÓN DE DEPENDENCIAS (@Autowired)**

| Decorador | Ubicación | Función |
|-----------|-----------|---------|
| `@Autowired` | [SecurityController.java](src/main/java/backend/ms_security/Controllers/SecurityController.java#L19) | Inyecta `SecurityService` automáticamente |
| `@Autowired` | [SecurityService.java](src/main/java/backend/ms_security/Services/SecurityService.java#L16) | Inyecta `UserService`, `JwtService`, etc. |
| `@Autowired` | [WebConfig.java](src/main/java/backend/ms_security/configurations/WebConfig.java#L18) | Inyecta `SecurityInterceptor` |

**Ejemplo:**
```java
@RestController
public class SecurityController {
    @Autowired
    private SecurityService theSecurityService;  // Spring lo instancia automáticamente
}
```

---

### **🟡 CONTROLADORES & ENRUTAMIENTO (@RestController, @RequestMapping, @GetMapping, @PostMapping, etc.)**

| Decorador | Ubicación | Función |
|-----------|-----------|---------|
| `@RestController` | [SecurityController.java](src/main/java/backend/ms_security/Controllers/SecurityController.java#L16) | Controller que retorna JSON (no vistas) |
| `@CrossOrigin` | [SecurityController.java](src/main/java/backend/ms_security/Controllers/SecurityController.java#L17) | Permite peticiones desde otros dominios |
| `@RequestMapping` | [SecurityController.java](src/main/java/backend/ms_security/Controllers/SecurityController.java#L18) | Define ruta base: `/api/public/security` |
| `@PostMapping` | [SecurityController.java](src/main/java/backend/ms_security/Controllers/SecurityController.java#L23) | Mapea POST `/api/public/security/register` |
| `@PutMapping` | [SecurityController.java](src/main/java/backend/ms_security/Controllers/SecurityController.java#L32) | Mapea PUT `/api/public/security/logout` |
| `@GetMapping` | [UserController.java](src/main/java/backend/ms_security/Controllers/UserController.java#L17) | Mapea GET `/api/users` |
| `@DeleteMapping` | [UserController.java](src/main/java/backend/ms_security/Controllers/UserController.java#L29) | Mapea DELETE `/api/users/{id}` |

**Ejemplo:**
```java
@RestController
@CrossOrigin
@RequestMapping("/api/public/security")
public class SecurityController {
    
    @PostMapping("login")  // POST /api/public/security/login
    public HashMap<String, Object> login(@RequestBody HashMap<String, String> body) {
        // ...
    }
}
```

---

### **🟢 PARÁMETROS (@RequestBody, @PathVariable, @RequestParam)**

| Decorador | Ubicación | Función |
|-----------|-----------|---------|
| `@RequestBody` | [SecurityController.java](src/main/java/backend/ms_security/Controllers/SecurityController.java#L25) | Extrae JSON del body y lo mapea a objeto |
| `@PathVariable` | [UserController.java](src/main/java/backend/ms_security/Controllers/UserController.java#L22) | Extrae parámetro de la URL: `{id}` |

**Ejemplo:**
```java
@PutMapping("{id}")
public User update(
    @PathVariable String id,           // Extrae /api/users/{id}
    @RequestBody User newUser          // Extrae body JSON
) {
    return this.theUserService.update(id, newUser);
}
```

---

### **🔵 SERVICIOS (@Service, @Autowired)**

| Decorador | Ubicación | Función |
|-----------|-----------|---------|
| `@Service` | [SecurityService.java](src/main/java/backend/ms_security/Services/SecurityService.java#L13) | Marca clase como servicio (componente gestión de negocio) |
| `@Autowired` | [SecurityService.java](src/main/java/backend/ms_security/Services/SecurityService.java#L16-32) | Inyecta múltiples servicios |
| `@Value` | [SecurityService.java](src/main/java/backend/ms_security/Services/SecurityService.java#L33) | Lee propiedades de `application.properties` |

**Ejemplo:**
```java
@Service
public class SecurityService {
    @Autowired
    private UserService theUserService;      // Inyecta UserService
    
    @Autowired
    private JwtService theJwtService;        // Inyecta JwtService
    
    @Value("${rest.expiration}")             // Lee de properties
    private Long RESET_TOKEN_EXPIRATION;
}
```

---

### **🟣 REPOSITORIOS (@Repository, @Query)**

| Decorador | Ubicación | Función |
|-----------|-----------|---------|
| `@Repository` | [UserRepository.java](src/main/java/backend/ms_security/Repositories/UserRepository.java#L6) | Implícito al extender `MongoRepository` |
| `@Query` | [UserRepository.java](src/main/java/backend/ms_security/Repositories/UserRepository.java#L9) | Define consulta personalizada en MongoDB |

**Ejemplo:**
```java
public interface UserRepository extends MongoRepository<User, String> {
    
    @Query("{'email': ?0}")  // Consulta MongoDB personalizada
    public User getUserByEmail(String email);
}
```

---

### **🟠 MODELOS (Lombok - @Data, @Document, @Id)**

| Decorador | Ubicación | Función |
|-----------|-----------|---------|
| `@Data` | [User.java](src/main/java/backend/ms_security/Models/User.java#L6) | 🚀 Genera automáticamente getters, setters, toString(), equals(), hashCode() |
| `@Document` | [User.java](src/main/java/backend/ms_security/Models/User.java#L7) | Marca como documento MongoDB (crea colección) |
| `@Id` | [User.java](src/main/java/backend/ms_security/Models/User.java#L9) | Define campo como clave primaria en MongoDB |

**Ejemplo:**
```java
@Data                           // ✨ Genera getters/setters/toString/equals
@Document                       // 📦 Crea colección "user" en MongoDB
public class User {
    @Id                         // 🔑 Primary Key
    private String id;
    private String name;
    private String email;
    private String password;
}
```

---

### **🔴 INTERCEPTORES (@Component, implements HandlerInterceptor)**

| Decorador | Ubicación | Función |
|-----------|-----------|---------|
| `@Component` | [SecurityInterceptor.java](src/main/java/backend/ms_security/interceptors/SecurityInterceptor.java#L14) | Registra la clase como componente Spring |
| `implements HandlerInterceptor` | [SecurityInterceptor.java](src/main/java/backend/ms_security/interceptors/SecurityInterceptor.java#L16) | Implementa métodos de interceptación |

**Métodos del Interceptor:**
```java
@Component
public class SecurityInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(...)   // ✅ ANTES del Controller
        // Valida token, roles, permisos
        // Si retorna false → bloquea acceso
    
    @Override
    public void postHandle(...)     // 👁️ DESPUÉS del Controller
        // Modificar respuesta si es necesario
    
    @Override
    public void afterCompletion(...) // 🔚 DESPUÉS de todo
        // Limpieza, logging
}
```

---

## 🔐 Flujo de Seguridad Detallado

### **1. Request a ruta PÚBLICA (/api/public/security/login)**

```
┌─────────────────────────────────┐
│ POST /api/public/security/login │
└────────────┬────────────────────┘
             │
             ▼
    ┌────────────────────┐
    │  CORS FILTER       │
    │  ✅ PASS           │
    └────────┬───────────┘
             │
             ▼
    ┌────────────────────────────────┐
    │ SECURITY INTERCEPTOR           │
    │ addPathPatterns: /api/**        │
    │ excludePathPatterns: /api/public/**
    │ ✅ SKIP (es pública)          │
    └────────┬───────────────────────┘
             │
             ▼
    ┌────────────────────┐
    │ SECURITY CONTROLLER│
    │ login() method     │
    └────────┬───────────┘
             │
             ▼
    ┌────────────────────────────┐
    │ SECURITY SERVICE           │
    │ ✅ Valida CAPTCHA          │
    │ ✅ Encuentra usuario       │
    │ ✅ Genera JWT token        │
    │ ✅ Crea sesión             │
    └────────┬───────────────────┘
             │
             ▼
    ┌────────────────────┐
    │ USER REPOSITORY    │
    │ Busca en MongoDB   │
    └────────┬───────────┘
             │
             ▼
    ┌────────────────────┐
    │ Retorna sesión     │
    │ + JWT token        │
    └────────────────────┘
```

### **2. Request a ruta PROTEGIDA (/api/users/123)**

```
┌────────────────────────────────────┐
│ GET /api/users/123                 │
│ Header: Authorization: Bearer <JWT>│
└────────┬─────────────────────────────┘
         │
         ▼
    ┌─────────────────────────┐
    │ CORS FILTER             │
    │ ✅ PASS                 │
    └────────┬────────────────┘
             │
             ▼
    ┌──────────────────────────────────────────────┐
    │ SECURITY INTERCEPTOR preHandle()             │
    │ addPathPatterns: /api/**                      │
    │ excludePathPatterns: /api/public/**           │
    │ ❌ NO SKIP (es protegida)                     │
    │                                              │
    │ 1. Extrae header Authorization               │
    │ 2. Valida JWT token                          │
    │ 3. Obtiene userId del token                  │
    │ 4. ValidatorsService.validationRolePermission│
    │    ├─ Obtiene User de DB                     │
    │    ├─ Obtiene Roles del User                 │
    │    ├─ Obtiene Permissions de Roles           │
    │    └─ Verifica permiso para GET /api/users   │
    │                                              │
    │ Resultado: SUCCESS ✅ o error 401/403        │
    └────────┬───────────────────────────────────┘
             │
         SUCCESS?
         /        \
       YES         NO
       │           │
       ▼           ▼
    CONTROLLER   401/403 Error
    │            Returned
    ▼
USER CONTROLLER
findById()
    │
    ▼
USER SERVICE
findById()
    │
    ▼
USER REPOSITORY
findById()
    │
    ▼
MongoDB
    │
    ▼
Return User JSON
```

---

## 🎨 Resumen de Decoradores por Nivel

| Nivel | Decorador | Propósito |
|-------|-----------|----------|
| **Aplicación** | `@SpringBootApplication` | Punto de entrada |
| **Configuración** | `@Configuration`, `@Bean`, `@PostConstruct` | Configura componentes |
| **Interceptores** | `@Component`, `implements HandlerInterceptor` | Valida requests |
| **Controllers** | `@RestController`, `@CrossOrigin`, `@RequestMapping` | Define endpoints |
| **Rutas** | `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping` | Mapea HTTP methods |
| **Parámetros** | `@RequestBody`, `@PathVariable`, `@RequestParam` | Extrae datos |
| **Servicios** | `@Service`, `@Autowired`, `@Value` | Lógica de negocio |
| **Repositorios** | `@Query` | Consultas personalizadas |
| **Modelos** | `@Data`, `@Document`, `@Id` | Mapeo a BD |
| **Inyección** | `@Autowired` | Inyecta dependencias |

---

## 🚀 Propiedades Clave (application.properties)

```properties
# Servidor
spring.application.name=ms-security
server.port=8080

# MongoDB
spring.mongodb.uri=mongodb+srv://jacobo:...
spring.mongodb.database=db_security

# Seguridad
jwt.secret=aaa
jwt.expiration=3600000              # 1 hora
rest.expiration=360000              # 100 minutos

# CAPTCHA
captcha.key=6LcykpEsAAAAAPjZv...

# Notificaciones
notifications.url=http://127.0.0.1:5000

# Almacenamiento
app.storage.location=uploads/photos
app.base.url=http://localhost:8080
```

---

## 📌 Puntos Clave

✅ **Las rutas públicas (`/api/public/**`)** no pasan por el `SecurityInterceptor`  
✅ **Las rutas protegidas (`/api/**`)** validan Token JWT + Roles/Permisos  
✅ **@Autowired** inyecta dependencias automáticamente  
✅ **@Data** (Lombok) evita escribir getters/setters manualmente  
✅ **@PostConstruct** ejecuta código después de crear el bean (inicializa Firebase)  
✅ **@Query** permite consultas personalizadas en MongoDB  
✅ **CORS Filter** resuelve problemas de origen en preflight OPTIONS  

