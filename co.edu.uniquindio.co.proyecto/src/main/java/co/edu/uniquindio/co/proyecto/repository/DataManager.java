package co.edu.uniquindio.co.proyecto.repository;

import co.edu.uniquindio.co.proyecto.model.Cancion;
import co.edu.uniquindio.co.proyecto.model.Usuario;

import java.io.*;
import java.util.*;

public class DataManager {

    private static final String USERS_FILE = "users.csv";
    private static final String SONGS_FILE = "songs.csv";

    // ------------------ USUARIOS ------------------

    // dentro de co.edu.uniquindio.co.proyecto.repository.DataManager
    public static void saveUsers(Map<String, Usuario> users) {
        try (PrintWriter pw = new PrintWriter(new FileWriter("users.csv"))) {
            for (Usuario u : users.values()) {
                pw.println(
                        u.getId() + ";" +
                                u.getUsername() + ";" +
                                u.getPassword() + ";" +
                                u.getNombre() + ";" +
                                u.getRole().name()
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static Map<String, Usuario> loadUsers() {
        Map<String, Usuario> map = new HashMap<>();

        try (BufferedReader br =
                     new BufferedReader(new FileReader("users.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(";");
                if (p.length == 5) {
                    String id       = p[0];
                    String username = p[1];
                    String password = p[2];
                    String nombre   = p[3];
                    Usuario.Role role = Usuario.Role.valueOf(p[4]);

                    Usuario u = new Usuario(id, username, password, nombre, role);
                    map.put(username, u);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }



    // ------------------ CANCIONES ------------------

    public static void saveSongs(List<Cancion> songs) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(SONGS_FILE))) {
            for (Cancion c : songs) {
                pw.println(c.getId() + ";" +
                        c.getTitulo() + ";" +
                        c.getArtista() + ";" +
                        c.getGenero() + ";" +
                        c.getAnio() + ";" +
                        c.getDuracionSegundos());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Cancion> loadSongs() {
        List<Cancion> songs = new ArrayList<>();

        File file = new File(SONGS_FILE);
        if (!file.exists()) return songs;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(";");
                if (p.length == 6) {
                    songs.add(new Cancion(
                            p[0],                // id
                            p[1],                // titulo
                            p[2],                // artista
                            p[3],                // genero
                            Integer.parseInt(p[4]),
                            Integer.parseInt(p[5])
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return songs;
    }
}
