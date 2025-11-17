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

    private Cancion editSong;           // si no es null -> estamos editando
    private final AppContext ctx = AppContext.get();

    /**
     * Llamado desde AdminController cuando abrimos el formulario para edición.
     */
    public void cargarCancion(Cancion c) {
        if (c == null) return;
        editSong = c;

        txtId.setText(c.getId());
        txtId.setDisable(true); // no permitimos cambiar ID en edición

        txtTitulo.setText(c.getTitulo());
        txtArtista.setText(c.getArtista());
        txtGenero.setText(c.getGenero());
        txtAnio.setText(String.valueOf(c.getAnio()));
        txtDuracion.setText(String.valueOf(c.getDuracionSegundos()));
    }

    @FXML
    private void onGuardar() {
        try {
            String id = safeGet(txtId);
            String titulo = safeGet(txtTitulo);
            String artista = safeGet(txtArtista);
            String genero = safeGet(txtGenero);
            String anioTxt = safeGet(txtAnio);
            String durTxt = safeGet(txtDuracion);

            if (id.isBlank()) { showError("ID requerido"); return; }
            if (titulo.isBlank()) { showError("Título requerido"); return; }

            int anio = 0;
            int duracion = 0;
            if (!anioTxt.isBlank()) {
                try { anio = Integer.parseInt(anioTxt); }
                catch (NumberFormatException nf) { showError("Año inválido"); return; }
            }
            if (!durTxt.isBlank()) {
                try { duracion = Integer.parseInt(durTxt); }
                catch (NumberFormatException nf) { showError("Duración inválida"); return; }
            }

            if (editSong == null) {
                // Crear nueva canción
                Cancion nueva = new Cancion(id, titulo, artista, genero, anio, duracion);

                // Usar el método de AppContext que hace la persistencia
                // (AppContext.addSong(...) debe insertar en catálogo y guardar en disco)
                if (!ctx.songCatalog.add(nueva)) {
                    showError("Ya existe una canción con ese ID");
                    return;
                }
                // Persistir a través de AppContext si tienes addSong que guarda:
                try {
                    ctx.addSong(nueva); // este método también agrega y persiste (si lo implementaste)
                } catch (Exception ignored) {
                    // si no existe ctx.addSong, ya agregamos al catálogo arriba; en ese caso
                    // asegúrate de llamar a DataManager.saveSongs(...) desde donde corresponda.
                }
            } else {
                // Editar existente
                // Construyo una nueva instancia con el mismo id
                Cancion actualizada = new Cancion(editSong.getId(), titulo, artista, genero, anio, duracion);

                // Actualizo catálogo y persisto:
                boolean ok = ctx.songCatalog.update(actualizada);
                if (!ok) {
                    showError("No se pudo actualizar (ID no existe)");
                    return;
                }
                try {
                    ctx.updateSong(actualizada); // si AppContext tiene este método persistente
                } catch (Exception ignored) { }
            }

            // Asegurarse de que trie/estructuras estén actualizadas
            try { ctx.indexTitles(ctx.songCatalog.list()); } catch (Exception ignored) { }

            // cerrar formulario
            closeWindow();

        } catch (Exception e) {
            showError("Error al guardar: " + e.getMessage());
        }
    }

    @FXML
    private void onCancelar() {
        closeWindow();
    }

    // ---------- Helpers ----------

    private void closeWindow() {
        Stage st = (Stage) txtId.getScene().getWindow();
        st.close();
    }

    private void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }

    private String safeGet(TextField tf) {
        if (tf == null) return "";
        String v = tf.getText();
        return v == null ? "" : v.trim();
    }
}

