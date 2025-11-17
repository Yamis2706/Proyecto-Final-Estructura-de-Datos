package co.edu.uniquindio.co.proyecto.app;

import co.edu.uniquindio.co.proyecto.model.Cancion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class AdminController {

    @FXML private TableView<Cancion> songTable;
    @FXML private TableColumn<Cancion, String> colId;
    @FXML private TableColumn<Cancion, String> colTitulo;
    @FXML private TableColumn<Cancion, String> colArtista;
    @FXML private TableColumn<Cancion, String> colGenero;
    @FXML private TableColumn<Cancion, Integer> colAnio;
    @FXML private TableColumn<Cancion, Integer> colDuracion; // asegúrate de agregar esta columna en el FXML

    // Campos opcionales en la vista (si los tienes)
    @FXML private TextField txtId;
    @FXML private TextField txtTitulo;
    @FXML private TextField txtArtista;
    @FXML private TextField txtGenero;
    @FXML private TextField txtAnio;
    @FXML private TextField txtDuracionField;

    private final AppContext ctx = AppContext.get();

    @FXML
    public void initialize() {
        if (colId != null)     colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        if (colTitulo != null) colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        if (colArtista != null)colArtista.setCellValueFactory(new PropertyValueFactory<>("artista"));
        if (colGenero != null) colGenero.setCellValueFactory(new PropertyValueFactory<>("genero"));
        if (colAnio != null)   colAnio.setCellValueFactory(new PropertyValueFactory<>("anio"));
        if (colDuracion != null) colDuracion.setCellValueFactory(new PropertyValueFactory<>("duracionSegundos"));

        refrescarTabla();

        if (songTable != null) {
            songTable.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
                if (sel != null) cargarSeleccion(sel);
            });
        }
    }

    // ----------------- Acciones -----------------

    @FXML
    private void onRegisterSong(ActionEvent evt) {
        try {
            openSongFormModal(null); // null => crear nueva
            refrescarTabla();
        } catch (IOException e) {
            e.printStackTrace();
            showError("No se pudo abrir el formulario de canción: " + e.getMessage());
        }
    }

    @FXML
    private void onEditSong(ActionEvent evt) {
        Cancion selected = songTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Seleccione una canción para editar");
            return;
        }
        try {
            openSongFormModal(selected); // pasar para edición
            refrescarTabla();
        } catch (IOException e) {
            e.printStackTrace();
            showError("No se pudo abrir el formulario de edición: " + e.getMessage());
        }
    }

    @FXML
    private void onDeleteSong(ActionEvent evt) {
        Cancion selected = songTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Seleccione una canción para eliminar");
            return;
        }
        boolean removed = ctx.songCatalog.remove(selected.getId());
        if (removed) {
            // persistir si tienes DataManager desde AppContext (ajusta si tu AppContext ofrece método)
            // AppContext.get().saveStateIfNeeded();
            refrescarTabla();
            new Alert(Alert.AlertType.INFORMATION, "Canción eliminada").showAndWait();
        } else {
            showError("No se pudo eliminar la canción");
        }
    }

    @FXML
    private void onLogout(ActionEvent evt) {
        try {
            Stage stage = (Stage) songTable.getScene().getWindow();
            ViewLoader.load(stage, "login-view.fxml", "SyncUp - Login");
        } catch (Exception e) {
            e.printStackTrace();
            showError("No se pudo regresar al login");
        }
    }

    // ----------------- Modal form opener -----------------

    /**
     * Abre el formulario song-form.fxml en modal. Si 'edit' es no nulo, el formulario cargará la canción.
     */
    private void openSongFormModal(Cancion edit) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("song-form.fxml"));
        Parent root = loader.load();

        // obtener el controlador y pasar la canción si es edición
        Object controller = loader.getController();
        if (edit != null) {
            if (controller instanceof co.edu.uniquindio.co.proyecto.app.SongFormController) {
                ((co.edu.uniquindio.co.proyecto.app.SongFormController) controller).cargarCancion(edit);
            } else {
                // por seguridad: si el controlador no es el esperado, lanzamos
                throw new IllegalStateException("Controller de song-form no es SongFormController");
            }
        }

        Stage modal = new Stage();
        modal.initOwner(songTable != null ? songTable.getScene().getWindow() : null);
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle(edit == null ? "Registrar Canción" : "Editar Canción");
        modal.setScene(new Scene(root));
        modal.showAndWait();
    }

    // ----------------- Helpers -----------------

    private void refrescarTabla() {
        if (songTable == null) return;
        ObservableList<Cancion> items = FXCollections.observableArrayList(ctx.songCatalog.list());
        songTable.setItems(items);
    }

    private void cargarSeleccion(Cancion c) {
        if (c == null) return;
        if (txtId != null) txtId.setText(c.getId());
        if (txtTitulo != null) txtTitulo.setText(safe(c.getTitulo()));
        if (txtArtista != null) txtArtista.setText(safe(c.getArtista()));
        if (txtGenero != null) txtGenero.setText(safe(c.getGenero()));
        if (txtAnio != null) txtAnio.setText(String.valueOf(c.getAnio()));
        if (txtDuracionField != null) txtDuracionField.setText(String.valueOf(c.getDuracionSegundos()));
    }

    private void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }
}

