package co.edu.uniquindio.co.proyecto.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Usuario {

    private String id;
    private String username;
    private String password;
    private String nombre;
    private Role role;


    public Usuario(String id, String username, String password, String nombre, Role role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.nombre = nombre;
        this.role = role;
    }




    // ⭐ FAVORITOS
    private final LinkedList<Cancion> listaFavoritos = new LinkedList<>();

    public enum Role { USER, ADMIN }




    // ------------ FAVORITOS --------------------

    public List<Cancion> getListaFavoritos() {
        return listaFavoritos;
    }

    public void agregarFavorito(Cancion c) {
        if (c != null && !listaFavoritos.contains(c)) {
            listaFavoritos.add(c);
        }
    }

    public void eliminarFavorito(Cancion c) {
        listaFavoritos.remove(c);
    }

    // ------------ GETTERS Y SETTERS ------------

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    // ------------ EQUALS & HASHCODE ------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Usuario)) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(username, usuario.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}
