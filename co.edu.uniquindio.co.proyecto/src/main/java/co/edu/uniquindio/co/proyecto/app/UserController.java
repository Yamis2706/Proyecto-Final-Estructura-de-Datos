package co.edu.uniquindio.co.proyecto.app;

import co.edu.uniquindio.co.proyecto.model.Cancion;
import co.edu.uniquindio.co.proyecto.model.Usuario;
import co.edu.uniquindio.co.proyecto.service.SearchService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Queue;

public class UserController {

    @FXML private TextField searchTitleField;
    @FXML private ListView<String> autocompleteList;
    @FXML private Button textSearchButton;
    @FXML private TextField artistaField;
    @FXML private TextField generoField;
    @FXML private TextField anioField;
    @FXML private ChoiceBox<SearchService.Logic> logicChoice;
    @FXML private ListView<String> resultsList;
    @FXML private ListView<String> favoritosList;
    @FXML private Button discoveryButton;
    @FXML private Button radioButton;
    @FXML private Button exportCsvButton;

    private final AppContext ctx = AppContext.get();
    private Usuario currentUser;

    @FXML
    public void initialize() {
        logicChoice.getItems().setAll(SearchService.Logic.AND, SearchService.Logic.OR);
        logicChoice.getSelectionModel().select(SearchService.Logic.OR);

        // ya no usamos demo hardcode; el usuario real se asigna desde LoginController vía setCurrentUser()

        searchTitleField.textProperty().addListener((obs, old, val) -> {
            List<String> words = ctx.trie.buscarPorPrefijo(val == null ? "" : val);
            autocompleteList.setItems(FXCollections.observableArrayList(words));
        });
        autocompleteList.setOnMouseClicked(evt -> {
            String sel = autocompleteList.getSelectionModel().getSelectedItem();
            if (sel != null) {
                searchTitleField.setText(sel);
                onTextSearch();
            }
        });
    }

    /**
     * Método para que LoginController pase el usuario autenticado.
     */
    public void setCurrentUser(Usuario u) {
        this.currentUser = u;
        // cargar favoritos inmediatamente si la UI ya está inicializada
        if (favoritosList != null && currentUser != null) {
            favoritosList.setItems(FXCollections.observableArrayList(currentUser.getListaFavoritos().stream().map(Cancion::getTitulo).toList()));
        }
    }

    @FXML
    public void onTextSearch() {
        String q = searchTitleField.getText();
        var res = ctx.songCatalog.findByTitleOrArtistContains(q);
        resultsList.setItems(FXCollections.observableArrayList(res.stream().map(c -> c.getTitulo()+" - "+c.getArtista()).toList()));
    }

    @FXML
    public void onAdvancedSearch() {
        SearchService.Criteria cr = new SearchService.Criteria();
        cr.artista = artistaField.getText();
        cr.genero = generoField.getText();
        try {
            if (!anioField.getText().isBlank()) cr.anio = Integer.parseInt(anioField.getText().trim());
        } catch (NumberFormatException ignored) { }
        var base = ctx.songCatalog.list();
        List<Cancion> res = ctx.searchService.advancedSearch(base, cr, logicChoice.getValue());
        resultsList.setItems(FXCollections.observableArrayList(res.stream().map(c -> c.getTitulo()+" - "+c.getArtista()).toList()));
    }

    @FXML
    public void onDiscovery() {
        var recs = ctx.recommendationService.descubrimientoSemanal(currentUser, ctx.songCatalog.list(), 20);
        resultsList.setItems(FXCollections.observableArrayList(recs.stream().map(c -> c.getTitulo()+" - "+c.getArtista()).toList()));
    }

    @FXML
    public void onRadioFromSelection() {
        String title = resultsList.getSelectionModel().getSelectedItem();
        if (title == null) return;
        String selectedTitle = title.contains(" - ") ? title.substring(0, title.indexOf(" - ")) : title;
        Cancion start = ctx.songCatalog.list().stream().filter(c -> selectedTitle.equals(c.getTitulo())).findFirst().orElse(null);
        if (start == null) return;
        Queue<Cancion> cola = ctx.radioService.generarRadio(ctx.grafoDeSimilitud, start, 15);
        resultsList.setItems(FXCollections.observableArrayList(cola.stream().map(c -> c.getTitulo()+" - "+c.getArtista()).toList()));
    }

    @FXML
    public void onAddFavoriteFromResults() {
        String title = resultsList.getSelectionModel().getSelectedItem();
        if (title == null || currentUser == null) return;
        String selectedTitle = title.contains(" - ") ? title.substring(0, title.indexOf(" - ")) : title;
        Cancion song = ctx.songCatalog.list().stream().filter(c -> selectedTitle.equals(c.getTitulo())).findFirst().orElse(null);
        if (song != null) {
            currentUser.agregarFavorito(song);
            favoritosList.setItems(FXCollections.observableArrayList(currentUser.getListaFavoritos().stream().map(Cancion::getTitulo).toList()));
        }
    }

    @FXML
    public void onExportCsv() throws IOException {
        if (currentUser == null) return;
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Guardar favoritos CSV");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));
        File target = chooser.showSaveDialog(exportCsvButton.getScene().getWindow());
        if (target == null) return;
        ctx.csvService.writeToFile(currentUser.getListaFavoritos(), Path.of(target.getAbsolutePath()));
        new Alert(Alert.AlertType.INFORMATION, "Favoritos exportados").showAndWait();
    }

    @FXML
    public void onGoPerfil() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/user_profile.fxml"));
            Scene scene = new Scene(loader.load());

            ProfileController controller = loader.getController();
            controller.setUser(currentUser); // ⚠ Asegúrate de tener currentUser en tu UserController

            Stage stage = new Stage();
            stage.setTitle("Perfil de Usuario");
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "No se pudo abrir el perfil").showAndWait();
        }
    }

}
