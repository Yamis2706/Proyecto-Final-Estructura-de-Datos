package co.edu.uniquindio.co.proyecto.service;

import co.edu.uniquindio.co.proyecto.model.Cancion;
import co.edu.uniquindio.co.proyecto.model.Usuario;

import java.util.*;
import java.util.stream.Collectors;

public class RecommendationService {

    /**
     * Descubrimiento semanal basado en favoritos del usuario.
     * Lógica:
     * - Score por canción: +2 si artista coincide con alguno de los favoritos, +1 si género coincide.
     * - Excluir canciones ya en favoritos.
     * - Ordenar por score descendente, luego por presencia en catálogo (estable).
     * - Devolver hasta 'limite' canciones (si limite <= 0 devuelve 1 mínimo).
     *
     * Firma y comportamiento ajustados a los tests del usuario.
     */
    public List<Cancion> descubrimientoSemanal(Usuario usuario, List<Cancion> catalogo, int limite) {
        if (usuario == null || catalogo == null || catalogo.isEmpty()) return Collections.emptyList();
        Set<String> favoritosIds = usuario.getListaFavoritos().stream().map(Cancion::getId).collect(Collectors.toSet());
        if (favoritosIds.isEmpty()) {
            // si no tiene favoritos, devolver top por catálogo limitado
            return catalogo.stream().filter(c -> !favoritosIds.contains(c.getId())).limit(Math.max(1, limite)).collect(Collectors.toList());
        }

        // perfilar artistas y géneros de favoritos
        Set<String> artistasFavoritos = usuario.getListaFavoritos().stream().map(Cancion::getArtista).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<String> generosFavoritos = usuario.getListaFavoritos().stream().map(Cancion::getGenero).filter(Objects::nonNull).collect(Collectors.toSet());

        Map<String, Integer> score = new HashMap<>();
        for (Cancion c : catalogo) {
            if (favoritosIds.contains(c.getId())) continue; // excluir favoritos
            int s = 0;
            if (c.getArtista() != null && artistasFavoritos.contains(c.getArtista())) s += 2;
            if (c.getGenero() != null && generosFavoritos.contains(c.getGenero())) s += 1;
            if (s > 0) score.put(c.getId(), s);
        }

        List<Cancion> ranked = catalogo.stream()
                .filter(c -> score.containsKey(c.getId()))
                .sorted((a, b) -> {
                    int sa = score.get(a.getId());
                    int sb = score.get(b.getId());
                    if (sb != sa) return Integer.compare(sb, sa); // desc
                    return a.getId().compareTo(b.getId()); // estable
                })
                .limit(Math.max(1, limite))
                .collect(Collectors.toList());

        return ranked;
    }
}
