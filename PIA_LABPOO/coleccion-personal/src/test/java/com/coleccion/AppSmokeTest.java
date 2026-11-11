package com.coleccion;

import javafx.application.Platform;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class AppSmokeTest {
    @TempDir
    static Path carpeta;

    @BeforeAll
    static void iniciarJavaFx() throws Exception {
        assumeTrue(System.getenv("DISPLAY") != null, "Se requiere un display para la prueba gráfica");
        CountDownLatch inicio = new CountDownLatch(1);
        Platform.startup(inicio::countDown);
        assertTrue(inicio.await(10, TimeUnit.SECONDS));
    }

    @AfterAll
    static void cerrarJavaFx() {
        if (System.getenv("DISPLAY") != null) {
            Platform.exit();
        }
    }

    @Test
    void cargaLaAplicacionYSusRecursos() throws Exception {
        System.setProperty("coleccion.db", carpeta.resolve("ui.db").toString());
        FutureTask<Void> tarea = new FutureTask<>(() -> {
            Stage stage = new Stage();
            new App().start(stage);
            assertNotNull(stage.getScene());
            assertTrue(stage.isShowing());
            assertFalse(stage.getScene().getStylesheets().isEmpty());
            stage.close();
            return null;
        });
        Platform.runLater(tarea);
        tarea.get(15, TimeUnit.SECONDS);
    }
}
