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


