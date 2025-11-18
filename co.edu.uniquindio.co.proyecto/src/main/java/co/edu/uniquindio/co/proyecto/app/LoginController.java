package co.edu.uniquindio.co.proyecto.app;

import co.edu.uniquindio.co.proyecto.model.Cancion;
import co.edu.uniquindio.co.proyecto.model.Usuario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.List;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ChoiceBox<String> roleChoice;
    @FXML private Button loginButton;
    @FXML private Button registerButton;
    @FXML private Label messageLabel;

    @FXML
    public void initialize() {
        //roleChoice.getItems().setAll("Usuario", "Administrador");
        //roleChoice.getSelectionModel().selectFirst();
        seedDemoData();
    }

    private void seedDemoData() {
        var ctx = AppContext.get();
        List<Cancion> demo = List.of(
                new Cancion("1","Shine On","Pink Floyd","Rock",1975,810),
                new Cancion("2","Billie Jean","Michael Jackson","Pop",1982,294),
                new Cancion("3","Yellow","Coldplay","Alternative",2000,269),
                new Cancion("4","Clocks","Coldplay","Alternative",2002,307)
        );
        for (Cancion c : demo) ctx.songCatalog.add(c);
        ctx.indexTitles(demo);
        // Conexiones de similitud mínimas
        ctx.grafoDeSimilitud.conectar(demo.get(2), demo.get(3), 1.0);
        ctx.grafoDeSimilitud.conectar(demo.get(0), demo.get(2), 3.0);
        // Usuario demo
        Usuario u = new Usuario("demo","demo","Demo", Usuario.Role.USER);
        u.agregarFavorito(demo.get(2));
        ctx.userRepository.add(u);
        // Admin demo
        ctx.userRepository.add(new Usuario("admin","admin","Administrador", Usuario.Role.ADMIN));
    }

    @FXML
    public void onLogin(ActionEvent e) {
        String username = usernameField.getText();
        String pass = passwordField.getText();
        String role = roleChoice.getValue();

        if (username == null || username.isBlank()) {
            messageLabel.setText("Ingrese username");
            return;
        }

        var ctx = AppContext.get();
        var opt = ctx.authService.authenticate(username, pass);
        if (opt.isEmpty()) {
            // Si no existe o contraseña no coincide, ofrecer registro rápido para rol USER
            if (username.equals("@yamis") && pass.equals("2706")){
                Alert ask = new Alert(Alert.AlertType.CONFIRMATION, "Usuario no registrado. ¿Desea registrarlo ahora?", ButtonType.YES, ButtonType.NO);
                ask.setHeaderText("Registrar nuevo usuario");
                var res = ask.showAndWait();
                if (res.isPresent() && res.get() == ButtonType.YES) {
                    ctx.authService.register(username, pass == null ? "" : pass, username, Usuario.Role.USER);
                    try { ctx.authService.save(); } catch (Exception ignored) {}
                    // continuar a vista de usuario
                    try {
                        Stage stage = (Stage) loginButton.getScene().getWindow();
                        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("user-view.fxml"));
                        stage.setScene(new Scene(loader.load(), 900, 640));
                        stage.setTitle("SyncUp - Usuario");
                        stage.show();
                    } catch (Exception ex) {
                        new Alert(Alert.AlertType.ERROR, "No se pudo abrir la vista: " + ex.getMessage()).showAndWait();
                    }
                    return;
                }
            }
            messageLabel.setText("Credenciales inválidas o usuario no registrado");
            new Alert(Alert.AlertType.ERROR, "Usuario o contraseña incorrectos").showAndWait();
            return;
        }
        try {
            Usuario user = opt.get();
            Stage stage = (Stage) loginButton.getScene().getWindow();
            if (user.getRole() == Usuario.Role.ADMIN) {
                FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("admin-view.fxml"));
                stage.setScene(new Scene(loader.load(), 800, 600));
                stage.setTitle("SyncUp - Admin");
            } else {
                FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("user-view.fxml"));
                stage.setScene(new Scene(loader.load(), 900, 640));
                stage.setTitle("SyncUp - Usuario");
            }
            stage.show();
        } catch (Exception ex) {
            String msg = ex.getClass().getSimpleName()+": "+(ex.getMessage()==null?"":ex.getMessage());
            if (ex.getCause()!=null) msg += "\nCausa: "+ex.getCause().getClass().getSimpleName()+": "+(ex.getCause().getMessage()==null?"":ex.getCause().getMessage());
            new Alert(Alert.AlertType.ERROR, "No se pudo abrir la vista:\n" + msg).showAndWait();
        }
    }

    @FXML
    public void onRegister(ActionEvent e) {
        String username = usernameField.getText();
        String pass = passwordField.getText();
        String role = roleChoice.getValue();
        if (username == null || username.isBlank()) { messageLabel.setText("Ingrese username"); return; }
        if (pass == null || pass.isBlank()) { messageLabel.setText("Ingrese contraseña"); return; }
        var ctx = AppContext.get();
        if (ctx.userRepository.get(username).isPresent()) {
            messageLabel.setText("Usuario ya existe");
            new Alert(Alert.AlertType.WARNING, "El usuario ya existe").showAndWait();
            return;
        }
        Usuario.Role targetRole = "Administrador".equals(role) ? Usuario.Role.ADMIN : Usuario.Role.USER;
        ctx.authService.register(username, pass, username, targetRole);
        try { ctx.authService.save(); } catch (Exception ignored) {}
        messageLabel.setText("Registro exitoso. Ahora puede ingresar.");
        new Alert(Alert.AlertType.INFORMATION, "Registro exitoso").showAndWait();
    }
}