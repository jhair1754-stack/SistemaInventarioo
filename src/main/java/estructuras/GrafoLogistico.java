package estructuras;

import excepciones.AlmacenNoEncontradoException;
import modelos.Almacen;

/**
 * Gestiona los almacenes y sus conexiones en la red logistica.
 * 
 * @author Equipo 2
 * @version Beta
 */
public class GrafoLogistico {

    /**
     * Representa un vertice en el grafo que contiene un almacen y sus conexiones.
     */
    public static class VerticeLogistico {
        /** Almacen. */
        public Almacen almacen;
        /** Aristas de conexion. */
        public ListaEnlazada<Arista> aristas;

        /**
         * Constructor.
         * @param almacen Almacen.
         */
        public VerticeLogistico(Almacen almacen) {
            this.almacen = almacen;
            this.aristas = new ListaEnlazada<>();
        }
    }

    private final ListaEnlazada<VerticeLogistico> vertices;
    private int numeroAristas;

    /**
     * Constructor principal.
     */
    public GrafoLogistico() {
        this.vertices = new ListaEnlazada<>();
        this.numeroAristas = 0;
    }

    /**
     * Agrega un almacen a la red logistica.
     * 
     * @param almacen Almacen a agregar.
     */
    public void agregarAlmacen(Almacen almacen) {
        if (!existeAlmacen(almacen.getId())) {
            vertices.add(new VerticeLogistico(almacen));
        }
    }

    /**
     * Crea una conexion directa entre dos almacenes.
     * 
     * @param idOrigen      ID del almacen de origen.
     * @param idDestino     ID del almacen de destino.
     * @param distanciaKm   Distancia en kilometros.
     * @param descripcion   Descripcion de la ruta.
     */
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
     * Calcula la ruta mas corta entre dos almacenes.
     * 
     * @param idOrigen  ID del almacen de inicio.
     * @param idDestino ID del almacen de destino.
     * @return ResultadoDijkstra con el camino y distancia total.
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
            double minDist = Double.MAX_VALUE;
            int u = -1;
            for (int i = 0; i < numV; i++) {
                if (!visitados[i] && distancias[i] < minDist) {
                    minDist = distancias[i];
                    u = i;
                }
            }

            if (u == -1 || ids[u].equalsIgnoreCase(idDestino)) break;

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

    /**
     * Muestra una representacion en texto de la red logistica.
     * 
     * @return Cadena con el formato de la red.
     */
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
        sb.append(String.format("%n Vertices: %d  |  Aristas: %d%n", vertices.size(), numeroAristas));
        return sb.toString();
    }

    /**
     * Devuelve una lista en texto de los almacenes registrados.
     * 
     * @return Cadena con los almacenes.
     */
    public String listarAlmacenes() {
        if (vertices.isEmpty()) return "  No hay almacenes registrados.\n";
        StringBuilder sb = new StringBuilder();
        sb.append("  ALMACENES EN LA RED LOGISTICA:\n");
        sb.append("  \n");
        for (VerticeLogistico v : vertices) {
            sb.append("  ").append(v.almacen).append("\n");
        }
        return sb.toString();
    }

    /**
     * Clase auxiliar para almacenar el resultado de la busqueda de ruta.
     */
    public static class ResultadoDijkstra {
        /** Camino de nodos. */
        public final ListaEnlazada<String> camino;
        /** Distancia total del camino. */
        public final double distanciaTotal;
        /** Si es alcanzable. */
        public final boolean esAlcanzable;

        /**
         * Constructor.
         * @param camino Camino.
         * @param distanciaTotal Distancia.
         * @param esAlcanzable Si es alcanzable.
         */
        public ResultadoDijkstra(ListaEnlazada<String> camino, double distanciaTotal, boolean esAlcanzable) {
            this.camino = camino;
            this.distanciaTotal = distanciaTotal;
            this.esAlcanzable = esAlcanzable;
        }
    }

    /**
     * Elimina un almacen y todas sus conexiones.
     * 
     * @param id ID del almacen a eliminar.
     * @return true si se elimino correctamente, false si no existe.
     */
    public boolean eliminarAlmacen(String id) {
        VerticeLogistico v = obtenerVertice(id);
        if (v == null) return false;

        for (Arista a : v.aristas) {
            VerticeLogistico vecino = obtenerVertice(a.getIdDestino());
            if (vecino != null) {
                for (int i = 0; i < vecino.aristas.size(); i++) {
                    if (vecino.aristas.get(i).getIdDestino().equalsIgnoreCase(id)) {
                        vecino.aristas.remove(i);
                        break;
                    }
                }
            }
            this.numeroAristas--;
        }

        return vertices.remove(v);
    }

    /**
     * Elimina la conexion directa entre dos almacenes.
     * 
     * @param idOrigen  ID del almacen de origen.
     * @param idDestino ID del almacen de destino.
     * @return true si se elimino correctamente, false en caso contrario.
     */
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

    /** Obtiene el valor. @return Numero total de almacenes (vertices). */
    public int getNumeroVertices() { return vertices.size(); }

    /** Obtiene el valor. @return Numero total de rutas (aristas). */
    public int getNumeroAristas() { return numeroAristas; }

    /** Obtiene el valor. @return Lista de vertices en la red. */
    public ListaEnlazada<VerticeLogistico> getVertices() { return vertices; }

    /**
     * Obtiene el vertice correspondiente a un ID de almacen.
     * 
     * @param id ID del almacen.
     * @return El vertice logistico, o null si no se encuentra.
     */
    public VerticeLogistico obtenerVertice(String id) {
        for (VerticeLogistico v : vertices) {
            if (v.almacen.getId().equalsIgnoreCase(id.trim())) {
                return v;
            }
        }
        return null;
    }

    /**
     * Obtiene un almacen por su ID.
     * 
     * @param id ID del almacen.
     * @return El objeto Almacen, o null si no se encuentra.
     */
    public Almacen getAlmacen(String id) {
        VerticeLogistico v = obtenerVertice(id);
        return v != null ? v.almacen : null;
    }

    /**
     * Verifica si existe un almacen en la red.
     * 
     * @param id ID del almacen.
     * @return true si existe, false en caso contrario.
     */
    public boolean existeAlmacen(String id) {
        return obtenerVertice(id) != null;
    }
}
