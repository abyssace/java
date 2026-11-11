package com.coleccion;

import com.coleccion.dao.ItemDao;
import com.coleccion.dao.PrestamoDao;
import com.coleccion.service.CsvService;
import com.coleccion.service.ItemService;
import com.coleccion.service.PrestamoService;
import com.coleccion.ui.MainController;
import com.coleccion.util.Database;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.nio.file.Path;

public final class App extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        String configurada = System.getProperty("coleccion.db");
        if (configurada == null || configurada.isBlank()) {
            configurada = System.getenv("COLECCION_DB");
        }
        Path ruta = configurada == null || configurada.isBlank()
                ? Path.of(System.getProperty("user.home"), ".coleccion-personal", "coleccion.db")
                : Path.of(configurada);
        Database database = new Database(ruta);
        ItemService items = new ItemService(new ItemDao(database));
        PrestamoService prestamos = new PrestamoService(new PrestamoDao(database));
        CsvService csv = new CsvService(items);

        FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/coleccion/ui/main.fxml"));
        Parent root = loader.load();
        loader.<MainController>getController().inicializar(items, prestamos, csv);
        Scene scene = new Scene(root, 1240, 800);
        scene.getStylesheets().add(App.class.getResource("/com/coleccion/ui/estilos.css").toExternalForm());
        stage.setTitle("Colección Personal");
        stage.setMinWidth(1080);
        stage.setMinHeight(700);
        stage.setScene(scene);
        stage.show();
    }
}
