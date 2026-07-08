package excepciones;

/**
 * EXCEPCIÓN PERSONALIZADA: CodigoProductoDuplicadoException
 * Se lanza cuando se intenta insertar en el BST un producto cuyo
 * código ya existe en el árbol (violación de clave única).
 *
 * Extiende {@code RuntimeException} (unchecked). Un código
 * duplicado es un error de lógica del que se espera que el
 * desarrollador tome medida correctiva inmediata.
 * 
 * @author Equipo 2
 * @version Beta
 */
public class CodigoProductoDuplicadoException extends RuntimeException {

    /** Código duplicado que causó el error. */
    private final String codigoDuplicado;

    /**
     * Constructor principal.
     * 
     * @param codigoDuplicado Código del producto que ya existe en el BST.
     */
    public CodigoProductoDuplicadoException(String codigoDuplicado) {
        super(String.format(
            "Ya existe un producto con el codigo '%s' en el inventario. " +
            "Los codigos de producto deben ser unicos. " +
            "Use la funcion de entrada de stock para modificar su cantidad.",
            codigoDuplicado
        ));
        this.codigoDuplicado = codigoDuplicado;
    }

    /** Obtiene el valor. @return Código duplicado que causó el error. */
    public String getCodigoDuplicado() { return codigoDuplicado; }
}
