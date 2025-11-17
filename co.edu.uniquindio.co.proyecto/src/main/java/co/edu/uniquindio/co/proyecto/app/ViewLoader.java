package co.edu.uniquindio.co.proyecto.app;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ViewLoader {

    private static final String BASE_PATH = "/co.edu.uniquindio.co.proyecto/app/";

    /**
     * Carga un FXML y devuelve el Parent
     */
    public static Parent load(String fxml) throws Exception {
        return FXMLLoader.load(ViewLoader.class.getResource(BASE_PATH + fxml));
    }

    /**
     * Carga una vista dentro de un Stage existente
     */
    public static void setView(Stage stage, String fxml, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(ViewLoader.class.getResource(BASE_PATH + fxml));
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
            throw new RuntimeException("No se pudo cargar la vista: " + BASE_PATH + fxml, e);
        }
    }
}

