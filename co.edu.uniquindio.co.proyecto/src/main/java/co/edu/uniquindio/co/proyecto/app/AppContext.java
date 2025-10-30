package co.edu.uniquindio.co.proyecto.app;

import co.edu.uniquindio.co.proyecto.catalog.SongCatalog;
import co.edu.uniquindio.co.proyecto.ds.TrieAutocompletado;
import co.edu.uniquindio.co.proyecto.graph.GrafoDeSimilitud;
import co.edu.uniquindio.co.proyecto.graph.GrafoSocial;
import co.edu.uniquindio.co.proyecto.model.Cancion;
import co.edu.uniquindio.co.proyecto.repository.UserRepository;
import co.edu.uniquindio.co.proyecto.service.CsvService;
import co.edu.uniquindio.co.proyecto.service.RadioService;
import co.edu.uniquindio.co.proyecto.service.RecommendationService;
import co.edu.uniquindio.co.proyecto.service.SearchService;

import java.util.List;

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

    private AppContext() { }

    public void indexTitles(List<Cancion> canciones) {
        if (canciones == null) return;
        for (Cancion c : canciones) {
            if (c != null && c.getTitulo() != null) {
                trie.insertar(c.getTitulo());
            }
        }
    }
}


