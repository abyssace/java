package com.coleccion.dao;

import com.coleccion.model.DetalleItem;
import com.coleccion.model.Estadisticas;
import com.coleccion.model.EstadoItem;
import com.coleccion.model.FiltroItem;
import com.coleccion.model.Item;
import com.coleccion.model.Pagina;
import com.coleccion.model.TipoItem;
import com.coleccion.util.Database;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class ItemDao {
    private static final String CAMPOS_ITEM = "titulo, tipo, anio, notas, fecha_adquisicion, "
            + "precio_pagado_centavos, portada_ruta, estado, calificacion, resena";
    private final Database database;

    public ItemDao(Database database) {
        this.database = database;
    }

    public Item guardar(Item item) {
        return item.id() == null ? crear(item) : actualizar(item);
    }

    private Item crear(Item item) {
        String sql = "INSERT INTO items (" + CAMPOS_ITEM + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = database.abrir()) {
            connection.setAutoCommit(false);
            try {
                long id;
                try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    vincularItem(statement, item);
                    statement.executeUpdate();
                    try (ResultSet keys = statement.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException("SQLite no devolvió el identificador generado");
                        }
                        id = keys.getLong(1);
                    }
                }
                insertarDetalle(connection, id, item.detalle());
                reemplazarEtiquetas(connection, id, item.etiquetas());
                connection.commit();
                return item.conId(id);
            } catch (SQLException exception) {
                deshacer(connection, exception);
                throw exception;
            }
        } catch (SQLException exception) {
            throw new DataAccessException("No se pudo guardar el ítem. Revisa que el ISBN no esté duplicado.", exception);
        }
    }

    private Item actualizar(Item item) {
        String sql = "UPDATE items SET titulo = ?, tipo = ?, anio = ?, notas = ?, fecha_adquisicion = ?, "
                + "precio_pagado_centavos = ?, portada_ruta = ?, estado = ?, calificacion = ?, resena = ? WHERE id = ?";
        try (Connection connection = database.abrir()) {
            connection.setAutoCommit(false);
            try {
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    vincularItem(statement, item);
                    statement.setLong(11, item.id());
                    if (statement.executeUpdate() == 0) {
                        throw new SQLException("El ítem ya no existe");
                    }
                }
                for (String tabla : List.of("libros", "peliculas", "videojuegos", "cartas")) {
                    ejecutarConId(connection, "DELETE FROM " + tabla + " WHERE item_id = ?", item.id());
                }
                insertarDetalle(connection, item.id(), item.detalle());
                reemplazarEtiquetas(connection, item.id(), item.etiquetas());
                connection.commit();
                return item;
            } catch (SQLException exception) {
                deshacer(connection, exception);
                throw exception;
            }
        } catch (SQLException exception) {
            throw new DataAccessException("No se pudo actualizar el ítem. Revisa que el ISBN no esté duplicado.", exception);
        }
    }

    public Optional<Item> buscar(long id) {
        String sql = "SELECT * FROM items WHERE id = ?";
        try (Connection connection = database.abrir()) {
            Item base;
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setLong(1, id);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (!resultSet.next()) {
                        return Optional.empty();
                    }
                    base = mapearBase(resultSet);
                }
            }
            return Optional.of(completar(connection, base));
        } catch (SQLException exception) {
            throw new DataAccessException("No se pudo consultar el ítem.", exception);
        }
    }

    public void eliminar(long id) {
        try (Connection connection = database.abrir();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM items WHERE id = ?")) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new DataAccessException("No se pudo eliminar el ítem.", exception);
        }
    }

    public Pagina<Item> buscar(FiltroItem filtro) {
        StringBuilder where = new StringBuilder(" FROM items i WHERE 1 = 1");
        List<Object> parametros = new ArrayList<>();
        construirFiltros(filtro, where, parametros);
        String orden = switch (filtro.orden()) {
            case TITULO_ASC -> "i.titulo COLLATE NOCASE ASC, i.id ASC";
            case TITULO_DESC -> "i.titulo COLLATE NOCASE DESC, i.id DESC";
            case ANIO_DESC -> "i.anio IS NULL, i.anio DESC, i.id DESC";
            case CALIFICACION_DESC -> "i.calificacion IS NULL, i.calificacion DESC, i.id DESC";
            case ADQUISICION_DESC -> "i.fecha_adquisicion IS NULL, i.fecha_adquisicion DESC, i.id DESC";
        };

        try (Connection connection = database.abrir()) {
            long total = contar(connection, "SELECT COUNT(*)" + where, parametros);
            String sql = "SELECT i.*" + where + " ORDER BY " + orden + " LIMIT ? OFFSET ?";
            List<Item> elementos = new ArrayList<>();
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                vincularParametros(statement, parametros);
                int indice = parametros.size() + 1;
                statement.setInt(indice, filtro.tamanoPagina());
                statement.setLong(indice + 1, (long) filtro.pagina() * filtro.tamanoPagina());
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        elementos.add(mapearBase(resultSet));
                    }
                }
            }
            List<Item> completos = new ArrayList<>(elementos.size());
            for (Item item : elementos) {
                completos.add(completar(connection, item));
            }
            int totalPaginas = total == 0 ? 0 : (int) ((total + filtro.tamanoPagina() - 1) / filtro.tamanoPagina());
            return new Pagina<>(completos, total, filtro.pagina(), totalPaginas);
        } catch (SQLException exception) {
            throw new DataAccessException("No se pudo realizar la búsqueda.", exception);
        }
    }

    public Estadisticas estadisticas() {
        try (Connection connection = database.abrir()) {
            long total;
            double promedio;
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT COUNT(*), COALESCE(AVG(calificacion), 0) FROM items");
                 ResultSet resultSet = statement.executeQuery()) {
                total = resultSet.getLong(1);
                promedio = resultSet.getDouble(2);
            }
            Map<TipoItem, Long> porTipo = new EnumMap<>(TipoItem.class);
            for (TipoItem tipo : TipoItem.values()) {
                porTipo.put(tipo, 0L);
            }
            try (PreparedStatement statement = connection.prepareStatement("SELECT tipo, COUNT(*) FROM items GROUP BY tipo");
                 ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    porTipo.put(TipoItem.valueOf(resultSet.getString(1)), resultSet.getLong(2));
                }
            }
            Map<EstadoItem, Long> porEstado = new EnumMap<>(EstadoItem.class);
            for (EstadoItem estado : EstadoItem.values()) {
                porEstado.put(estado, 0L);
            }
            try (PreparedStatement statement = connection.prepareStatement("SELECT estado, COUNT(*) FROM items GROUP BY estado");
                 ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    porEstado.put(EstadoItem.valueOf(resultSet.getString(1)), resultSet.getLong(2));
                }
            }
            long activos;
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT COUNT(*) FROM prestamos WHERE fecha_devolucion IS NULL");
                 ResultSet resultSet = statement.executeQuery()) {
                activos = resultSet.getLong(1);
            }
            return new Estadisticas(total, porTipo, promedio, porEstado, activos);
        } catch (SQLException exception) {
            throw new DataAccessException("No se pudieron calcular las estadísticas.", exception);
        }
    }

    public List<String> etiquetas() {
        List<String> etiquetas = new ArrayList<>();
        try (Connection connection = database.abrir();
             PreparedStatement statement = connection.prepareStatement("SELECT nombre FROM etiquetas ORDER BY nombre COLLATE NOCASE");
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                etiquetas.add(resultSet.getString(1));
            }
            return etiquetas;
        } catch (SQLException exception) {
            throw new DataAccessException("No se pudieron consultar las etiquetas.", exception);
        }
    }

    private static void construirFiltros(FiltroItem filtro, StringBuilder where, List<Object> parametros) {
        if (filtro.texto() != null && !filtro.texto().isBlank()) {
            where.append(" AND (i.titulo LIKE ? ESCAPE '\\' OR i.notas LIKE ? ESCAPE '\\' OR i.resena LIKE ? ESCAPE '\\')");
            String texto = "%" + escaparLike(filtro.texto()) + "%";
            parametros.addAll(List.of(texto, texto, texto));
        }
        if (filtro.tipo() != null) {
            where.append(" AND i.tipo = ?");
            parametros.add(filtro.tipo().name());
        }
        if (filtro.estado() != null) {
            where.append(" AND i.estado = ?");
            parametros.add(filtro.estado().name());
        }
        agregarRango(where, parametros, "i.calificacion", filtro.calificacionMin(), filtro.calificacionMax());
        agregarRango(where, parametros, "i.anio", filtro.anioMin(), filtro.anioMax());
        if (!filtro.etiquetas().isEmpty()) {
            String marcas = String.join(",", Collections.nCopies(filtro.etiquetas().size(), "?"));
            where.append(" AND i.id IN (SELECT ie.item_id FROM item_etiquetas ie ")
                    .append("JOIN etiquetas e ON e.id = ie.etiqueta_id WHERE e.nombre IN (")
                    .append(marcas)
                    .append(") GROUP BY ie.item_id HAVING COUNT(DISTINCT e.nombre COLLATE NOCASE) = ?)");
            parametros.addAll(filtro.etiquetas());
            parametros.add(filtro.etiquetas().size());
        }
        if (filtro.soloPrestados()) {
            where.append(" AND EXISTS (SELECT 1 FROM prestamos p WHERE p.item_id = i.id AND p.fecha_devolucion IS NULL)");
        }
        if (filtro.soloVencidos()) {
            where.append(" AND EXISTS (SELECT 1 FROM prestamos p WHERE p.item_id = i.id ")
                    .append("AND p.fecha_devolucion IS NULL AND p.fecha_limite < ?)");
            parametros.add(LocalDate.now().toString());
        }
    }

    private static void agregarRango(StringBuilder where, List<Object> parametros, String columna,
                                     Integer minimo, Integer maximo) {
        if (minimo != null) {
            where.append(" AND ").append(columna).append(" >= ?");
            parametros.add(minimo);
        }
        if (maximo != null) {
            where.append(" AND ").append(columna).append(" <= ?");
            parametros.add(maximo);
        }
    }

    private static String escaparLike(String texto) {
        return texto.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }

    private static long contar(Connection connection, String sql, List<Object> parametros) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            vincularParametros(statement, parametros);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.getLong(1);
            }
        }
    }

    private static void vincularParametros(PreparedStatement statement, List<Object> parametros) throws SQLException {
        for (int i = 0; i < parametros.size(); i++) {
            Object valor = parametros.get(i);
            if (valor instanceof Integer entero) {
                statement.setInt(i + 1, entero);
            } else if (valor instanceof Long largo) {
                statement.setLong(i + 1, largo);
            } else {
                statement.setString(i + 1, valor.toString());
            }
        }
    }

    private static void vincularItem(PreparedStatement statement, Item item) throws SQLException {
        statement.setString(1, item.titulo());
        statement.setString(2, item.tipo().name());
        ponerEntero(statement, 3, item.anio());
        statement.setString(4, item.notas());
        statement.setString(5, item.fechaAdquisicion() == null ? null : item.fechaAdquisicion().toString());
        if (item.precioPagado() == null) {
            statement.setNull(6, java.sql.Types.BIGINT);
        } else {
            statement.setLong(6, item.precioPagado().movePointRight(2).longValueExact());
        }
        statement.setString(7, item.portadaRuta());
        statement.setString(8, item.estado().name());
        ponerEntero(statement, 9, item.calificacion());
        statement.setString(10, item.resena());
    }

    private static void ponerEntero(PreparedStatement statement, int indice, Integer valor) throws SQLException {
        if (valor == null) {
            statement.setNull(indice, java.sql.Types.INTEGER);
        } else {
            statement.setInt(indice, valor);
        }
    }

    private static Item mapearBase(ResultSet resultSet) throws SQLException {
        long centavos = resultSet.getLong("precio_pagado_centavos");
        BigDecimal precio = resultSet.wasNull() ? null : BigDecimal.valueOf(centavos, 2);
        String adquisicion = resultSet.getString("fecha_adquisicion");
        return new Item(resultSet.getLong("id"), resultSet.getString("titulo"),
                TipoItem.valueOf(resultSet.getString("tipo")), (Integer) resultSet.getObject("anio"),
                resultSet.getString("notas"), adquisicion == null ? null : LocalDate.parse(adquisicion), precio,
                resultSet.getString("portada_ruta"), EstadoItem.valueOf(resultSet.getString("estado")),
                (Integer) resultSet.getObject("calificacion"), resultSet.getString("resena"), null, Set.of());
    }

    private static Item completar(Connection connection, Item base) throws SQLException {
        return new Item(base.id(), base.titulo(), base.tipo(), base.anio(), base.notas(), base.fechaAdquisicion(),
                base.precioPagado(), base.portadaRuta(), base.estado(), base.calificacion(), base.resena(),
                consultarDetalle(connection, base.id(), base.tipo()), consultarEtiquetas(connection, base.id()));
    }

    private static DetalleItem consultarDetalle(Connection connection, long id, TipoItem tipo) throws SQLException {
        String sql = switch (tipo) {
            case LIBRO -> "SELECT autor, isbn, editorial FROM libros WHERE item_id = ?";
            case PELICULA -> "SELECT director, duracion_minutos FROM peliculas WHERE item_id = ?";
            case VIDEOJUEGO -> "SELECT plataforma, desarrollador FROM videojuegos WHERE item_id = ?";
            case CARTA -> "SELECT juego, rareza, condicion FROM cartas WHERE item_id = ?";
        };
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new SQLException("Falta el detalle del ítem " + id);
                }
                return switch (tipo) {
                    case LIBRO -> new DetalleItem.Libro(resultSet.getString("autor"), resultSet.getString("isbn"),
                            resultSet.getString("editorial"));
                    case PELICULA -> new DetalleItem.Pelicula(resultSet.getString("director"),
                            (Integer) resultSet.getObject("duracion_minutos"));
                    case VIDEOJUEGO -> new DetalleItem.Videojuego(resultSet.getString("plataforma"),
                            resultSet.getString("desarrollador"));
                    case CARTA -> new DetalleItem.Carta(resultSet.getString("juego"), resultSet.getString("rareza"),
                            resultSet.getString("condicion"));
                };
            }
        }
    }

    private static Set<String> consultarEtiquetas(Connection connection, long id) throws SQLException {
        Set<String> etiquetas = new LinkedHashSet<>();
        String sql = "SELECT e.nombre FROM etiquetas e JOIN item_etiquetas ie ON ie.etiqueta_id = e.id "
                + "WHERE ie.item_id = ? ORDER BY e.nombre COLLATE NOCASE";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    etiquetas.add(resultSet.getString(1));
                }
            }
        }
        return etiquetas;
    }

    private static void insertarDetalle(Connection connection, long id, DetalleItem detalle) throws SQLException {
        switch (detalle) {
            case DetalleItem.Libro libro -> {
                try (PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO libros (item_id, autor, isbn, editorial) VALUES (?, ?, ?, ?)")) {
                    statement.setLong(1, id);
                    statement.setString(2, libro.autor());
                    statement.setString(3, libro.isbn());
                    statement.setString(4, libro.editorial());
                    statement.executeUpdate();
                }
            }
            case DetalleItem.Pelicula pelicula -> {
                try (PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO peliculas (item_id, director, duracion_minutos) VALUES (?, ?, ?)")) {
                    statement.setLong(1, id);
                    statement.setString(2, pelicula.director());
                    ponerEntero(statement, 3, pelicula.duracionMinutos());
                    statement.executeUpdate();
                }
            }
            case DetalleItem.Videojuego videojuego -> {
                try (PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO videojuegos (item_id, plataforma, desarrollador) VALUES (?, ?, ?)")) {
                    statement.setLong(1, id);
                    statement.setString(2, videojuego.plataforma());
                    statement.setString(3, videojuego.desarrollador());
                    statement.executeUpdate();
                }
            }
            case DetalleItem.Carta carta -> {
                try (PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO cartas (item_id, juego, rareza, condicion) VALUES (?, ?, ?, ?)")) {
                    statement.setLong(1, id);
                    statement.setString(2, carta.juego());
                    statement.setString(3, carta.rareza());
                    statement.setString(4, carta.condicion());
                    statement.executeUpdate();
                }
            }
        }
    }

    private static void reemplazarEtiquetas(Connection connection, long itemId, Set<String> etiquetas) throws SQLException {
        ejecutarConId(connection, "DELETE FROM item_etiquetas WHERE item_id = ?", itemId);
        for (String etiqueta : etiquetas) {
            long etiquetaId;
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO etiquetas (nombre) VALUES (?) ON CONFLICT(nombre) DO NOTHING")) {
                statement.setString(1, etiqueta);
                statement.executeUpdate();
            }
            try (PreparedStatement statement = connection.prepareStatement("SELECT id FROM etiquetas WHERE nombre = ?")) {
                statement.setString(1, etiqueta);
                try (ResultSet resultSet = statement.executeQuery()) {
                    etiquetaId = resultSet.getLong(1);
                }
            }
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO item_etiquetas (item_id, etiqueta_id) VALUES (?, ?)")) {
                statement.setLong(1, itemId);
                statement.setLong(2, etiquetaId);
                statement.executeUpdate();
            }
        }
    }

    private static void ejecutarConId(Connection connection, String sql, long id) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        }
    }

    private static void deshacer(Connection connection, SQLException original) {
        if (connection != null) {
            try {
                connection.rollback();
            } catch (SQLException rollback) {
                original.addSuppressed(rollback);
            }
        }
    }
}
