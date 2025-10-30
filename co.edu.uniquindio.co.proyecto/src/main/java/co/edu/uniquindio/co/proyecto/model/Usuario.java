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
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("username no puede ser nulo o vacío");
        }
        this.username = username;
        this.password = password;
        this.nombre = nombre;
        this.listaFavoritos = new LinkedList<>();
    }

    public Usuario(String username, String password, String nombre, Role role) {
        this(username, password, nombre);
        this.role = role == null ? Role.USER : role;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Role getRole() { return role; }

    public void setRole(Role role) { this.role = role == null ? Role.USER : role; }

    public LinkedList<Cancion> getListaFavoritos() {
        return listaFavoritos;
    }

    public void agregarFavorito(Cancion cancion) {
        if (cancion != null && !listaFavoritos.contains(cancion)) {
            listaFavoritos.add(cancion);
        }
    }

    public boolean eliminarFavorito(Cancion cancion) {
        return listaFavoritos.remove(cancion);
    }

    public List<Cancion> listarFavoritos() {
        return List.copyOf(listaFavoritos);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return username.equals(usuario.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "username='" + username + '\'' +
                ", nombre='" + nombre + '\'' +
                ", favoritos=" + listaFavoritos.size() +
                '}';
    }
}


