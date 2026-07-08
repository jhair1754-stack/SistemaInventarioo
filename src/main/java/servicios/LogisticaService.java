package servicios;

import excepciones.AlmacenNoEncontradoException;
import modelos.Almacen;
import estructuras.GrafoLogistico;
import estructuras.GrafoLogistico.ResultadoDijkstra;

import estructuras.ListaEnlazada;

/**
 * Capa de lógica de negocio que gestiona la red logística
 * delegando al {@link GrafoLogistico}.
 * 
 * @author Equipo 2
 * @version Beta
 */
public class LogisticaService {

    /**
     * GRAFO LOGÍSTICO que modela la red de almacenes y rutas.
     *
     * Instancia del grafo en el
     * servicio de logística.
     */
    private final GrafoLogistico grafo;

    /** Crea el servicio de logística con el grafo vacío. */
    public LogisticaService() {
        this.grafo = new GrafoLogistico();
    }

    /**
     * Agrega un almacén (vértice) a la red logística.
 * 
 * @param almacen El almacén a agregar.
     */
    public void agregarAlmacen(Almacen almacen) {
        grafo.agregarAlmacen(almacen);
        System.out.printf("  [OK] Almacen '%s' (%s) agregado a la red.%n",
            almacen.getId(), almacen.getNombre());
    }

    /**
     * Conecta dos almacenes con una ruta bidireccional.
 * 
 * @param idOrigen    ID del almacén de origen.
     * @param idDestino   ID del almacén de destino.
     * @param distanciaKm Distancia en kilómetros.
     * @param descripcion Descripción de la vía.
     * @throws AlmacenNoEncontradoException si algún ID no existe.
     */
    public void conectarAlmacenes(String idOrigen, String idDestino,
                                   double distanciaKm, String descripcion) {
        grafo.conectar(idOrigen, idDestino, distanciaKm, descripcion);
        System.out.printf("  [OK] Ruta: [%s] <--(%.1f km)--> [%s] via %s%n",
            idOrigen, distanciaKm, idDestino, descripcion);
    }

    /**
     * Calcula y muestra la ruta óptima entre dos almacenes (Dijkstra).
 * 
 * @param idOrigen  ID de inicio.
     * @param idDestino ID de destino.
     * @throws AlmacenNoEncontradoException si algún ID no existe.
     */
    public void calcularRutaOptima(String idOrigen, String idDestino) {
        ResultadoDijkstra res = grafo.dijkstra(idOrigen, idDestino);

        System.out.println();
        System.out.println("  ╔══════════════════════════════════════════════╗");
        System.out.println("  ║       RUTA OPTIMA – ALGORITMO DIJKSTRA      ║");
        System.out.println("  ╠══════════════════════════════════════════════╣");

        if (!res.esAlcanzable) {
            System.out.println("  ║  No existe ruta entre los almacenes dados. ║");
        } else {
            System.out.printf("  ║  Origen  : %-33s║%n", idOrigen);
            System.out.printf("  ║  Destino : %-33s║%n", idDestino);
            System.out.printf("  ║  Dist.   : %-30.2f km║%n", res.distanciaTotal);
            System.out.println("  ╠══════════════════════════════════════════════╣");
            System.out.println("  ║  Recorrido:                                  ║");

            // Mostrar el camino
            StringBuilder ruta = new StringBuilder("  ║    ");
            ListaEnlazada<String> camino = res.camino;
            for (int i = 0; i < camino.size(); i++) {
                ruta.append("[").append(camino.get(i)).append("]");
                if (i < camino.size() - 1) ruta.append(" -> ");
            }
            // Mostrar en líneas de hasta 46 caracteres
            String rutaStr = ruta.toString();
            while (rutaStr.length() > 0) {
                int end = Math.min(46, rutaStr.length());
                System.out.printf("  ║  %-44s║%n", rutaStr.substring(0, end).trim());
                rutaStr = rutaStr.substring(end).trim();
            }
        }
        System.out.println("  ╚══════════════════════════════════════════════╝");
    }

    /** Muestra la lista de adyacencia completa del grafo. */
    public void mostrarRedLogistica() {
        System.out.println(grafo.mostrarRed());
    }

    /** Lista todos los almacenes registrados. */
    public void listarAlmacenes() {
        System.out.println(grafo.listarAlmacenes());
    }

    /**
     * Genera el siguiente ID de almacén con el formato ALM-XXX
     */
    /**
     * Genera un codigo de almacen.
     * @return Codigo generado.
     */
    public String generarCodigoAlmacen() {
        int max = 0;
        ListaEnlazada<GrafoLogistico.VerticeLogistico> vertices = grafo.getVertices();
        for (GrafoLogistico.VerticeLogistico v : vertices) {
            String id = v.almacen.getId();
            if (id.startsWith("ALM-")) {
                try {
                    int num = Integer.parseInt(id.substring(4));
                    if (num > max) max = num;
                } catch (NumberFormatException ignored) {}
            }
        }
        max = Math.max(max, 6); // Asegurar que empiece en ALM-07
        return String.format("ALM-%02d", max + 1);
    }

    //  GETTERS

    /**
     * Elimina un almacen.
     * @param id ID del almacen.
     * @return true si se elimino.
     */
    public boolean eliminarAlmacen(String id) {
        boolean exito = grafo.eliminarAlmacen(id);
        if (exito) {
            System.out.printf("  [OK] Almacen '%s' eliminado de la red.%n", id);
        }
        return exito;
    }

    /**
     * Elimina una ruta.
     * @param idOrigen ID origen.
     * @param idDestino ID destino.
     * @return true si se elimino.
     */
    public boolean eliminarRuta(String idOrigen, String idDestino) {
        boolean exito = grafo.eliminarRuta(idOrigen, idDestino);
        if (exito) {
            System.out.printf("  [OK] Ruta entre '%s' y '%s' eliminada.%n", idOrigen, idDestino);
        }
        return exito;
    }

    /** Obtiene el valor. @return Referencia al grafo logístico. */
    public GrafoLogistico getGrafo() { return grafo; }

    /** Obtiene el valor. @return Número de almacenes. */
    public int getNumeroAlmacenes() { return grafo.getNumeroVertices(); }

    /** Obtiene el valor. @return Número de rutas. */
    public int getNumeroRutas() { return grafo.getNumeroAristas(); }

    /** Verifica existencia.
     * @param id ID del almacen.
     * @return {@code true} si existe. */
    public boolean existeAlmacen(String id) { return grafo.existeAlmacen(id); }

    /** Obtiene el almacen.
     * @param id ID del almacen.
     * @return El almacen. */
    public Almacen getAlmacen(String id) { return grafo.getAlmacen(id); }
}
