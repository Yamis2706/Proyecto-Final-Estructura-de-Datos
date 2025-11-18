package co.edu.uniquindio.co.proyecto.app;

import co.edu.uniquindio.co.proyecto.model.Cancion;
import co.edu.uniquindio.co.proyecto.model.Usuario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.*;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ChoiceBox<String> roleChoice;
    @FXML private Button loginButton;
    @FXML private Button registerButton;
    @FXML private Label messageLabel;

    @FXML
    public void initialize() {

        roleChoice.getItems().setAll("Usuario", "Administrador");
        roleChoice.getSelectionModel().selectFirst();

        var ctx = AppContext.get();

        // ------- Cargar canciones iniciales si el catálogo está vacío -------
        if (ctx.songCatalog.list().isEmpty()) {
            List<Cancion> demo = List.of(
                    new Cancion("1","Shine On","Pink Floyd","Rock",1975,810),
                    new Cancion("2","Billie Jean","Michael Jackson","Pop",1982,294),
                    new Cancion("3","Yellow","Coldplay","Alternative",2000,269),
                    new Cancion("4","Clocks","Coldplay","Alternative",2002,307),
                    new Cancion("5","Imagine","John Lennon","Rock",1971,183),
                    new Cancion("6","Hotel California","Eagles","Rock",1976,390),
                    new Cancion("7","Stairway to Heaven","Led Zeppelin","Rock",1971,480),
                    new Cancion("8","Smells Like Teen Spirit","Nirvana","Grunge",1991,301),
                    new Cancion("9","Sweet Child O' Mine","Guns N' Roses","Rock",1987,356),
                    new Cancion("10","Wonderwall","Oasis","Britpop",1995,259),
                    new Cancion("11","Rolling in the Deep","Adele","Pop",2010,228),
                    new Cancion("12","Hey Jude","The Beatles","Rock",1968,431),
                    new Cancion("13","Bohemian Rhapsody","Queen","Rock",1975,354),
                    new Cancion("14","Lose Yourself","Eminem","Rap",2002,326),
                    new Cancion("15","Hallelujah","Leonard Cohen","Folk",1984,282),
                    new Cancion("16","Creep","Radiohead","Alternative",1992,238),
                    new Cancion("17","Viva La Vida","Coldplay","Alternative",2008,242),
                    new Cancion("18","Back in Black","AC/DC","Rock",1980,255),
                    new Cancion("19","All of Me","John Legend","Pop",2013,269),
                    new Cancion("20","Bad Guy","Billie Eilish","Pop",2019,194)
            );
            for (Cancion c : demo) ctx.addSong(c);
        }

        // ------- Semilla de usuarios solo si está vacío -------
        if (ctx.userRepository.size() == 0) {

            Usuario u = new Usuario(
                    UUID.randomUUID().toString(),
                    "demo",
                    "demo",
                    "Usuario Demo",
                    Usuario.Role.USER
            );
            ctx.registerUser(u);

            Usuario admin = new Usuario(
                    UUID.randomUUID().toString(),
                    "admin",
                    "admin",
                    "Administrador",
                    Usuario.Role.ADMIN
            );
            ctx.registerUser(admin);
        }
    }

    @FXML
    public void onLogin(ActionEvent e) {

        String username = usernameField.getText().trim();
        String pass = passwordField.getText().trim();

        if (username.isBlank() || pass.isBlank()) {
            messageLabel.setText("Ingrese usuario y contraseña");
            return;
        }

        var ctx = AppContext.get();
        var result = ctx.authService.authenticate(username, pass);

        if (result.isEmpty()) {
            messageLabel.setText("Credenciales inválidas");
            return;
        }

        Usuario user = result.get();

        try {
            Stage stage = (Stage) loginButton.getScene().getWindow();

            if (user.getRole() == Usuario.Role.ADMIN) {
                ViewLoader.load(stage, "admin-view.fxml", "SyncUp - Admin");
            } else {
                ViewLoader.load(stage, "user-view.fxml", "SyncUp - Usuario");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR,
                    "No se pudo abrir la ventana: " + ex.getMessage()
            ).showAndWait();
        }
    }

    @FXML
    public void onRegister(ActionEvent e) {
        Stage stage = (Stage) registerButton.getScene().getWindow();
        ViewLoader.load(stage, "register-view.fxml", "Crear cuenta");
    }

    /** Lista usuarios ordenados por ID ascendente */
    public List<Usuario> listarUsuariosPorId() {
        List<Usuario> lista = new ArrayList<>(AppContext.get().userRepository.list());
        lista.sort(Comparator.comparing(Usuario::getId));
        return lista;
    }
}
