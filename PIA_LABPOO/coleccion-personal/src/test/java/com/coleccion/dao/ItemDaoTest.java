package com.coleccion.dao;

import com.coleccion.ItemFixtures;
import com.coleccion.model.DetalleItem;
import com.coleccion.model.Estadisticas;
import com.coleccion.model.EstadoItem;
import com.coleccion.model.FiltroItem;
import com.coleccion.model.Item;
import com.coleccion.model.Pagina;
import com.coleccion.model.TipoItem;
import com.coleccion.service.ItemService;
import com.coleccion.util.Database;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ItemDaoTest {
    @TempDir
    Path carpeta;
    Database database;
    ItemService service;

    @BeforeEach
    void preparar() throws Exception {
        database = new Database(carpeta.resolve("dao.db"));
        service = new ItemService(new ItemDao(database));
    }

    @Test
    void creaRecuperaActualizaYEliminaUnItemCompleto() {
        Item original = ItemFixtures.libro("  El libro  ");
        original = new Item(null, original.titulo(), original.tipo(), original.anio(), original.notas(),
                original.fechaAdquisicion(), original.precioPagado(), original.portadaRuta(), original.estado(),
                original.calificacion(), original.resena(), original.detalle(), Set.of(" Fantasía ", "FAVORITO", "favorito"));

        Item guardado = service.guardar(original);
        Item recuperado = service.buscar(guardado.id()).orElseThrow();

        assertNotNull(guardado.id());
        assertEquals("El libro", recuperado.titulo());
        assertEquals(Set.of("fantasía", "favorito"), recuperado.etiquetas());
        assertEquals("Autor", ((DetalleItem.Libro) recuperado.detalle()).autor());

        Item editado = new Item(guardado.id(), "Editado", guardado.tipo(), 2024, null, null, null, null,
                EstadoItem.COMPLETADO, 9, null, new DetalleItem.Libro("Otra autora", "978-2", null), Set.of("clásico"));
        service.guardar(editado);
        Item actualizado = service.buscar(guardado.id()).orElseThrow();
        assertEquals("Editado", actualizado.titulo());
        assertEquals(Set.of("clásico"), actualizado.etiquetas());
        assertEquals("Otra autora", ((DetalleItem.Libro) actualizado.detalle()).autor());

        service.eliminar(guardado.id());
        assertTrue(service.buscar(guardado.id()).isEmpty());
    }

    @Test
    void combinaFiltrosYExigeTodasLasEtiquetas() {
        Item libro = item(ItemFixtures.libro("Cien años"), 1967, 10, EstadoItem.COMPLETADO, Set.of("fantasía", "favorito"));
        service.guardar(libro);
        service.guardar(item(ItemFixtures.deTipo(TipoItem.VIDEOJUEGO, "Juego"), 2017, 7, EstadoItem.PENDIENTE, Set.of("fantasía")));

        FiltroItem filtro = new FiltroItem("Cien", TipoItem.LIBRO, EstadoItem.COMPLETADO,
                8, 10, 1960, 1970, Set.of("fantasía", "favorito"), false, false,
                FiltroItem.Orden.TITULO_ASC, 0, 20);

        Pagina<Item> resultado = service.buscar(filtro);
        assertEquals(1, resultado.total());
        assertEquals("Cien años", resultado.elementos().getFirst().titulo());
    }

    @Test
    void filtraPrestadosYVencidosYCalculaEstadisticas() throws Exception {
        Item vencido = service.guardar(item(ItemFixtures.libro("Cien años"), 1967, 10, EstadoItem.COMPLETADO, Set.of("fantasía")));
        Item activo = service.guardar(item(ItemFixtures.deTipo(TipoItem.PELICULA, "Matrix"), 1999, 9, EstadoItem.COMPLETADO, Set.of("ciencia")));
        service.guardar(item(ItemFixtures.deTipo(TipoItem.VIDEOJUEGO, "Juego"), 2017, 7, EstadoItem.PENDIENTE, Set.of("fantasía")));
        prestar(vencido.id(), "2000-01-01");
        prestar(activo.id(), "2999-01-01");

        FiltroItem prestados = new FiltroItem("", null, null, null, null, null, null, Set.of(),
                true, false, FiltroItem.Orden.TITULO_ASC, 0, 20);
        FiltroItem vencidos = new FiltroItem("", null, null, null, null, null, null, Set.of(),
                false, true, FiltroItem.Orden.TITULO_ASC, 0, 20);

        assertEquals(List.of("Cien años", "Matrix"), service.buscar(prestados).elementos().stream().map(Item::titulo).toList());
        assertEquals(List.of("Cien años"), service.buscar(vencidos).elementos().stream().map(Item::titulo).toList());

        Estadisticas estadisticas = service.estadisticas();
        assertEquals(3, estadisticas.total());
        assertEquals(2, estadisticas.prestamosActivos());
        assertEquals(1, estadisticas.porTipo().get(TipoItem.LIBRO));
        assertEquals(2, estadisticas.porEstado().get(EstadoItem.COMPLETADO));
        assertEquals(8.666, estadisticas.promedioCalificacion(), 0.001);
    }

    @Test
    void ordenaYPaginaSinDuplicarPorEtiquetas() {
        service.guardar(item(ItemFixtures.libro("Cien años"), 1967, 10, EstadoItem.COMPLETADO, Set.of("fantasía", "favorito")));
        service.guardar(item(ItemFixtures.deTipo(TipoItem.PELICULA, "Matrix"), 1999, 9, EstadoItem.COMPLETADO, Set.of("favorito")));
        service.guardar(item(ItemFixtures.deTipo(TipoItem.VIDEOJUEGO, "Juego"), 2017, 7, EstadoItem.PENDIENTE, Set.of()));

        FiltroItem filtro = new FiltroItem("", null, null, null, null, null, null, Set.of(),
                false, false, FiltroItem.Orden.CALIFICACION_DESC, 1, 1);
        Pagina<Item> resultado = service.buscar(filtro);

        assertEquals(3, resultado.total());
        assertEquals(3, resultado.totalPaginas());
        assertEquals(1, resultado.elementos().size());
        assertEquals("Matrix", resultado.elementos().getFirst().titulo());
        assertEquals(List.of("fantasía", "favorito"), service.etiquetas());
    }

    @Test
    void trataElPorcentajeDelTextoComoLiteral() {
        service.guardar(ItemFixtures.libro("100% Java"));
        service.guardar(ItemFixtures.libro("100 años de Java"));
        FiltroItem filtro = new FiltroItem("100%", null, null, null, null, null, null, Set.of(),
                false, false, FiltroItem.Orden.TITULO_ASC, 0, 20);

        assertEquals(List.of("100% Java"), service.buscar(filtro).elementos().stream().map(Item::titulo).toList());
    }

    private Item item(Item base, int anio, int calificacion, EstadoItem estado, Set<String> etiquetas) {
        return new Item(null, base.titulo(), base.tipo(), anio, base.notas(), base.fechaAdquisicion(),
                base.precioPagado(), base.portadaRuta(), estado, calificacion, base.resena(), base.detalle(), etiquetas);
    }

    private void prestar(long itemId, String limite) throws Exception {
        try (Connection connection = database.abrir();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO prestamos (item_id, amigo, fecha_prestamo, fecha_limite) VALUES (?, 'Ana', '1999-01-01', ?)")) {
            statement.setLong(1, itemId);
            statement.setString(2, limite);
            statement.executeUpdate();
        }
    }
}
