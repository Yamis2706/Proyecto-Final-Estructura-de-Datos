package co.edu.uniquindio.co.proyecto.repository;

import co.edu.uniquindio.co.proyecto.model.Cancion;
import co.edu.uniquindio.co.proyecto.model.Usuario;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class DataManager {

    // =======================================
    //   ESTRUCTURA PRINCIPAL DE USUARIOS
    // =======================================
    private static Map<String, Usuario> usuarios = new HashMap<>();


    // =======================================
    //   CARGA INICIAL DE USUARIOS (opcional)
    // =======================================
    static {
        Map<String, Usuario> loaded = loadUsers();
        if (loaded != null && !loaded.isEmpty()) {
            usuarios.putAll(loaded);
        }
    }


    // =======================================
    //   REGISTRAR USUARIO
    // =======================================
    public static boolean registerUser(Usuario usuario) {
        if (usuarios.containsKey(usuario.getUsername())) {
            return false; // Usuario ya existe
        }

        usuarios.put(usuario.getUsername(), usuario);
        saveUsers(usuarios); // persistir en disco

        return true;
    }


    // =======================================
    //   LOGIN
    // =======================================
    public static Usuario login(String username, String password) {
        Usuario user = usuarios.get(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }


    // =======================================
    //   GET MAPA DE USUARIOS
    // =======================================
    public static Map<String, Usuario> getUsuarios() {
        return usuarios;
    }


    // =======================================
    //   GUARDAR USUARIOS EN ARCHIVO
    // =======================================
    public static void saveUsers(Map<String, Usuario> usuarios) {
        try (PrintWriter writer = new PrintWriter("usuarios.txt")) {

            for (Usuario u : usuarios.values()) {
                writer.println(
                        u.getId() + ";" +
                                u.getUsername() + ";" +
                                u.getPassword() + ";" +
                                u.getNombre() + ";" +
                                u.getRole()
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // =======================================
    //   CARGAR USUARIOS DESDE ARCHIVO
    // =======================================
    public static Map<String, Usuario> loadUsers() {

        Map<String, Usuario> loaded = new HashMap<>();
        File file = new File("usuarios.txt");

        if (!file.exists()) {
            return loaded; // devuelve mapa vacío
        }

        try (Scanner sc = new Scanner(file)) {

            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] partes = line.split(";");

                if (partes.length == 5) {

                    Usuario u = new Usuario(
                            partes[0],                        // id
                            partes[1],                        // username
                            partes[2],                        // password
                            partes[3],                        // nombre
                            Usuario.Role.valueOf(partes[4])   // role
                    );

                    loaded.put(u.getUsername(), u);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return loaded;
    }


    // =======================================
    //   GUARDAR CANCIONES EN ARCHIVO
    // =======================================
    public static void saveSongs(List<Cancion> canciones) {
        try (PrintWriter writer = new PrintWriter("canciones.txt")) {

            for (Cancion c : canciones) {
                writer.println(
                        c.getId() + ";" +
                                c.getTitulo() + ";" +
                                c.getArtista() + ";" +
                                c.getGenero() + ";" +
                                c.getAnio() + ";" +
                                c.getDuracion()
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // =======================================
    //   CARGAR CANCIONES DESDE ARCHIVO
    // =======================================
    public static List<Cancion> loadSongs() {

        List<Cancion> songs = new ArrayList<>();
        File file = new File("canciones.txt");

        if (!file.exists()) return songs;

        try (Scanner sc = new Scanner(file)) {

            while (sc.hasNextLine()) {

                String line = sc.nextLine();
                String[] partes = line.split(";");

                if (partes.length == 6) {
                    Cancion c = new Cancion(
                            partes[0],                           // id
                            partes[1],                           // título
                            partes[2],                           // artista
                            partes[3],                           // género
                            Integer.parseInt(partes[4]),         // año
                            Double.parseDouble(partes[5])        // duración
                    );

                    songs.add(c);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return songs;
    }
}
