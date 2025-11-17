package co.edu.uniquindio.co.proyecto.app;

import co.edu.uniquindio.co.proyecto.model.Cancion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.function.Consumer;

public class SongFormController {

    @FXML private TextField txtId;
    @FXML private TextField txtTitulo;
    @FXML private TextField txtArtista;
    @FXML private TextField txtGenero;
    @FXML private TextField txtAnio;
    @FXML private TextField txtDuracion;
    @FXML private Button btnSave;
    @FXML private Button btnCancel;
    @FXML private Label lblMsg;

    private Runnable onSaveCallback;
    private final AppContext ctx = AppContext.get();
    private boolean editing = false;

    public void setOnSaveCallback(Runnable r) { this.onSaveCallback = r; }

    public void cargarCancion(Cancion c) {
        if (c == null) return;
        editing = true;
        txtId.setText(c.getId());
        txtId.setDisable(true); // no editar id
        txtTitulo.setText(c.getTitulo());
        txtArtista.setText(c.getArtista());
        txtGenero.setText(c.getGenero());
        txtAnio.setText(String.valueOf(c.getAnio()));
        // intenta getter de duración
        try { txtDuracion.setText(String.valueOf(c.getDuracionSegundos())); } catch (Throwable ignored) {}
    }

    @FXML
    private void onSave(ActionEvent e) {
        try {
            String id = txtId.getText().trim();
            String t = txtTitulo.getText().trim();
            String art = txtArtista.getText().trim();
            String gen = txtGenero.getText().trim();
            int anio = Integer.parseInt(txtAnio.getText().trim());
            int dur = Integer.parseInt(txtDuracion.getText().trim());

            Cancion c = new Cancion(id, t, art, gen, anio, dur);
            if (editing) {
                // actualizar
                boolean ok = ctx.songCatalog.update(c);
                if (!ok) { lblMsg.setText("No existe ID para actualizar"); return; }
                ctx.updateSong(c); // si tu AppContext hace persistencia
            } else {
                boolean ok = ctx.songCatalog.add(c);
                if (!ok) { lblMsg.setText("ID ya existe"); return; }
                ctx.addSong(c);
            }

            if (onSaveCallback != null) onSaveCallback.run();
            // cerrar ventana:
            btnSave.getScene().getWindow().hide();
        } catch (NumberFormatException ex) {
            lblMsg.setText("Año/duración inválidos");
        } catch (Exception ex) {
            ex.printStackTrace();
            lblMsg.setText("Error: " + ex.getMessage());
        }
    }

    @FXML
    private void onCancel(ActionEvent e) {
        btnCancel.getScene().getWindow().hide();
    }
}


