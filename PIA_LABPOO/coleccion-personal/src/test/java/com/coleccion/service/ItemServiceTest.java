package com.coleccion.service;

import com.coleccion.ItemFixtures;
import com.coleccion.dao.ItemDao;
import com.coleccion.model.DetalleItem;
import com.coleccion.model.Item;
import com.coleccion.util.Database;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ItemServiceTest {
    @TempDir
    Path carpeta;
    ItemService service;

    @BeforeEach
    void preparar() throws Exception {
        service = new ItemService(new ItemDao(new Database(carpeta.resolve("servicio.db"))));
    }

    @Test
    void rechazaTituloVacio() {
        Item item = new Item(null, "  ", null, null, null, null, null, null,
                null, null, null, null, Set.of());
        assertThrows(ValidacionException.class, () -> service.guardar(item));
    }

    @Test
    void rechazaCalificacionFueraDeRango() {
        Item original = ItemFixtures.libro("Libro");
        Item item = new Item(null, original.titulo(), original.tipo(), original.anio(), original.notas(),
                original.fechaAdquisicion(), original.precioPagado(), original.portadaRuta(), original.estado(),
                11, original.resena(), original.detalle(), original.etiquetas());
        assertThrows(ValidacionException.class, () -> service.guardar(item));
    }

    @Test
    void rechazaPrecioNegativoODemasiadosDecimales() {
        Item original = ItemFixtures.libro("Libro");
        Item negativo = new Item(null, original.titulo(), original.tipo(), original.anio(), original.notas(),
                original.fechaAdquisicion(), new BigDecimal("-1"), original.portadaRuta(), original.estado(),
                original.calificacion(), original.resena(), original.detalle(), original.etiquetas());
        Item tresDecimales = new Item(null, original.titulo(), original.tipo(), original.anio(), original.notas(),
                original.fechaAdquisicion(), new BigDecimal("1.999"), original.portadaRuta(), original.estado(),
                original.calificacion(), original.resena(), original.detalle(), original.etiquetas());

        assertThrows(ValidacionException.class, () -> service.guardar(negativo));
        assertThrows(ValidacionException.class, () -> service.guardar(tresDecimales));
    }

    @Test
    void rechazaDetalleQueNoCorrespondeAlTipo() {
        Item original = ItemFixtures.libro("Libro");
        Item item = new Item(null, original.titulo(), original.tipo(), original.anio(), original.notas(),
                original.fechaAdquisicion(), original.precioPagado(), original.portadaRuta(), original.estado(),
                original.calificacion(), original.resena(), new DetalleItem.Pelicula("Otro", 90), original.etiquetas());
        assertThrows(ValidacionException.class, () -> service.guardar(item));
    }

    @Test
    void rechazaEtiquetasConElSeparadorReservado() {
        Item base = ItemFixtures.libro("Libro");
        Item item = new Item(null, base.titulo(), base.tipo(), base.anio(), base.notas(),
                base.fechaAdquisicion(), base.precioPagado(), base.portadaRuta(), base.estado(),
                base.calificacion(), base.resena(), base.detalle(), Set.of("ciencia|ficción"));

        assertThrows(ValidacionException.class, () -> service.guardar(item));
    }
}
