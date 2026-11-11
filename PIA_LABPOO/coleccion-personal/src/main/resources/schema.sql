CREATE TABLE IF NOT EXISTS items (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    titulo TEXT NOT NULL CHECK (length(trim(titulo)) > 0),
    tipo TEXT NOT NULL CHECK (tipo IN ('LIBRO', 'PELICULA', 'VIDEOJUEGO', 'CARTA')),
    anio INTEGER CHECK (anio IS NULL OR anio BETWEEN 0 AND 9999),
    notas TEXT,
    fecha_adquisicion TEXT,
    precio_pagado_centavos INTEGER CHECK (precio_pagado_centavos IS NULL OR precio_pagado_centavos >= 0),
    portada_ruta TEXT,
    estado TEXT NOT NULL DEFAULT 'PENDIENTE'
        CHECK (estado IN ('PENDIENTE', 'EN_PROGRESO', 'COMPLETADO', 'ABANDONADO')),
    calificacion INTEGER CHECK (calificacion IS NULL OR calificacion BETWEEN 1 AND 10),
    resena TEXT
);

CREATE TABLE IF NOT EXISTS libros (
    item_id INTEGER PRIMARY KEY REFERENCES items(id) ON DELETE CASCADE,
    autor TEXT,
    isbn TEXT UNIQUE,
    editorial TEXT
);

CREATE TABLE IF NOT EXISTS peliculas (
    item_id INTEGER PRIMARY KEY REFERENCES items(id) ON DELETE CASCADE,
    director TEXT,
    duracion_minutos INTEGER CHECK (duracion_minutos IS NULL OR duracion_minutos > 0)
);

CREATE TABLE IF NOT EXISTS videojuegos (
    item_id INTEGER PRIMARY KEY REFERENCES items(id) ON DELETE CASCADE,
    plataforma TEXT,
    desarrollador TEXT
);

CREATE TABLE IF NOT EXISTS cartas (
    item_id INTEGER PRIMARY KEY REFERENCES items(id) ON DELETE CASCADE,
    juego TEXT,
    rareza TEXT,
    condicion TEXT
);

CREATE TABLE IF NOT EXISTS etiquetas (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL COLLATE NOCASE UNIQUE CHECK (length(trim(nombre)) > 0)
);

CREATE TABLE IF NOT EXISTS item_etiquetas (
    item_id INTEGER NOT NULL REFERENCES items(id) ON DELETE CASCADE,
    etiqueta_id INTEGER NOT NULL REFERENCES etiquetas(id) ON DELETE CASCADE,
    PRIMARY KEY (item_id, etiqueta_id)
);

CREATE TABLE IF NOT EXISTS prestamos (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    item_id INTEGER NOT NULL REFERENCES items(id) ON DELETE CASCADE,
    amigo TEXT NOT NULL CHECK (length(trim(amigo)) > 0),
    fecha_prestamo TEXT NOT NULL,
    fecha_limite TEXT NOT NULL CHECK (fecha_limite >= fecha_prestamo),
    fecha_devolucion TEXT CHECK (fecha_devolucion IS NULL OR fecha_devolucion >= fecha_prestamo)
);

CREATE INDEX IF NOT EXISTS idx_items_titulo ON items(titulo COLLATE NOCASE);
CREATE INDEX IF NOT EXISTS idx_items_tipo ON items(tipo);
CREATE INDEX IF NOT EXISTS idx_items_estado ON items(estado);
CREATE INDEX IF NOT EXISTS idx_items_calificacion ON items(calificacion);
CREATE INDEX IF NOT EXISTS idx_items_anio ON items(anio);
CREATE INDEX IF NOT EXISTS idx_item_etiquetas_etiqueta ON item_etiquetas(etiqueta_id, item_id);
CREATE INDEX IF NOT EXISTS idx_prestamos_item_devolucion ON prestamos(item_id, fecha_devolucion);
CREATE INDEX IF NOT EXISTS idx_prestamos_vencidos ON prestamos(fecha_limite)
    WHERE fecha_devolucion IS NULL;
CREATE UNIQUE INDEX IF NOT EXISTS idx_prestamo_activo_unico ON prestamos(item_id)
    WHERE fecha_devolucion IS NULL;

PRAGMA user_version = 1;
