package co.edu.uniquindio.co.proyecto.service;

import co.edu.uniquindio.co.proyecto.graph.GrafoDeSimilitud;
import co.edu.uniquindio.co.proyecto.model.Cancion;

import java.util.*;

public class RadioService {

    public Queue<Cancion> generarRadio(GrafoDeSimilitud grafo, Cancion inicio, int maxSize) {
        Queue<Cancion> cola = new ArrayDeque<>();
        if (grafo == null || inicio == null || maxSize <= 0) return cola;

        Set<Cancion> visitado = new HashSet<>();
        PriorityQueue<Map.Entry<Cancion, Double>> pq = new PriorityQueue<>(Comparator.comparingDouble(Map.Entry::getValue));

        visitado.add(inicio);
        cola.add(inicio);

        Map<Cancion, Double> vecinos = grafo.vecinos(inicio);
        pq.addAll(vecinos.entrySet());

        while (!pq.isEmpty() && cola.size() < maxSize) {
            Map.Entry<Cancion, Double> e = pq.poll();
            Cancion next = e.getKey();
            if (visitado.add(next)) {
                cola.add(next);
                for (Map.Entry<Cancion, Double> n2 : grafo.vecinos(next).entrySet()) {
                    if (!visitado.contains(n2.getKey())) {
                        pq.add(n2);
                    }
                }
            }
        }
        return cola;
    }
}


