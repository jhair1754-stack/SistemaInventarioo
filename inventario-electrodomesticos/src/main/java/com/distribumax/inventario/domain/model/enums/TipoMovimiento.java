package com.distribumax.inventario.domain.model.enums;

/**
 * Tipos de movimiento de inventario.
 * ENTRADA  — Ingreso de mercadería (compras, devoluciones de cliente).
 * SALIDA   — Egreso de mercadería (ventas, envíos).
 * AJUSTE   — Corrección manual de stock (auditoría, merma, avería).
 */
public enum TipoMovimiento {
    ENTRADA,
    SALIDA,
    AJUSTE
}
