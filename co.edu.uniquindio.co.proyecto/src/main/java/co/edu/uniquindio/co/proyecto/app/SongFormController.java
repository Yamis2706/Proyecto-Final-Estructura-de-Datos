package co.edu.uniquindio.co.proyecto.app;

import co.edu.uniquindio.co.proyecto.model.Cancion;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class SongFormController {

    @FXML private TextField txtId;
    @FXML private TextField txtTitulo;
    @FXML private TextField txtArtista;
    @FXML private TextField txtGenero;
    @FXML private TextField txtAnio;
    @FXML private TextField txtDuracion;

    private Cancion cancionEdicion; // null si es nueva

    @FXML
    private void onGuardar() {
        try {
            String id = txtId.getText();
            String titulo = txtTitulo.getText();
            String artista = txtArtista.getText();
            String genero = txtGenero.getText();
            int anio = Integer.parseInt(txtAnio.getText());
            int duracion = Integer.parseInt(txtDuracion.getText());

            Cancion c = new Cancion(id, titulo, artista, genero, anio, duracion);

            if (cancionEdicion == null) {
                AppContext.get().songCatalog.add(c);
            } else {
                AppContext.get().songCatalog.update(c);
            }

            ((Stage) txtId.getScene().getWindow()).close();

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Datos inválidos").show();
        }
    }

    @FXML
    private void onCancelar() {
        ((Stage) txtId.getScene().getWindow()).close();
    }

    public void cargarCancion(Cancion c) {
        cancionEdicion = c;
        txtId.setText(c.getId());
        txtTitulo.setText(c.getTitulo());
        txtArtista.setText(c.getArtista());
        txtGenero.setText(c.getGenero());
        txtAnio.setText(String.valueOf(c.getAnio()));
        txtDuracion.setText(String.valueOf(c.getDuracionSegundos()));
    }
}
