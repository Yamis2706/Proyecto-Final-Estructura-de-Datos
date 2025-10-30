package co.edu.uniquindio.co.proyecto;

import co.edu.uniquindio.co.proyecto.catalog.SongCatalog;
import co.edu.uniquindio.co.proyecto.ds.TrieAutocompletado;
import co.edu.uniquindio.co.proyecto.graph.GrafoDeSimilitud;
import co.edu.uniquindio.co.proyecto.graph.GrafoSocial;
import co.edu.uniquindio.co.proyecto.model.Cancion;
import co.edu.uniquindio.co.proyecto.model.Usuario;
import co.edu.uniquindio.co.proyecto.repository.UserRepository;
import co.edu.uniquindio.co.proyecto.service.CsvService;
import co.edu.uniquindio.co.proyecto.service.RecommendationService;
import co.edu.uniquindio.co.proyecto.service.SearchService;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class AllCoreTests {

    @Test
    void trie_autocompleta_por_prefijo() {
        TrieAutocompletado trie = new TrieAutocompletado();
        trie.insertar("hello");
        trie.insertar("helios");
        trie.insertar("hero");
        List<String> res = trie.buscarPorPrefijo("he");
        assertTrue(res.contains("hello"));
        assertTrue(res.contains("helios"));
        assertTrue(res.contains("hero"));
    }

    @Test
    void grafoSimilitud_dijkstra_camino_mas_corto() {
        Cancion a = new Cancion("1","a","x","g",2020,100);
        Cancion b = new Cancion("2","b","x","g",2020,100);
        Cancion c = new Cancion("3","c","y","g",2020,100);
        GrafoDeSimilitud g = new GrafoDeSimilitud();
        g.conectar(a,b,1);
        g.conectar(b,c,1);
        g.conectar(a,c,5);
        GrafoDeSimilitud.Ruta r = g.dijkstra(a,c);
        assertEquals(2.0, r.getCostoTotal(), 0.0001);
        assertEquals(Arrays.asList(a,b,c), r.getCamino());
    }

    @Test
    void grafoSocial_bfs_y_sugerencias() {
        Usuario u1 = new Usuario("u1","p","U1");
        Usuario u2 = new Usuario("u2","p","U2");
        Usuario u3 = new Usuario("u3","p","U3");
        Usuario u4 = new Usuario("u4","p","U4");
        GrafoSocial gs = new GrafoSocial();
        gs.conectar(u1,u2);
        gs.conectar(u2,u3);
        gs.conectar(u3,u4);
        List<Usuario> bfs = gs.bfs(u1);
        assertEquals(4, bfs.size());
        Set<Usuario> suger = gs.sugerirSegundosGrados(u1);
        assertTrue(suger.contains(u3));
        assertFalse(suger.contains(u2));
    }

    @Test
    void userRepository_operaciones_basicas() {
        UserRepository repo = new UserRepository();
        Usuario u = new Usuario("ana","p","Ana");
        assertTrue(repo.add(u));
        assertTrue(repo.get("ana").isPresent());
        assertTrue(repo.remove("ana"));
        assertEquals(0, repo.size());
    }

    @Test
    void searchService_busqueda_avanzada_AND_y_OR() {
        Cancion a = new Cancion("1","t1","art1","rock",2020,100);
        Cancion b = new Cancion("2","t2","art2","pop",2021,100);
        Cancion c = new Cancion("3","t3","art1","pop",2021,100);
        List<Cancion> base = Arrays.asList(a,b,c);
        SearchService svc = new SearchService();
        SearchService.Criteria cr = new SearchService.Criteria();
        cr.artista = "art1";
        cr.genero = "pop";
        // AND
        List<Cancion> andRes = svc.advancedSearch(base, cr, SearchService.Logic.AND);
        assertEquals(1, andRes.size());
        assertEquals("3", andRes.get(0).getId());
        // OR
        List<Cancion> orRes = svc.advancedSearch(base, cr, SearchService.Logic.OR);
        assertEquals(3, orRes.size());
        svc.shutdown();
    }

    @Test
    void recommendation_descubrimiento_semanal() {
        Usuario u = new Usuario("ana","p","Ana");
        Cancion f1 = new Cancion("f1","tf1","A","rock",2020,100);
        u.agregarFavorito(f1);
        List<Cancion> catalogo = Arrays.asList(
                f1,
                new Cancion("x","tx","A","pop",2021,100),
                new Cancion("y","ty","B","rock",2022,100),
                new Cancion("z","tz","C","jazz",2023,100)
        );
        RecommendationService rs = new RecommendationService();
        List<Cancion> recs = rs.descubrimientoSemanal(u, catalogo, 10);
        assertTrue(recs.stream().anyMatch(c -> c.getArtista().equals("A")));
        assertTrue(recs.stream().anyMatch(c -> c.getGenero().equals("rock")));
        assertTrue(recs.stream().noneMatch(c -> c.getId().equals("f1")));
    }

    @Test
    void csv_export_generates_header_and_rows() {
        CsvService csv = new CsvService();
        List<Cancion> songs = Arrays.asList(
                new Cancion("1","t1","a1","g1",2020,100),
                new Cancion("2","t2","a2","g2",2021,110)
        );
        String out = csv.toCsv(songs);
        assertTrue(out.startsWith("id,titulo,artista,genero,anio,duracionSegundos"));
        assertTrue(out.contains("1,t1,a1,g1,2020,100"));
        assertTrue(out.contains("2,t2,a2,g2,2021,110"));
    }

    @Test
    void catalog_add_update_remove_find() {
        SongCatalog cat = new SongCatalog();
        Cancion c = new Cancion("1","t","a","g",2020,100);
        assertTrue(cat.add(c));
        assertTrue(cat.get("1").isPresent());
        c.setTitulo("t2");
        assertTrue(cat.update(c));
        assertEquals("t2", cat.get("1").get().getTitulo());
        assertTrue(cat.remove("1"));
        assertFalse(cat.get("1").isPresent());
    }
}


