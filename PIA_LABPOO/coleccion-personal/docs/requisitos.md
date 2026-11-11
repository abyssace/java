# Prompt: Aplicación de Colección Personal (Java + SQLite)

Actúa como un **programador senior y desarrollador profesional de Java**, con más de 10 años de experiencia y un dominio profundo de **SQL** (diseño relacional, normalización, índices, consultas avanzadas y optimización en SQLite). Tu código es limpio, probado y sin errores: antes de entregarlo, revisa mentalmente que compile, que las consultas SQL sean válidas y que no falten imports, dependencias ni configuraciones.

## Objetivo

Construir una aplicación de **COLECCIÓN PERSONAL** para una sola persona. Nada más. No incluyas login, roles, usuarios múltiples, carrito, ventas, pagos, red social ni funciones en la nube. Debe funcionar **100% local y sin internet**.

## Stack

- Java 21 (LTS)
- Maven
- SQLite con `org.xerial:sqlite-jdbc`
- JDBC puro con patrón DAO (sin ORM)
- JavaFX para la interfaz de escritorio
- Flyway o un script `schema.sql` versionado para crear la base de datos
- JUnit 5 para pruebas

## Qué se colecciona

Libros, películas, videojuegos y cartas coleccionables, en un modelo unificado y extensible (por ejemplo, una tabla `items` con un tipo y tablas o campos específicos por categoría).

## Funcionalidades (CRUD completo)

1. Crear, ver, editar y eliminar ítems de la colección.
2. Campos comunes: título, tipo, año, notas, fecha de adquisición, precio pagado, portada (ruta local de imagen opcional).
3. Estado de cada ítem: pendiente, en progreso, completado, abandonado.
4. Calificación de 1 a 10 y reseña corta opcional.
5. Etiquetas/géneros (relación muchos a muchos).
6. Préstamos a amigos: amigo, ítem prestado, fecha de préstamo, fecha límite, fecha de devolución, estado (activo/devuelto/vencido). Un ítem no puede prestarse si ya está prestado.
7. Búsqueda y filtros avanzados combinables: texto libre, tipo, estado, rango de calificación, rango de años, etiquetas, "solo prestados", "solo vencidos", ordenamiento por varios criterios y paginación.
8. Estadísticas simples: total por tipo, promedio de calificación, ítems por estado, préstamos activos.
9. Exportar e importar la colección en CSV.

## Reglas técnicas obligatorias

- Ejecuta `PRAGMA foreign_keys = ON;` en cada conexión y `PRAGMA journal_mode = WAL;`.
- Usa `PreparedStatement` en **todas** las consultas (cero concatenación de strings, para evitar inyección SQL).
- Usa `try-with-resources` para cerrar conexiones, statements y result sets.
- Usa transacciones donde haya más de una operación dependiente.
- Guarda fechas como texto ISO-8601 y conviértelas con `java.time`.
- Define restricciones en la base: `PRIMARY KEY`, `FOREIGN KEY` con `ON DELETE` adecuado, `NOT NULL`, `UNIQUE` y `CHECK` (por ejemplo, calificación entre 1 y 10).
- Crea índices en las columnas usadas para buscar y filtrar.
- Valida los datos en la capa de servicio antes de llegar a la base de datos y maneja las excepciones con mensajes claros para el usuario.
- Separa en capas: `model`, `dao`, `service`, `ui` (controladores JavaFX) y `util`.
- Nada de código duplicado, nombres claros y comentarios solo donde aporten.

## Forma de entrega

Trabaja **por pasos** y espera mi confirmación antes de avanzar al siguiente:

1. Diagrama entidad-relación en texto y justificación breve del diseño.
2. Script SQL completo (tablas, índices, restricciones y datos de ejemplo).
3. `pom.xml` completo y estructura de carpetas.
4. Clases `model` y `DAO` con código completo.
5. Capa de servicio con las validaciones.
6. Consulta dinámica de búsqueda con filtros combinables y explicación de cómo se construye de forma segura.
7. Interfaz JavaFX (vistas FXML y controladores).
8. Pruebas unitarias de los DAO y de las reglas de préstamo.
9. Instrucciones para compilar, ejecutar y empaquetar con `jpackage`.

Entrega siempre código completo y funcional, sin "..." ni fragmentos omitidos. Si algo es ambiguo, pregúntame antes de asumir. Si detectas un posible error o mejora en lo que ya generaste, avísame de inmediato.

**Empieza por el paso 1.**
