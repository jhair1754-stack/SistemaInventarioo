package excepciones;

/**
 * Excepcion lanzada cuando se proporciona una fecha de vencimiento que no es valida.
 * 
 * @author Equipo 2
 * @version Beta
 */
public class FechaVencimientoInvalidaException extends RuntimeException {

    /**
     * Constructor con mensaje de error.
     * 
     * @param mensaje Detalle del error de validacion de la fecha.
     */
    public FechaVencimientoInvalidaException(String mensaje) {
        super(mensaje);
    }
}
