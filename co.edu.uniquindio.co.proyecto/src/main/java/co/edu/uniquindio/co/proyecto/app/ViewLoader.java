package co.edu.uniquindio.co.proyecto.app;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class ViewLoader {

    /**
     * Intenta cargar el fxml con varias heurísticas:
     * - si fxml empieza con '/' lo usa tal cual
     * - si no, intenta prefijar la ruta del paquete: /co/edu/uniquindio/co/proyecto/app/{fxml}
     */
    public static void load(Stage stage, String fxml, String title) {
        try {
            URL res = resolve(fxml);
            if (res == null) throw new IllegalStateException("FXML no encontrado: " + fxml);

            FXMLLoader loader = new FXMLLoader(res);
            Parent root = loader.load();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("No se pudo abrir la ventana: " + fxml + "\n" + e.getMessage());
        }
    }

    private static URL resolve(String fxml) {
        // si viene con slash absoluto
        if (fxml.startsWith("/")) {
            URL u = ViewLoader.class.getResource(fxml);
            if (u != null) return u;
        }
        // buscar tal cual
        URL u = ViewLoader.class.getResource(fxml);
        if (u != null) return u;
        // intentar con empaquetado por defecto
        String guess = "/co/edu/uniquindio/co/proyecto/app/" + fxml;
        u = ViewLoader.class.getResource(guess);
        if (u != null) return u;
        // intentar con sufijo .fxml si el usuario no lo puso
        if (!fxml.endsWith(".fxml")) return resolve(fxml + ".fxml");
        return null;
    }

    public static void openModal(String fxml, String title) {
        // implementación opcional si la necesitas más adelante
    }
}
