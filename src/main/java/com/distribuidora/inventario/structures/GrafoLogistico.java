package com.distribuidora.inventario.structures;

import com.distribuidora.inventario.exceptions.AlmacenNoEncontradoException;
import com.distribuidora.inventario.models.Almacen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

// ====================================================================
// [REQUISITO RUBRICA: GRAFOS] - Clase GrafoLogistico
// ====================================================================
// Implementación COMPLETA de un Grafo NO DIRIGIDO con PESOS usando
// LISTA DE ADYACENCIA. Representa la red de almacenes y rutas.
//
// REPRESENTACIÓN INTERNA (Lista de Adyacencia):
//
//   vertices:         listaAdyacencia:
//   {                 {
//     "ALM-01" → ...    "ALM-01" → [Arista(ALM-02,12.5), Arista(ALM-03,25.0)]
//     "ALM-02" → ...    "ALM-02" → [Arista(ALM-01,12.5), Arista(ALM-04,18.0)]
//     ...               ...
//   }                 }
//
// ALGORITMO DE DIJKSTRA:
//   Encuentra la ruta de MENOR DISTANCIA TOTAL entre dos almacenes.
//   Complejidad: O((V + E) log V) usando cola de prioridad.
//
// ====================================================================

/**
 * ============================================================
 * ESTRUCTURA: GrafoLogistico
 * ============================================================
 * Grafo no dirigido con pesos implementado con Lista de Adyacencia.
 * Modela la red logística de la empresa: los <b>vértices</b> son
 * {@link Almacen}es y las <b>aristas</b> son {@link Arista}s
 * con distancias en kilómetros.
 *
 * @author  Equipo Proyecto 4 – Ingeniería de Sistemas UNMSM
 * @version 2.0
 */
public class GrafoLogistico {

    // ─────────────────────────────────────────────────────────
    //  ESTRUCTURA INTERNA: LISTA DE ADYACENCIA
    // ─────────────────────────────────────────────────────────

    /**
     * Mapa de vértices del grafo.
     * Clave: ID del almacén. Valor: objeto {@link Almacen}.
     */
    private final Map<String, Almacen> vertices;

    /**
     * LISTA DE ADYACENCIA del grafo.
     * Clave: ID del almacén origen. Valor: Lista de {@link Arista}s salientes.
     */
    private final Map<String, List<Arista>> listaAdyacencia;

    /** Número total de aristas no dirigidas en el grafo. */
    private int numeroAristas;

    // ─────────────────────────────────────────────────────────
    //  CONSTRUCTOR
    // ─────────────────────────────────────────────────────────

    /**
     * Crea un grafo logístico vacío.
     */
    public GrafoLogistico() {
        // [REQUISITO RUBRICA: GRAFOS] - Inicialización del grafo con lista de adyacencia
        this.vertices        = new HashMap<>();
        this.listaAdyacencia = new HashMap<>();
        this.numeroAristas   = 0;
    }

    // ====================================================================
    // [REQUISITO RUBRICA: GRAFOS] - OPERACIÓN: AGREGAR VÉRTICE
    // ====================================================================

    /**
     * Agrega un nuevo almacén (vértice) al grafo.
     *
     * @param almacen El almacén a agregar como vértice.
     */
    public void agregarAlmacen(Almacen almacen) {
        // [REQUISITO RUBRICA: GRAFOS] - Agregar vértice al grafo
        if (!vertices.containsKey(almacen.getId())) {
            vertices.put(almacen.getId(), almacen);
            listaAdyacencia.put(almacen.getId(), new ArrayList<>());
        }
    }

    // ====================================================================
    // [REQUISITO RUBRICA: GRAFOS] - OPERACIÓN: CONECTAR (AGREGAR ARISTA)
    // ====================================================================

    /**
     * Crea una conexión bidireccional (arista no dirigida) entre dos almacenes.
     *
     * @param idOrigen    ID del almacén de partida.
     * @param idDestino   ID del almacén de llegada.
     * @param distanciaKm Distancia en kilómetros (peso).
     * @param descripcion Descripción de la vía.
     * @throws AlmacenNoEncontradoException si algún ID no existe en el grafo.
     */
    public void conectar(String idOrigen, String idDestino,
                         double distanciaKm, String descripcion) {
        // [REQUISITO RUBRICA: GRAFOS] - Agregar arista bidireccional ponderada
        validarExistencia(idOrigen);
        validarExistencia(idDestino);

        listaAdyacencia.get(idOrigen)
                       .add(new Arista(idDestino, distanciaKm, descripcion));

        listaAdyacencia.get(idDestino)
                       .add(new Arista(idOrigen, distanciaKm, descripcion));

        this.numeroAristas++;
    }

    // ====================================================================
    // [REQUISITO RUBRICA: GRAFOS] - ALGORITMO: DIJKSTRA (ruta más corta)
    // ====================================================================

