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

/**
 * Contexto global de la aplicación.
 * Usa la API pública de UserRepository y SongCatalog que ya existen en tu proyecto.
 */
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
        // Cargar usuarios desde disco: DataManager.loadUsers() devuelve Map<String, Usuario>
        try {
            Map<String, Usuario> loadedUsers = DataManager.loadUsers();
            if (loadedUsers != null && !loadedUsers.isEmpty()) {
                for (Usuario u : loadedUsers.values()) {
                    userRepository.add(u); // usa el método público add
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Cargar canciones desde disco: DataManager.loadSongs() devuelve List<Cancion>
        try {
            List<Cancion> loadedSongs = DataManager.loadSongs();
            if (loadedSongs != null && !loadedSongs.isEmpty()) {
                for (Cancion c : loadedSongs) {
                    songCatalog.add(c); // add() ya existe en SongCatalog
                    grafoDeSimilitud.agregarCancion(c); // registra nodo en grafo
                }
                indexTitles(loadedSongs); // indexar títulos en trie
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Nota: si guardas aristas del grafo, reconstruirlas aquí
    }

    /**
     * Indexa una lista de canciones en el trie (autocompletado).
     */
    public void indexTitles(List<Cancion> canciones) {
        if (canciones == null) return;
        for (Cancion c : canciones) {
            if (c != null && c.getTitulo() != null) trie.insertar(c.getTitulo());
        }
    }

    // ---------------- Usuarios ----------------

    /**
     * Registra un usuario y persiste todos los usuarios en disco.
     * @return true si se agregó correctamente (username único).
     */
    public boolean registerUser(Usuario u) {
        if (userRepository.contains(u.getUsername())) {
            return false;
        }

        userRepository.add(u);

        userRepository.persist(); // guarda en users.txt

        return true;
    }




    // ---------------- Canciones ----------------

    /**
     * Agrega una canción al catálogo y persiste el catálogo.
     * Lanzará IllegalArgumentException si el id ya existe (songCatalog.add lo maneja).
     */
    public void addSong(Cancion c) {
        boolean added = songCatalog.add(c);
        if (!added) throw new IllegalArgumentException("ID existente: " + c.getId());
        // indexar y registrar en grafo
        if (c.getTitulo() != null) trie.insertar(c.getTitulo());
        grafoDeSimilitud.agregarCancion(c);
        DataManager.saveSongs(songCatalog.list());
    }

    /**
     * Actualiza una canción en el catálogo y persiste.
     * Lanzará IllegalArgumentException si el id no existe.
     */
    public void updateSong(Cancion c) {
        boolean updated = songCatalog.update(c);
        if (!updated) throw new IllegalArgumentException("ID no existe: " + c.getId());
        if (c.getTitulo() != null) trie.insertar(c.getTitulo());
        DataManager.saveSongs(songCatalog.list());
    }

    /**
     * Elimina una canción por id y persiste.
     */
    public void deleteSong(String id) {
        boolean removed = songCatalog.remove(id);
        if (!removed) throw new IllegalArgumentException("ID no existe: " + id);
        // Nota: si quieres eliminar del trie o del grafo, hazlo aquí.
        DataManager.saveSongs(songCatalog.list());
    }
}
