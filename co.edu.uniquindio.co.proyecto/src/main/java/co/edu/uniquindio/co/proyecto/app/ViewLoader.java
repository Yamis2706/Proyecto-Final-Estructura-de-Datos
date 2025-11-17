package co.edu.uniquindio.co.proyecto.app;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ViewLoader {

    public static Parent load(String fxml) throws Exception {
        return FXMLLoader.load(ViewLoader.class.getResource(fxml));
    }

    public static void setView(Stage stage, String fxml, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(ViewLoader.class.getResource(fxml));
            Parent root = loader.load();

            if (stage.getScene() == null) {
                stage.setScene(new Scene(root));
            } else {
                stage.getScene().setRoot(root);
            }

            stage.setTitle(title);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("No se pudo cargar la vista: " + fxml, e);
        }
    }
}

