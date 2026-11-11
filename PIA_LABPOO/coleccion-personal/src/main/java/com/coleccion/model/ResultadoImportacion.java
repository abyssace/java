package com.coleccion.model;

import java.util.List;

public record ResultadoImportacion(int importados, List<String> errores) {
    public ResultadoImportacion {
        errores = List.copyOf(errores);
    }
}
