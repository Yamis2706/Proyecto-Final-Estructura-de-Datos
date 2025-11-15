package co.edu.uniquindio.co.proyecto.catalog;

import co.edu.uniquindio.co.proyecto.model.Cancion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Catálogo de canciones del sistema.
 * Soporta agregar, actualizar, eliminar y consultar canciones.
 */
public class SongCatalog {

    // Lista principal de canciones
    private final List<Cancion> list = new ArrayList<>();

    // -------------------------------------------------------------------------
    //   GETTERS Y SETTERS PARA PERSISTENCIA
    // -------------------------------------------------------------------------

    /**
     * Devuelve la lista de canciones (copia para seguridad).
     */
    public List<Cancion> getCanciones() {
        return new ArrayList<>(list);
    }

    /**
     * Reemplaza completamente el catálogo (para cargar desde archivo).
     */
    public void setCanciones(List<Cancion> canciones) {
        list.clear();
        if (canciones != null) {
            list.addAll(canciones);
        }
    }

    public void clear() {
        list.clear();
    }

    // -------------------------------------------------------------------------
    //   CONSULTAS
    // -------------------------------------------------------------------------

    /**
     * Devuelve una vista directa de la lista (solo lectura recomendada).
     */
    public List<Cancion> list() {
        return list;
    }

    /**
     * Devuelve una canción por id.
     */
    public Optional<Cancion> get(String id) {
        if (id == null) return Optional.empty();
        return list.stream().filter(c -> c.getId().equals(id)).findFirst();
    }

    /**
     * Verifica si existe una canción con ese ID.
     */
    public boolean existsId(String id) {
        return get(id).isPresent();
    }

    // -------------------------------------------------------------------------
    //   AGREGAR
    // -------------------------------------------------------------------------

    /**
     * Agrega una nueva canción si el ID no existe.
     * @return true si se agregó, false si ya existe ese id.
     */
    public boolean add(Cancion c) {
        if (c == null || c.getId() == null) return false;
        if (existsId(c.getId())) return false;
        list.add(c);
        return true;
    }

    // -------------------------------------------------------------------------
    //   ACTUALIZAR
    // -------------------------------------------------------------------------

    /**
     * Actualiza una canción existente con mismo ID.
     * @return true si se modificó, false si no existe.
     */
    public boolean update(Cancion newSong) {
        if (newSong == null || newSong.getId() == null) return false;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equals(newSong.getId())) {
                list.set(i, newSong);
                return true;
            }
        }
        return false;
    }

    // -------------------------------------------------------------------------
    //   ELIMINAR
    // -------------------------------------------------------------------------

    /**
     * Elimina una canción por id.
     * @return true si se eliminó, false si no existe.
     */
    public boolean remove(String id) {
        if (id == null) return false;
        return list.removeIf(c -> c.getId().equals(id));
    }

    // -------------------------------------------------------------------------
    //   BÚSQUEDA BÁSICA (para UserController)
    // -------------------------------------------------------------------------

    /**
     * Busca canciones cuyo título o artista contengan el texto.
     */
    public List<Cancion> findByTitleOrArtistContains(String texto) {
        if (texto == null || texto.isBlank()) return Collections.emptyList();

        String t = texto.toLowerCase();

        List<Cancion> result = new ArrayList<>();
        for (Cancion c : list) {
            if (c == null) continue;

            boolean matchesTitle =
                    c.getTitulo() != null && c.getTitulo().toLowerCase().contains(t);
            boolean matchesArtist =
                    c.getArtista() != null && c.getArtista().toLowerCase().contains(t);

            if (matchesTitle || matchesArtist) {
                result.add(c);
            }
        }
        return result;
    }
}
