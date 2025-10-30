package co.edu.uniquindio.co.proyecto.app;

import co.edu.uniquindio.co.proyecto.model.Cancion;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class AdminController {
    @FXML private TextField idField;
    @FXML private TextField tituloField;
    @FXML private TextField artistaField;
    @FXML private TextField generoField;
    @FXML private TextField anioField;
    @FXML private TextField duracionField;
    @FXML private ListView<String> catalogList;

    private final AppContext ctx = AppContext.get();

    @FXML
    public void initialize() {
        refrescarLista();
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

    private void limpiarCampos() {
        idField.clear(); tituloField.clear(); artistaField.clear(); generoField.clear(); anioField.clear(); duracionField.clear();
    }

    private void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }
}


