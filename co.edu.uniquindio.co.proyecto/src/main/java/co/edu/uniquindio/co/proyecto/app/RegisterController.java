package co.edu.uniquindio.co.proyecto.app;

import co.edu.uniquindio.co.proyecto.model.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.UUID;

public class RegisterController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField nameField;
    @FXML private Label messageLabel;

    @FXML
    public void onRegister() {
        String username = usernameField.getText();
        String pass = passwordField.getText();
        String nombre = nameField.getText();

        if (username.isBlank() || pass.isBlank() || nombre.isBlank()) {
            messageLabel.setText("Complete todos los campos");
            return;
        }

        // ✔ Generar ID obligatorio para cumplir con el constructor
        String id = java.util.UUID.randomUUID().toString();

        Usuario nuevo = new Usuario(
                UUID.randomUUID().toString(),
                username,
                pass,
                nameField.getText(),
                Usuario.Role.USER
        );

        boolean ok = AppContext.get().registerUser(nuevo);

        if (!ok) {
            messageLabel.setText("El usuario ya existe");
            return;
        }

        messageLabel.setText("¡Registro exitoso!");
    }

    @FXML
    public void onGoBack() {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        ViewLoader.load(stage, "login-view.fxml", "Iniciar Sesión");
    }
}
