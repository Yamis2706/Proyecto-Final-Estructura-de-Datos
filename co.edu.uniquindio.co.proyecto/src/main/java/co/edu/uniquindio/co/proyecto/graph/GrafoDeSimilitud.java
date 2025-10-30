package co.edu.uniquindio.co.proyecto.graph;

import co.edu.uniquindio.co.proyecto.model.Cancion;

import java.util.*;

public class GrafoDeSimilitud {
    private final Map<Cancion, Map<Cancion, Double>> adyacencia = new HashMap<>();

    public void agregarCancion(Cancion c) {
        adyacencia.computeIfAbsent(c, k -> new HashMap<>());
    }

    public void conectar(Cancion a, Cancion b, double peso) {
        if (a == null || b == null) return;
        if (peso < 0) throw new IllegalArgumentException("El peso debe ser no negativo (menor = mayor similitud)");
        agregarCancion(a);
        agregarCancion(b);
        adyacencia.get(a).put(b, peso);
        adyacencia.get(b).put(a, peso);
    }

    public Map<Cancion, Double> vecinos(Cancion c) {
        return adyacencia.getOrDefault(c, Collections.emptyMap());
    }

    public static final class Ruta {
        private final List<Cancion> camino;
        private final double costoTotal;

        public Ruta(List<Cancion> camino, double costoTotal) {
            this.camino = camino;
            this.costoTotal = costoTotal;
        }

        public List<Cancion> getCamino() {
            return camino;
        }

        public double getCostoTotal() {
            return costoTotal;
        }
    }

    public Ruta dijkstra(Cancion origen, Cancion destino) {
        if (!adyacencia.containsKey(origen) || !adyacencia.containsKey(destino)) {
            return new Ruta(Collections.emptyList(), Double.POSITIVE_INFINITY);
        }
        Map<Cancion, Double> dist = new HashMap<>();
        Map<Cancion, Cancion> prev = new HashMap<>();
        PriorityQueue<Cancion> pq = new PriorityQueue<>(Comparator.comparingDouble(dist::get));

        for (Cancion c : adyacencia.keySet()) {
            dist.put(c, Double.POSITIVE_INFINITY);
        }
        dist.put(origen, 0.0);
        pq.add(origen);

        while (!pq.isEmpty()) {
            Cancion u = pq.poll();
            if (u.equals(destino)) break;
            for (Map.Entry<Cancion, Double> e : vecinos(u).entrySet()) {
                Cancion v = e.getKey();
                double peso = e.getValue();
                double alt = dist.get(u) + peso;
                if (alt < dist.get(v)) {
                    dist.put(v, alt);
                    prev.put(v, u);
                    pq.remove(v);
                    pq.add(v);
                }
            }
        }

        if (dist.get(destino).isInfinite()) {
            return new Ruta(Collections.emptyList(), Double.POSITIVE_INFINITY);
        }
        LinkedList<Cancion> camino = new LinkedList<>();
        for (Cancion at = destino; at != null; at = prev.get(at)) {
            camino.addFirst(at);
        }
        return new Ruta(camino, dist.get(destino));
    }
}


