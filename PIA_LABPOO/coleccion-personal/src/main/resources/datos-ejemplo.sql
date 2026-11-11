INSERT OR IGNORE INTO items (id, titulo, tipo, anio, fecha_adquisicion, precio_pagado_centavos, estado, calificacion, resena)
VALUES (1, 'Cien años de soledad', 'LIBRO', 1967, '2026-01-10', 24990, 'COMPLETADO', 10, 'Una lectura imprescindible.');
INSERT OR IGNORE INTO libros (item_id, autor, isbn, editorial) VALUES (1, 'Gabriel García Márquez', '978-84-376-0494-7', 'Cátedra');

INSERT OR IGNORE INTO items (id, titulo, tipo, anio, estado, calificacion)
VALUES (2, 'El viaje de Chihiro', 'PELICULA', 2001, 'COMPLETADO', 9);
INSERT OR IGNORE INTO peliculas (item_id, director, duracion_minutos) VALUES (2, 'Hayao Miyazaki', 125);

INSERT OR IGNORE INTO items (id, titulo, tipo, anio, estado)
VALUES (3, 'The Legend of Zelda: Breath of the Wild', 'VIDEOJUEGO', 2017, 'EN_PROGRESO');
INSERT OR IGNORE INTO videojuegos (item_id, plataforma, desarrollador) VALUES (3, 'Nintendo Switch', 'Nintendo');

INSERT OR IGNORE INTO items (id, titulo, tipo, anio, estado)
VALUES (4, 'Charizard', 'CARTA', 1999, 'PENDIENTE');
INSERT OR IGNORE INTO cartas (item_id, juego, rareza, condicion) VALUES (4, 'Pokémon', 'Rara holográfica', 'Buena');

INSERT OR IGNORE INTO etiquetas (id, nombre) VALUES (1, 'favorito');
INSERT OR IGNORE INTO etiquetas (id, nombre) VALUES (2, 'fantasía');
INSERT OR IGNORE INTO item_etiquetas (item_id, etiqueta_id) VALUES (1, 1);
INSERT OR IGNORE INTO item_etiquetas (item_id, etiqueta_id) VALUES (1, 2);
INSERT OR IGNORE INTO item_etiquetas (item_id, etiqueta_id) VALUES (2, 1);
INSERT OR IGNORE INTO item_etiquetas (item_id, etiqueta_id) VALUES (3, 2);
