package co.edu.uniquindio.co.proyecto.app;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private ComboBox<String> roleCombo;
    @FXML private Button btnLogin;
    @FXML private Hyperlink btnGoRegister;
    @FXML private Label lblMensaje;

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            roleCombo.getItems().addAll("Usuario", "Administrador");
        });
    }

    @FXML
    public void onLogin(ActionEvent event) {
        lblMensaje.setText("");

        String user = txtUsuario.getText();
        String pass = txtPassword.getText();
        String rol  = roleCombo.getValue();

        if (user.isEmpty() || pass.isEmpty() || rol == null) {
            lblMensaje.setText("Completa todos los campos");
            return;
        }

        Stage stage = (Stage) btnLogin.getScene().getWindow();

        try {
            if (rol.equals("Administrador")) {
                ViewLoader.setView(stage, "admin-view.fxml", "Panel Administrador");
            } else {
                ViewLoader.setView(stage, "user-view.fxml", "Panel Usuario");
            }
        } catch (Exception e) {
            lblMensaje.setText("Error cargando la vista");
            e.printStackTrace();
        }
    }

    @FXML
    public void onGoToRegister(ActionEvent event) {
        try {
            Stage stage = (Stage) btnGoRegister.getScene().getWindow();
            ViewLoader.setView(stage, "register-view.fxml", "Registro");
        } catch (Exception e) {
            lblMensaje.setText("No se pudo abrir el registro");
            e.printStackTrace();
        }
    }
}



