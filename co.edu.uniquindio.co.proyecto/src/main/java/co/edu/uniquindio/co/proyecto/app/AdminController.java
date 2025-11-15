package co.edu.uniquindio.co.proyecto.app;

import co.edu.uniquindio.co.proyecto.model.Cancion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.Optional;

public class AdminController {

    @FXML private TableView<Cancion> songTable;
    @FXML private TableColumn<Cancion, String> colId;
    @FXML private TableColumn<Cancion, String> colTitulo;
    @FXML private TableColumn<Cancion, String> colArtista;
    @FXML private TableColumn<Cancion, String> colGenero;
    @FXML private TableColumn<Cancion, Integer> colAnio;
    @FXML private TableColumn<Cancion, Integer> colDuracion;


    // campos para edición/registro (opcional si los integras en una ventana modal)
    @FXML private TextField txtId;
    @FXML private TextField txtTitulo;
    @FXML private TextField txtArtista;
    @FXML private TextField txtGenero;
    @FXML private TextField txtAnio;
    @FXML private TextField txtDuracion;


    private final AppContext ctx = AppContext.get();

    @FXML
    public void initialize() {
        // defensivo: puede que alguna vista no tenga la tabla (evita NPEs)
        if (colId != null)    colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        if (colTitulo != null)colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        if (colArtista != null)colArtista.setCellValueFactory(new PropertyValueFactory<>("artista"));
        if (colGenero != null)colGenero.setCellValueFactory(new PropertyValueFactory<>("genero"));
        if (colAnio != null)   colAnio.setCellValueFactory(new PropertyValueFactory<>("anio"));
        if (colAnio != null)   colDuracion.setCellValueFactory(new PropertyValueFactory<>("duracionSegundos"));




        if (songTable != null) {
            refrescarTabla();

            // cuando seleccionen una fila, cargar datos en campos (si existen)
            songTable.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
                if (sel != null) cargarSeleccion(sel);
            });
        }
    }

    // ----------------- Acciones de los botones -----------------

    @FXML
    private void onRegisterSong(ActionEvent evt) {
        try {
            String id = safeGet(txtId);
            String titulo = safeGet(txtTitulo);
            String artista = safeGet(txtArtista);
            String genero = safeGet(txtGenero);
            int anio = parseOrThrow(txtAnio, "Año inválido");

            if (id.isBlank()) { showError("ID no puede estar vacío"); return; }

            Cancion c = new Cancion(id, titulo, artista, genero, anio, 0);
            boolean added = ctx.songCatalog.add(c);
            if (added) {
                ctx.indexTitles(ctx.songCatalog.list()); // opcional: mantener trie actualizado
                // si AppContext tiene persistencia centralizada, usa su método; si no, guarda aquí
                // ejemplo: AppContext.get().addSong(c);
                refrescarTabla();
                limpiarCampos();
                new Alert(Alert.AlertType.INFORMATION, "Canción agregada").showAndWait();
            } else {
                showError("ID ya existe");
            }
        } catch (Exception ex) {
            showError("Error agregando canción: " + ex.getMessage());
        }
    }

    @FXML
    private void onEditSong(ActionEvent evt) {
        try {
            Cancion selected = songTable.getSelectionModel().getSelectedItem();
            if (selected == null) { showError("Seleccione una canción de la lista"); return; }

            String id = selected.getId(); // no permitir cambiar id desde aquí
            String titulo = safeGet(txtTitulo);
            String artista = safeGet(txtArtista);
            String genero = safeGet(txtGenero);
            int anio = parseOrThrow(txtAnio, "Año inválido");

            Cancion updated = new Cancion(id, titulo, artista, genero, anio, selected.getDuracionSegundos());
            boolean ok = ctx.songCatalog.update(updated);
            if (ok) {
                ctx.indexTitles(ctx.songCatalog.list());
                refrescarTabla();
                limpiarCampos();
                new Alert(Alert.AlertType.INFORMATION, "Canción actualizada").showAndWait();
            } else {
                showError("No se pudo actualizar (ID no existe)");
            }
        } catch (Exception ex) {
            showError("Error actualizando: " + ex.getMessage());
        }
    }

    @FXML
    private void onDeleteSong(ActionEvent evt) {
        Cancion selected = songTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showError("Seleccione una canción"); return; }
        boolean removed = ctx.songCatalog.remove(selected.getId());
        if (removed) {
            refrescarTabla();
            limpiarCampos();
            new Alert(Alert.AlertType.INFORMATION, "Canción eliminada").showAndWait();
        } else {
            showError("No se pudo eliminar (ID no existe)");
        }
    }

    @FXML
    private void onLogout(ActionEvent evt) {
        try {
            Stage stage = (Stage) songTable.getScene().getWindow();
            // usa el ViewLoader que definimos (carga y setea escena)
            ViewLoader.load(stage, "login-view.fxml", "SyncUp - Login");
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "No se pudo regresar al login").showAndWait();
        }
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
    }

    private void limpiarCampos() {
        if (txtId != null) txtId.clear();
        if (txtTitulo != null) txtTitulo.clear();
        if (txtArtista != null) txtArtista.clear();
        if (txtGenero != null) txtGenero.clear();
        if (txtAnio != null) txtAnio.clear();
    }

    private void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }

    private String safeGet(TextField tf) {
        return tf == null ? "" : (tf.getText() == null ? "" : tf.getText().trim());
    }

    private int parseOrThrow(TextField tf, String errMsg) {
        String v = safeGet(tf);
        if (v.isBlank()) return 0;
        try {
            return Integer.parseInt(v);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(errMsg);
        }
    }

    @FXML
    private void onRegisterSong() throws Exception {
        ViewLoader.openModal("song-form.fxml", "Registrar Canción");
        refrescarTabla();
    }

    @FXML
    private void onEditSong() throws Exception {
        Cancion c = songTable.getSelectionModel().getSelectedItem();
        if (c == null) {
            new Alert(Alert.AlertType.WARNING, "Seleccione una canción").show();
            return;
        }

        ViewLoader.openModal("song-form.fxml", "Editar Canción", controller -> {
            ((SongFormController) controller).cargarCancion(c);
        });

        refrescarTabla();
    }

}
