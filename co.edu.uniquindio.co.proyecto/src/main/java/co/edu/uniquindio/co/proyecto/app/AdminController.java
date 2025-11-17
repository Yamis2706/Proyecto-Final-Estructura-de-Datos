package co.edu.uniquindio.co.proyecto.app;

import co.edu.uniquindio.co.proyecto.model.Cancion;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AdminController {

    @FXML private TableView<Cancion> songTable;
    @FXML private TableColumn<Cancion, String> colId;
    @FXML private TableColumn<Cancion, String> colTitulo;
    @FXML private TableColumn<Cancion, String> colArtista;
    @FXML private TableColumn<Cancion, String> colGenero;
    @FXML private TableColumn<Cancion, Integer> colAnio;
    @FXML private TableColumn<Cancion, Integer> colDuracion;

    @FXML private Button btnRegisterSong;
    @FXML private Button btnEditSong;
    @FXML private Button btnDeleteSong;
    @FXML private Button btnLogout;

    private final AppContext ctx = AppContext.get();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue() == null ? "" : c.getValue().getId()));
        colTitulo.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue() == null ? "" : c.getValue().getTitulo()));
        colArtista.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue() == null ? "" : c.getValue().getArtista()));
        colGenero.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue() == null ? "" : c.getValue().getGenero()));
        colAnio.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue() == null ? null : c.getValue().getAnio()));
        colDuracion.setCellValueFactory(c -> {
            Integer d = null;
            try { d = c.getValue().getDuracionSegundos(); } catch (Throwable ignored) {}
            if (d == null) {
                try { d = c.getValue().getDuracionSegundos(); } catch (Throwable ignored) {}
            }
            return new ReadOnlyObjectWrapper<>(d);
        });

        refreshTable();
    }

    private void refreshTable() {
        songTable.getItems().setAll(ctx.songCatalog.list());
    }

    private void openSongForm(Cancion c) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/edu/uniquindio/co/proyecto/app/song-form.fxml"));
            Parent root = loader.load();
            SongFormController ctrl = loader.getController();
            if (c != null) ctrl.cargarCancion(c);
            ctrl.setOnSaveCallback(() -> refreshTable());

            Stage st = new Stage();
            st.setScene(new Scene(root));
            st.setTitle(c == null ? "Agregar canción" : "Editar canción");
            st.show();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "No se pudo abrir el formulario: " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    private void onRegisterSong(ActionEvent e) {
        openSongForm(null);
    }

    @FXML
    private void onEditSong(ActionEvent e) {
        Cancion sel = songTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            new Alert(Alert.AlertType.WARNING, "Seleccione una canción").showAndWait();
            return;
        }
        openSongForm(sel);
    }

    @FXML
    private void onDeleteSong(ActionEvent e) {
        Cancion sel = songTable.getSelectionModel().getSelectedItem();
        if (sel == null) { new Alert(Alert.AlertType.WARNING, "Seleccione una canción").showAndWait(); return; }
        boolean ok = ctx.songCatalog.remove(sel.getId());
        if (ok) {
            refreshTable();
            new Alert(Alert.AlertType.INFORMATION, "Canción eliminada").showAndWait();
        } else {
            new Alert(Alert.AlertType.ERROR, "No se pudo eliminar").showAndWait();
        }
    }

    @FXML
    private void onLogout(ActionEvent e) {
        try {
            Stage stage = (Stage) btnLogout.getScene().getWindow();
            ViewLoader.setView(stage, "co/edu/uniquindio/co/proyecto/app/login-view.fxml", "SyncUp - Login");
        } catch (Exception ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "No se pudo regresar al login").showAndWait();
        }
    }
}


