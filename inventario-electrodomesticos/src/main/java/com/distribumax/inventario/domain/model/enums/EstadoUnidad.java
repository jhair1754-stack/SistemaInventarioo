package com.distribumax.inventario.domain.model.enums;

/**
 * Estado del ciclo de vida de una unidad física de producto.
 * Cada unidad con número de serie tiene un estado independiente.
 */
public enum EstadoUnidad {
    DISPONIBLE,
    VENDIDO,
    EN_GARANTIA,
    DEFECTUOSO,
    EN_TRANSITO
}
