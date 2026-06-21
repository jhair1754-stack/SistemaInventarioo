package com.distribumax.inventario.domain.exception;

/**
 * Excepción de dominio lanzada cuando se intenta registrar una unidad
 * con un número de serie que ya existe en el sistema.
 */
public class NumeroSerieExistenteException extends RuntimeException {

    public NumeroSerieExistenteException(String numeroSerie) {
        super("Ya existe una unidad registrada con el número de serie: " + numeroSerie);
    }

}
