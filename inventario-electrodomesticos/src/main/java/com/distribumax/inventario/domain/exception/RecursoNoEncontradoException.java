package com.distribumax.inventario.domain.exception;

/**
 * Excepción de dominio lanzada cuando no se encuentra un recurso
 * solicitado por su identificador.
 */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String entidad, Long id) {
        super(String.format("No se encontró %s con ID: %d", entidad, id));
    }

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }

}
