package co.edu.uniquindio.co.proyecto.app;

import co.edu.uniquindio.co.proyecto.model.Usuario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.Map;

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

        String id = generarIdUnicoDeTresDigitos();

        Usuario user = new Usuario(
                id,
                username,
                password,
                username,  // nombre del usuario
                Usuario.Role.USER
        );

        boolean registrado = AppContext.get().registerUser(user);

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

    private String generarIdUnicoDeTresDigitos() {

        // Crear lista de IDs existentes como effectively final
        var idsExistentes = AppContext.get().userRepository.list()
                .stream()
                .map(Usuario::getId)
                .toList();

        String id;

        do {
            int numero = (int) (Math.random() * 900) + 100;  // 100–999
            id = String.valueOf(numero);
        } while (idsExistentes.contains(id));

        return id;
    }
}
