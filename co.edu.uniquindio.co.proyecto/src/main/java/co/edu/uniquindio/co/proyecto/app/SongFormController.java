package co.edu.uniquindio.co.proyecto.app;

import co.edu.uniquindio.co.proyecto.model.Cancion;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SongFormController {

    @FXML private TextField txtId;
    @FXML private TextField txtTitulo;
    @FXML private TextField txtArtista;
    @FXML private TextField txtGenero;
    @FXML private TextField txtAnio;
    @FXML private TextField txtDuracion;

    private Cancion editSong;

    private final AppContext ctx = AppContext.get();

    public void cargarCancion(Cancion c) {
        editSong = c;

        txtId.setText(c.getId());
        txtId.setDisable(true);

        txtTitulo.setText(c.getTitulo());
        txtArtista.setText(c.getArtista());
        txtGenero.setText(c.getGenero());
        txtAnio.setText(String.valueOf(c.getAnio()));
        txtDuracion.setText(String.valueOf(c.getDuracionSegundos()));
    }

    @FXML
    private void onGuardar() {
        try {
            String id = txtId.getText().trim();
            String titulo = txtTitulo.getText().trim();
            String artista = txtArtista.getText().trim();
            String genero = txtGenero.getText().trim();
            int anio = Integer.parseInt(txtAnio.getText().trim());
            int duracion = Integer.parseInt(txtDuracion.getText().trim());

            if (editSong == null) {
                // Crear nueva
                Cancion nueva = new Cancion(id, titulo, artista, genero, anio, duracion);
                if (!ctx.songCatalog.add(nueva)) {
                    showError("Ya existe una canción con ese ID");
                    return;
                }
            } else {
                // Editar existente
                Cancion actualizada = new Cancion(id, titulo, artista, genero, anio, duracion);
                ctx.songCatalog.update(actualizada);
            }

            ctx.data.saveAll(ctx.songCatalog, ctx.userRepo); // persistencia

            closeWindow();

        } catch (Exception e) {
            showError("Error: " + e.getMessage());
        }
    }

    @FXML
    private void onCancelar() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) txtId.getScene().getWindow();
        stage.close();
    }

    private void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }
}
