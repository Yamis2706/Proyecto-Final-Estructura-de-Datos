package co.edu.uniquindio.co.proyecto.app;

import co.edu.uniquindio.co.proyecto.model.Cancion;
import co.edu.uniquindio.co.proyecto.model.Usuario;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

import java.util.*;

public class AdminController {

    @FXML private TableView<Cancion> songTable;
    @FXML private TableColumn<Cancion, String> colId;
    @FXML private TableColumn<Cancion, String> colTitulo;
    @FXML private TableColumn<Cancion, String> colArtista;
    @FXML private TableColumn<Cancion, String> colGenero;
    @FXML private TableColumn<Cancion, Integer> colAnio;
    @FXML private TableColumn<Cancion, Integer> colDuracion;

    @FXML private TableView<Usuario> userTable;
    @FXML private TableColumn<Usuario, String> colUserId;
    @FXML private TableColumn<Usuario, String> colUserUsername;
    @FXML private TableColumn<Usuario, String> colUserPassword;
    @FXML private TableColumn<Usuario, String> colUserRole;

    private final AppContext ctx = AppContext.get();

    @FXML
    public void initialize() {
        songTable.setEditable(true);

        // Configurar columnas de canciones
        colId.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getId()));
        colId.setCellFactory(TextFieldTableCell.forTableColumn());

        colTitulo.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTitulo()));
        colTitulo.setCellFactory(TextFieldTableCell.forTableColumn());
        colTitulo.setOnEditCommit(event -> {
            Cancion c = event.getRowValue();
            c.setTitulo(event.getNewValue());
            ctx.updateSong(c);
        });

        colArtista.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getArtista()));
        colArtista.setCellFactory(TextFieldTableCell.forTableColumn());
        colArtista.setOnEditCommit(event -> {
            Cancion c = event.getRowValue();
            c.setArtista(event.getNewValue());
            ctx.updateSong(c);
        });

        colGenero.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getGenero()));
        colGenero.setCellFactory(TextFieldTableCell.forTableColumn());
        colGenero.setOnEditCommit(event -> {
            Cancion c = event.getRowValue();
            c.setGenero(event.getNewValue());
            ctx.updateSong(c);
        });

        colAnio.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getAnio()).asObject());
        colAnio.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colAnio.setOnEditCommit(event -> {
            Cancion c = event.getRowValue();
            c.setAnio(event.getNewValue());
            ctx.updateSong(c);
        });

        colDuracion.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getDuracionSegundos()).asObject());
        colDuracion.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colDuracion.setOnEditCommit(event -> {
            Cancion c = event.getRowValue();
            c.setDuracionSegundos(event.getNewValue());
            ctx.updateSong(c);
        });

        // Configurar columnas de usuarios
        colUserId.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getId()));
        colUserUsername.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getUsername()));
        colUserPassword.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getPassword()));
        colUserRole.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getRole().toString()));
        userTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // Cargar todas las canciones y usuarios al iniciar
        refreshTables();
    }

    private void refreshTables() {
        List<Cancion> canciones = ctx.songCatalog.list();
        songTable.getItems().setAll(canciones);

        List<Usuario> usuarios = ctx.userRepository.list();
        userTable.getItems().setAll(usuarios);
    }

    @FXML
    public void onRegisterSong() {
        Dialog<Cancion> dialog = new Dialog<>();
        dialog.setTitle("Registrar nueva canción");

        TextField tfId = new TextField();
        tfId.setPromptText("ID");

        TextField tfTitulo = new TextField();
        tfTitulo.setPromptText("Título");

        TextField tfArtista = new TextField();
        tfArtista.setPromptText("Artista");

        TextField tfGenero = new TextField();
        tfGenero.setPromptText("Género");

        TextField tfAnio = new TextField();
        tfAnio.setPromptText("Año");

        TextField tfDuracion = new TextField();
        tfDuracion.setPromptText("Duración (seg)");

        VBox content = new VBox(10, tfId, tfTitulo, tfArtista, tfGenero, tfAnio, tfDuracion);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                try {
                    return new Cancion(
                            tfId.getText().isBlank() ? UUID.randomUUID().toString() : tfId.getText(),
                            tfTitulo.getText(),
                            tfArtista.getText(),
                            tfGenero.getText(),
                            Integer.parseInt(tfAnio.getText()),
                            Integer.parseInt(tfDuracion.getText())
                    );
                } catch (NumberFormatException e) {
                    new Alert(Alert.AlertType.ERROR, "Año y duración deben ser números").showAndWait();
                }
            }
            return null;
        });

        Optional<Cancion> result = dialog.showAndWait();
        result.ifPresent(c -> {
            ctx.addSong(c);
            refreshTables();
            new Alert(Alert.AlertType.INFORMATION, "Canción registrada correctamente").showAndWait();
        });
    }

    @FXML
    public void onEditSong() {
        Cancion selected = songTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Seleccione una canción para editar.").showAndWait();
            return;
        }

        Dialog<Cancion> dialog = new Dialog<>();
        dialog.setTitle("Editar canción");

        TextField tfId = new TextField(selected.getId());
        tfId.setPromptText("ID");

        TextField tfTitulo = new TextField(selected.getTitulo());
        tfTitulo.setPromptText("Título");

        TextField tfArtista = new TextField(selected.getArtista());
        tfArtista.setPromptText("Artista");

        TextField tfGenero = new TextField(selected.getGenero());
        tfGenero.setPromptText("Género");

        TextField tfAnio = new TextField(String.valueOf(selected.getAnio()));
        tfAnio.setPromptText("Año");

        TextField tfDuracion = new TextField(String.valueOf(selected.getDuracionSegundos()));
        tfDuracion.setPromptText("Duración (seg)");

        VBox content = new VBox(10, tfId, tfTitulo, tfArtista, tfGenero, tfAnio, tfDuracion);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                try {
                    return new Cancion(
                            tfId.getText(),
                            tfTitulo.getText(),
                            tfArtista.getText(),
                            tfGenero.getText(),
                            Integer.parseInt(tfAnio.getText()),
                            Integer.parseInt(tfDuracion.getText())
                    );
                } catch (NumberFormatException e) {
                    new Alert(Alert.AlertType.ERROR, "Año y duración deben ser números").showAndWait();
                }
            }
            return null;
        });

        Optional<Cancion> result = dialog.showAndWait();
        result.ifPresent(c -> {
            ctx.deleteSong(selected.getId()); // eliminar la versión antigua
            ctx.addSong(c); // agregar la versión editada
            refreshTables();
            new Alert(Alert.AlertType.INFORMATION, "Canción editada correctamente").showAndWait();
        });
    }

    @FXML
    public void onDeleteSong() {
        Cancion selected = songTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Seleccione una canción para eliminar.").showAndWait();
            return;
        }
        ctx.deleteSong(selected.getId());
        songTable.getItems().remove(selected);
        new Alert(Alert.AlertType.INFORMATION, "Canción eliminada correctamente.").showAndWait();
    }

    @FXML
    public void onListUsers() {
        // Obtener lista de usuarios ordenada por ID ascendente
        List<Usuario> usuariosOrdenados = new ArrayList<>(ctx.userRepository.list());
        usuariosOrdenados.sort(Comparator.comparing(Usuario::getId));

        // Actualizar la tabla
        userTable.getItems().setAll(usuariosOrdenados);
    }


    @FXML
    public void onDeleteUser() {
        Usuario selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Seleccione un usuario para eliminar.").showAndWait();
            return;
        }
        boolean removed = ctx.userRepository.remove(selected.getUsername());
        if (removed) {
            userTable.getItems().remove(selected);
            new Alert(Alert.AlertType.INFORMATION, "Usuario eliminado correctamente.").showAndWait();
        } else {
            new Alert(Alert.AlertType.ERROR, "No se pudo eliminar el usuario.").showAndWait();
        }
    }

    @FXML
    public void onLogout() {
        Stage stage = (Stage) songTable.getScene().getWindow();
        ViewLoader.load(stage, "/co/edu/uniquindio/co/proyecto/app/login-view.fxml", "SyncUp - Login");
    }
}