package co.edu.uniquindio.co.proyecto.repository;

import co.edu.uniquindio.co.proyecto.model.Usuario;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class UserRepository {
    private final Map<String, Usuario> usernameToUser = new HashMap<>();

    public boolean add(Usuario usuario) {
        if (usuario == null) return false;
        return usernameToUser.putIfAbsent(usuario.getUsername(), usuario) == null;
    }

    public Optional<Usuario> get(String username) {
        return Optional.ofNullable(usernameToUser.get(username));
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
}


