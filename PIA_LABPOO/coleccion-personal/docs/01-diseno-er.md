# Paso 1: diagrama entidad-relación

## Enfoque elegido

Se reutiliza de los laboratorios 12 y 13 la organización de un CRUD en modelo, persistencia, servicio e interfaz. La tecnología cambia deliberadamente a **JavaFX + JDBC puro + SQLite**.

Se consideraron tres modelos:

- **Una tabla única con todos los campos:** sencilla, pero acumularía columnas inaplicables y valores nulos para cada tipo.
- **Entidad–atributo–valor (EAV):** muy flexible, pero dificulta las restricciones, las consultas y la validación; es innecesaria para cuatro categorías conocidas.
- **Tabla común + detalles por categoría (elegida):** mantiene una sola entidad para buscar, prestar, etiquetar y estadificar, sin mezclar atributos específicos en `items`.

## Diagrama en texto

```text
items
├── id INTEGER PK
├── titulo TEXT NOT NULL
├── tipo TEXT NOT NULL                 LIBRO | PELICULA | VIDEOJUEGO | CARTA
├── anio INTEGER NULL
├── notas TEXT NULL
├── fecha_adquisicion TEXT NULL        fecha ISO-8601: YYYY-MM-DD
├── precio_pagado_centavos INTEGER NULL >= 0
├── portada_ruta TEXT NULL
├── estado TEXT NOT NULL               PENDIENTE | EN_PROGRESO | COMPLETADO | ABANDONADO
├── calificacion INTEGER NULL          CHECK entre 1 y 10
└── resena TEXT NULL
     │
     ├── 1 : 0..1 ── libros
     │                ├── item_id INTEGER PK, FK → items.id
     │                ├── autor TEXT NULL
     │                ├── isbn TEXT NULL UNIQUE
     │                └── editorial TEXT NULL
     │
     ├── 1 : 0..1 ── peliculas
     │                ├── item_id INTEGER PK, FK → items.id
     │                ├── director TEXT NULL
     │                └── duracion_minutos INTEGER NULL > 0
     │
     ├── 1 : 0..1 ── videojuegos
     │                ├── item_id INTEGER PK, FK → items.id
     │                ├── plataforma TEXT NULL
     │                └── desarrollador TEXT NULL
     │
     ├── 1 : 0..1 ── cartas
     │                ├── item_id INTEGER PK, FK → items.id
     │                ├── juego TEXT NULL
     │                ├── rareza TEXT NULL
     │                └── condicion TEXT NULL
     │
     ├── 1 : N ── item_etiquetas N : 1 ── etiquetas
     │             ├── item_id INTEGER PK, FK → items.id
     │             └── etiqueta_id INTEGER PK, FK → etiquetas.id
     │                                       ├── id INTEGER PK
     │                                       └── nombre TEXT NOT NULL UNIQUE
     │
     └── 1 : N ── prestamos
                   ├── id INTEGER PK
                   ├── item_id INTEGER NOT NULL, FK → items.id
                   ├── amigo TEXT NOT NULL
                   ├── fecha_prestamo TEXT NOT NULL       ISO-8601
                   ├── fecha_limite TEXT NOT NULL         ISO-8601
                   └── fecha_devolucion TEXT NULL         ISO-8601
```

## Relaciones y reglas

- Cada ítem tiene exactamente un detalle correspondiente a su `tipo`. La PK de cada tabla de detalle también es FK hacia `items`, por lo que no puede haber dos detalles de la misma categoría para un ítem.
- La creación o el cambio de tipo de un ítem y sus detalles se ejecutará en una sola transacción; el servicio comprobará que solo exista el detalle correspondiente.
- `item_etiquetas` tiene como PK compuesta `(item_id, etiqueta_id)`, lo que impide asignar dos veces la misma etiqueta al mismo ítem. Los nombres de etiquetas se guardan limpios y con unicidad sin distinguir mayúsculas de minúsculas.
- Un ítem puede tener muchos préstamos históricos, pero como máximo **uno sin devolver**. Esto se reforzará con un índice único parcial sobre los préstamos cuya `fecha_devolucion` sea `NULL`.
- `fecha_limite` no puede ser anterior a `fecha_prestamo`; una devolución tampoco puede ser anterior al préstamo.
- El estado del préstamo no se almacena porque quedaría desactualizado al cambiar la fecha. Se calcula así:
  - `DEVUELTO`: existe `fecha_devolucion`.
  - `VENCIDO`: no existe devolución y `fecha_limite` es anterior a la fecha local actual.
  - `ACTIVO`: no existe devolución y aún no se cumple la condición de vencido.
- Al borrar un ítem se eliminan en cascada su detalle, sus relaciones con etiquetas y sus préstamos. Al borrar una etiqueta solo se eliminan sus asociaciones, no los ítems. La interfaz pedirá confirmación antes de borrar.
- El precio se almacena como entero en centavos para evitar errores de coma flotante; la capa de servicio convertirá desde y hacia `BigDecimal`. No se añade gestión de divisas porque la aplicación es local y para una sola colección.
- Los filtros, las estadísticas y la importación/exportación CSV se resolverán como consultas o servicios; no requieren tablas adicionales.

## Justificación breve

El diseño mantiene en `items` todo lo que comparten las operaciones principales y evita duplicar préstamos, etiquetas o búsquedas por categoría. Las tablas de detalle 1:1 conservan la extensibilidad sin sacrificar restricciones SQL. La relación muchos a muchos representa etiquetas sin listas de texto, y el historial de préstamos queda separado del estado editorial del ítem. Las fechas ISO-8601 en texto son ordenables y directamente compatibles con `java.time.LocalDate`.
