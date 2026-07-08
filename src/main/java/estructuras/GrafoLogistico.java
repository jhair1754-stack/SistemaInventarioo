package estructuras;

import excepciones.AlmacenNoEncontradoException;
import modelos.Almacen;

/**
 * ============================================================
 * ESTRUCTURA: GrafoLogistico
 * ============================================================
 * Grafo no dirigido con pesos implementado con Lista de Adyacencia
 * adaptado para NO usar colecciones de java.util.*
 *
 * @author Equipo Proyecto 4 – Ingeniería de Sistemas UNMSM
 * @version 3.0
 */
public class GrafoLogistico {

    public static class VerticeLogistico {
        public Almacen almacen;
        public ListaEnlazada<Arista> aristas;

        public VerticeLogistico(Almacen almacen) {
            this.almacen = almacen;
            this.aristas = new ListaEnlazada<>();
        }
    }

    private final ListaEnlazada<VerticeLogistico> vertices;
    private int numeroAristas;

    public GrafoLogistico() {
        this.vertices = new ListaEnlazada<>();
        this.numeroAristas = 0;
    }

    public void agregarAlmacen(Almacen almacen) {
        if (!existeAlmacen(almacen.getId())) {
            vertices.add(new VerticeLogistico(almacen));
        }
    }

    public void conectar(String idOrigen, String idDestino, double distanciaKm, String descripcion) {
        VerticeLogistico vOrigen = obtenerVertice(idOrigen);
        VerticeLogistico vDestino = obtenerVertice(idDestino);

        if (vOrigen == null) throw new AlmacenNoEncontradoException(idOrigen);
        if (vDestino == null) throw new AlmacenNoEncontradoException(idDestino);

        vOrigen.aristas.add(new Arista(idDestino, distanciaKm, descripcion));
        vDestino.aristas.add(new Arista(idOrigen, distanciaKm, descripcion));

        this.numeroAristas++;
    }

    /**
     * Calcula la ruta logística óptima usando Dijkstra (O(V^2)) usando
     * estructuras personalizadas en lugar de PriorityQueue.
     */
    public ResultadoDijkstra dijkstra(String idOrigen, String idDestino) {
        if (!existeAlmacen(idOrigen)) throw new AlmacenNoEncontradoException(idOrigen);
        if (!existeAlmacen(idDestino)) throw new AlmacenNoEncontradoException(idDestino);

        int numV = vertices.size();
        String[] ids = new String[numV];
        double[] distancias = new double[numV];
        String[] predecesores = new String[numV];
        boolean[] visitados = new boolean[numV];

        int indiceOrigen = -1;
        int indiceDestino = -1;

        for (int i = 0; i < numV; i++) {
            VerticeLogistico v = vertices.get(i);
            ids[i] = v.almacen.getId();
            distancias[i] = Double.MAX_VALUE;
            predecesores[i] = null;
            visitados[i] = false;

            if (ids[i].equalsIgnoreCase(idOrigen)) indiceOrigen = i;
            if (ids[i].equalsIgnoreCase(idDestino)) indiceDestino = i;
        }

        distancias[indiceOrigen] = 0.0;

        for (int count = 0; count < numV; count++) {
            // Encontrar el vértice con menor distancia no visitado
            double minDist = Double.MAX_VALUE;
            int u = -1;
            for (int i = 0; i < numV; i++) {
                if (!visitados[i] && distancias[i] < minDist) {
                    minDist = distancias[i];
                    u = i;
                }
            }

            if (u == -1 || ids[u].equalsIgnoreCase(idDestino)) break; // No hay más alcanzables o llegamos

            visitados[u] = true;
            VerticeLogistico vActual = vertices.get(u);

            for (Arista arista : vActual.aristas) {
                String idVecino = arista.getIdDestino();
                int v = buscarIndice(ids, idVecino);
                
                if (v != -1 && !visitados[v] && distancias[u] != Double.MAX_VALUE) {
                    double nuevaDist = distancias[u] + arista.getPeso();
                    if (nuevaDist < distancias[v]) {
                        distancias[v] = nuevaDist;
                        predecesores[v] = ids[u];
                    }
                }
            }
        }

        ListaEnlazada<String> camino = new ListaEnlazada<>();
        String paso = idDestino;
        while (paso != null) {
            camino.add(paso);
            int idx = buscarIndice(ids, paso);
            paso = (idx != -1) ? predecesores[idx] : null;
        }

        // Invertir el camino usando arreglo temporal
        ListaEnlazada<String> caminoInvertido = new ListaEnlazada<>();
        for (int i = camino.size() - 1; i >= 0; i--) {
            caminoInvertido.add(camino.get(i));
        }

        double distanciaTotal = distancias[indiceDestino];
        boolean alcanzable = distanciaTotal != Double.MAX_VALUE;

        return new ResultadoDijkstra(caminoInvertido, distanciaTotal, alcanzable);
    }

