Índice
Descripción del Proyecto
Estado del Proyecto
Demostración de Funciones y Aplicaciones
Acceso al Proyecto
Tecnologías Utilizadas
Personas Contribuyentes
Personas Desarrolladoras del Proyecto

1. Descripción del Proyecto
Foro_Hub es un sistema de gestión de foros educativos diseñado para facilitar la creación, administración y solución de temas académicos. El proyecto incluye:
Gestión de cursos: Registro y consulta de cursos con validación de nombres (solo letras).
Gestión de tópicos: Creación, actualización y eliminación lógica de tópicos (estado activo = 0).
Sistema de soluciones: Un único tópico puede tener una solución marcada como RESUELTO.
Autenticación segura: Uso de JWT para generación de tokens (ej: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...).
La base de datos principal es vollmed_forohub, con tablas: curso, respuesta, topico, usuarios y flyway_schema_history.

2. Estado del Proyecto
En desarrollo activo con pruebas completadas en la base de datos de prueba vollmed_forohub_test.
Funcionalidades clave implementadas:
Autenticación con token JWT.
Validaciones de entrada (ej: título de tópico ≥ 10 caracteres).
Reglas de negocio (ej: un tópico solo puede tener una solución).
Archivo generado: ForoHub-0.0.1-SNAPSHOT.jar.original.

3. Demostración de Funciones y Aplicaciones
3.1. Inicio de Sesión
Endpoint: POST /login (Insomnia: INICIO DE SESION > Iniciar Sesión).
Entrada: Correo (ej: carlospalacios@voll.med) y clave hash generada via GenerarHash.java.
Salida: Token JWT para acceder a endpoints protegidos.
3.2. Gestión de Cursos
Consulta de cursos: GET /cursos (Insomnia: CONSULTAS > Cursos Existentes).
Registro de cursos: POST /cursos (Insomnia: REGISTRAR > Registro de Cursos).
Validaciones:
Nombre: Solo letras (ej: Matemáticas).
Categoría: Solo letras (ej: Ciencias).
3.3. Gestión de Tópicos (CRUD)
Registro: POST /topicos (ej: título ≥ 10 caracteres, cursoId numérico).
Mensaje de error si el título ya existe: "Ya existe un tópico con ese título y mensaje".
Actualización: PUT /topicos/{id} (Insomnia: ACTUALIZAR).
Eliminación lógica: DELETE /topicos/{id} (estado activo = 0 en la BD).
Consulta por curso/año: GET /topicos/curso?nombreCurso=...&ano=....
3.4. Sistema de Soluciones
Dar solución: POST /respuestas (ej: solucion=true).
Reglas:
El autor no puede marcar su propia respuesta como solución.
Mensaje de error si el tópico ya está resuelto: "El tópico ID 5 ya cuenta con una solución dada".
Consulta de tópicos resueltos: GET /topicos/soluciones/{id}.

4. Acceso al Proyecto
Configuración local:
Ejecutar ForoHub-0.0.1-SNAPSHOT.jar.original.
Base de datos: vollmed_forohub (usuario: root, contraseña: password).
Usar Insomnia:
Importar el archivo de colección de Insomnia incluido en el proyecto.
Generar token mediante INICIO DE SESION > Iniciar Sesión.
Usar el token en el header Authorization: Bearer <token>.

5. Tecnologías Utilizadas
Backend: Java 17, Spring Boot 3.0, Spring Data JPA.
Base de datos: MySQL 8.0, Flyway (migraciones).
API Testing: Insomnia.
Seguridad: JWT (JSON Web Tokens).
Pruebas: JUnit, Mockito (con TopicoRepositoryTest).

6. Personas Contribuyentes
Carlos Palacios (correo: carloswalker@gmail.com.co).

7. Personas Desarrolladoras del Proyecto
Carlos Palacios:
Diseño de la base de datos.
Implementación de reglas de negocio y validaciones.
Configuración de pruebas con vollmed_forohub_test.
