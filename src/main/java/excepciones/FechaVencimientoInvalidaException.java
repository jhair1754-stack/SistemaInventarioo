package excepciones;

public class FechaVencimientoInvalidaException extends RuntimeException {
    public FechaVencimientoInvalidaException(String mensaje) {
        super(mensaje);
    }
}
