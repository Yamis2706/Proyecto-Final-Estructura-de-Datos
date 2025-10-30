package co.edu.uniquindio.co.proyecto.service;

import co.edu.uniquindio.co.proyecto.model.Cancion;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SearchService {
    private final ExecutorService executor = Executors.newFixedThreadPool(Math.max(2, Runtime.getRuntime().availableProcessors() / 2));

    public enum Logic { AND, OR }

    public static class Criteria {
        public String artista;
        public String genero;
        public Integer anio;
    }

    public List<Cancion> advancedSearch(List<Cancion> base, Criteria criteria, Logic logic) {
        if (base == null || base.isEmpty() || criteria == null) return new ArrayList<>();

        List<Callable<List<Cancion>>> tasks = new ArrayList<>();
        if (criteria.artista != null && !criteria.artista.isBlank()) {
            String a = criteria.artista.toLowerCase();
            tasks.add(() -> filter(base, c -> c.getArtista() != null && c.getArtista().toLowerCase().contains(a)));
        }
        if (criteria.genero != null && !criteria.genero.isBlank()) {
            String g = criteria.genero.toLowerCase();
            tasks.add(() -> filter(base, c -> c.getGenero() != null && c.getGenero().toLowerCase().contains(g)));
        }
        if (criteria.anio != null) {
            int year = criteria.anio;
            tasks.add(() -> filter(base, c -> c.getAnio() == year));
        }

        if (tasks.isEmpty()) return new ArrayList<>();

        try {
            List<Future<List<Cancion>>> futures = executor.invokeAll(tasks);
            List<List<Cancion>> results = new ArrayList<>();
            for (Future<List<Cancion>> f : futures) {
                results.add(f.get());
            }
            return logic == Logic.AND ? intersect(results) : union(results);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new ArrayList<>();
        } catch (ExecutionException e) {
            return new ArrayList<>();
        }
    }

    private List<Cancion> filter(List<Cancion> base, Predicate<Cancion> predicate) {
        return base.stream().filter(predicate).collect(Collectors.toList());
    }

    private List<Cancion> intersect(List<List<Cancion>> lists) {
        if (lists.isEmpty()) return new ArrayList<>();
        return lists.stream().skip(1).reduce(new ArrayList<>(lists.get(0)), (acc, next) -> {
            acc.retainAll(next);
            return acc;
        });
    }

    private List<Cancion> union(List<List<Cancion>> lists) {
        return lists.stream().flatMap(List::stream).filter(Objects::nonNull).distinct().collect(Collectors.toList());
    }

    public void shutdown() {
        executor.shutdownNow();
    }
}


