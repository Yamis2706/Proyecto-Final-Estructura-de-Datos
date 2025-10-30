package co.edu.uniquindio.co.proyecto.app;

import co.edu.uniquindio.co.proyecto.model.Cancion;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.file.Path;

public class AdminController {
    @FXML private TextField idField;
    @FXML private TextField tituloField;
    @FXML private TextField artistaField;
    @FXML private TextField generoField;
    @FXML private TextField anioField;
    @FXML private TextField duracionField;
    @FXML private ListView<String> catalogList;
    @FXML private ListView<String> userList;

    private final AppContext ctx = AppContext.get();

    @FXML
    public void initialize() {
        try {
            if (catalogList != null) {
                refrescarLista();
            }
            if (userList != null) {
                refrescarUsuarios();
            }
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Error inicializando Admin: " + ex.getMessage()).showAndWait();
        }
    }

    @FXML
    public void onAgregar() {
        try {
            Cancion c = new Cancion(
                    idField.getText(),
                    tituloField.getText(),
                    artistaField.getText(),
                    generoField.getText(),
                    Integer.parseInt(anioField.getText().trim()),
                    Integer.parseInt(duracionField.getText().trim())
            );
            if (ctx.songCatalog.add(c)) {
                if (c.getTitulo() != null) ctx.trie.insertar(c.getTitulo());
                refrescarLista();
                limpiarCampos();
            } else {
                showError("ID existente");
            }
        } catch (Exception ex) {
            showError("Datos inválidos");
        }
    }

    @FXML
    public void onActualizar() {
        try {
            Cancion c = new Cancion(
                    idField.getText(),
                    tituloField.getText(),
                    artistaField.getText(),
                    generoField.getText(),
                    Integer.parseInt(anioField.getText().trim()),
                    Integer.parseInt(duracionField.getText().trim())
            );
            if (ctx.songCatalog.update(c)) {
                refrescarLista();
                limpiarCampos();
            } else {
                showError("ID no existe");
            }
        } catch (Exception ex) {
            showError("Datos inválidos");
        }
    }

    @FXML
    public void onEliminar() {
        String id = idField.getText();
        if (id == null || id.isBlank()) { showError("Ingrese ID"); return; }
        if (ctx.songCatalog.remove(id)) {
            refrescarLista();
            limpiarCampos();
        } else {
            showError("ID no existe");
        }
    }

    private void refrescarLista() {
        catalogList.setItems(FXCollections.observableArrayList(ctx.songCatalog.list().stream().map(c -> c.getId()+" - "+c.getTitulo()).toList()));
    }

    private void refrescarUsuarios() {
        userList.setItems(FXCollections.observableArrayList(ctx.userRepository.list().stream().map(u -> u.getUsername()+" ("+u.getRole()+")").toList()));
    }

    private void limpiarCampos() {
        idField.clear(); tituloField.clear(); artistaField.clear(); generoField.clear(); anioField.clear(); duracionField.clear();
    }

    private void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }

    @FXML
    public void onBulkLoad() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Cargar canciones (texto plano)");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Texto/CSV", "*.txt", "*.csv"));
        File f = chooser.showOpenDialog(catalogList.getScene().getWindow());
        if (f == null) return;
        try {
            var result = ctx.bulkImportService.importSongs(Path.of(f.getAbsolutePath()), ctx.songCatalog);
            ctx.indexTitles(ctx.songCatalog.list());
            refrescarLista();
            new Alert(Alert.AlertType.INFORMATION, "Insertadas: "+result.inserted+". Errores: "+result.errors.size()).showAndWait();
        } catch (Exception ex) {
            showError("Error importando: "+ex.getMessage());
        }
    }

    @FXML
    public void onDeleteSelectedUser() {
        String selected = userList.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        String username = selected.split(" ", 2)[0];
        if (ctx.userRepository.remove(username)) {
            try { ctx.authService.save(); } catch (Exception ignored) {}
            refrescarUsuarios();
        }
    }
}


