package com.distribuidora.inventario.exceptions;

public class FechaVencimientoInvalidaException extends RuntimeException {
    public FechaVencimientoInvalidaException(String mensaje) {
        super(mensaje);
    }
}
