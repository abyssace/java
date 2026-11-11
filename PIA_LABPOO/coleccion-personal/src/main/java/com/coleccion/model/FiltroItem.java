package com.coleccion.model;

import java.util.Set;

public record FiltroItem(
        String texto,
        TipoItem tipo,
        EstadoItem estado,
        Integer calificacionMin,
        Integer calificacionMax,
        Integer anioMin,
        Integer anioMax,
        Set<String> etiquetas,
        boolean soloPrestados,
        boolean soloVencidos,
        Orden orden,
        int pagina,
        int tamanoPagina) {

    public enum Orden {
        TITULO_ASC("Título (A-Z)"),
        TITULO_DESC("Título (Z-A)"),
        ANIO_DESC("Año más reciente"),
        CALIFICACION_DESC("Mejor calificación"),
        ADQUISICION_DESC("Adquisición reciente");

        private final String descripcion;

        Orden(String descripcion) {
            this.descripcion = descripcion;
        }

        @Override
        public String toString() {
            return descripcion;
        }
    }

    public FiltroItem {
        etiquetas = etiquetas == null ? Set.of() : Set.copyOf(etiquetas);
        orden = orden == null ? Orden.TITULO_ASC : orden;
    }

    public static FiltroItem porDefecto() {
        return new FiltroItem("", null, null, null, null, null, null,
                Set.of(), false, false, Orden.TITULO_ASC, 0, 20);
    }

    public FiltroItem conPagina(int nuevaPagina) {
        return new FiltroItem(texto, tipo, estado, calificacionMin, calificacionMax, anioMin, anioMax,
                etiquetas, soloPrestados, soloVencidos, orden, nuevaPagina, tamanoPagina);
    }
}
