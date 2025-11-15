package co.edu.uniquindio.co.proyecto.app;

import co.edu.uniquindio.co.proyecto.model.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

public class ProfileController {

    @FXML private Label nameLabel;
    @FXML private Label usernameLabel;
    @FXML private Label roleLabel;

    private Usuario currentUser;

    public void setUser(Usuario u) {
        this.currentUser = u;
        loadUserData();
    }

    private void loadUserData() {
        if (currentUser == null) return;

        nameLabel.setText(currentUser.getNombre());
        usernameLabel.setText(currentUser.getUsername());
        roleLabel.setText(currentUser.getRole().name());
    }

    @FXML
    private void onBack() {
        // Cierra esta ventana
        Stage stage = (Stage) nameLabel.getScene().getWindow();
        stage.close();
    }
}
