package co.edu.uniquindio.co.proyecto.service;

import co.edu.uniquindio.co.proyecto.model.Cancion;
import co.edu.uniquindio.co.proyecto.model.Usuario;

import java.util.*;
import java.util.stream.Collectors;

public class RecommendationService {

    public List<Cancion> descubrimientoSemanal(Usuario usuario, List<Cancion> catalogo, int limite) {
        if (usuario == null || catalogo == null || catalogo.isEmpty()) return Collections.emptyList();
        Set<String> favoritosIds = usuario.getListaFavoritos().stream().map(Cancion::getId).collect(Collectors.toSet());
        Map<String, Integer> score = new HashMap<>();

        // perfila gustos por artista y género en favoritos
        Set<String> artistas = usuario.getListaFavoritos().stream().map(Cancion::getArtista).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<String> generos = usuario.getListaFavoritos().stream().map(Cancion::getGenero).filter(Objects::nonNull).collect(Collectors.toSet());

        for (Cancion c : catalogo) {
            if (favoritosIds.contains(c.getId())) continue;
            int s = 0;
            if (c.getArtista() != null && artistas.contains(c.getArtista())) s += 2;
            if (c.getGenero() != null && generos.contains(c.getGenero())) s += 1;
            if (s > 0) score.put(c.getId(), s);
        }

        return catalogo.stream()
                .filter(c -> score.containsKey(c.getId()))
                .sorted((a, b) -> Integer.compare(score.get(b.getId()), score.get(a.getId())))
                .limit(Math.max(1, limite))
                .collect(Collectors.toList());
    }
}


