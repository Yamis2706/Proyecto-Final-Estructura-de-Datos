package co.edu.uniquindio.co.proyecto.service;

import co.edu.uniquindio.co.proyecto.model.Usuario;
import co.edu.uniquindio.co.proyecto.repository.UserRepository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<Usuario> authenticate(String username, String password) {
        return userRepository.get(username)
                .filter(u -> (u.getPassword() == null && (password == null || password.isBlank())) || (u.getPassword() != null && u.getPassword().equals(password)));
    }

    public Usuario register(String username, String password, String nombre, Usuario.Role role) {
        Usuario u = new Usuario(username, password, nombre, role);
        userRepository.add(u);
        return u;
    }




}


