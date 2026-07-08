package excepciones;

/**
 * EXCEPCIÓN PERSONALIZADA: AlmacenNoEncontradoException
 * Se lanza cuando se intenta operar sobre un almacén cuyo ID
 * no existe en el grafo logístico.
 *
 * Extiende {@code RuntimeException} (unchecked) ya que representa
 * un error de referencia a un vértice inválido del grafo.
 * 
 * @author Equipo 2
 * @version Beta
 */
public class AlmacenNoEncontradoException extends RuntimeException {

    /** ID del almacén que no fue hallado en el grafo. */
    private final String idAlmacen;

    /**
     * Constructor principal.
     * 
     * @param idAlmacen ID del almacén no encontrado en el grafo.
     */
    public AlmacenNoEncontradoException(String idAlmacen) {
        super(String.format(
            "El almacen con ID '%s' no existe en la red logistica. " +
            "Agregue el almacen antes de realizar operaciones sobre el.",
            idAlmacen
        ));
        this.idAlmacen = idAlmacen;
    }

    /** Obtiene el valor. @return ID del almacén que no fue encontrado. */
    public String getIdAlmacen() { return idAlmacen; }
}
