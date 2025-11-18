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
    private final Path storePath;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
        String home = System.getProperty("user.home");
        Path dir = Path.of(home, ".syncup");
        this.storePath = dir.resolve("users.csv");
        try {
            Files.createDirectories(dir);
        } catch (IOException ignored) { }
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

    public void load() {
        if (!Files.exists(storePath)) return;
        try {
            List<String> lines = Files.readAllLines(storePath, StandardCharsets.UTF_8);
            for (String line : lines) {
                if (line.isBlank() || line.startsWith("#")) continue;
                String[] p = line.split(",", -1);
                if (p.length < 4) continue;
                String username = unescape(p[0]);
                String password = unescape(p[1]);
                String nombre = unescape(p[2]);
                Usuario.Role role = Usuario.Role.valueOf(unescape(p[3]));
                userRepository.add(new Usuario(username, password, nombre, role));
            }
        } catch (IOException ignored) { }
    }

    public void save() throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("#username,password,nombre,role\n");
        for (Usuario u : userRepository.list()) {
            sb.append(escape(u.getUsername())).append(',')
                    .append(escape(u.getPassword())).append(',')
                    .append(escape(u.getNombre())).append(',')
                    .append(u.getRole().name()).append('\n');
        }
        Files.writeString(storePath, sb.toString(), StandardCharsets.UTF_8);
    }

    private String escape(String v) {
        if (v == null) return "";
        return v.replace("\\", "\\\\").replace(",", "\\,");
    }
    private String unescape(String v) {
        StringBuilder out = new StringBuilder();
        boolean esc = false;
        for (char c : v.toCharArray()) {
            if (esc) { out.append(c); esc = false; }
            else if (c == '\\') esc = true;
            else out.append(c);
        }
        return out.toString();
    }
}