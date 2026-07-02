package com.distribuidora.inventario.models;

import java.time.LocalDate;

/**
 * ================================================================
 * CLASE: ProductoPerecible
 * ================================================================
 * Representa un producto con fecha de vencimiento (ej. alimentos,
 * medicamentos, productos químicos). Extiende {@link Producto} y
 * añade campos específicos para gestión de caducidad.
 *
 * <p>Demuestra HERENCIA y POLIMORFISMO de la OOP: hereda todo el
 * comportamiento base y sobreescribe {@code getTipo()} y
 * {@code getDetallesAdicionales()} para mostrar sus propios campos.</p>
 *
 * @author  Equipo Proyecto 4 - Ingeniería de Sistemas UNMSM
 * @version 1.0
 */
public class ProductoPerecible extends Producto {

    // ----------------------------------------------------------------
    // CAMPOS PROPIOS DEL SUBTIPO (ENCAPSULAMIENTO)
    // ----------------------------------------------------------------

    /** Fecha límite de consumo o uso del producto. */
    private LocalDate fechaVencimiento;

    /** Temperatura máxima de almacenamiento en grados Celsius. */
    private double temperaturaMaxAlmacenamiento;

    /** Indica si requiere cadena de frío durante transporte. */
    private boolean requiereCadenaFrio;

    // ----------------------------------------------------------------
    // CONSTRUCTOR
    // ----------------------------------------------------------------

    /**
     * Constructor completo de ProductoPerecible.
     *
     * @param codigo                       Código único del producto.
     * @param nombre                       Nombre del producto.
     * @param categoria                    Categoría (ej. "Alimentos", "Medicamentos").
     * @param precioUnitario               Precio unitario en soles.
     * @param cantidadStock                Stock inicial.
     * @param stockMinimo                  Umbral mínimo de stock para alertas.
     * @param fechaVencimiento             Fecha de vencimiento del lote.
     * @param temperaturaMaxAlmacenamiento Temperatura máxima de almacenamiento (°C).
     * @param requiereCadenaFrio           {@code true} si debe mantenerse refrigerado.
     */
    public ProductoPerecible(String codigo, String nombre, String categoria,
                             double precioUnitario, int cantidadStock, int stockMinimo,
                             LocalDate fechaVencimiento, double temperaturaMaxAlmacenamiento,
                             boolean requiereCadenaFrio) {
        // Llama al constructor de la clase base (Producto)
        // HERENCIA - OOP: super() invoca el constructor del padre
        super(codigo, nombre, categoria, precioUnitario, cantidadStock, stockMinimo);
        this.fechaVencimiento             = fechaVencimiento;
        this.temperaturaMaxAlmacenamiento = temperaturaMaxAlmacenamiento;
        this.requiereCadenaFrio           = requiereCadenaFrio;
    }

    // ----------------------------------------------------------------
    // IMPLEMENTACIÓN DE MÉTODOS ABSTRACTOS (POLIMORFISMO - OOP)
    // ----------------------------------------------------------------

    /**
     * Identifica este producto como de tipo "Perecible".
     *
     * @return La cadena {@code "Perecible"}.
     */
    @Override
    public String getTipo() {
        return "Perecible";
    }

    /**
     * Retorna los detalles adicionales del producto perecible para
     * ser incluidos en el método {@code toString()} heredado del padre.
     *
     * @return Cadena formateada con campos de caducidad y temperatura.
     */
    @Override
    public String getDetallesAdicionales() {
        boolean vencido = LocalDate.now().isAfter(fechaVencimiento);
        return String.format(
            "│  [Perecible] Vence:  %-20s │%n" +
            "│  [Perecible] Temp.Max:  %-17.1f°C │%n" +
            "│  [Perecible] C.Frio: %-20s │%n" +
            "│  [Perecible] Estado: %-20s │%n",
            fechaVencimiento,
            temperaturaMaxAlmacenamiento,
            requiereCadenaFrio ? "SI" : "NO",
            vencido ? "¡VENCIDO!" : "Vigente"
        );
    }

    // ----------------------------------------------------------------
    // LÓGICA DE NEGOCIO ESPECÍFICA
    // ----------------------------------------------------------------

    /**
     * Verifica si el producto ya superó su fecha de vencimiento.
     *
     * @return {@code true} si la fecha actual es posterior a {@code fechaVencimiento}.
     */
    public boolean estaVencido() {
        return LocalDate.now().isAfter(fechaVencimiento);
    }

    /**
     * Calcula los días restantes hasta el vencimiento.
     * Retorna un valor negativo si ya venció.
     *
     * @return Número de días hasta o desde el vencimiento.
     */
    public long diasParaVencimiento() {
        return LocalDate.now().until(fechaVencimiento).getDays()
             + LocalDate.now().until(fechaVencimiento).getMonths() * 30L
             + LocalDate.now().until(fechaVencimiento).getYears() * 365L;
    }

    // ----------------------------------------------------------------
    // GETTERS Y SETTERS
    // ----------------------------------------------------------------

    /** @return Fecha de vencimiento del producto. */
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }

    /** @param fechaVencimiento Nueva fecha de vencimiento. */
    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    /** @return Temperatura máxima de almacenamiento en °C. */
    public double getTemperaturaMaxAlmacenamiento() { return temperaturaMaxAlmacenamiento; }

    /** @param temperatura Nueva temperatura máxima. */
    public void setTemperaturaMaxAlmacenamiento(double temperatura) {
        this.temperaturaMaxAlmacenamiento = temperatura;
    }

    /** @return {@code true} si el producto requiere cadena de frío. */
    public boolean isRequiereCadenaFrio() { return requiereCadenaFrio; }

    /** @param requiereCadenaFrio Nuevo valor del indicador de cadena de frío. */
    public void setRequiereCadenaFrio(boolean requiereCadenaFrio) {
        this.requiereCadenaFrio = requiereCadenaFrio;
    }
}
