package co.edu.uniquindio.co.proyecto.service;

import co.edu.uniquindio.co.proyecto.model.Usuario;
import co.edu.uniquindio.co.proyecto.repository.UserRepository;

import java.util.Optional;

public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<Usuario> authenticate(String username, String password) {
        return userRepository.get(username)
                .filter(u -> u.getPassword().equals(password));
    }

    public Usuario register(String username, String password, String nombre, Usuario.Role role) {
        String id = java.util.UUID.randomUUID().toString();
        Usuario u = new Usuario(id, username, password, nombre, role);
        userRepository.add(u);
        return u;
    }
}
