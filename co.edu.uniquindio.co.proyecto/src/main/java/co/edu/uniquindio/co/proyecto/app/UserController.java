package co.edu.uniquindio.co.proyecto.app;

import co.edu.uniquindio.co.proyecto.model.Cancion;
import co.edu.uniquindio.co.proyecto.model.Usuario;
import co.edu.uniquindio.co.proyecto.service.SearchService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Queue;

public class UserController {

    @FXML private TextField searchTitleField;
    @FXML private ListView<String> autocompleteList;
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
        // usuario actual: usar demo si existe
        currentUser = ctx.userRepository.get("demo").orElse(null);

        searchTitleField.textProperty().addListener((obs, old, val) -> {
            List<String> words = ctx.trie.buscarPorPrefijo(val == null ? "" : val);
            autocompleteList.setItems(FXCollections.observableArrayList(words));
        });
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
        resultsList.setItems(FXCollections.observableArrayList(res.stream().map(Cancion::getTitulo).toList()));
    }

    @FXML
    public void onDiscovery() {
        var recs = ctx.recommendationService.descubrimientoSemanal(currentUser, ctx.songCatalog.list(), 20);
        resultsList.setItems(FXCollections.observableArrayList(recs.stream().map(Cancion::getTitulo).toList()));
    }

    @FXML
    public void onRadioFromSelection() {
        String title = resultsList.getSelectionModel().getSelectedItem();
        if (title == null) return;
        Cancion start = ctx.songCatalog.list().stream().filter(c -> title.equals(c.getTitulo())).findFirst().orElse(null);
        if (start == null) return;
        Queue<Cancion> cola = ctx.radioService.generarRadio(ctx.grafoDeSimilitud, start, 15);
        resultsList.setItems(FXCollections.observableArrayList(cola.stream().map(Cancion::getTitulo).toList()));
    }

    @FXML
    public void onAddFavoriteFromResults() {
        String title = resultsList.getSelectionModel().getSelectedItem();
        if (title == null || currentUser == null) return;
        Cancion song = ctx.songCatalog.list().stream().filter(c -> title.equals(c.getTitulo())).findFirst().orElse(null);
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
}


