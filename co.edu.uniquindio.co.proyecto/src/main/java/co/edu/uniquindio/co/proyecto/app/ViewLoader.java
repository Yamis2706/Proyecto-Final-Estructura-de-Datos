package co.edu.uniquindio.co.proyecto.app;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ViewLoader {

    public static void load(Stage stage, String fxml, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    ViewLoader.class.getResource(fxml)
            );

            if (loader.getLocation() == null) {
                throw new IllegalStateException("FXML no encontrado: " + fxml);
            }

            Parent root = loader.load();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("No se pudo abrir la ventana: " + fxml + "\n" + e.getMessage());
        }
    }

    public static void openModal(String s, String registrarCanción) {
    }
}
