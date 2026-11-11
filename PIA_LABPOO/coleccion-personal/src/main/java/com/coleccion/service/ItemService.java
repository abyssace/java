package com.coleccion.service;

import com.coleccion.dao.ItemDao;
import com.coleccion.model.DetalleItem;
import com.coleccion.model.Estadisticas;
import com.coleccion.model.EstadoItem;
import com.coleccion.model.FiltroItem;
import com.coleccion.model.Item;
import com.coleccion.model.Pagina;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

public final class ItemService {
    private final ItemDao dao;

    public ItemService(ItemDao dao) {
        this.dao = dao;
    }

    public Item guardar(Item item) {
        validar(item);
        return dao.guardar(normalizar(item));
    }

    public Optional<Item> buscar(long id) {
        if (id <= 0) {
            throw new ValidacionException("El identificador del ítem no es válido.");
        }
        return dao.buscar(id);
    }

    public void eliminar(long id) {
        if (id <= 0) {
            throw new ValidacionException("El identificador del ítem no es válido.");
        }
        dao.eliminar(id);
    }

    public Pagina<Item> buscar(FiltroItem filtro) {
        if (filtro == null) {
            filtro = FiltroItem.porDefecto();
        }
        validarRango(filtro.calificacionMin(), 1, 10, "La calificación mínima");
        validarRango(filtro.calificacionMax(), 1, 10, "La calificación máxima");
        validarRango(filtro.anioMin(), 0, 9999, "El año mínimo");
        validarRango(filtro.anioMax(), 0, 9999, "El año máximo");
        compararLimites(filtro.calificacionMin(), filtro.calificacionMax(), "calificación");
        compararLimites(filtro.anioMin(), filtro.anioMax(), "año");
        if (filtro.pagina() < 0 || filtro.tamanoPagina() < 1 || filtro.tamanoPagina() > 100) {
            throw new ValidacionException("La paginación indicada no es válida.");
        }
        FiltroItem normalizado = new FiltroItem(limpiar(filtro.texto()), filtro.tipo(), filtro.estado(),
                filtro.calificacionMin(), filtro.calificacionMax(), filtro.anioMin(), filtro.anioMax(),
                normalizarEtiquetas(filtro.etiquetas()), filtro.soloPrestados(), filtro.soloVencidos(),
                filtro.orden(), filtro.pagina(), filtro.tamanoPagina());
        return dao.buscar(normalizado);
    }

    public Estadisticas estadisticas() {
        return dao.estadisticas();
    }

    public List<String> etiquetas() {
        return dao.etiquetas();
    }

    private static void validar(Item item) {
        if (item == null || limpiar(item.titulo()) == null) {
            throw new ValidacionException("El título es obligatorio.");
        }
        if (item.tipo() == null) {
            throw new ValidacionException("El tipo es obligatorio.");
        }
        if (item.estado() == null) {
            throw new ValidacionException("El estado es obligatorio.");
        }
        validarRango(item.anio(), 0, 9999, "El año");
        validarRango(item.calificacion(), 1, 10, "La calificación");
        if (item.precioPagado() != null && (item.precioPagado().signum() < 0
                || item.precioPagado().stripTrailingZeros().scale() > 2)) {
            throw new ValidacionException("El precio debe ser positivo y tener como máximo dos decimales.");
        }
        validarDetalle(item.tipo(), item.detalle());
    }

    private static void validarDetalle(com.coleccion.model.TipoItem tipo, DetalleItem detalle) {
        boolean correcto = switch (tipo) {
            case LIBRO -> detalle instanceof DetalleItem.Libro;
            case PELICULA -> detalle instanceof DetalleItem.Pelicula pelicula
                    && (pelicula.duracionMinutos() == null || pelicula.duracionMinutos() > 0);
            case VIDEOJUEGO -> detalle instanceof DetalleItem.Videojuego;
            case CARTA -> detalle instanceof DetalleItem.Carta;
        };
        if (!correcto) {
            throw new ValidacionException("Los datos específicos no corresponden al tipo seleccionado.");
        }
    }

    private static Item normalizar(Item item) {
        return new Item(item.id(), item.titulo().trim(), item.tipo(), item.anio(), limpiar(item.notas()),
                item.fechaAdquisicion(), item.precioPagado(), limpiar(item.portadaRuta()), item.estado(),
                item.calificacion(), limpiar(item.resena()), item.detalle(), normalizarEtiquetas(item.etiquetas()));
    }

    private static Set<String> normalizarEtiquetas(Set<String> etiquetas) {
        Set<String> resultado = new LinkedHashSet<>();
        if (etiquetas != null) {
            for (String etiqueta : etiquetas) {
                String limpia = limpiar(etiqueta);
                if (limpia != null) {
                    if (limpia.contains("|")) {
                        throw new ValidacionException("Las etiquetas no pueden contener el carácter |.");
                    }
                    resultado.add(limpia.toLowerCase(Locale.ROOT));
                }
            }
        }
        return resultado;
    }

    private static String limpiar(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        return texto.trim();
    }

    private static void validarRango(Integer valor, int minimo, int maximo, String nombre) {
        if (valor != null && (valor < minimo || valor > maximo)) {
            throw new ValidacionException(nombre + " debe estar entre " + minimo + " y " + maximo + ".");
        }
    }

    private static void compararLimites(Integer minimo, Integer maximo, String nombre) {
        if (minimo != null && maximo != null && minimo > maximo) {
            throw new ValidacionException("El rango de " + nombre + " está invertido.");
        }
    }
}
