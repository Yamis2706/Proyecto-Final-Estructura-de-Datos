package co.edu.uniquindio.co.proyecto.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelloApplication extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        FXMLLoader loader = new FXMLLoader(
                HelloApplication.class.getResource("login-view.fxml")
        );
        Scene scene = new Scene(loader.load(), 900, 600);

        stage.setTitle("SyncUp - Inicio de Sesión");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Cambia la raíz de la escena sin crear una ventana nueva.
     */
    public static void switchTo(String viewName) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    HelloApplication.class.getResource(viewName)
            );
            Scene newScene = new Scene(loader.load(), 900, 600);

            primaryStage.setScene(newScene);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error cargando vista: " + viewName + "\n" + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch();
    }
}




