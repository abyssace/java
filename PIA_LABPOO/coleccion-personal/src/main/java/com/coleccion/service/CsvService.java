package com.coleccion.service;

import com.coleccion.model.DetalleItem;
import com.coleccion.model.EstadoItem;
import com.coleccion.model.FiltroItem;
import com.coleccion.model.Item;
import com.coleccion.model.Pagina;
import com.coleccion.model.ResultadoImportacion;
import com.coleccion.model.TipoItem;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class CsvService {
    private static final List<String> COLUMNAS = List.of("titulo", "tipo", "anio", "notas", "fecha_adquisicion",
            "precio_pagado", "portada_ruta", "estado", "calificacion", "resena", "etiquetas", "autor", "isbn",
            "editorial", "director", "duracion_minutos", "plataforma", "desarrollador", "juego", "rareza", "condicion");
    private final ItemService items;

    public CsvService(ItemService items) {
        this.items = items;
    }

    public void exportar(Path archivo) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(archivo);
             CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT)) {
            printer.printRecord(COLUMNAS);
            for (Item item : todosLosItems()) {
                printer.printRecord(fila(item));
            }
        }
    }

    public ResultadoImportacion importar(Path archivo) throws IOException {
        int importados = 0;
        List<String> errores = new ArrayList<>();
        CSVFormat formato = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .get();
        try (BufferedReader reader = Files.newBufferedReader(archivo);
             CSVParser parser = new CSVParser(reader, formato)) {
            if (!new HashSet<>(parser.getHeaderNames()).equals(new HashSet<>(COLUMNAS))) {
                throw new IllegalArgumentException(
                        "El CSV debe contener exactamente estas columnas: " + String.join(", ", COLUMNAS));
            }
            Iterator<CSVRecord> registros = parser.iterator();
            while (true) {
                CSVRecord record;
                try {
                    if (!registros.hasNext()) {
                        break;
                    }
                    record = registros.next();
                } catch (RuntimeException exception) {
                    errores.add("No se pudo leer el resto del archivo: " + mensaje(exception));
                    break;
                }
                try {
                    items.guardar(convertir(record));
                    importados++;
                } catch (RuntimeException exception) {
                    errores.add("Fila " + (record.getRecordNumber() + 1) + ": " + mensaje(exception));
                }
            }
        }
        return new ResultadoImportacion(importados, errores);
    }

    private List<Item> todosLosItems() {
        List<Item> resultado = new ArrayList<>();
        FiltroItem filtro = new FiltroItem("", null, null, null, null, null, null, Set.of(),
                false, false, FiltroItem.Orden.TITULO_ASC, 0, 100);
        int pagina = 0;
        int totalPaginas;
        do {
            Pagina<Item> actual = items.buscar(filtro.conPagina(pagina));
            resultado.addAll(actual.elementos());
            totalPaginas = actual.totalPaginas();
            pagina++;
        } while (pagina < totalPaginas);
        return resultado;
    }

    private static List<String> fila(Item item) {
        Map<String, String> valores = new LinkedHashMap<>();
        valores.put("titulo", texto(item.titulo()));
        valores.put("tipo", item.tipo().name());
        valores.put("anio", texto(item.anio()));
        valores.put("notas", texto(item.notas()));
        valores.put("fecha_adquisicion", texto(item.fechaAdquisicion()));
        valores.put("precio_pagado", item.precioPagado() == null ? "" : item.precioPagado().toPlainString());
        valores.put("portada_ruta", texto(item.portadaRuta()));
        valores.put("estado", item.estado().name());
        valores.put("calificacion", texto(item.calificacion()));
        valores.put("resena", texto(item.resena()));
        valores.put("etiquetas", String.join("|", item.etiquetas().stream().sorted().toList()));
        switch (item.detalle()) {
            case DetalleItem.Libro libro -> {
                valores.put("autor", texto(libro.autor()));
                valores.put("isbn", texto(libro.isbn()));
                valores.put("editorial", texto(libro.editorial()));
            }
            case DetalleItem.Pelicula pelicula -> {
                valores.put("director", texto(pelicula.director()));
                valores.put("duracion_minutos", texto(pelicula.duracionMinutos()));
            }
            case DetalleItem.Videojuego videojuego -> {
                valores.put("plataforma", texto(videojuego.plataforma()));
                valores.put("desarrollador", texto(videojuego.desarrollador()));
            }
            case DetalleItem.Carta carta -> {
                valores.put("juego", texto(carta.juego()));
                valores.put("rareza", texto(carta.rareza()));
                valores.put("condicion", texto(carta.condicion()));
            }
        }
        return COLUMNAS.stream().map(columna -> valores.getOrDefault(columna, "")).toList();
    }

    private static Item convertir(CSVRecord record) {
        TipoItem tipo = TipoItem.valueOf(limpiar(record.get("tipo")).toUpperCase(Locale.ROOT));
        EstadoItem estado = EstadoItem.valueOf(limpiar(record.get("estado")).toUpperCase(Locale.ROOT));
        DetalleItem detalle = switch (tipo) {
            case LIBRO -> new DetalleItem.Libro(limpiar(record.get("autor")), limpiar(record.get("isbn")),
                    limpiar(record.get("editorial")));
            case PELICULA -> new DetalleItem.Pelicula(limpiar(record.get("director")),
                    entero(record.get("duracion_minutos")));
            case VIDEOJUEGO -> new DetalleItem.Videojuego(limpiar(record.get("plataforma")),
                    limpiar(record.get("desarrollador")));
            case CARTA -> new DetalleItem.Carta(limpiar(record.get("juego")), limpiar(record.get("rareza")),
                    limpiar(record.get("condicion")));
        };
        String fecha = limpiar(record.get("fecha_adquisicion"));
        String precio = limpiar(record.get("precio_pagado"));
        return new Item(null, record.get("titulo"), tipo, entero(record.get("anio")), record.get("notas"),
                fecha == null ? null : LocalDate.parse(fecha), precio == null ? null : new BigDecimal(precio),
                limpiar(record.get("portada_ruta")), estado, entero(record.get("calificacion")), record.get("resena"),
                detalle, etiquetas(record.get("etiquetas")));
    }

    private static Set<String> etiquetas(String texto) {
        Set<String> etiquetas = new LinkedHashSet<>();
        if (texto != null && !texto.isBlank()) {
            for (String etiqueta : texto.split("\\|")) {
                if (!etiqueta.isBlank()) {
                    etiquetas.add(etiqueta.trim());
                }
            }
        }
        return etiquetas;
    }

    private static Integer entero(String texto) {
        String limpio = limpiar(texto);
        return limpio == null ? null : Integer.valueOf(limpio);
    }

    private static String limpiar(String texto) {
        return texto == null || texto.isBlank() ? null : texto.trim();
    }

    private static String texto(Object valor) {
        return valor == null ? "" : valor.toString();
    }

    private static String mensaje(Exception exception) {
        return exception.getMessage() == null ? exception.getClass().getSimpleName() : exception.getMessage();
    }
}
