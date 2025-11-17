package co.edu.uniquindio.co.proyecto.app;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField txtUsuario;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private ComboBox<String> comboRol;

    @FXML
    public void initialize() {
        comboRol.getItems().addAll("Administrador", "Usuario");
    }

    @FXML
    private void onLogin() {
        String user = txtUsuario.getText().trim();
        String pass = txtPassword.getText().trim();
        String rol = comboRol.getValue();

        if (user.isEmpty() || pass.isEmpty() || rol == null) {
            new Alert(Alert.AlertType.WARNING, "Debe llenar todos los campos").show();
            return;
        }

        try {
            Stage stage = (Stage) txtUsuario.getScene().getWindow();

            if (rol.equals("Administrador")) {
                ViewLoader.load(stage, "admin-view.fxml", "Panel Administrador");
            } else {
                ViewLoader.load(stage, "user-view.fxml", "Panel Usuario");
            }

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "No se pudo abrir la ventana").show();
        }
    }

    @FXML
    private void onGoToRegister() {
        try {
            Stage stage = (Stage) txtUsuario.getScene().getWindow();
            ViewLoader.load(stage, "register-view.fxml", "Crear Cuenta");
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "No se pudo abrir el registro").show();
        }
    }
}

