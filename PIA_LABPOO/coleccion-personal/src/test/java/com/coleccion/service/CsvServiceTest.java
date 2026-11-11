package com.coleccion.service;

import com.coleccion.ItemFixtures;
import com.coleccion.dao.ItemDao;
import com.coleccion.model.FiltroItem;
import com.coleccion.model.Item;
import com.coleccion.model.ResultadoImportacion;
import com.coleccion.util.Database;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CsvServiceTest {
    @TempDir
    Path carpeta;

    @Test
    void exportaEImportaTextoConComasComillasYSaltosDeLinea() throws Exception {
        ItemService origen = servicio("origen.db");
        Item original = ItemFixtures.libro("El \"mejor\", libro");
        original = new Item(null, original.titulo(), original.tipo(), original.anio(), "Línea 1\nLínea 2",
                original.fechaAdquisicion(), original.precioPagado(), original.portadaRuta(), original.estado(),
                original.calificacion(), original.resena(), original.detalle(), Set.of("favorito", "clásico"));
        origen.guardar(original);
        Path archivo = carpeta.resolve("coleccion.csv");

        new CsvService(origen).exportar(archivo);
        ItemService destino = servicio("destino.db");
        ResultadoImportacion resultado = new CsvService(destino).importar(archivo);

        assertEquals(1, resultado.importados());
        assertTrue(resultado.errores().isEmpty());
        Item importado = destino.buscar(FiltroItem.porDefecto()).elementos().getFirst();
        assertEquals("El \"mejor\", libro", importado.titulo());
        assertEquals("Línea 1\nLínea 2", importado.notas());
        assertEquals(Set.of("favorito", "clásico"), importado.etiquetas());
    }

    @Test
    void importaFilasValidasYReportaLasInvalidas() throws Exception {
        Path archivo = carpeta.resolve("mixto.csv");
        escribir(archivo, List.of(fila("Inválido", "99"), fila("Válido", "8")));
        ItemService servicio = servicio("mixto.db");

        ResultadoImportacion resultado = new CsvService(servicio).importar(archivo);

        assertEquals(1, resultado.importados());
        assertEquals(1, resultado.errores().size());
        assertTrue(resultado.errores().getFirst().contains("Fila 2"));
        assertEquals("Válido", servicio.buscar(FiltroItem.porDefecto()).elementos().getFirst().titulo());
    }

    @Test
    void rechazaUnaCabeceraDistintaSinImportarNada() throws Exception {
        Path archivo = carpeta.resolve("cabecera.csv");
        Files.writeString(archivo, "titulo,tipo\nSolo,LIBRO\n");
        ItemService servicio = servicio("cabecera.db");

        assertThrows(IllegalArgumentException.class, () -> new CsvService(servicio).importar(archivo));
        assertEquals(0, servicio.buscar(FiltroItem.porDefecto()).total());
    }

    @Test
    void conservaFilasValidasAntesDeUnErrorDeSintaxis() throws Exception {
        Path archivo = carpeta.resolve("roto.csv");
        Files.writeString(archivo, String.join("\n",
                String.join(",", cabecera()),
                String.join(",", fila("Válido", "8")),
                "\"sin cerrar,resto"));
        ItemService servicio = servicio("roto.db");

        ResultadoImportacion resultado = new CsvService(servicio).importar(archivo);

        assertEquals(1, resultado.importados());
        assertEquals(1, resultado.errores().size());
        assertTrue(resultado.errores().getFirst().contains("No se pudo leer"));
        assertEquals("Válido", servicio.buscar(FiltroItem.porDefecto()).elementos().getFirst().titulo());
    }

    private ItemService servicio(String nombre) throws Exception {
        return new ItemService(new ItemDao(new Database(carpeta.resolve(nombre))));
    }

    private static List<String> fila(String titulo, String calificacion) {
        return List.of(titulo, "LIBRO", "2020", "", "2026-01-01", "10.50", "", "PENDIENTE",
                calificacion, "", "favorito|clásico", "Autor", "", "Editorial", "", "", "", "", "", "", "");
    }

    private static List<String> cabecera() {
        return List.of("titulo", "tipo", "anio", "notas", "fecha_adquisicion", "precio_pagado",
                "portada_ruta", "estado", "calificacion", "resena", "etiquetas", "autor", "isbn", "editorial",
                "director", "duracion_minutos", "plataforma", "desarrollador", "juego", "rareza", "condicion");
    }

    private static void escribir(Path archivo, List<List<String>> filas) throws Exception {
        try (BufferedWriter writer = Files.newBufferedWriter(archivo);
             CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT)) {
            printer.printRecord(cabecera());
            for (List<String> fila : filas) {
                printer.printRecord(fila);
            }
        }
    }
}
