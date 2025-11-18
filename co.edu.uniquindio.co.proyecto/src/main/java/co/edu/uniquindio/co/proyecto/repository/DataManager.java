package co.edu.uniquindio.co.proyecto.repository;

import co.edu.uniquindio.co.proyecto.model.Cancion;
import co.edu.uniquindio.co.proyecto.model.Usuario;

import java.io.File;
import java.io.PrintWriter;
import java.util.*;
import java.util.Scanner;

public class DataManager {

    private static Map<String, Usuario> usuarios = new HashMap<>();

    static {
        Map<String, Usuario> loaded = loadUsers();
        if (loaded != null && !loaded.isEmpty()) {
            usuarios.putAll(loaded);
        }
    }

    // REGISTRAR USUARIO
    public static boolean registerUser(Usuario usuario) {
        if (usuarios.containsKey(usuario.getUsername())) return false;

        usuarios.put(usuario.getUsername(), usuario);
        saveUsers(usuarios);
        return true;
    }

    // LOGIN
    public static Usuario login(String username, String password) {
        Usuario user = usuarios.get(username);
        if (user != null && user.getPassword().equals(password)) return user;
        return null;
    }

    // OBTENER MAPA COMPLETO
    public static Map<String, Usuario> getUsuarios() {
        return usuarios;
    }

    // GUARDAR USUARIOS
    public static void saveUsers(Map<String, Usuario> usuarios) {
        try (PrintWriter writer = new PrintWriter("usuarios.txt")) {
            for (Usuario u : usuarios.values()) {
                writer.println(u.getId() + ";" + u.getUsername() + ";" + u.getPassword() + ";" + u.getNombre() + ";" + u.getRole());
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    // CARGAR USUARIOS
    public static Map<String, Usuario> loadUsers() {
        Map<String, Usuario> loaded = new HashMap<>();
        File file = new File("usuarios.txt");
        if (!file.exists()) return loaded;

        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] partes = line.split(";");
                if (partes.length == 5) {
                    Usuario u = new Usuario(partes[0], partes[1], partes[2], partes[3], Usuario.Role.valueOf(partes[4]));
                    loaded.put(u.getUsername(), u);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return loaded;
    }

    // GUARDAR CANCIONES
    public static void saveSongs(List<Cancion> canciones) {
        try (PrintWriter writer = new PrintWriter("canciones.txt")) {
            for (Cancion c : canciones) {
                writer.println(c.getId() + ";" + c.getTitulo() + ";" + c.getArtista() + ";" + c.getGenero() + ";" + c.getAnio() + ";" + c.getDuracionSegundos());
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    // CARGAR CANCIONES
    public static List<Cancion> loadSongs() {
        List<Cancion> songs = new ArrayList<>();
        File file = new File("canciones.txt");
        if (!file.exists()) return songs;

        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] partes = line.split(";");
                if (partes.length == 6) {
                    Cancion c = new Cancion(partes[0], partes[1], partes[2], partes[3], Integer.parseInt(partes[4]), Integer.parseInt(partes[5]));
                    songs.add(c);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return songs;
    }
}
