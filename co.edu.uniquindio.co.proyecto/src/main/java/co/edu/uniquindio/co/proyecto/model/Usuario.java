package co.edu.uniquindio.co.proyecto.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Usuario {
    public enum Role { USER, ADMIN }

    private final String username;
    private String password;
    private String nombre;
    private Role role = Role.USER;
    private final LinkedList<Cancion> listaFavoritos;

    public Usuario(String username, String password, String nombre) {
        if (username == null || username.isBlank()) throw new IllegalArgumentException("username no puede ser nulo");
        this.username = username;
        this.password = password;
        this.nombre = nombre;
        this.listaFavoritos = new LinkedList<>();
    }

    public Usuario(String username, String password, String nombre, Role role) {
        this(username, password, nombre);
        this.role = role == null ? Role.USER : role;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role == null ? Role.USER : role; }

    // ---- Favoritos API compatible con tests ----
    public void agregarFavorito(Cancion cancion) {
        if (cancion == null) return;
        if (!listaFavoritos.contains(cancion)) listaFavoritos.add(cancion);
    }

    public boolean eliminarFavorito(Cancion cancion) {
        if (cancion == null) return false;
        return listaFavoritos.remove(cancion);
    }

    /**
     * Nombre usado por tests y por código legado: getListaFavoritos()
     */
    public List<Cancion> getListaFavoritos() {
        return List.copyOf(listaFavoritos);
    }

    /**
     * Alias moderno: getFavoritos()
     */
    public LinkedList<Cancion> getFavoritos() {
        return listaFavoritos;
    }

    // ---- equals & hashCode (por username) ----
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Usuario)) return false;
        Usuario usuario = (Usuario) o;
        return username.equals(usuario.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    @Override
    public String toString() {
        return "Usuario{" + "username='" + username + '\'' + ", nombre='" + nombre + '\'' + ", role=" + role + '}';
    }
}
