package com.coleccion.service;

import com.coleccion.ItemFixtures;
import com.coleccion.dao.ItemDao;
import com.coleccion.dao.PrestamoDao;
import com.coleccion.model.EstadoPrestamo;
import com.coleccion.model.Item;
import com.coleccion.model.Prestamo;
import com.coleccion.util.Database;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class PrestamoServiceTest {
    @TempDir
    Path carpeta;
    Database database;
    ItemService items;
    PrestamoService prestamos;
    Item item;

    @BeforeEach
    void preparar() throws Exception {
        database = new Database(carpeta.resolve("prestamos.db"));
        items = new ItemService(new ItemDao(database));
        prestamos = new PrestamoService(new PrestamoDao(database));
        item = items.guardar(ItemFixtures.libro("Libro prestado"));
    }

    @Test
    void noPermiteDosPrestamosActivosYDetectaVencido() {
        Prestamo prestamo = prestamos.prestar(item.id(), " Ana ",
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 10));

        assertEquals("Ana", prestamo.amigo());
        assertEquals(EstadoPrestamo.VENCIDO, prestamo.estadoEn(LocalDate.of(2026, 1, 11)));
        assertThrows(ValidacionException.class, () -> prestamos.prestar(item.id(), "Luis",
                LocalDate.of(2026, 1, 2), LocalDate.of(2026, 1, 12)));
    }

    @Test
    void permiteDevolverYVolverAPrestarConservandoHistorial() {
        Prestamo original = prestamos.prestar(item.id(), "Ana",
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 10));
        Prestamo devuelto = prestamos.devolver(original.id(), LocalDate.of(2026, 1, 9));

        assertEquals(EstadoPrestamo.DEVUELTO, devuelto.estadoEn(LocalDate.of(2026, 1, 20)));
        assertTrue(prestamos.activoPorItem(item.id()).isEmpty());
        assertEquals(1, prestamos.historial(item.id()).size());

        prestamos.prestar(item.id(), "Luis", LocalDate.of(2026, 2, 1), LocalDate.of(2026, 2, 10));
        assertEquals(2, prestamos.historial(item.id()).size());
        assertEquals(1, prestamos.activos().size());
    }

    @Test
    void validaAmigoYFechas() {
        LocalDate inicio = LocalDate.of(2026, 1, 10);
        assertThrows(ValidacionException.class, () -> prestamos.prestar(item.id(), " ", inicio, inicio));
        assertThrows(ValidacionException.class, () -> prestamos.prestar(item.id(), "Ana", inicio, inicio.minusDays(1)));

        Prestamo prestamo = prestamos.prestar(item.id(), "Ana", inicio, inicio.plusDays(5));
        assertThrows(ValidacionException.class, () -> prestamos.devolver(prestamo.id(), inicio.minusDays(1)));
    }

    @Test
    void soloUnPrestamoProsperaBajoCarrera() throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(2);
        CountDownLatch listos = new CountDownLatch(2);
        CountDownLatch salida = new CountDownLatch(1);
        try {
            Callable<Boolean> intento = () -> {
                PrestamoService servicio = new PrestamoService(new PrestamoDao(database));
                listos.countDown();
                salida.await(5, TimeUnit.SECONDS);
                try {
                    servicio.prestar(item.id(), "Ana", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 10));
                    return true;
                } catch (RuntimeException exception) {
                    return false;
                }
            };
            Future<Boolean> primero = pool.submit(intento);
            Future<Boolean> segundo = pool.submit(intento);
            assertTrue(listos.await(5, TimeUnit.SECONDS));
            salida.countDown();

            long exitos = (primero.get(10, TimeUnit.SECONDS) ? 1 : 0)
                    + (segundo.get(10, TimeUnit.SECONDS) ? 1 : 0);
            assertEquals(1, exitos);
            assertEquals(1, prestamos.activos().size());
        } finally {
            pool.shutdownNow();
        }
    }

    @Test
    void noPermiteDevolverDosVeces() {
        Prestamo prestamo = prestamos.prestar(item.id(), "Ana",
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 10));
        prestamos.devolver(prestamo.id(), LocalDate.of(2026, 1, 9));

        assertThrows(ValidacionException.class,
                () -> prestamos.devolver(prestamo.id(), LocalDate.of(2026, 1, 10)));
    }
}
