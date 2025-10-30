package co.edu.uniquindio.co.proyecto.app;

import co.edu.uniquindio.co.proyecto.model.Cancion;
import co.edu.uniquindio.co.proyecto.model.Usuario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ChoiceBox<String> roleChoice;
    @FXML private Button loginButton;
    @FXML private Label messageLabel;

    @FXML
    public void initialize() {
        roleChoice.getItems().setAll("Usuario", "Administrador");
        roleChoice.getSelectionModel().selectFirst();
        seedDemoData();
    }

    private void seedDemoData() {
        var ctx = AppContext.get();
        List<Cancion> demo = List.of(
                new Cancion("1","Shine On","Pink Floyd","Rock",1975,810),
                new Cancion("2","Billie Jean","Michael Jackson","Pop",1982,294),
                new Cancion("3","Yellow","Coldplay","Alternative",2000,269),
                new Cancion("4","Clocks","Coldplay","Alternative",2002,307)
        );
        for (Cancion c : demo) ctx.songCatalog.add(c);
        ctx.indexTitles(demo);
        // Conexiones de similitud mínimas
        ctx.grafoDeSimilitud.conectar(demo.get(2), demo.get(3), 1.0);
        ctx.grafoDeSimilitud.conectar(demo.get(0), demo.get(2), 3.0);
        // Usuario demo
        Usuario u = new Usuario("demo","demo","Demo");
        u.agregarFavorito(demo.get(2));
        ctx.userRepository.add(u);
    }

    @FXML
    public void onLogin(ActionEvent e) throws IOException {
        String username = usernameField.getText();
        String pass = passwordField.getText();
        String role = roleChoice.getValue();

        if (username == null || username.isBlank()) {
            messageLabel.setText("Ingrese username");
            return;
        }

        var ctx = AppContext.get();
        Usuario user = ctx.userRepository.get(username).orElseGet(() -> {
            Usuario u = new Usuario(username, pass == null ? "" : pass, username);
            ctx.userRepository.add(u);
            return u;
        });

        Stage stage = (Stage) loginButton.getScene().getWindow();
        if ("Administrador".equals(role)) {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("admin-view.fxml"));
            stage.setScene(new Scene(loader.load(), 800, 600));
            stage.setTitle("SyncUp - Admin");
        } else {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("user-view.fxml"));
            stage.setScene(new Scene(loader.load(), 900, 640));
            stage.setTitle("SyncUp - Usuario");
        }
        stage.show();
    }
}


