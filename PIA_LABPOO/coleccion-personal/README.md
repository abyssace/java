# Colección Personal

Aplicación de escritorio **local y para una sola persona** que permite gestionar libros, películas, videojuegos y cartas coleccionables. Está inspirada en la organización CRUD de los laboratorios 12 y 13, adaptada a JavaFX, JDBC puro y SQLite.

## Funcionalidades

- Crear, consultar, editar y eliminar ítems con datos comunes y específicos por categoría.
- Estado, calificación, reseña, precio, fecha de adquisición, portada local y etiquetas.
- Búsqueda por texto y filtros combinables por tipo, estado, calificación, años, etiquetas, préstamos y vencimientos.
- Ordenamiento y páginas de 20 ítems.
- Préstamos con fecha límite, devolución, historial y bloqueo de doble préstamo activo.
- Estadísticas por tipo y estado, calificación media y préstamos activos.
- Importación y exportación CSV.

## Requisitos

- **JDK 21**.
- **Maven 3.9 o superior**.
- No se necesita instalar SQLite ni ningún servidor. La primera compilación con Maven necesita internet para descargar dependencias; después la aplicación funciona sin conexión.

## Ejecutar

Desde esta carpeta:

```bash
mvn clean test
mvn javafx:run
```

Las pruebas de lógica y base de datos no necesitan pantalla. La prueba de arranque JavaFX se omite si no existe `DISPLAY`; en Linux con Xvfb puede ejecutarse toda la suite así:

```bash
xvfb-run -a mvn test
```

## Datos locales

La base se crea automáticamente en:

```text
<tu carpeta personal>/.coleccion-personal/coleccion.db
```

Para desarrollo o pruebas se puede cambiar con la variable de entorno `COLECCION_DB`:

```bash
COLECCION_DB=/ruta/temporal/coleccion.db mvn javafx:run
```

En Windows PowerShell: `$env:COLECCION_DB="C:\ruta\coleccion.db"; mvn javafx:run`.

El esquema está en `src/main/resources/schema.sql`. Incluye restricciones, índices, claves foráneas y la protección contra préstamos activos duplicados. Los datos opcionales de `datos-ejemplo.sql` no se cargan automáticamente; puedes ejecutarlos sobre una base vacía con el cliente `sqlite3` si los necesitas.

## Empaquetar

Generar un JAR ejecutable con dependencias:

```bash
mvn clean package
java -jar target/coleccion-personal.jar
```

Crear un instalador/imagen de aplicación con `jpackage` (en el mismo sistema operativo de destino):

```bash
jpackage \
  --name ColeccionPersonal \
  --input target \
  --main-jar coleccion-personal.jar \
  --main-class com.coleccion.Main \
  --dest dist
```

En Windows puedes escribir el mismo comando en una sola línea. `dist/` no se sube al repositorio.

## CSV

La exportación incluye todos los ítems, sus detalles y etiquetas. La importación:

- Exige exactamente las mismas columnas que la exportación; el orden no importa y una cabecera distinta se rechaza antes de guardar nada.
- Crea ítems nuevos; no reutiliza IDs ni reemplaza registros existentes.
- Usa `|` para separar etiquetas y UTF-8 para el texto. Ese carácter está reservado y no puede formar parte de una etiqueta.
- Conserva las filas válidas y muestra un informe completo de las filas rechazadas, incluidas las que no se pudieron leer por un error de sintaxis.
- No incluye el historial de préstamos, que permanece en SQLite.

Las portadas se guardan como rutas locales; si mueves o borras la imagen original, la ruta dejará de ser válida.

## Estructura

```text
src/main/java/com/coleccion/
├── model/       Entidades, enums y objetos de consulta
├── dao/         Acceso JDBC y transacciones
├── service/     Validaciones, préstamos y CSV
├── ui/          Controladores JavaFX
├── util/        Conexión e inicialización SQLite
├── App.java
└── Main.java
src/main/resources/
├── schema.sql
├── datos-ejemplo.sql
└── com/coleccion/ui/   Vistas FXML y estilos
src/test/               Pruebas JUnit 5
docs/                   Requisitos y diseño
```

## Decisiones de diseño

- Una tabla `items` y tablas 1:1 por categoría evitan una tabla enorme con campos inaplicables.
- Los precios se almacenan como centavos enteros y se convierten mediante `BigDecimal`.
- `VENCIDO` se calcula con la fecha actual; nunca queda guardado un estado obsoleto.
- Las consultas usan `PreparedStatement`; solo se interpolan fragmentos SQL internos y órdenes de una lista cerrada.
- Al eliminar un ítem se eliminan también sus asociaciones y préstamos, siempre con confirmación en la interfaz.

Consulta [el diseño entidad-relación](docs/01-diseno-er.md) para más detalle.
