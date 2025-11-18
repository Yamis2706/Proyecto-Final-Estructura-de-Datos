package co.edu.uniquindio.co.proyecto.app;

import co.edu.uniquindio.co.proyecto.model.Usuario;
import co.edu.uniquindio.co.proyecto.repository.DataManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class RegisterController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label messageLabel;

    @FXML
    private void onRegister() {

        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirm = confirmPasswordField.getText();

        if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            messageLabel.setText("Todos los campos son obligatorios.");
            return;
        }

        if (!password.equals(confirm)) {
            messageLabel.setText("Las contraseñas no coinciden.");
            return;
        }

        Usuario user = new Usuario(
                java.util.UUID.randomUUID().toString(), // id único
                username,                               // username
                password,                               // password
                username,                               // nombre (lo tomamos igual)
                Usuario.Role.USER                       // rol
        );

        boolean registrado = DataManager.registerUser(user);

        if (!registrado) {
            messageLabel.setText("El usuario ya existe.");
            return;
        }

        messageLabel.setText("Usuario registrado correctamente ✔");
    }

    @FXML
    private void onGoBack() {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        ViewLoader.load(stage, "login-view.fxml", "Iniciar Sesión");
    }
}
