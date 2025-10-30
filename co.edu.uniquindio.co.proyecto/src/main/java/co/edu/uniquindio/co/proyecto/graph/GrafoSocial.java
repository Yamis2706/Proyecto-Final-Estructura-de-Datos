package co.edu.uniquindio.co.proyecto.graph;

import co.edu.uniquindio.co.proyecto.model.Usuario;

import java.util.*;

public class GrafoSocial {
    private final Map<Usuario, Set<Usuario>> adyacencia = new HashMap<>();

    public void agregarUsuario(Usuario u) {
        adyacencia.computeIfAbsent(u, k -> new HashSet<>());
    }

    public void conectar(Usuario a, Usuario b) {
        if (a == null || b == null || a.equals(b)) return;
        agregarUsuario(a);
        agregarUsuario(b);
        adyacencia.get(a).add(b);
        adyacencia.get(b).add(a);
    }

    public Set<Usuario> vecinos(Usuario u) {
        return Collections.unmodifiableSet(adyacencia.getOrDefault(u, Collections.emptySet()));
    }

    public List<Usuario> bfs(Usuario origen) {
        List<Usuario> orden = new ArrayList<>();
        if (!adyacencia.containsKey(origen)) return orden;
        Set<Usuario> visitado = new HashSet<>();
        Queue<Usuario> q = new ArrayDeque<>();
        visitado.add(origen);
        q.add(origen);
        while (!q.isEmpty()) {
            Usuario u = q.poll();
            orden.add(u);
            for (Usuario v : adyacencia.get(u)) {
                if (visitado.add(v)) {
                    q.add(v);
                }
            }
        }
        return orden;
    }

    public Set<Usuario> sugerirSegundosGrados(Usuario origen) {
        Set<Usuario> sugerencias = new HashSet<>();
        if (!adyacencia.containsKey(origen)) return sugerencias;
        Set<Usuario> amigos = adyacencia.get(origen);
        for (Usuario amigo : amigos) {
            for (Usuario amigoDeAmigo : adyacencia.getOrDefault(amigo, Collections.emptySet())) {
                if (!amigoDeAmigo.equals(origen) && !amigos.contains(amigoDeAmigo)) {
                    sugerencias.add(amigoDeAmigo);
                }
            }
        }
        return sugerencias;
    }
}


