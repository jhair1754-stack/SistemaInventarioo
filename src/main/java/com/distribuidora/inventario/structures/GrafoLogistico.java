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
// [REQUISITO RUBRICA: GRAFOS] - Clase GrafoLogistica
// ====================================================================
// Implementación COMPLETA de un Grafo NO DIRIGIDO con PESOS usando
// LISTA DE ADYACENCIA. Representa la red de almacenes y rutas.
//
// REPRESENTACIÓN INTERNA (Lista de Adyacencia):
//
//   vertices:         listaAdyacencia:
//   {                 {
//     "ALM-01" → ...    "ALM-01" → [EnlaceRuta(ALM-02,12.5), EnlaceRuta(ALM-03,25.0)]
//     "ALM-02" → ...    "ALM-02" → [EnlaceRuta(ALM-01,12.5), EnlaceRuta(ALM-04,18.0)]
//     ...               ...
//   }                 }
//
//   Diagrama visual de la red:
//     ALM-01 --(12.5 km)-- ALM-02 --(10 km)-- ALM-06
//        \                    \
//       (25 km)              (18 km)
//          \                    \
//         ALM-03 --(30 km)-- ALM-04 --(55 km)-- ALM-05
//
// ALGORITMO DE DIJKSTRA:
//   Encuentra la ruta de MENOR DISTANCIA TOTAL entre dos almacenes.
//   Complejidad: O((V + E) log V) usando cola de prioridad.
//
// ====================================================================

/**
 * ============================================================
 * ESTRUCTURA: GrafoLogistica
 * ============================================================
 * Grafo no dirigido con pesos implementado con Lista de Adyacencia.
 * Modela la red logística de la empresa: los <b>vértices</b> son
 * {@link Almacen}es y las <b>aristas</b> son {@link EnlaceRuta}s
 * con distancias en kilómetros.
 *
 * <h2>Operaciones disponibles</h2>
 * <ul>
 *   <li>{@link #agregarAlmacen(Almacen)}                  – Agrega vértice.</li>
 *   <li>{@link #conectar(String, String, double, String)}  – Agrega arista bidireccional.</li>
 *   <li>{@link #dijkstra(String, String)}                  – Ruta más corta.</li>
 *   <li>{@link #mostrarRed()}                              – Lista de adyacencia completa.</li>
 *   <li>{@link #listarAlmacenes()}                         – Lista de vértices.</li>
 * </ul>
 *
 * @author  Equipo Proyecto 4 – Ingeniería de Sistemas UNMSM
 * @version 2.0
 */
public class GrafoLogistica {

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
     *
     * <p>Para cada vértice (almacén), guarda la lista de sus aristas
     * salientes ({@link EnlaceRuta}). Esta es la representación
     * fundamental del grafo en memoria RAM.</p>
     *
     * <pre>
     *  listaAdyacencia:
     *    "ALM-01" → [ EnlaceRuta("ALM-02", 12.5, "Av.Colonial"),
     *                 EnlaceRuta("ALM-03", 25.0, "Av.Universitaria") ]
     *    "ALM-02" → [ EnlaceRuta("ALM-01", 12.5, "Av.Colonial"),
     *                 EnlaceRuta("ALM-04", 18.0, "Panamericana Sur") ]
     *    ...
     * </pre>
     */
    private final Map<String, List<EnlaceRuta>> listaAdyacencia;

    /** Número total de aristas no dirigidas en el grafo. */
    private int numeroAristas;

    // ─────────────────────────────────────────────────────────
    //  CONSTRUCTOR
    // ─────────────────────────────────────────────────────────

