package co.edu.uniquindio.co.proyecto.app;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class RegisterController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button registerButton;
    @FXML private Hyperlink backToLogin;
    @FXML private Label messageLabel;

    @FXML
    public void onRegister() {
        messageLabel.setText("");

        String user = usernameField.getText();
        String pass = passwordField.getText();
        String confirm = confirmPasswordField.getText();

        if (user.isBlank() || pass.isBlank() || confirm.isBlank()) {
            messageLabel.setText("Todos los campos son obligatorios");
            return;
        }

        if (!pass.equals(confirm)) {
            messageLabel.setText("Las contraseñas no coinciden");
            return;
        }

        messageLabel.setText("Usuario registrado con éxito");
    }

    @FXML
    public void onBackToLogin() {
        try {
            Stage stage = (Stage) registerButton.getScene().getWindow();
            ViewLoader.setView(stage, "login-view.fxml", "SyncUp - Login");
        } catch (Exception e) {
            messageLabel.setText("No se pudo volver al login");
        }
    }
}


