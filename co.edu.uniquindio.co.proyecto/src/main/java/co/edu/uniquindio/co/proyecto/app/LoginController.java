package co.edu.uniquindio.co.proyecto.app;

import co.edu.uniquindio.co.proyecto.model.Cancion;
import co.edu.uniquindio.co.proyecto.model.Usuario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.List;
import java.util.UUID;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ChoiceBox<String> roleChoice;
    @FXML private Button loginButton;
    @FXML private Button registerButton;
    @FXML private Label messageLabel;

    @FXML
    public void initialize() {

        roleChoice.getItems().setAll("Usuario", "Administrador");
        roleChoice.getSelectionModel().selectFirst();

        var ctx = AppContext.get();

        // ------- Semilla de canciones solo si está vacío -------
        if (ctx.songCatalog.list().isEmpty()) {
            List<Cancion> demo = List.of(
                    new Cancion("1","Shine On","Pink Floyd","Rock",1975,810),
                    new Cancion("2","Billie Jean","Michael Jackson","Pop",1982,294),
                    new Cancion("3","Yellow","Coldplay","Alternative",2000,269),
                    new Cancion("4","Clocks","Coldplay","Alternative",2002,307)
            );
            for (Cancion c : demo) ctx.addSong(c);
        }

        // ------- Semilla de usuarios solo si está vacío -------
        if (ctx.userRepository.size() == 0) {

            Usuario u = new Usuario(
                    UUID.randomUUID().toString(),
                    "demo",
                    "demo",
                    "Usuario Demo",
                    Usuario.Role.USER
            );
            ctx.registerUser(u);

            Usuario admin = new Usuario(
                    UUID.randomUUID().toString(),
                    "admin",
                    "admin",
                    "Administrador",
                    Usuario.Role.ADMIN
            );
            ctx.registerUser(admin);
        }
    }

    @FXML
    public void onLogin(ActionEvent e) {

        String username = usernameField.getText().trim();
        String pass = passwordField.getText().trim();

        if (username.isBlank() || pass.isBlank()) {
            messageLabel.setText("Ingrese usuario y contraseña");
            return;
        }

        var ctx = AppContext.get();
        var result = ctx.authService.authenticate(username, pass);

        if (result.isEmpty()) {
            messageLabel.setText("Credenciales inválidas");
            return;
        }

        Usuario user = result.get();

        try {
            Stage stage = (Stage) loginButton.getScene().getWindow();

            if (user.getRole() == Usuario.Role.ADMIN) {
                ViewLoader.load(stage, "admin-view.fxml", "SyncUp - Admin");
            } else {
                ViewLoader.load(stage, "user-view.fxml", "SyncUp - Usuario");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR,
                    "No se pudo abrir la ventana: " + ex.getMessage()
            ).showAndWait();
        }
    }

    @FXML
    public void onRegister(ActionEvent e) {
        Stage stage = (Stage) registerButton.getScene().getWindow();
        ViewLoader.load(stage, "register-view.fxml", "Crear cuenta");
    }
}
