package co.edu.uniquindio.co.proyecto.app;

import co.edu.uniquindio.co.proyecto.catalog.SongCatalog;
import co.edu.uniquindio.co.proyecto.ds.TrieAutocompletado;
import co.edu.uniquindio.co.proyecto.graph.GrafoDeSimilitud;
import co.edu.uniquindio.co.proyecto.graph.GrafoSocial;
import co.edu.uniquindio.co.proyecto.model.Cancion;
import co.edu.uniquindio.co.proyecto.model.Usuario;
import co.edu.uniquindio.co.proyecto.repository.DataManager;
import co.edu.uniquindio.co.proyecto.repository.UserRepository;
import co.edu.uniquindio.co.proyecto.service.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppContext {

    private static final AppContext INSTANCE = new AppContext();
    public static AppContext get() { return INSTANCE; }

    public final UserRepository userRepository = new UserRepository();
    public final SongCatalog songCatalog = new SongCatalog();
    public final TrieAutocompletado trie = new TrieAutocompletado();
    public final GrafoDeSimilitud grafoDeSimilitud = new GrafoDeSimilitud();
    public final GrafoSocial grafoSocial = new GrafoSocial();

    public final SearchService searchService = new SearchService();
    public final RecommendationService recommendationService = new RecommendationService();
    public final RadioService radioService = new RadioService();
    public final CsvService csvService = new CsvService();
    public final AuthService authService = new AuthService(userRepository);
    public final BulkImportService bulkImportService = new BulkImportService();

    private AppContext() {

        // Cargar usuarios desde disco
        try {
            Map<String, Usuario> loadedUsers = DataManager.loadUsers();
            if (loadedUsers != null && !loadedUsers.isEmpty()) {
                for (Usuario u : loadedUsers.values()) {
                    userRepository.add(u);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Crear administrador si no existe
        boolean adminExiste = userRepository.list().stream()
                .anyMatch(u -> u.getUsername().equals("admin"));

        if (!adminExiste) {
            Usuario admin = new Usuario(
                    "999", // ID fijo o generarIdUnicoDeTresDigitos()
                    "admin",
                    "admin",
                    "Administrador",
                    Usuario.Role.ADMIN
            );
            userRepository.add(admin);

            // Persistir archivo
            Map<String, Usuario> map = new HashMap<>();
            for (Usuario u : userRepository.list()) map.put(u.getUsername(), u);
            DataManager.saveUsers(map);
        }

        /*for (Usuario u : userRepository.list()) {
            System.out.println("Usuario cargado: " + u.getUsername() + " | Role: " + u.getRole());
        }

         */


        // Cargar canciones desde disco
        try {
            List<Cancion> loadedSongs = DataManager.loadSongs();
            if (loadedSongs != null && !loadedSongs.isEmpty()) {
                for (Cancion c : loadedSongs) {
                    songCatalog.add(c);
                    grafoDeSimilitud.agregarCancion(c);
                }
                indexTitles(loadedSongs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void indexTitles(List<Cancion> canciones) {
        if (canciones == null) return;
        for (Cancion c : canciones) {
            if (c != null && c.getTitulo() != null) trie.insertar(c.getTitulo());
        }
    }

    // ---------------- Usuarios ----------------

    public boolean registerUser(Usuario u) {
        boolean ok = userRepository.add(u);
        if (ok) {
            Map<String, Usuario> map = new HashMap<>();
            for (Usuario item : userRepository.list()) map.put(item.getUsername(), item);
            DataManager.saveUsers(map);
        }
        return ok;
    }

    // ---------------- Canciones ----------------

    public void addSong(Cancion c) {
        boolean added = songCatalog.add(c);
        if (!added) throw new IllegalArgumentException("ID existente: " + c.getId());

        if (c.getTitulo() != null) trie.insertar(c.getTitulo());
        grafoDeSimilitud.agregarCancion(c);
        DataManager.saveSongs(songCatalog.list());
    }

    public void updateSong(Cancion c) {
        boolean updated = songCatalog.update(c);
        if (!updated) throw new IllegalArgumentException("ID no existe: " + c.getId());

        if (c.getTitulo() != null) trie.insertar(c.getTitulo());
        DataManager.saveSongs(songCatalog.list());
    }

    public void deleteSong(String id) {
        boolean removed = songCatalog.remove(id);
        if (!removed) throw new IllegalArgumentException("ID no existe: " + id);

        DataManager.saveSongs(songCatalog.list());
    }
}
