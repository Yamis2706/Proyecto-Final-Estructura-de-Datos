package co.edu.uniquindio.co.proyecto.repository;

import co.edu.uniquindio.co.proyecto.model.Usuario;

import java.util.*;

public class UserRepository {

    private final Map<String, Usuario> usuarios = new HashMap<>();

    // Agregar usuario
    public boolean add(Usuario u) {
        if (usuarios.containsKey(u.getUsername())) return false;
        usuarios.put(u.getUsername(), u);
        return true;
    }

    // Obtener usuario por username
    public Optional<Usuario> get(String username) {
        return Optional.ofNullable(usuarios.get(username));
    }

    // Eliminar usuario por username
    public boolean remove(String username) {
        return usuarios.remove(username) != null;
    }

    // Listar todos los usuarios como List
    public List<Usuario> list() {
        return new ArrayList<>(usuarios.values());
    }

    // Retornar mapa completo (opcional)
    public Map<String, Usuario> getAllUsers() {
        return usuarios;
    }

    // Retornar cantidad de usuarios
    public int size() {
        return usuarios.size();
    }
}