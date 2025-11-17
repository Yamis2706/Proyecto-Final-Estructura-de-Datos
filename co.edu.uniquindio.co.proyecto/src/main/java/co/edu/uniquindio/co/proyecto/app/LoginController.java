package co.edu.uniquindio.co.proyecto.app;

import co.edu.uniquindio.co.proyecto.model.Usuario;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private ComboBox<String> roleCombo;
    @FXML private Button btnLogin;
    @FXML private Hyperlink btnGoRegister;
    @FXML private Label lblMensaje;

    private final AppContext ctx = AppContext.get();

    @FXML
    public void initialize() {
        // asegurar ejecución en UI thread
        Platform.runLater(() -> {
            roleCombo.getItems().clear();
            roleCombo.getItems().addAll("Usuario", "Administrador");
            roleCombo.getSelectionModel().selectFirst();
        });
    }

    @FXML
    public void onLogin(ActionEvent event) {
        lblMensaje.setText("");
        String user = txtUsuario.getText() == null ? "" : txtUsuario.getText().trim();
        String pass = txtPassword.getText() == null ? "" : txtPassword.getText().trim();
        String rol  = roleCombo.getValue();

        if (user.isEmpty() || pass.isEmpty() || rol == null) {
            lblMensaje.setText("Completa todos los campos");
            return;
        }

        // Autenticación vía AppContext.authService (asumo que existe)
        var opt = ctx.authService.authenticate(user, pass);
        if (opt.isEmpty()) {
            lblMensaje.setText("Credenciales inválidas");
            return;
        }

        Usuario usuario = opt.get();
        try {
            Stage stage = (Stage) btnLogin.getScene().getWindow();
            if (usuario.getRole() == Usuario.Role.ADMIN) {
                ViewLoader.setView(stage, "co/edu/uniquindio/co/proyecto/app/admin-view.fxml", "SyncUp - Admin");
                // si el AdminController necesita setCurrentUser, puedes recuperarlo:
                // FXMLLoader loader = new FXMLLoader(getClass().getResource("/.../admin-view.fxml"));
                // Parent root = loader.load(); AdminController ctrl = loader.getController(); ctrl.setCurrentUser(usuario);
            } else {
                ViewLoader.setView(stage, "co/edu/uniquindio/co/proyecto/app/user-view.fxml", "SyncUp - Usuario");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            lblMensaje.setText("No se pudo abrir la vista: " + ex.getMessage());
        }
    }

    @FXML
    public void onGoToRegister(ActionEvent event) {
        lblMensaje.setText("");
        try {
            Stage stage = (Stage) btnGoRegister.getScene().getWindow();
            ViewLoader.setView(stage, "co/edu/uniquindio/co/proyecto/app/register-view.fxml", "SyncUp - Registrar");
        } catch (Exception e) {
            e.printStackTrace();
            lblMensaje.setText("No se pudo abrir registro");
        }
    }
}




