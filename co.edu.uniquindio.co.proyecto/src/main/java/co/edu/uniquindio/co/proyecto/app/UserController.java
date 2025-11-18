package co.edu.uniquindio.co.proyecto.app;

import co.edu.uniquindio.co.proyecto.model.Cancion;
import co.edu.uniquindio.co.proyecto.model.Usuario;
import co.edu.uniquindio.co.proyecto.service.SearchService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * Controlador para la interfaz de usuario.
 * Implementa funcionalidades de búsqueda, favoritos y red social.
 */
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
    // alias/rescate para compatibilidad con FXML que usaba onShowCatalog
    @FXML
    public void onShowCatalog() {
        onDiscovery();
    }


    // Nuevos campos para funcionalidad social
    @FXML private TextField followUserField;

    private final AppContext ctx = AppContext.get();
    private Usuario currentUser;

    @FXML
    public void initialize() {
        logicChoice.getItems().setAll(SearchService.Logic.AND, SearchService.Logic.OR);
        logicChoice.getSelectionModel().select(SearchService.Logic.OR);

        // Usuario actual: usar demo si existe
        currentUser = ctx.userRepository.get("demo").orElse(null);
        if (currentUser == null) {
            // Buscar cualquier usuario disponible
            currentUser = ctx.userRepository.list().stream()
                    .filter(u -> u.getRole() == Usuario.Role.USER)
                    .findFirst().orElse(null);
        }

        configurarAutocompletado();
        actualizarFavoritos();
    }

    /**
     * Configura el autocompletado de títulos (RF-003).
     */
    private void configurarAutocompletado() {
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
     * RF-003: Búsqueda por texto con autocompletado.
     */
    @FXML
    public void onTextSearch() {
        String q = searchTitleField.getText();
        var res = ctx.songCatalog.findByTitleOrArtistContains(q);
        resultsList.setItems(FXCollections.observableArrayList(
                res.stream().map(c -> c.getTitulo() + " - " + c.getArtista()).toList()
        ));
    }

    /**
     * RF-004: Búsqueda avanzada con hilos y lógica AND/OR.
     */
    @FXML
    public void onAdvancedSearch() {
        SearchService.Criteria cr = new SearchService.Criteria();
        cr.artista = artistaField.getText();
        cr.genero = generoField.getText();
        try {
            if (!anioField.getText().isBlank()) {
                cr.anio = Integer.parseInt(anioField.getText().trim());
            }
        } catch (NumberFormatException ignored) { }

        var base = ctx.songCatalog.list();
        List<Cancion> res = ctx.searchService.advancedSearch(base, cr, logicChoice.getValue());
        resultsList.setItems(FXCollections.observableArrayList(
                res.stream().map(c -> c.getTitulo() + " - " + c.getArtista()).toList()
        ));
    }

    /**
     * RF-005: Generar Descubrimiento Semanal.
     */
    @FXML
    public void onDiscovery() {
        if (currentUser == null) {
            showError("No hay usuario autenticado");
            return;
        }

        var recs = ctx.recommendationService.descubrimientoSemanal(currentUser, ctx.songCatalog.list(), 20);
        resultsList.setItems(FXCollections.observableArrayList(
                recs.stream().map(c -> c.getTitulo() + " - " + c.getArtista()).toList()
        ));

        new Alert(Alert.AlertType.INFORMATION,
                "Descubrimiento Semanal generado con " + recs.size() + " canciones").showAndWait();
    }

    /**
     * RF-006: Iniciar Radio desde canción seleccionada.
     */
    @FXML
    public void onRadioFromSelection() {
        String title = resultsList.getSelectionModel().getSelectedItem();
        if (title == null) {
            showError("Seleccione una canción para iniciar la radio");
            return;
        }

        String selectedTitle = title.contains(" - ") ? title.substring(0, title.indexOf(" - ")) : title;
        Cancion start = ctx.songCatalog.list().stream()
                .filter(c -> selectedTitle.equals(c.getTitulo()))
                .findFirst().orElse(null);

        if (start == null) {
            showError("No se encontró la canción seleccionada");
            return;
        }

        // Usar el método mejorado del grafo de similitud
        Queue<Cancion> cola = ctx.grafoDeSimilitud.generarRadioMejorada(start, 15);
        resultsList.setItems(FXCollections.observableArrayList(
                cola.stream().map(c -> c.getTitulo() + " - " + c.getArtista()).toList()
        ));

        new Alert(Alert.AlertType.INFORMATION,
                "Radio iniciada con " + cola.size() + " canciones similares").showAndWait();
    }

    /**
     * RF-002: Agregar canción a favoritos.
     */
    @FXML
    public void onAddFavoriteFromResults() {
        if (currentUser == null) {
            showError("No hay usuario autenticado");
            return;
        }

        String title = resultsList.getSelectionModel().getSelectedItem();
        if (title == null) {
            showError("Seleccione una canción para agregar a favoritos");
            return;
        }

        String selectedTitle = title.contains(" - ") ? title.substring(0, title.indexOf(" - ")) : title;
        Cancion song = ctx.songCatalog.list().stream()
                .filter(c -> selectedTitle.equals(c.getTitulo()))
                .findFirst().orElse(null);

        if (song != null) {
            currentUser.agregarFavorito(song);
            actualizarFavoritos();
            new Alert(Alert.AlertType.INFORMATION, "Canción agregada a favoritos").showAndWait();
        }
    }

    /**
     * RF-009: Exportar favoritos a CSV.
     */
    @FXML
    public void onExportCsv() throws IOException {
        if (currentUser == null) {
            showError("No hay usuario autenticado");
            return;
        }

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Guardar favoritos CSV");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));
        File target = chooser.showSaveDialog(exportCsvButton.getScene().getWindow());

        if (target == null) return;

        ctx.csvService.writeToFile(currentUser.getListaFavoritos(), Path.of(target.getAbsolutePath()));
        new Alert(Alert.AlertType.INFORMATION, "Favoritos exportados exitosamente").showAndWait();
    }

    /**
     * RF-007: Seguir a otro usuario.
     */
    @FXML
    public void onFollowUser() {
        if (currentUser == null) {
            showError("No hay usuario autenticado");
            return;
        }

        String usernameToFollow = followUserField.getText().trim();
        if (usernameToFollow.isEmpty()) {
            showError("Ingrese el nombre del usuario a seguir");
            return;
        }

        var userToFollow = ctx.userRepository.get(usernameToFollow);
        if (userToFollow.isEmpty()) {
            showError("Usuario no encontrado: " + usernameToFollow);
            return;
        }

        if (userToFollow.get().equals(currentUser)) {
            showError("No puedes seguirte a ti mismo");
            return;
        }

        ctx.grafoSocial.conectar(currentUser, userToFollow.get());
        followUserField.clear();
        new Alert(Alert.AlertType.INFORMATION,
                "Ahora sigues a " + userToFollow.get().getNombre()).showAndWait();
    }

    /**
     * RF-008 y RF-022: Mostrar red social y sugerencias.
     */
    @FXML
    public void onShowSocialNetwork() {
        if (currentUser == null) {
            showError("No hay usuario autenticado");
            return;
        }

        try {
            Stage stage = new Stage();
            VBox root = new VBox(10);
            root.setStyle("-fx-padding: 20;");

            Label titulo = new Label("Mi Red Social");
            titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

            // Mostrar usuarios que sigo
            Label seguidosLabel = new Label("Usuarios que sigo:");
            seguidosLabel.setStyle("-fx-font-weight: bold;");
            ListView<String> seguidosList = new ListView<>();
            Set<Usuario> seguidos = ctx.grafoSocial.vecinos(currentUser);
            seguidosList.setItems(FXCollections.observableArrayList(
                    seguidos.stream().map(u -> u.getUsername() + " - " + u.getNombre()).toList()
            ));
            seguidosList.setPrefHeight(150);

            // Mostrar sugerencias (RF-008)
            Label sugerenciasLabel = new Label("Sugerencias de usuarios (amigos de amigos):");
            sugerenciasLabel.setStyle("-fx-font-weight: bold;");
            ListView<String> sugerenciasList = new ListView<>();
            Set<Usuario> sugerencias = ctx.grafoSocial.sugerirSegundosGrados(currentUser);
            sugerenciasList.setItems(FXCollections.observableArrayList(
                    sugerencias.stream().map(u -> u.getUsername() + " - " + u.getNombre()).toList()
            ));
            sugerenciasList.setPrefHeight(150);

            Button seguirSugerenciaBtn = new Button("Seguir Usuario Sugerido");
            seguirSugerenciaBtn.setOnAction(e -> {
                String selected = sugerenciasList.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    String username = selected.split(" - ")[0];
                    var userToFollow = ctx.userRepository.get(username);
                    if (userToFollow.isPresent()) {
                        ctx.grafoSocial.conectar(currentUser, userToFollow.get());
                        new Alert(Alert.AlertType.INFORMATION, "Ahora sigues a " + username).showAndWait();
                        // Actualizar listas
                        seguidos.add(userToFollow.get());
                        seguidosList.getItems().add(selected);
                        sugerenciasList.getItems().remove(selected);
                    }
                }
            });

            Button closeButton = new Button("Cerrar");
            closeButton.setOnAction(e -> stage.close());

            root.getChildren().addAll(titulo, seguidosLabel, seguidosList,
                    sugerenciasLabel, sugerenciasList, seguirSugerenciaBtn, closeButton);

            stage.setTitle("Red Social - " + currentUser.getNombre());
            stage.setScene(new Scene(root, 500, 600));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (Exception ex) {
            showError("Error mostrando red social: " + ex.getMessage());
        }
    }

    /**
     * Ir al perfil del usuario.
     */
    @FXML
    public void onGoToProfile() {
        if (currentUser == null) {
            showError("No hay usuario autenticado");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("profile-view.fxml"));
            Scene scene = new Scene(loader.load(), 400, 300);

            ProfileController controller = loader.getController();
            controller.setUser(currentUser);

            Stage stage = new Stage();
            stage.setTitle("Mi Perfil");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (Exception ex) {
            showError("Error abriendo perfil: " + ex.getMessage());
        }
    }

    /**
     * Cerrar sesión y volver al login.
     */
    @FXML
    public void onLogout() {
        try {
            HelloApplication.switchTo("login-view.fxml");
        } catch (Exception ex) {
            showError("Error cerrando sesión: " + ex.getMessage());
        }
    }

    /**
     * Actualizar la lista de favoritos.
     */
    private void actualizarFavoritos() {
        if (currentUser != null) {
            favoritosList.setItems(FXCollections.observableArrayList(
                    currentUser.getListaFavoritos().stream().map(Cancion::getTitulo).toList()
            ));
        }
    }

    /**
     * Mostrar mensaje de error.
     */
    private void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }
}