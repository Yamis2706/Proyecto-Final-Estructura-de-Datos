package co.edu.uniquindio.co.proyecto.app;

import co.edu.uniquindio.co.proyecto.model.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class RegisterController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField nameField;
    @FXML private Label messageLabel;

    @FXML
    public void onRegister() {
        var ctx = AppContext.get();

        String username = usernameField.getText();
        String pass = passwordField.getText();
        String name = nameField.getText();

        if (username.isBlank() || pass.isBlank() || name.isBlank()) {
            messageLabel.setText("Todos los campos son obligatorios");
            return;
        }

        if (ctx.userRepository.get(username).isPresent()) {
            messageLabel.setText("Usuario ya existe");
            return;
        }

        ctx.registerUser(new Usuario(username, pass, name, Usuario.Role.USER));

        Stage stage = (Stage) usernameField.getScene().getWindow();
        ViewLoader.load(stage, "login-view.fxml", "Iniciar Sesión");
    }

    @FXML
    public void onGoBack() {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        ViewLoader.load(stage, "login-view.fxml", "Iniciar Sesión");
    }
}
