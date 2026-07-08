package estructuras;

/**
 *  * Representa una conexión con peso entre dos almacenes dentro
 * del {@link GrafoLogistico}. Se almacena en la lista de
 * adyacencia del vértice origen.
 * 
 * @author Equipo 2
 * @version Beta
 */
public class Arista {

    //  CAMPOS

    /**
     * ID del almacén DESTINO de esta conexión.
     */
    private final String idDestino;

    /**
     * PESO de la arista: distancia en kilómetros (o costo de transporte).
     * Usado por el algoritmo de Dijkstra para encontrar la ruta óptima.
     */
    private double peso;

    /**
     * Descripción de la vía o carretera que une los dos almacenes.
     */
    private String descripcion;

    //  CONSTRUCTOR

    /**
     * Crea una arista hacia el almacén destino.
 * 
 * @param idDestino   ID del almacén de destino.
     * @param peso        Peso (distancia en km o costo).
     * @param descripcion Descripción de la ruta o vía.
     */
    public Arista(String idDestino, double peso, String descripcion) {
        this.idDestino = idDestino.toUpperCase().trim();
        this.peso = peso;
        this.descripcion = descripcion;
    }

    //  ACCESORES

    /** Obtiene el valor. @return ID del almacén de destino. */
    public String getIdDestino() { return idDestino; }

    /** Obtiene el valor. @return Distancia en km (peso de la arista). */
    public double getPeso() { return peso; }
    /**
     * Obtiene la distancia en km.
     * @return Distancia en km.
     */
    public double getDistanciaKm() { return peso; }

    /** Obtiene el valor. @return Descripción de la ruta. */
    public String getDescripcion() { return descripcion; }

    @Override
    public String toString() {
        return String.format(" --(%.1f km)-> [%s] via %s",
            peso, idDestino, descripcion);
    }
}
