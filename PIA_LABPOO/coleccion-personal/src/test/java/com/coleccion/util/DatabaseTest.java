package com.coleccion.util;

import com.coleccion.model.EstadoPrestamo;
import com.coleccion.model.Prestamo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseTest {

    @TempDir
    Path carpeta;

    @Test
    void aplicaRestriccionesDeIntegridad() throws Exception {
        Database database = new Database(carpeta.resolve("prueba.db"));

        try (Connection connection = database.abrir()) {
            assertEquals(1, valorEntero(connection, "PRAGMA foreign_keys"));
            assertEquals("wal", valorTexto(connection, "PRAGMA journal_mode"));

            assertThrows(SQLException.class, () -> ejecutar(connection,
                    "INSERT INTO items (titulo, tipo, estado, calificacion) VALUES ('Malo', 'LIBRO', 'PENDIENTE', 11)"));
            assertThrows(SQLException.class, () -> ejecutar(connection,
                    "INSERT INTO libros (item_id) VALUES (999)"));

            ejecutar(connection, "INSERT INTO items (id, titulo, tipo, estado) VALUES (1, 'Uno', 'LIBRO', 'PENDIENTE')");
            ejecutar(connection, "INSERT INTO items (id, titulo, tipo, estado) VALUES (2, 'Dos', 'LIBRO', 'PENDIENTE')");
            ejecutar(connection, "INSERT INTO libros (item_id, isbn) VALUES (1, '978-1')");
            assertThrows(SQLException.class, () -> ejecutar(connection,
                    "INSERT INTO libros (item_id, isbn) VALUES (2, '978-1')"));

            ejecutar(connection, "INSERT INTO prestamos (item_id, amigo, fecha_prestamo, fecha_limite) VALUES (1, 'Ana', '2026-01-01', '2026-01-10')");
            assertThrows(SQLException.class, () -> ejecutar(connection,
                    "INSERT INTO prestamos (item_id, amigo, fecha_prestamo, fecha_limite) VALUES (1, 'Luis', '2026-01-02', '2026-01-11')"));
        }
    }

    @Test
    void calculaElEstadoDelPrestamoSinGuardarlo() {
        LocalDate hoy = LocalDate.of(2026, 2, 1);
        Prestamo activo = new Prestamo(1L, 2L, "Ana",
                LocalDate.of(2026, 1, 20), LocalDate.of(2026, 2, 1), null);
        Prestamo devuelto = activo.conDevolucion(LocalDate.of(2026, 1, 31));

        assertEquals(EstadoPrestamo.ACTIVO, activo.estadoEn(hoy));
        assertEquals(EstadoPrestamo.VENCIDO, activo.estadoEn(hoy.plusDays(1)));
        assertEquals(EstadoPrestamo.DEVUELTO, devuelto.estadoEn(hoy.plusDays(1)));
    }

    private static int valorEntero(Connection connection, String sql) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            return resultSet.getInt(1);
        }
    }

    private static String valorTexto(Connection connection, String sql) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            return resultSet.getString(1).toLowerCase();
        }
    }

    private static void ejecutar(Connection connection, String sql) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
        }
    }
}
