package com.coleccion.ui;

import com.coleccion.model.DetalleItem;
import com.coleccion.model.EstadoItem;
import com.coleccion.model.Item;
import com.coleccion.model.TipoItem;
import com.coleccion.service.ItemService;
import com.coleccion.service.ValidacionException;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public final class ItemFormController {
    @FXML private Label tituloVentana, errorLabel;
    @FXML private TextField titulo, anio, precio, calificacion, etiquetas, portada;
    @FXML private ComboBox<TipoItem> tipo;
    @FXML private ComboBox<EstadoItem> estado;
    @FXML private DatePicker fechaAdquisicion;
    @FXML private TextArea notas, resena;
    @FXML private VBox detalleContenedor;

    private final Map<String, TextField> camposDetalle = new LinkedHashMap<>();
    private ItemService items;
    private Item original;
    private Stage dialogo;
    private boolean guardado;

    @FXML
    private void initialize() {
        tipo.getItems().setAll(TipoItem.values());
        estado.getItems().setAll(EstadoItem.values());
        tipo.getSelectionModel().selectedItemProperty().addListener((observable, anterior, actual) -> configurarDetalle(actual));
        tipo.getSelectionModel().select(TipoItem.LIBRO);
        estado.getSelectionModel().select(EstadoItem.PENDIENTE);
    }

    public void inicializar(ItemService items, Item original, Stage dialogo) {
        this.items = items;
        this.original = original;
        this.dialogo = dialogo;
        if (original == null) {
            return;
        }
        tituloVentana.setText("Editar ítem");
        titulo.setText(original.titulo());
        tipo.setValue(original.tipo());
        estado.setValue(original.estado());
        anio.setText(texto(original.anio()));
        fechaAdquisicion.setValue(original.fechaAdquisicion());
        precio.setText(original.precioPagado() == null ? "" : original.precioPagado().toPlainString());
        calificacion.setText(texto(original.calificacion()));
        etiquetas.setText(String.join(", ", original.etiquetas()));
        portada.setText(texto(original.portadaRuta()));
        notas.setText(texto(original.notas()));
        resena.setText(texto(original.resena()));
        cargarDetalle(original.detalle());
    }

    public boolean isGuardado() {
        return guardado;
    }

    @FXML
    private void guardar() {
        try {
            Item item = new Item(original == null ? null : original.id(), titulo.getText(), tipo.getValue(),
                    entero(anio.getText(), "año"), notas.getText(), fechaAdquisicion.getValue(), precio(),
                    portada.getText(), estado.getValue(), entero(calificacion.getText(), "calificación"),
                    resena.getText(), detalle(), etiquetas());
            items.guardar(item);
            guardado = true;
            dialogo.close();
        } catch (RuntimeException exception) {
            mostrarError(exception.getMessage());
        }
    }

    @FXML
    private void cancelar() {
        dialogo.close();
    }

    @FXML
    private void elegirPortada() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Elegir portada");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        File archivo = chooser.showOpenDialog(dialogo);
        if (archivo != null) {
            portada.setText(archivo.getAbsolutePath());
        }
    }

    private void configurarDetalle(TipoItem tipo) {
        detalleContenedor.getChildren().clear();
        camposDetalle.clear();
        if (tipo == null) {
            return;
        }
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(8);
        switch (tipo) {
            case LIBRO -> {
                agregarCampo(grid, 0, "Autor", "autor");
                agregarCampo(grid, 1, "ISBN", "isbn");
                agregarCampo(grid, 2, "Editorial", "editorial");
            }
            case PELICULA -> {
                agregarCampo(grid, 0, "Director", "director");
                agregarCampo(grid, 1, "Duración (min)", "duracionMinutos");
            }
            case VIDEOJUEGO -> {
                agregarCampo(grid, 0, "Plataforma", "plataforma");
                agregarCampo(grid, 1, "Desarrollador", "desarrollador");
            }
            case CARTA -> {
                agregarCampo(grid, 0, "Juego", "juego");
                agregarCampo(grid, 1, "Rareza", "rareza");
                agregarCampo(grid, 2, "Condición", "condicion");
            }
        }
        detalleContenedor.getChildren().add(grid);
    }

    private void agregarCampo(GridPane grid, int fila, String etiqueta, String clave) {
        Label label = new Label(etiqueta);
        TextField campo = new TextField();
        GridPane.setHgrow(campo, Priority.ALWAYS);
        grid.add(label, 0, fila);
        grid.add(campo, 1, fila);
        camposDetalle.put(clave, campo);
    }

    private void cargarDetalle(DetalleItem detalle) {
        switch (detalle) {
            case DetalleItem.Libro libro -> {
                poner("autor", libro.autor());
                poner("isbn", libro.isbn());
                poner("editorial", libro.editorial());
            }
            case DetalleItem.Pelicula pelicula -> {
                poner("director", pelicula.director());
                poner("duracionMinutos", texto(pelicula.duracionMinutos()));
            }
            case DetalleItem.Videojuego videojuego -> {
                poner("plataforma", videojuego.plataforma());
                poner("desarrollador", videojuego.desarrollador());
            }
            case DetalleItem.Carta carta -> {
                poner("juego", carta.juego());
                poner("rareza", carta.rareza());
                poner("condicion", carta.condicion());
            }
        }
    }

    private DetalleItem detalle() {
        return switch (tipo.getValue()) {
            case LIBRO -> new DetalleItem.Libro(valor("autor"), valor("isbn"), valor("editorial"));
            case PELICULA -> new DetalleItem.Pelicula(valor("director"), entero(valor("duracionMinutos"), "duración"));
            case VIDEOJUEGO -> new DetalleItem.Videojuego(valor("plataforma"), valor("desarrollador"));
            case CARTA -> new DetalleItem.Carta(valor("juego"), valor("rareza"), valor("condicion"));
        };
    }

    private BigDecimal precio() {
        String valor = limpio(precio.getText());
        if (valor == null) {
            return null;
        }
        try {
            return new BigDecimal(valor);
        } catch (NumberFormatException exception) {
            throw new ValidacionException("El precio debe ser un número con como máximo dos decimales.");
        }
    }

    private Set<String> etiquetas() {
        Set<String> resultado = new LinkedHashSet<>();
        if (etiquetas.getText() != null && !etiquetas.getText().isBlank()) {
            for (String etiqueta : etiquetas.getText().split(",")) {
                if (!etiqueta.isBlank()) {
                    resultado.add(etiqueta.trim());
                }
            }
        }
        return resultado;
    }

    private static Integer entero(String texto, String nombre) {
        String valor = limpio(texto);
        if (valor == null) {
            return null;
        }
        try {
            return Integer.valueOf(valor);
        } catch (NumberFormatException exception) {
            throw new ValidacionException("El campo " + nombre + " debe ser un número entero.");
        }
    }

    private void poner(String clave, String valor) {
        TextField campo = camposDetalle.get(clave);
        if (campo != null) {
            campo.setText(texto(valor));
        }
    }

    private String valor(String clave) {
        TextField campo = camposDetalle.get(clave);
        return campo == null ? null : valor(campo.getText());
    }

    private static String limpio(String texto) {
        return texto == null || texto.isBlank() ? null : texto.trim();
    }

    private static String texto(Object valor) {
        return valor == null ? "" : valor.toString();
    }

    private void mostrarError(String mensaje) {
        errorLabel.setText(mensaje == null ? "Revisa los datos introducidos." : mensaje);
        errorLabel.setManaged(true);
        errorLabel.setVisible(true);
    }
}
