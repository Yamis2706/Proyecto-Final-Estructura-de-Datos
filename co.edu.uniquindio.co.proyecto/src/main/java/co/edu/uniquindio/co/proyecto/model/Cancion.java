package co.edu.uniquindio.co.proyecto.model;

import java.util.Objects;

public class Cancion {
    private final String id;
    private String titulo;
    private String artista;
    private String genero;
    private int anio;
    private int duracionSegundos;

    public Cancion(String id, String titulo, String artista, String genero, int anio, int duracionSegundos) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id no puede ser nulo o vacío");
        }
        this.id = id;
        this.titulo = titulo;
        this.artista = artista;
        this.genero = genero;
        this.anio = anio;
        this.duracionSegundos = duracionSegundos;
    }

    /**
     * Calcula la similitud entre esta canción y otra.
     * Implementa RF-017: Funcionar como nodo en el Grafo de Similitud.
     *
     * @param otra Canción a comparar
     * @return Peso de similitud (menor peso = mayor similitud)
     */
    public double calcularSimilitud(Cancion otra) {
        if (otra == null) return Double.POSITIVE_INFINITY;
        if (this.equals(otra)) return 0.0; // Misma canción

        double similitud = 0.0;

        // Similitud por artista (peso: 40%)
        if (this.artista != null && otra.artista != null &&
                this.artista.equalsIgnoreCase(otra.artista)) {
            similitud += 0.4;
        }

        // Similitud por género (peso: 30%)
        if (this.genero != null && otra.genero != null &&
                this.genero.equalsIgnoreCase(otra.genero)) {
            similitud += 0.3;
        }

        // Similitud por año (peso: 20%)
        int diferenciaAño = Math.abs(this.anio - otra.anio);
        if (diferenciaAño <= 5) {
            similitud += 0.2 * (1.0 - (diferenciaAño / 10.0));
        }

        // Similitud por duración (peso: 10%)
        int diferenciaDuracion = Math.abs(this.duracionSegundos - otra.duracionSegundos);
        if (diferenciaDuracion <= 60) { // Diferencia menor a 1 minuto
            similitud += 0.1 * (1.0 - (diferenciaDuracion / 300.0));
        }

        // Convertir similitud a peso (menor peso = mayor similitud)
        // Similitud 1.0 -> peso 1.0, Similitud 0.0 -> peso 10.0
        return Math.max(1.0, 10.0 - (similitud * 9.0));
    }

    public String getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getArtista() {
        return artista;
    }

    public void setArtista(String artista) {
        this.artista = artista;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public int getDuracionSegundos() {
        return duracionSegundos;
    }

    public void setDuracionSegundos(int duracionSegundos) {
        this.duracionSegundos = duracionSegundos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cancion cancion = (Cancion) o;
        return id.equals(cancion.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Cancion{" +
                "id='" + id + '\'' +
                ", titulo='" + titulo + '\'' +
                ", artista='" + artista + '\'' +
                ", genero='" + genero + '\'' +
                ", anio=" + anio +
                ", duracionSegundos=" + duracionSegundos +
                '}';
    }
}