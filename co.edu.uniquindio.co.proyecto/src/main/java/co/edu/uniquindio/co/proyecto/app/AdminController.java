package co.edu.uniquindio.co.proyecto.app;

import co.edu.uniquindio.co.proyecto.model.Cancion;
import co.edu.uniquindio.co.proyecto.model.Usuario;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AdminController {

    // ---------------- TABLA CANCIONES ----------------
    @FXML private TableView<Cancion> songTable;
    @FXML private TableColumn<Cancion, String> colId;
    @FXML private TableColumn<Cancion, String> colTitulo;
    @FXML private TableColumn<Cancion, String> colArtista;
    @FXML private TableColumn<Cancion, String> colGenero;
    @FXML private TableColumn<Cancion, Integer> colAnio;
    @FXML private TableColumn<Cancion, Integer> colDuracion;

    // ---------------- TABLA USUARIOS ----------------
    @FXML private TableView<Usuario> songTable1;
    @FXML private TableColumn<Usuario, String> colId1;
    @FXML private TableColumn<Usuario, String> colTitulo1;
    @FXML private TableColumn<Usuario, String> colArtista1;
    @FXML private TableColumn<Usuario, String> colGenero1;
    @FXML private TableColumn<Usuario, String> colAnio1;
    @FXML private TableColumn<Usuario, String> colDuracion1;

    private final AppContext ctx = AppContext.get();

    @FXML
    public void initialize() {

        // ---------------------- CANCIONES -------------------------
        colId.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().getId()));
        colTitulo.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().getTitulo()));
        colArtista.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().getArtista()));
        colGenero.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().getGenero()));
        colAnio.setCellValueFactory(f -> new SimpleObjectProperty<>(f.getValue().getAnio()));
        colDuracion.setCellValueFactory(f -> new SimpleObjectProperty<>(f.getValue().getDuracionSegundos()));

        cargarTablaCanciones();

        // ---------------------- USUARIOS -------------------------
        colId1.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().getUsername()));   // ID → username
        colTitulo1.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().getNombre())); // Nombre
        colArtista1.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().getRole().name())); // Rol
        colGenero1.setCellValueFactory(f -> new SimpleStringProperty("")); // vacío
        colAnio1.setCellValueFactory(f -> new SimpleStringProperty("")); // vacío
        colDuracion1.setCellValueFactory(f -> new SimpleStringProperty("")); // vacío

        cargarTablaUsuarios();
    }

    // =====================================================
    // ================ CARGAR TABLAS ======================
    // =====================================================

    private void cargarTablaCanciones() {
        songTable.getItems().setAll(ctx.songCatalog.list());
    }

    private void cargarTablaUsuarios() {
        songTable1.getItems().setAll(ctx.userRepository.getAll());
    }

    // =====================================================
    // ================ BOTONES CANCIONES ===================
    // =====================================================

    @FXML
    private void onRegisterSong() {
        abrirFormularioCancion(null);
    }

    @FXML
    private void onEditSong() {
        Cancion seleccionada = songTable.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            new Alert(Alert.AlertType.WARNING, "Selecciona una canción primero").show();
            return;
        }
        abrirFormularioCancion(seleccionada);
    }

    @FXML
    private void onDeleteSong() {
        Cancion seleccionada = songTable.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            new Alert(Alert.AlertType.WARNING, "Selecciona una canción").show();
            return;
        }

        ctx.deleteSong(seleccionada.getId());
        cargarTablaCanciones();
    }

    // =====================================================
    // ================ BOTONES USUARIOS ===================
    // =====================================================

    @FXML
    private void onListUsers() {
        cargarTablaUsuarios();
    }

    @FXML
    private void onDeleteUser() {
        Usuario u = songTable1.getSelectionModel().getSelectedItem();

        if (u == null) {
            new Alert(Alert.AlertType.WARNING, "Selecciona un usuario").show();
            return;
        }

        ctx.userRepository.delete(u.getUsername());
        ctx.userRepository.persist(); // 💾 guardar cambios

        cargarTablaUsuarios();
    }

    // =====================================================
    // ================ FORMULARIO CANCIONES ===============
    // =====================================================

    private void abrirFormularioCancion(Cancion c) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("song-form.fxml"));
            Stage modal = new Stage();
            modal.setScene(new Scene(loader.load()));
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.setTitle(c == null ? "Registrar Canción" : "Editar Canción");

            SongFormController controller = loader.getController();
            if (c != null) controller.cargarCancion(c);

            modal.setOnHidden(e -> cargarTablaCanciones());
            modal.show();

        } catch (Exception ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "No se pudo abrir el formulario").show();
        }
    }

    // =====================================================
    // ================ LOGOUT =============================
    // =====================================================

    @FXML
    private void onLogout() {
        Stage stage = (Stage) songTable.getScene().getWindow();
        ViewLoader.load(stage, "login-view.fxml", "Login");
    }
}
