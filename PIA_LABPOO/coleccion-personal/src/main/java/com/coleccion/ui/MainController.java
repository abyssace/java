package com.coleccion.ui;

import com.coleccion.model.Estadisticas;
import com.coleccion.model.EstadoItem;
import com.coleccion.model.FiltroItem;
import com.coleccion.model.Item;
import com.coleccion.model.Pagina;
import com.coleccion.model.Prestamo;
import com.coleccion.model.ResultadoImportacion;
import com.coleccion.model.TipoItem;
import com.coleccion.service.CsvService;
import com.coleccion.service.ItemService;
import com.coleccion.service.PrestamoService;
import com.coleccion.service.ValidacionException;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public final class MainController {
    @FXML private Label statsTotales, statsTipos, statsEstados, statsPrestamos;
    @FXML private TextField busqueda, calificacionMin, calificacionMax, anioMin, anioMax, filtroEtiquetas;
    @FXML private ComboBox<TipoItem> filtroTipo;
    @FXML private ComboBox<EstadoItem> filtroEstado;
    @FXML private ComboBox<FiltroItem.Orden> orden;
    @FXML private CheckBox soloPrestados, soloVencidos;
    @FXML private TableView<Item> tabla;
    @FXML private TableColumn<Item, String> columnaTitulo, columnaTipo, columnaEstado, columnaEtiquetas, columnaPrestamo;
    @FXML private TableColumn<Item, Integer> columnaAnio, columnaCalificacion;
    @FXML private Label paginaLabel, detalleLabel, estadoLabel;
    @FXML private Button botonAnterior, botonSiguiente, botonDevolver;

    private ItemService items;
    private PrestamoService prestamos;
    private CsvService csv;
    private FiltroItem filtroActual = FiltroItem.porDefecto();
    private Map<Long, Prestamo> prestamosActivos = Map.of();

    @FXML
    private void initialize() {
        filtroTipo.getItems().setAll(TipoItem.values());
        filtroEstado.getItems().setAll(EstadoItem.values());
        orden.getItems().setAll(FiltroItem.Orden.values());
        orden.getSelectionModel().selectFirst();
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        columnaTitulo.setCellValueFactory(dato -> new ReadOnlyStringWrapper(dato.getValue().titulo()));
        columnaTipo.setCellValueFactory(dato -> new ReadOnlyStringWrapper(dato.getValue().tipo().toString()));
        columnaAnio.setCellValueFactory(dato -> new ReadOnlyObjectWrapper<>(dato.getValue().anio()));
        columnaEstado.setCellValueFactory(dato -> new ReadOnlyStringWrapper(dato.getValue().estado().toString()));
        columnaCalificacion.setCellValueFactory(dato -> new ReadOnlyObjectWrapper<>(dato.getValue().calificacion()));
        columnaEtiquetas.setCellValueFactory(dato -> new ReadOnlyStringWrapper(String.join(", ", dato.getValue().etiquetas())));
        columnaPrestamo.setCellValueFactory(dato -> {
            Prestamo prestamo = prestamosActivos.get(dato.getValue().id());
            return new ReadOnlyStringWrapper(prestamo == null ? "Disponible" : prestamo.estadoEn(LocalDate.now()).toString());
        });
        tabla.getSelectionModel().selectedItemProperty().addListener((observable, anterior, actual) -> actualizarSeleccion());
        tabla.setRowFactory(view -> {
            javafx.scene.control.TableRow<Item> fila = new javafx.scene.control.TableRow<>();
            fila.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !fila.isEmpty()) {
                    editar();
                }
            });
            return fila;
        });
        botonAnterior.setDisable(true);
        botonSiguiente.setDisable(true);
        botonDevolver.setDisable(true);
    }

    public void inicializar(ItemService items, PrestamoService prestamos, CsvService csv) {
        this.items = items;
        this.prestamos = prestamos;
        this.csv = csv;
        refrescar();
    }

    @FXML
    private void buscar() {
        try {
            filtroActual = filtroDesdeFormulario(0);
            refrescar();
        } catch (RuntimeException exception) {
            mostrarError(exception);
        }
    }

    @FXML
    private void limpiarFiltros() {
        busqueda.clear();
        filtroTipo.getSelectionModel().clearSelection();
        filtroEstado.getSelectionModel().clearSelection();
        calificacionMin.clear();
        calificacionMax.clear();
        anioMin.clear();
        anioMax.clear();
        filtroEtiquetas.clear();
        soloPrestados.setSelected(false);
        soloVencidos.setSelected(false);
        orden.getSelectionModel().selectFirst();
        filtroActual = FiltroItem.porDefecto();
        refrescar();
    }

    @FXML
    private void paginaAnterior() {
        filtroActual = filtroActual.conPagina(Math.max(0, filtroActual.pagina() - 1));
        refrescar();
    }

    @FXML
    private void paginaSiguiente() {
        filtroActual = filtroActual.conPagina(filtroActual.pagina() + 1);
        refrescar();
    }

    @FXML
    private void nuevo() {
        abrirFormularioItem(null);
    }

    @FXML
    private void editar() {
        Item seleccionado = seleccionado();
        if (seleccionado != null) {
            abrirFormularioItem(seleccionado);
        }
    }

    @FXML
    private void eliminar() {
        Item seleccionado = seleccionado();
        if (seleccionado == null) {
            return;
        }
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION,
                "Se eliminará '" + seleccionado.titulo() + "' junto con sus etiquetas y su historial de préstamos.",
                ButtonType.OK, ButtonType.CANCEL);
        confirmacion.setHeaderText("Eliminar ítem");
        if (confirmacion.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                items.eliminar(seleccionado.id());
                estadoLabel.setText("Ítem eliminado.");
                refrescar();
            } catch (RuntimeException exception) {
                mostrarError(exception);
            }
        }
    }

    @FXML
    private void prestar() {
        Item seleccionado = seleccionado();
        if (seleccionado == null) {
            return;
        }
        try {
            if (prestamos.activoPorItem(seleccionado.id()).isPresent()) {
                estadoLabel.setText("Este ítem ya está prestado.");
                return;
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource("prestamo-form.fxml"));
            Parent root = loader.load();
            Stage dialogo = dialogo("Prestar ítem", root, 480, 330);
            PrestamoFormController controller = loader.getController();
            controller.inicializar(prestamos, seleccionado.id(), dialogo);
            dialogo.showAndWait();
            if (controller.isGuardado()) {
                estadoLabel.setText("Préstamo registrado.");
                refrescar();
            }
        } catch (Exception exception) {
            mostrarError(exception);
        }
    }

    @FXML
    private void devolver() {
        Item seleccionado = seleccionado();
        if (seleccionado == null) {
            return;
        }
        try {
            Optional<Prestamo> activo = prestamos.activoPorItem(seleccionado.id());
            if (activo.isEmpty()) {
                estadoLabel.setText("El ítem no tiene un préstamo activo.");
                return;
            }
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION,
                    "Registrar hoy como fecha de devolución del préstamo a " + activo.get().amigo() + "?",
                    ButtonType.OK, ButtonType.CANCEL);
            confirmacion.setHeaderText("Registrar devolución");
            if (confirmacion.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                prestamos.devolver(activo.get().id(), LocalDate.now());
                estadoLabel.setText("Devolución registrada.");
                refrescar();
            }
        } catch (RuntimeException exception) {
            mostrarError(exception);
        }
    }

    @FXML
    private void verHistorial() {
        Item seleccionado = seleccionado();
        if (seleccionado == null) {
            return;
        }
        try {
            String texto = prestamos.historial(seleccionado.id()).stream()
                    .map(p -> p.fechaPrestamo() + " · " + p.amigo() + " · " + p.estadoEn(LocalDate.now())
                            + " · límite " + p.fechaLimite()
                            + (p.fechaDevolucion() == null ? "" : " · devuelto " + p.fechaDevolucion()))
                    .collect(Collectors.joining("\n"));
            if (texto.isEmpty()) {
                texto = "Este ítem todavía no tiene préstamos.";
            }
            TextArea area = new TextArea(texto);
            area.setEditable(false);
            area.setWrapText(true);
            area.setPrefSize(560, 260);
            Alert alerta = new Alert(Alert.AlertType.INFORMATION);
            alerta.setTitle("Historial de préstamos");
            alerta.setHeaderText(seleccionado.titulo());
            alerta.getDialogPane().setContent(area);
            alerta.showAndWait();
        } catch (RuntimeException exception) {
            mostrarError(exception);
        }
    }

    @FXML
    private void importarCsv() {
        File archivo = elegirArchivo("Importar colección", false);
        if (archivo == null) {
            return;
        }
        try {
            ResultadoImportacion resultado = csv.importar(archivo.toPath());
            refrescar();
            String mensaje = "Se importaron " + resultado.importados() + " ítems.";
            if (resultado.errores().isEmpty()) {
                new Alert(Alert.AlertType.INFORMATION, mensaje).showAndWait();
                return;
            }
            TextArea detalle = new TextArea(String.join("\n", resultado.errores()));
            detalle.setEditable(false);
            detalle.setWrapText(true);
            detalle.setPrefSize(640, 300);
            Alert alerta = new Alert(Alert.AlertType.INFORMATION);
            alerta.setTitle("Importación CSV");
            alerta.setHeaderText(mensaje + " Revisa las filas rechazadas:");
            alerta.getDialogPane().setContent(detalle);
            alerta.showAndWait();
        } catch (Exception exception) {
            mostrarError(exception);
        }
    }

    @FXML
    private void exportarCsv() {
        File archivo = elegirArchivo("Exportar colección", true);
        if (archivo == null) {
            return;
        }
        try {
            csv.exportar(archivo.toPath());
            estadoLabel.setText("Colección exportada correctamente.");
        } catch (Exception exception) {
            mostrarError(exception);
        }
    }

    private void refrescar() {
        try {
            Pagina<Item> pagina = items.buscar(filtroActual);
            if (pagina.totalPaginas() > 0 && filtroActual.pagina() >= pagina.totalPaginas()) {
                filtroActual = filtroActual.conPagina(pagina.totalPaginas() - 1);
                pagina = items.buscar(filtroActual);
            }
            tabla.setItems(FXCollections.observableArrayList(pagina.elementos()));
            prestamosActivos = prestamos.activos().stream().collect(Collectors.toMap(Prestamo::itemId, p -> p));
            tabla.refresh();
            paginaLabel.setText(pagina.total() == 0 ? "Sin resultados"
                    : "Página " + (pagina.pagina() + 1) + " de " + pagina.totalPaginas() + " · " + pagina.total() + " ítems");
            botonAnterior.setDisable(pagina.pagina() <= 0);
            botonSiguiente.setDisable(pagina.totalPaginas() == 0 || pagina.pagina() + 1 >= pagina.totalPaginas());
            actualizarEstadisticas(items.estadisticas());
            actualizarSeleccion();
        } catch (RuntimeException exception) {
            mostrarError(exception);
        }
    }

    private void actualizarEstadisticas(Estadisticas estadisticas) {
        statsTotales.setText(estadisticas.total() + (estadisticas.total() == 1 ? " ítem" : " ítems"));
        statsTipos.setText("Libros " + estadisticas.porTipo().get(TipoItem.LIBRO)
                + " · Películas " + estadisticas.porTipo().get(TipoItem.PELICULA)
                + "\nVideojuegos " + estadisticas.porTipo().get(TipoItem.VIDEOJUEGO)
                + " · Cartas " + estadisticas.porTipo().get(TipoItem.CARTA));
        statsEstados.setText("Completados " + estadisticas.porEstado().get(EstadoItem.COMPLETADO)
                + " · En progreso " + estadisticas.porEstado().get(EstadoItem.EN_PROGRESO)
                + "\nPendientes " + estadisticas.porEstado().get(EstadoItem.PENDIENTE)
                + " · Abandonados " + estadisticas.porEstado().get(EstadoItem.ABANDONADO));
        statsPrestamos.setText(String.format(java.util.Locale.ROOT, "Nota media %.1f / 10\n%d préstamos activos",
                estadisticas.promedioCalificacion(), estadisticas.prestamosActivos()));
    }

    private void actualizarSeleccion() {
        Item seleccionado = tabla.getSelectionModel().getSelectedItem();
        botonDevolver.setDisable(seleccionado == null || !prestamosActivos.containsKey(seleccionado.id()));
        if (seleccionado == null) {
            detalleLabel.setText("Selecciona un ítem para ver su detalle.");
            return;
        }
        String detalle = seleccionado.titulo() + " · " + seleccionado.tipo();
        Prestamo activo = prestamosActivos.get(seleccionado.id());
        if (activo != null) {
            detalle += " · Prestado a " + activo.amigo() + " hasta " + activo.fechaLimite();
        } else if (seleccionado.notas() != null) {
            detalle += " · " + seleccionado.notas();
        }
        detalleLabel.setText(detalle);
    }

    private FiltroItem filtroDesdeFormulario(int pagina) {
        return new FiltroItem(busqueda.getText(), filtroTipo.getValue(), filtroEstado.getValue(),
                numero(calificacionMin, "calificación mínima"), numero(calificacionMax, "calificación máxima"),
                numero(anioMin, "año inicial"), numero(anioMax, "año final"), etiquetas(filtroEtiquetas.getText()),
                soloPrestados.isSelected(), soloVencidos.isSelected(), orden.getValue(), pagina, 20);
    }

    private static Integer numero(TextField campo, String nombre) {
        if (campo.getText() == null || campo.getText().isBlank()) {
            return null;
        }
        try {
            return Integer.valueOf(campo.getText().trim());
        } catch (NumberFormatException exception) {
            throw new ValidacionException("La " + nombre + " debe ser un número entero.");
        }
    }

    private static Set<String> etiquetas(String texto) {
        Set<String> resultado = new LinkedHashSet<>();
        if (texto != null && !texto.isBlank()) {
            for (String etiqueta : texto.split(",")) {
                if (!etiqueta.isBlank()) {
                    resultado.add(etiqueta.trim());
                }
            }
        }
        return resultado;
    }

    private Item seleccionado() {
        Item seleccionado = tabla.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            estadoLabel.setText("Selecciona primero un ítem de la tabla.");
        }
        return seleccionado;
    }

    private void abrirFormularioItem(Item item) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("item-form.fxml"));
            Parent root = loader.load();
            Stage dialogo = dialogo(item == null ? "Nuevo ítem" : "Editar ítem", root, 700, 760);
            ItemFormController controller = loader.getController();
            controller.inicializar(items, item, dialogo);
            dialogo.showAndWait();
            if (controller.isGuardado()) {
                estadoLabel.setText(item == null ? "Ítem creado." : "Cambios guardados.");
                refrescar();
            }
        } catch (Exception exception) {
            mostrarError(exception);
        }
    }

    private Stage dialogo(String titulo, Parent root, int ancho, int alto) {
        Stage dialogo = new Stage();
        dialogo.setTitle(titulo);
        dialogo.initOwner(tabla.getScene().getWindow());
        dialogo.initModality(Modality.WINDOW_MODAL);
        Scene scene = new Scene(root, ancho, alto);
        scene.getStylesheets().add(getClass().getResource("estilos.css").toExternalForm());
        dialogo.setScene(scene);
        return dialogo;
    }

    private File elegirArchivo(String titulo, boolean guardar) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(titulo);
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos CSV", "*.csv"));
        if (guardar) {
            chooser.setInitialFileName("coleccion.csv");
            return chooser.showSaveDialog(tabla.getScene().getWindow());
        }
        return chooser.showOpenDialog(tabla.getScene().getWindow());
    }

    private void mostrarError(Exception exception) {
        String mensaje = exception.getMessage() == null ? exception.getClass().getSimpleName() : exception.getMessage();
        estadoLabel.setText(mensaje);
        Alert alerta = new Alert(Alert.AlertType.ERROR, mensaje);
        alerta.setHeaderText("No se pudo completar la acción");
        alerta.showAndWait();
    }
}
