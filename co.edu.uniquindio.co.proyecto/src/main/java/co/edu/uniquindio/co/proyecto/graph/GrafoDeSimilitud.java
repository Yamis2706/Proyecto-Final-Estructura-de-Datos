package co.edu.uniquindio.co.proyecto.graph;

import co.edu.uniquindio.co.proyecto.model.Cancion;

import java.util.*;

/**
 * Grafo ponderado no dirigido de canciones con conexión automática por similitud.
 * Implementa RF-019 y RF-020.
 */
public class GrafoDeSimilitud {

    private final Map<Cancion, Map<Cancion, Double>> ady = new HashMap<>();
    private final double umbralSimilitud;

    /**
     * Constructor con umbral de similitud personalizado.
     * @param umbralSimilitud Peso máximo para considerar conexión (menor = más similar)
     */
    public GrafoDeSimilitud(double umbralSimilitud) {
        this.umbralSimilitud = umbralSimilitud;
    }

    /**
     * Constructor con umbral por defecto.
     */
    public GrafoDeSimilitud() {
        this(5.0); // Umbral por defecto
    }

    /**
     * Agrega una canción y la conecta automáticamente con canciones similares.
     * Implementa RF-017: Canción funciona como nodo en el grafo.
     */
    public void agregarCancion(Cancion c) {
        if (c == null) return;
        ady.computeIfAbsent(c, k -> new HashMap<>());

        // Conectar automáticamente con canciones existentes si son similares
        for (Cancion existente : ady.keySet()) {
            if (!existente.equals(c)) {
                double peso = c.calcularSimilitud(existente);
                if (peso <= umbralSimilitud) {
                    conectar(c, existente, peso);
                }
            }
        }
    }

    public void conectar(Cancion a, Cancion b, double peso) {
        if (a == null || b == null) return;
        if (peso < 0) throw new IllegalArgumentException("Peso debe ser no negativo");
        agregarCancionSinConectar(a);
        agregarCancionSinConectar(b);
        ady.get(a).put(b, peso);
        ady.get(b).put(a, peso);
    }

    private void agregarCancionSinConectar(Cancion c) {
        if (c != null) {
            ady.computeIfAbsent(c, k -> new HashMap<>());
        }
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
     * Dijkstra optimizado para encontrar rutas de mayor similitud (menor costo).
     * Implementa RF-020: Algoritmos de recorrido como Dijkstra para menor costo.
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
     * Dijkstra que devuelve una Ruta completa (camino y costo).
     * Optimizado para encontrar rutas de mayor similitud (menor costo total).
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

    /**
     * Genera una radio de canciones similares a partir de una canción semilla.
     * Utiliza el grafo de similitud para encontrar canciones relacionadas.
     */
    public Queue<Cancion> generarRadioMejorada(Cancion inicio, int maxSize) {
        Queue<Cancion> cola = new ArrayDeque<>();
        if (inicio == null || maxSize <= 0 || !ady.containsKey(inicio)) return cola;

        Set<Cancion> visitado = new HashSet<>();
        PriorityQueue<Map.Entry<Cancion, Double>> pq = new PriorityQueue<>(
                Comparator.comparingDouble(Map.Entry::getValue)
        );

        visitado.add(inicio);
        cola.add(inicio);

        // Agregar vecinos inmediatos ordenados por similitud
        Map<Cancion, Double> vecinos = vecinos(inicio);
        pq.addAll(vecinos.entrySet());

        while (!pq.isEmpty() && cola.size() < maxSize) {
            Map.Entry<Cancion, Double> e = pq.poll();
            Cancion next = e.getKey();
            if (visitado.add(next)) {
                cola.add(next);
                // Agregar vecinos de segundo nivel
                for (Map.Entry<Cancion, Double> n2 : vecinos(next).entrySet()) {
                    if (!visitado.contains(n2.getKey())) {
                        pq.add(n2);
                    }
                }
            }
        }
        return cola;
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