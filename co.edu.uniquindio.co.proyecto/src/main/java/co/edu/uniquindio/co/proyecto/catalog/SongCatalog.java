package co.edu.uniquindio.co.proyecto.catalog;

import co.edu.uniquindio.co.proyecto.model.Cancion;

import java.util.*;
import java.util.stream.Collectors;

public class SongCatalog {
    private final Map<String, Cancion> idToSong = new HashMap<>();

    public boolean add(Cancion c) {
        if (c == null) return false;
        return idToSong.putIfAbsent(c.getId(), c) == null;
    }

    public boolean update(Cancion c) {
        if (c == null) return false;
        if (!idToSong.containsKey(c.getId())) return false;
        idToSong.put(c.getId(), c);
        return true;
    }

    public boolean remove(String id) {
        return idToSong.remove(id) != null;
    }

    public Optional<Cancion> get(String id) {
        return Optional.ofNullable(idToSong.get(id));
    }

    public List<Cancion> list() {
        return new ArrayList<>(idToSong.values());
    }

    public List<Cancion> findByTitleContains(String query) {
        if (query == null || query.isBlank()) return Collections.emptyList();
        String q = query.toLowerCase();
        return idToSong.values().stream()
                .filter(c -> c.getTitulo() != null && c.getTitulo().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }
}


