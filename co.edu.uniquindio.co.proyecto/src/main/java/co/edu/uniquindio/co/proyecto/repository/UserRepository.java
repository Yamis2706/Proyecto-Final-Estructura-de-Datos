package co.edu.uniquindio.co.proyecto.repository;

import co.edu.uniquindio.co.proyecto.model.Usuario;

import java.util.*;

public class UserRepository {

    // 🌟 ESTE es tu mapa real (corrige tu error del "map" inexistente)
    private final Map<String, Usuario> usernameToUser = new HashMap<>();

    // ----------------------- CRUD -----------------------

    public boolean add(Usuario usuario) {
        if (usuario == null || usuario.getUsername() == null) return false;
        return usernameToUser.putIfAbsent(usuario.getUsername(), usuario) == null;
    }

    public Optional<Usuario> get(String username) {
        return Optional.ofNullable(usernameToUser.get(username));
    }

    public boolean contains(String username) {
        return usernameToUser.containsKey(username);
    }

    public boolean remove(String username) {
        return usernameToUser.remove(username) != null;
    }

    public Collection<Usuario> list() {
        return usernameToUser.values();
    }

    public int size() {
        return usernameToUser.size();
    }

    // ------------------ Utilidades ------------------

    /** Retorna copia del mapa (para DataManager). */
    public Map<String, Usuario> asMap() {
        return new HashMap<>(usernameToUser);
    }

    /** Lista de usuarios (copia). */
    public List<Usuario> getAll() {
        return new ArrayList<>(usernameToUser.values());
    }

    /** Elimina por username. */
    public void delete(String username) {
        usernameToUser.remove(username);
    }

    /** Persiste en users.txt */
    public void persist() {
        DataManager.saveUsers(usernameToUser);
    }
}
