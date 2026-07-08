package excepciones;

/**
 * EXCEPCIÓN PERSONALIZADA: ProductoNoEncontradoException
 * Se lanza cuando se busca un producto por código en el BST
 * y no existe ningún nodo con ese código.
 *
 * Extiende {@code RuntimeException} (unchecked) ya que representa
 * un error de lógica del cliente que provee un código inválido.
 * 
 * @author Equipo 2
 * @version Beta
 */
public class ProductoNoEncontradoException extends RuntimeException {

    /** Código buscado que no fue hallado en el árbol. */
    private final String codigoBuscado;

    /**
     * Constructor principal.
     * 
     * @param codigoBuscado Código del producto que no fue encontrado.
     */
    public ProductoNoEncontradoException(String codigoBuscado) {
        super(String.format(
            "No se encontro ningun producto con el codigo '%s' en el inventario. " +
            "Verifique el codigo e intente nuevamente.",
            codigoBuscado
        ));
        this.codigoBuscado = codigoBuscado;
    }

    /**
     * Constructor con mensaje personalizado.
     * 
     * @param codigoBuscado Código del producto que no fue encontrado.
     * @param mensaje       Mensaje descriptivo adicional.
     */
    public ProductoNoEncontradoException(String codigoBuscado, String mensaje) {
        super(mensaje);
        this.codigoBuscado = codigoBuscado;
    }

    /** Obtiene el valor. @return Código del producto que no fue encontrado. */
    public String getCodigoBuscado() { return codigoBuscado; }
}
