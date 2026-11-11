package com.coleccion.dao;

import com.coleccion.model.Prestamo;
import com.coleccion.util.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class PrestamoDao {
    private static final String CAMPOS = "id, item_id, amigo, fecha_prestamo, fecha_limite, fecha_devolucion";
    private final Database database;

    public PrestamoDao(Database database) {
        this.database = database;
    }

    public Prestamo prestar(long itemId, String amigo, LocalDate inicio, LocalDate limite) {
        String sql = "INSERT INTO prestamos (item_id, amigo, fecha_prestamo, fecha_limite) VALUES (?, ?, ?, ?)";
        try (Connection connection = database.abrir()) {
            connection.setAutoCommit(false);
            try {
                if (consultarActivo(connection, itemId).isPresent()) {
                    throw new SQLException("Ya existe un préstamo activo para este ítem");
                }
                Prestamo prestamo;
                try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    statement.setLong(1, itemId);
                    statement.setString(2, amigo);
                    statement.setString(3, inicio.toString());
                    statement.setString(4, limite.toString());
                    statement.executeUpdate();
                    try (ResultSet keys = statement.getGeneratedKeys()) {
                        prestamo = new Prestamo(keys.getLong(1), itemId, amigo, inicio, limite, null);
                    }
                }
                connection.commit();
                return prestamo;
            } catch (SQLException exception) {
                deshacer(connection, exception);
                throw exception;
            }
        } catch (SQLException exception) {
            throw new DataAccessException("No se pudo registrar el préstamo; comprueba que el ítem exista y no esté prestado.", exception);
        }
    }

    public Prestamo devolver(Prestamo prestamo, LocalDate fecha) {
        try (Connection connection = database.abrir();
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE prestamos SET fecha_devolucion = ? WHERE id = ? AND fecha_devolucion IS NULL")) {
            statement.setString(1, fecha.toString());
            statement.setLong(2, prestamo.id());
            if (statement.executeUpdate() == 0) {
                throw new DataAccessException("El préstamo ya fue devuelto o no existe.", null);
            }
            return prestamo.conDevolucion(fecha);
        } catch (SQLException exception) {
            throw new DataAccessException("No se pudo registrar la devolución.", exception);
        }
    }

    public Optional<Prestamo> buscar(long id) {
        String sql = "SELECT " + CAMPOS + " FROM prestamos WHERE id = ?";
        try (Connection connection = database.abrir();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            return consultarOpcional(statement);
        } catch (SQLException exception) {
            throw new DataAccessException("No se pudo consultar el préstamo.", exception);
        }
    }

    public Optional<Prestamo> activoPorItem(long itemId) {
        try (Connection connection = database.abrir()) {
            return consultarActivo(connection, itemId);
        } catch (SQLException exception) {
            throw new DataAccessException("No se pudo consultar el préstamo activo.", exception);
        }
    }

    public List<Prestamo> historial(long itemId) {
        String sql = "SELECT " + CAMPOS + " FROM prestamos WHERE item_id = ? ORDER BY fecha_prestamo DESC, id DESC";
        try (Connection connection = database.abrir();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, itemId);
            return consultarLista(statement);
        } catch (SQLException exception) {
            throw new DataAccessException("No se pudo consultar el historial de préstamos.", exception);
        }
    }

    public List<Prestamo> activos() {
        String sql = "SELECT " + CAMPOS + " FROM prestamos WHERE fecha_devolucion IS NULL ORDER BY fecha_limite";
        try (Connection connection = database.abrir();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            return consultarLista(statement);
        } catch (SQLException exception) {
            throw new DataAccessException("No se pudieron consultar los préstamos activos.", exception);
        }
    }

    private static Optional<Prestamo> consultarActivo(Connection connection, long itemId) throws SQLException {
        String sql = "SELECT " + CAMPOS + " FROM prestamos WHERE item_id = ? AND fecha_devolucion IS NULL";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, itemId);
            return consultarOpcional(statement);
        }
    }

    private static Optional<Prestamo> consultarOpcional(PreparedStatement statement) throws SQLException {
        try (ResultSet resultSet = statement.executeQuery()) {
            return resultSet.next() ? Optional.of(mapear(resultSet)) : Optional.empty();
        }
    }

    private static List<Prestamo> consultarLista(PreparedStatement statement) throws SQLException {
        List<Prestamo> prestamos = new ArrayList<>();
        try (ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                prestamos.add(mapear(resultSet));
            }
        }
        return prestamos;
    }

    private static Prestamo mapear(ResultSet resultSet) throws SQLException {
        String devolucion = resultSet.getString("fecha_devolucion");
        return new Prestamo(resultSet.getLong("id"), resultSet.getLong("item_id"), resultSet.getString("amigo"),
                LocalDate.parse(resultSet.getString("fecha_prestamo")), LocalDate.parse(resultSet.getString("fecha_limite")),
                devolucion == null ? null : LocalDate.parse(devolucion));
    }

    private static void deshacer(Connection connection, SQLException original) {
        try {
            connection.rollback();
        } catch (SQLException rollback) {
            original.addSuppressed(rollback);
        }
    }
}
