package co.edu.uniquindio.co.proyecto.ds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrieAutocompletado {

    private static class Nodo {
        Map<Character, Nodo> hijos = new HashMap<>();
        boolean finDePalabra;
    }

    private final Nodo raiz = new Nodo();

    public void insertar(String palabra) {
        if (palabra == null || palabra.isBlank()) return;
        String p = palabra.toLowerCase();
        Nodo actual = raiz;
        for (char c : p.toCharArray()) {
            actual = actual.hijos.computeIfAbsent(c, k -> new Nodo());
        }
        actual.finDePalabra = true;
    }

    public List<String> buscarPorPrefijo(String prefijo) {
        List<String> resultados = new ArrayList<>();
        if (prefijo == null) return resultados;
        String p = prefijo.toLowerCase();
        Nodo actual = raiz;
        for (char c : p.toCharArray()) {
            actual = actual.hijos.get(c);
            if (actual == null) return resultados;
        }
        recolectar(actual, new StringBuilder(p), resultados);
        return resultados;
    }

    private void recolectar(Nodo nodo, StringBuilder camino, List<String> acc) {
        if (nodo.finDePalabra) acc.add(camino.toString());
        for (Map.Entry<Character, Nodo> e : nodo.hijos.entrySet()) {
            camino.append(e.getKey());
            recolectar(e.getValue(), camino, acc);
            camino.deleteCharAt(camino.length() - 1);
        }
    }
}


