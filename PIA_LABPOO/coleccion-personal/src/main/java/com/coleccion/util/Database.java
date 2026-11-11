package com.coleccion.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class Database {
    private final String url;

    public Database(Path archivo) throws IOException, SQLException {
        Path ruta = archivo.toAbsolutePath();
        Files.createDirectories(ruta.getParent());
        this.url = "jdbc:sqlite:" + ruta;
        inicializar();
    }

    public Connection abrir() throws SQLException {
        Connection connection = DriverManager.getConnection(url);
        try {
            ejecutar(connection, "PRAGMA foreign_keys = ON");
            ejecutar(connection, "PRAGMA journal_mode = WAL");
            ejecutar(connection, "PRAGMA busy_timeout = 5000");
            return connection;
        } catch (SQLException exception) {
            connection.close();
            throw exception;
        }
    }

    private void inicializar() throws IOException, SQLException {
        String script;
        try (InputStream input = Database.class.getResourceAsStream("/schema.sql")) {
            if (input == null) {
                throw new IOException("No se encontró schema.sql");
            }
            script = new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
        try (Connection connection = abrir()) {
            for (String sql : script.split(";")) {
                if (!sql.trim().isEmpty()) {
                    ejecutar(connection, sql);
                }
            }
        }
    }

    private static void ejecutar(Connection connection, String sql) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.execute();
        }
    }
}
