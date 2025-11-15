package co.edu.uniquindio.co.proyecto.repository;

import co.edu.uniquindio.co.proyecto.model.Usuario;
import co.edu.uniquindio.co.proyecto.model.Cancion;

import java.io.*;
import java.util.*;

public class DataManager {

    private static final String USERS_FILE = "users.csv";
    private static final String SONGS_FILE = "songs.csv";

    // -------------------------------------------------------------------------
    //   USUARIOS
    // -------------------------------------------------------------------------

    public static Map<String, Usuario> loadUsers() {
        Map<String, Usuario> map = new HashMap<>();

        File file = new File(USERS_FILE);
        if (!file.exists()) return map;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] p = line.split(";");
                if (p.length < 4) continue;

                String username = p[0].trim();
                String pass = p[1].trim();
                String nombre = p[2].trim();
                String roleStr = p[3].trim();

                Usuario u = new Usuario(username, pass, nombre);

                // Conversión String → Enum Role
                try {
                    u.setRole(Usuario.Role.valueOf(roleStr.toUpperCase()));
                } catch (Exception e) {
                    u.setRole(Usuario.Role.USER);
                }

                map.put(username, u);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    public static void saveUsers(Map<String, Usuario> users) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(USERS_FILE))) {

            for (Usuario u : users.values()) {
                pw.println(u.getUsername() + ";" +
                        u.getPassword() + ";" +
                        u.getNombre() + ";" +
                        u.getRole());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // -------------------------------------------------------------------------
    //   CANCIONES
    // -------------------------------------------------------------------------

    public static List<Cancion> loadSongs() {
        List<Cancion> list = new ArrayList<>();

        File file = new File(SONGS_FILE);
        if (!file.exists()) return list;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] p = line.split(";");
                if (p.length < 6) continue;

                String id = p[0];
                String titulo = p[1];
                String artista = p[2];
                String genero = p[3];
                int anio = Integer.parseInt(p[4]);
                int dur = Integer.parseInt(p[5]);

                Cancion c = new Cancion(id, titulo, artista, genero, anio, dur);
                list.add(c);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static void saveSongs(List<Cancion> songs) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(SONGS_FILE))) {

            for (Cancion c : songs) {
                pw.println(
                        c.getId() + ";" +
                                c.getTitulo() + ";" +
                                c.getArtista() + ";" +
                                c.getGenero() + ";" +
                                c.getAnio() + ";" +
                                c.getDuracionSegundos()
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