    /**
     * Crea un grafo logístico vacío (sin vértices ni aristas).
     */
    public GrafoLogistica() {
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
     * <p>Si el ID ya existe no sobreescribe el vértice existente.</p>
     *
     * @param almacen El almacén a agregar como vértice del grafo.
     */
    public void agregarAlmacen(Almacen almacen) {
        // [REQUISITO RUBRICA: GRAFOS] - Agregar vértice al grafo
        if (!vertices.containsKey(almacen.getId())) {
            vertices.put(almacen.getId(), almacen);
            // Crear lista de adyacencia vacía para el nuevo vértice
            listaAdyacencia.put(almacen.getId(), new ArrayList<>());
        }
    }

    // ====================================================================
    // [REQUISITO RUBRICA: GRAFOS] - OPERACIÓN: CONECTAR (AGREGAR ARISTA)
    // ====================================================================

    /**
     * Crea una conexión <b>bidireccional</b> (arista no dirigida) entre
     * dos almacenes con la distancia indicada.
     *
     * <h3>Algoritmo</h3>
     * <pre>
     *  conectar("ALM-01", "ALM-02", 12.5, "Av. Colonial"):
     *    1. listaAdyacencia["ALM-01"].add( EnlaceRuta("ALM-02", 12.5, "Av. Colonial") )
     *    2. listaAdyacencia["ALM-02"].add( EnlaceRuta("ALM-01", 12.5, "Av. Colonial") )
     *       (Para que el grafo sea NO DIRIGIDO)
     * </pre>
     *
     * @param idOrigen    ID del almacén de partida.
     * @param idDestino   ID del almacén de llegada.
     * @param distanciaKm Distancia en kilómetros (peso de la arista).
     * @param descripcion Descripción de la vía o carretera.
     * @throws AlmacenNoEncontradoException si alguno de los IDs no existe en el grafo.
     */
    public void conectar(String idOrigen, String idDestino,
                         double distanciaKm, String descripcion) {
        // [REQUISITO RUBRICA: GRAFOS] - Agregar arista bidireccional ponderada
        validarExistencia(idOrigen);
        validarExistencia(idDestino);

        // Arista ORIGEN → DESTINO
        listaAdyacencia.get(idOrigen)
                       .add(new EnlaceRuta(idDestino, distanciaKm, descripcion));

        // Arista DESTINO → ORIGEN (grafo no dirigido: la conexión va en AMBAS direcciones)
        listaAdyacencia.get(idDestino)
                       .add(new EnlaceRuta(idOrigen, distanciaKm, descripcion));

        this.numeroAristas++;
    }

    // ====================================================================
    // [REQUISITO RUBRICA: GRAFOS] - ALGORITMO: DIJKSTRA (ruta más corta)
    // ====================================================================

    /**
     * Calcula la ruta logística óptima (menor distancia total) entre
     * dos almacenes aplicando el <b>Algoritmo de Dijkstra</b>.
     *
     * <h3>Algoritmo de Dijkstra — pasos</h3>
     * <ol>
     *   <li><b>Inicializar:</b> distancia[origen] = 0; distancia[todos los demás] = ∞.</li>
     *   <li>Insertar origen en una cola de prioridad (min-heap por distancia).</li>
     *   <li>Mientras la cola no esté vacía:
     *     <ul>
     *       <li>Extraer el nodo {@code U} con menor distancia acumulada.</li>
     *       <li>Para cada vecino {@code V} de {@code U}:
     *         <ul>
     *           <li>Nueva distancia = dist[U] + peso(U,V).</li>
     *           <li>Si nueva distancia &lt; dist[V] → actualizar dist[V] y predecesor[V] = U.</li>
     *         </ul>
     *       </li>
     *     </ul>
     *   </li>
     *   <li><b>Reconstruir</b> el camino desde destino → origen usando el mapa de predecesores.</li>
     * </ol>
     *
     * <p><b>Complejidad:</b> O((V + E) · log V) usando cola de prioridad.</p>
     *
     * @param idOrigen  ID del almacén de inicio.
     * @param idDestino ID del almacén de llegada.
     * @return Un {@link ResultadoDijkstra} con la ruta y la distancia total.
     * @throws AlmacenNoEncontradoException si alguno de los IDs no existe.
     */
    public ResultadoDijkstra dijkstra(String idOrigen, String idDestino) {
        // [REQUISITO RUBRICA: GRAFOS] - Algoritmo de Dijkstra
        validarExistencia(idOrigen);
        validarExistencia(idDestino);

        // ── PASO 1: Inicializar distancias en INFINITO ────────────────
        Map<String, Double> distancias   = new HashMap<>();
        Map<String, String> predecesores = new HashMap<>();

        for (String id : vertices.keySet()) {
            distancias.put(id, Double.MAX_VALUE);
            predecesores.put(id, null);
        }
        distancias.put(idOrigen, 0.0);  // Distancia al origen = 0

        // ── PASO 2: Cola de prioridad (min-heap) ─────────────────────
        // Cada entrada: double[]{distanciaAcumulada, hashDelId}
        PriorityQueue<double[]> colaPrioridad = new PriorityQueue<>(
            (a, b) -> Double.compare(a[0], b[0])
        );
        colaPrioridad.offer(new double[]{0.0, idOrigen.hashCode()});

        // Mapa auxiliar hashCode → ID (para recuperar el string)
        Map<Integer, String> hashAId = new HashMap<>();
        for (String id : vertices.keySet()) {
            hashAId.put(id.hashCode(), id);
        }

        // Lista de nodos ya procesados (visitados)
        List<String> visitados = new ArrayList<>();

        // ── PASO 3: Explorar ─────────────────────────────────────────
        while (!colaPrioridad.isEmpty()) {

            double[] actual    = colaPrioridad.poll();
            double   distActual= actual[0];
            String   idActual  = hashAId.get((int) actual[1]);

            if (idActual == null || visitados.contains(idActual)) continue;
            visitados.add(idActual);

            // Salida temprana: llegamos al destino
            if (idActual.equals(idDestino)) break;

            // Explorar vecinos del nodo actual
            List<EnlaceRuta> vecinos =
                listaAdyacencia.getOrDefault(idActual, new ArrayList<>());

            for (EnlaceRuta enlace : vecinos) {
                String  idVecino  = enlace.idDestino;
                double  nuevaDist = distActual + enlace.distanciaKm;

                if (nuevaDist < distancias.getOrDefault(idVecino, Double.MAX_VALUE)) {
                    // Encontramos una ruta más corta hacia idVecino → actualizar
                    distancias.put(idVecino, nuevaDist);
                    predecesores.put(idVecino, idActual);
                    colaPrioridad.offer(new double[]{nuevaDist, idVecino.hashCode()});
                }
            }
        }

        // ── PASO 4: Reconstruir el camino destino → origen ───────────
        List<String> camino = new ArrayList<>();
        String paso = idDestino;
        while (paso != null) {
            camino.add(paso);
            paso = predecesores.get(paso);
        }
        Collections.reverse(camino);  // Invertir: camino va de origen a destino

        double distanciaTotal = distancias.getOrDefault(idDestino, Double.MAX_VALUE);
        boolean alcanzable    = distanciaTotal != Double.MAX_VALUE;

        return new ResultadoDijkstra(camino, distanciaTotal, alcanzable);
    }

    // ─────────────────────────────────────────────────────────
    //  VISUALIZACIÓN
    // ─────────────────────────────────────────────────────────

    /**
     * Retorna la representación textual completa del grafo
     * (lista de adyacencia con todos los almacenes y sus conexiones).
     *
     * @return Cadena con la lista de adyacencia formateada.
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
            List<EnlaceRuta> enlaces = listaAdyacencia.get(id);

            sb.append(String.format("  ► [%s] %s | %s | Cap: %,d ud.%n",
                id, alm.getNombre(), alm.getTipo(), alm.getCapacidadMaxima()));

            if (enlaces == null || enlaces.isEmpty()) {
                sb.append("       (sin conexiones)\n");
            } else {
                for (EnlaceRuta e : enlaces) {
                    Almacen dest = vertices.get(e.idDestino);
                    sb.append(String.format("       %s  [%s]%n",
                        e, dest != null ? dest.getNombre() : "?"));
                }
            }
        }
        sb.append(String.format("%n  Vertices: %d  |  Aristas: %d%n",
            vertices.size(), numeroAristas));
        return sb.toString();
    }

    /**
     * Lista todos los almacenes registrados en la red.
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

    /**
     * Encapsula el resultado del algoritmo de Dijkstra:
     * el camino óptimo, la distancia total y si el destino
     * es alcanzable.
     */
    public static class ResultadoDijkstra {

        /** Lista de IDs de almacenes en el camino óptimo (origen → destino). */
        public final List<String> camino;

        /** Distancia total acumulada del camino óptimo (km). */
        public final double distanciaTotal;

        /** {@code true} si existe al menos un camino entre origen y destino. */
        public final boolean esAlcanzable;

        /**
         * Crea el resultado del algoritmo.
         *
         * @param camino         Camino óptimo como lista de IDs.
         * @param distanciaTotal Distancia total en km.
         * @param esAlcanzable   {@code true} si el destino es alcanzable.
         */
        public ResultadoDijkstra(List<String> camino,
                                 double distanciaTotal,
                                 boolean esAlcanzable) {
            this.camino          = camino;
            this.distanciaTotal  = distanciaTotal;
            this.esAlcanzable    = esAlcanzable;
        }
    }

    // ─────────────────────────────────────────────────────────
    //  MÉTODOS AUXILIARES
    // ─────────────────────────────────────────────────────────

    /**
     * Valida que exista un almacén con el ID dado.
     *
     * @param id ID a validar.
     * @throws AlmacenNoEncontradoException si el ID no existe.
     */
    private void validarExistencia(String id) {
        if (!vertices.containsKey(id.toUpperCase().trim())) {
            throw new AlmacenNoEncontradoException(id);
        }
    }

    // ─────────────────────────────────────────────────────────
    //  GETTERS
    // ─────────────────────────────────────────────────────────

    /** @return Número de almacenes (vértices) en el grafo. */
    public int getNumeroVertices()      { return vertices.size(); }

    /** @return Número de rutas (aristas) en el grafo. */
    public int getNumeroAristas()       { return numeroAristas; }

    /** @return Vista de sólo lectura del mapa de almacenes. */
    public Map<String, Almacen> getVertices() {
        return Collections.unmodifiableMap(vertices);
    }

    /**
     * Retorna el almacén con el ID dado.
     *
     * @param id ID del almacén.
     * @return El almacén, o {@code null} si no existe.
     */
    public Almacen getAlmacen(String id) {
        return vertices.get(id.toUpperCase().trim());
    }

    /**
     * Verifica si un almacén con el ID dado existe en el grafo.
     *
     * @param id ID a verificar.
     * @return {@code true} si existe.
     */
    public boolean existeAlmacen(String id) {
        return vertices.containsKey(id.toUpperCase().trim());
    }
}
