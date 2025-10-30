package co.edu.uniquindio.co.proyecto.service;

import co.edu.uniquindio.co.proyecto.catalog.SongCatalog;
import co.edu.uniquindio.co.proyecto.model.Cancion;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class BulkImportService {

    public static class ImportResult {
        public final int inserted;
        public final List<String> errors;

        public ImportResult(int inserted, List<String> errors) {
            this.inserted = inserted;
            this.errors = errors;
        }
    }

    // Formato esperado por línea (texto plano): id,titulo,artista,genero,anio,duracionSegundos
    public ImportResult importSongs(Path file, SongCatalog catalog) throws IOException {
        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        int ok = 0;
        List<String> errors = new ArrayList<>();
        int lineNum = 0;
        for (String line : lines) {
            lineNum++;
            if (line.isBlank() || line.startsWith("#") || line.toLowerCase().startsWith("id,")) continue;
            String[] p = line.split(",", -1);
            if (p.length < 6) { errors.add("L"+lineNum+": columnas insuficientes"); continue; }
            try {
                String id = p[0].trim();
                String titulo = p[1].trim();
                String artista = p[2].trim();
                String genero = p[3].trim();
                int anio = Integer.parseInt(p[4].trim());
                int dur = Integer.parseInt(p[5].trim());
                Cancion c = new Cancion(id, titulo, artista, genero, anio, dur);
                boolean added = catalog.add(c);
                if (added) ok++; else errors.add("L"+lineNum+": id duplicado: "+id);
            } catch (Exception ex) {
                errors.add("L"+lineNum+": "+ex.getMessage());
            }
        }
        return new ImportResult(ok, errors);
    }
}