    private int buscarIndice(String[] ids, String idBuscado) {
        for (int i = 0; i < ids.length; i++) {
            if (ids[i].equalsIgnoreCase(idBuscado)) return i;
        }
        return -1;
    }

    public String mostrarRed() {
        if (vertices.isEmpty()) return "  La red logistica esta vacia.";

        StringBuilder sb = new StringBuilder();
        sb.append("  RED LOGISTICA – LISTA DE ADYACENCIA\n");
        sb.append("  ═══════════════════════════════════════\n");

        for (VerticeLogistico v : vertices) {
            sb.append(String.format("  ► [%s] %s | %s | Cap: %,d ud.%n",
                v.almacen.getId(), v.almacen.getNombre(), v.almacen.getTipo(), v.almacen.getCapacidadMaxima()));
            
            if (v.aristas.isEmpty()) {
                sb.append("       (sin conexiones)\n");
            } else {
                for (Arista a : v.aristas) {
                    Almacen dest = getAlmacen(a.getIdDestino());
                    sb.append(String.format("       %s  [%s]%n", a, dest != null ? dest.getNombre() : "?"));
                }
            }
        }
        sb.append(String.format("%n  Vertices: %d  |  Aristas: %d%n", vertices.size(), numeroAristas));
        return sb.toString();
    }

    public String listarAlmacenes() {
        if (vertices.isEmpty()) return "  No hay almacenes registrados.\n";
        StringBuilder sb = new StringBuilder();
        sb.append("  ALMACENES EN LA RED LOGISTICA:\n");
        sb.append("  ─────────────────────────────────────────\n");
        for (VerticeLogistico v : vertices) {
            sb.append("  ").append(v.almacen).append("\n");
        }
        return sb.toString();
    }

    public static class ResultadoDijkstra {
        public final ListaEnlazada<String> camino;
        public final double distanciaTotal;
        public final boolean esAlcanzable;

        public ResultadoDijkstra(ListaEnlazada<String> camino, double distanciaTotal, boolean esAlcanzable) {
            this.camino = camino;
            this.distanciaTotal = distanciaTotal;
            this.esAlcanzable = esAlcanzable;
        }
    }

    public boolean eliminarAlmacen(String id) {
        VerticeLogistico v = obtenerVertice(id);
        if (v == null) return false;

        // Eliminar todas las aristas de este almacén en los otros almacenes
        for (Arista a : v.aristas) {
            VerticeLogistico vecino = obtenerVertice(a.getIdDestino());
            if (vecino != null) {
                // Eliminar la arista de vuelta
                for (int i = 0; i < vecino.aristas.size(); i++) {
                    if (vecino.aristas.get(i).getIdDestino().equalsIgnoreCase(id)) {
                        vecino.aristas.remove(i);
                        break;
                    }
                }
            }
            this.numeroAristas--;
        }

        // Eliminar el vértice de la lista
        return vertices.remove(v);
    }

    public boolean eliminarRuta(String idOrigen, String idDestino) {
        VerticeLogistico vOrigen = obtenerVertice(idOrigen);
        VerticeLogistico vDestino = obtenerVertice(idDestino);

        if (vOrigen == null || vDestino == null) return false;

        boolean removidaOrigen = false;
        for (int i = 0; i < vOrigen.aristas.size(); i++) {
            if (vOrigen.aristas.get(i).getIdDestino().equalsIgnoreCase(idDestino)) {
                vOrigen.aristas.remove(i);
                removidaOrigen = true;
                break;
            }
        }

        boolean removidaDestino = false;
        for (int i = 0; i < vDestino.aristas.size(); i++) {
            if (vDestino.aristas.get(i).getIdDestino().equalsIgnoreCase(idOrigen)) {
                vDestino.aristas.remove(i);
                removidaDestino = true;
                break;
            }
        }

        if (removidaOrigen || removidaDestino) {
            this.numeroAristas--;
            return true;
        }
        return false;
    }

    public int getNumeroVertices() { return vertices.size(); }
    public int getNumeroAristas() { return numeroAristas; }

    public ListaEnlazada<VerticeLogistico> getVertices() { return vertices; }

    public VerticeLogistico obtenerVertice(String id) {
        for (VerticeLogistico v : vertices) {
            if (v.almacen.getId().equalsIgnoreCase(id.trim())) {
                return v;
            }
        }
        return null;
    }

    public Almacen getAlmacen(String id) {
        VerticeLogistico v = obtenerVertice(id);
        return v != null ? v.almacen : null;
    }

    public boolean existeAlmacen(String id) {
        return obtenerVertice(id) != null;
    }
}
