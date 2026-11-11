package com.coleccion.ui;

import com.coleccion.service.PrestamoService;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;

public final class PrestamoFormController {
    @FXML private Label errorLabel;
    @FXML private TextField amigo;
    @FXML private DatePicker fechaPrestamo, fechaLimite;

    private PrestamoService prestamos;
    private long itemId;
    private Stage dialogo;
    private boolean guardado;

    @FXML
    private void initialize() {
        fechaPrestamo.setValue(LocalDate.now());
        fechaLimite.setValue(LocalDate.now().plusDays(14));
    }

    public void inicializar(PrestamoService prestamos, long itemId, Stage dialogo) {
        this.prestamos = prestamos;
        this.itemId = itemId;
        this.dialogo = dialogo;
    }

    public boolean isGuardado() {
        return guardado;
    }

    @FXML
    private void guardar() {
        try {
            prestamos.prestar(itemId, amigo.getText(), fechaPrestamo.getValue(), fechaLimite.getValue());
            guardado = true;
            dialogo.close();
        } catch (RuntimeException exception) {
            errorLabel.setText(exception.getMessage());
            errorLabel.setManaged(true);
            errorLabel.setVisible(true);
        }
    }

    @FXML
    private void cancelar() {
        dialogo.close();
    }
}
