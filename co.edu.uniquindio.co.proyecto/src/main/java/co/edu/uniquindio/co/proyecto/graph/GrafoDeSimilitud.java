package co.edu.uniquindio.co.proyecto.graph;

import co.edu.uniquindio.co.proyecto.model.Cancion;

import java.util.*;

/**
 * Grafo ponderado no dirigido de canciones.
 * Provee:
 *  - conectar(a,b,peso)
 *  - vecinos(c)
 *  - obtenerCancionesSimilares(c) -> vecinos ordenados por peso asc.
 *  - calcularDistancias(origen) -> Dijkstra: mapa Cancion->distancia (costo)
 *  - dijkstra(origen,destino) -> Ruta (camino y costo)
 *
 * Nota: menor peso = mayor similitud según tu modelo.
 */
public class GrafoDeSimilitud {

    private final Map<Cancion, Map<Cancion, Double>> ady = new HashMap<>();

    public void agregarCancion(Cancion c) {
        if (c == null) return;
        ady.computeIfAbsent(c, k -> new HashMap<>());
    }

    public void conectar(Cancion a, Cancion b, double peso) {
        if (a == null || b == null) return;
        if (peso < 0) throw new IllegalArgumentException("Peso debe ser no negativo");
        agregarCancion(a);
        agregarCancion(b);
        ady.get(a).put(b, peso);
        ady.get(b).put(a, peso);
    }

    public Map<Cancion, Double> vecinos(Cancion c) {
        return ady.getOrDefault(c, Collections.emptyMap());
    }

    public List<Cancion> obtenerCancionesSimilares(Cancion c) {
        Map<Cancion, Double> v = vecinos(c);
        if (v.isEmpty()) return Collections.emptyList();
        List<Map.Entry<Cancion, Double>> entries = new ArrayList<>(v.entrySet());
        entries.sort(Comparator.comparingDouble(Map.Entry::getValue));
        List<Cancion> out = new ArrayList<>();
        for (Map.Entry<Cancion, Double> e : entries) out.add(e.getKey());
        return out;
    }

    /**
     * Dijkstra que devuelve mapa Cancion->distancia desde origen.
     */
    public Map<Cancion, Double> calcularDistancias(Cancion origen) {
        Map<Cancion, Double> dist = new HashMap<>();
        if (!ady.containsKey(origen)) return dist;

        for (Cancion c : ady.keySet()) dist.put(c, Double.POSITIVE_INFINITY);
        dist.put(origen, 0.0);

        PriorityQueue<Cancion> pq = new PriorityQueue<>(Comparator.comparingDouble(dist::get));
        pq.add(origen);
        Set<Cancion> visitado = new HashSet<>();

        while (!pq.isEmpty()) {
            Cancion u = pq.poll();
            if (!visitado.add(u)) continue;
            double du = dist.get(u);
            for (Map.Entry<Cancion, Double> e : vecinos(u).entrySet()) {
                Cancion v = e.getKey();
                double peso = e.getValue();
                double alt = du + peso;
                if (alt < dist.getOrDefault(v, Double.POSITIVE_INFINITY)) {
                    dist.put(v, alt);
                    pq.remove(v);
                    pq.add(v);
                }
            }
        }
        return dist;
    }

    /**
     * Dijkstra que devuelve una Ruta (camino y costo). Firma compatible con los tests.
     */
    public Ruta dijkstra(Cancion origen, Cancion destino) {
        if (!ady.containsKey(origen) || !ady.containsKey(destino)) {
            return new Ruta(Collections.emptyList(), Double.POSITIVE_INFINITY);
        }

        Map<Cancion, Double> dist = new HashMap<>();
        Map<Cancion, Cancion> prev = new HashMap<>();
        for (Cancion c : ady.keySet()) dist.put(c, Double.POSITIVE_INFINITY);
        dist.put(origen, 0.0);

        PriorityQueue<Cancion> pq = new PriorityQueue<>(Comparator.comparingDouble(dist::get));
        pq.add(origen);
        Set<Cancion> visitado = new HashSet<>();

        while (!pq.isEmpty()) {
            Cancion u = pq.poll();
            if (!visitado.add(u)) continue;
            if (u.equals(destino)) break;

            for (Map.Entry<Cancion, Double> e : vecinos(u).entrySet()) {
                Cancion v = e.getKey();
                double peso = e.getValue();
                double alt = dist.get(u) + peso;
                if (alt < dist.getOrDefault(v, Double.POSITIVE_INFINITY)) {
                    dist.put(v, alt);
                    prev.put(v, u);
                    pq.remove(v);
                    pq.add(v);
                }
            }
        }

        double costo = dist.getOrDefault(destino, Double.POSITIVE_INFINITY);
        if (Double.isInfinite(costo)) {
            return new Ruta(Collections.emptyList(), Double.POSITIVE_INFINITY);
        }
        LinkedList<Cancion> camino = new LinkedList<>();
        for (Cancion at = destino; at != null; at = prev.get(at)) camino.addFirst(at);
        return new Ruta(camino, costo);
    }

    // ---------------- Ruta ----------------
    public static final class Ruta {
        private final List<Cancion> camino;
        private final double costoTotal;

        public Ruta(List<Cancion> camino, double costoTotal) {
            this.camino = camino;
            this.costoTotal = costoTotal;
        }

        public List<Cancion> getCamino() { return camino; }
        public double getCostoTotal() { return costoTotal; }
    }
}