    /**
     * Calcula la ruta logística óptima usando el Algoritmo de Dijkstra.
     *
     * @param idOrigen  ID del almacén de inicio.
     * @param idDestino ID del almacén de llegada.
     * @return {@link ResultadoDijkstra} con la ruta y distancia total.
     * @throws AlmacenNoEncontradoException si algún ID no existe.
     */
    public ResultadoDijkstra dijkstra(String idOrigen, String idDestino) {
        // [REQUISITO RUBRICA: GRAFOS] - Algoritmo de Dijkstra
        validarExistencia(idOrigen);
        validarExistencia(idDestino);

        Map<String, Double> distancias   = new HashMap<>();
        Map<String, String> predecesores = new HashMap<>();

        for (String id : vertices.keySet()) {
            distancias.put(id, Double.MAX_VALUE);
            predecesores.put(id, null);
        }
        distancias.put(idOrigen, 0.0);

        PriorityQueue<double[]> colaPrioridad = new PriorityQueue<>(
            (a, b) -> Double.compare(a[0], b[0])
        );
        colaPrioridad.offer(new double[]{0.0, idOrigen.hashCode()});

        Map<Integer, String> hashAId = new HashMap<>();
        for (String id : vertices.keySet()) {
            hashAId.put(id.hashCode(), id);
        }

        List<String> visitados = new ArrayList<>();

        while (!colaPrioridad.isEmpty()) {
            double[] actual    = colaPrioridad.poll();
            double   distActual= actual[0];
            String   idActual  = hashAId.get((int) actual[1]);

            if (idActual == null || visitados.contains(idActual)) continue;
            visitados.add(idActual);

            if (idActual.equals(idDestino)) break;

            List<Arista> vecinos =
                listaAdyacencia.getOrDefault(idActual, new ArrayList<>());

            for (Arista arista : vecinos) {
                String  idVecino  = arista.getIdDestino();
                double  nuevaDist = distActual + arista.getPeso();

                if (nuevaDist < distancias.getOrDefault(idVecino, Double.MAX_VALUE)) {
                    distancias.put(idVecino, nuevaDist);
                    predecesores.put(idVecino, idActual);
                    colaPrioridad.offer(new double[]{nuevaDist, idVecino.hashCode()});
                }
            }
        }

        List<String> camino = new ArrayList<>();
        String paso = idDestino;
        while (paso != null) {
            camino.add(paso);
            paso = predecesores.get(paso);
        }
        Collections.reverse(camino);

        double distanciaTotal = distancias.getOrDefault(idDestino, Double.MAX_VALUE);
        boolean alcanzable    = distanciaTotal != Double.MAX_VALUE;

        return new ResultadoDijkstra(camino, distanciaTotal, alcanzable);
    }

    // ─────────────────────────────────────────────────────────
    //  VISUALIZACIÓN
    // ─────────────────────────────────────────────────────────

    /**
     * Retorna la representación de la red como lista de adyacencia.
     *
     * @return Cadena con la lista de adyacencia.
     */
    public String mostrarRed() {
        // [REQUISITO RUBRICA: GRAFOS] - Visualización de la lista de adyacencia
        if (vertices.isEmpty()) {
            return "  La red logistica esta vacia. Agregue almacenes primero.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("  RED LOGISTICA – LISTA DE ADYACENCIA\n");
        sb.append("  ═══════════════════════════════════════\n");

        for (Map.Entry<String, Almacen> entrada : vertices.entrySet()) {
            String  id      = entrada.getKey();
            Almacen alm     = entrada.getValue();
            List<Arista> aristas = listaAdyacencia.get(id);

            sb.append(String.format("  ► [%s] %s | %s | Cap: %,d ud.%n",
                id, alm.getNombre(), alm.getTipo(), alm.getCapacidadMaxima()));

            if (aristas == null || aristas.isEmpty()) {
                sb.append("       (sin conexiones)\n");
            } else {
                for (Arista a : aristas) {
                    Almacen dest = vertices.get(a.getIdDestino());
                    sb.append(String.format("       %s  [%s]%n",
                        a, dest != null ? dest.getNombre() : "?"));
                }
            }
        }
        sb.append(String.format("%n  Vertices: %d  |  Aristas: %d%n",
            vertices.size(), numeroAristas));
        return sb.toString();
    }

    /**
     * Lista todos los almacenes registrados.
     *
     * @return Cadena con la lista de almacenes.
     */
    public String listarAlmacenes() {
        if (vertices.isEmpty()) return "  No hay almacenes registrados.\n";
        StringBuilder sb = new StringBuilder();
        sb.append("  ALMACENES EN LA RED LOGISTICA:\n");
        sb.append("  ─────────────────────────────────────────\n");
        vertices.values().forEach(a ->
            sb.append("  ").append(a).append("\n")
        );
        return sb.toString();
    }

    // ─────────────────────────────────────────────────────────
    //  CLASE INTERNA: RESULTADO DE DIJKSTRA
    // ─────────────────────────────────────────────────────────

    public static class ResultadoDijkstra {
        public final List<String> camino;
        public final double distanciaTotal;
        public final boolean esAlcanzable;

        public ResultadoDijkstra(List<String> camino,
                                 double distanciaTotal,
                                 boolean esAlcanzable) {
            this.camino          = camino;
            this.distanciaTotal  = distanciaTotal;
            this.esAlcanzable    = esAlcanzable;
        }
    }

    private void validarExistencia(String id) {
        if (!vertices.containsKey(id.toUpperCase().trim())) {
            throw new AlmacenNoEncontradoException(id);
        }
    }

    public int getNumeroVertices()      { return vertices.size(); }
    public int getNumeroAristas()       { return numeroAristas; }
    public Map<String, Almacen> getVertices() { return Collections.unmodifiableMap(vertices); }
    public Almacen getAlmacen(String id) { return vertices.get(id.toUpperCase().trim()); }
    public boolean existeAlmacen(String id) { return vertices.containsKey(id.toUpperCase().trim()); }
}
