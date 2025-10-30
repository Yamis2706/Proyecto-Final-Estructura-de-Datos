package co.edu.uniquindio.co.proyecto.service;

import co.edu.uniquindio.co.proyecto.model.Cancion;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class CsvService {

    public String toCsv(List<Cancion> canciones) {
        StringBuilder sb = new StringBuilder();
        sb.append("id,titulo,artista,genero,anio,duracionSegundos\n");
        if (canciones != null) {
            for (Cancion c : canciones) {
                if (c == null) continue;
                sb.append(escape(c.getId())).append(',')
                        .append(escape(c.getTitulo())).append(',')
                        .append(escape(c.getArtista())).append(',')
                        .append(escape(c.getGenero())).append(',')
                        .append(c.getAnio()).append(',')
                        .append(c.getDuracionSegundos()).append('\n');
            }
        }
        return sb.toString();
    }

    public void writeToFile(List<Cancion> canciones, Path path) throws IOException {
        String csv = toCsv(canciones);
        Files.writeString(path, csv, StandardCharsets.UTF_8);
    }

    private String escape(String value) {
        if (value == null) return "";
        String v = value.replace("\"", "\"\"");
        if (v.contains(",") || v.contains("\n") || v.contains("\r") || v.contains("\"")) {
            return '"' + v + '"';
        }
        return v;
    }
}


