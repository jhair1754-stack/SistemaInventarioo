package com.distribuidora.inventario.models;

/**
 * ================================================================
 * CLASE: ProductoGeneral
 * ================================================================
 * Representa un producto no perecible (ej. electrodomésticos, ropa,
 * herramientas, materiales de oficina). Extiende {@link Producto}
 * con atributos específicos de productos durables.
 *
 * <p>Segunda hoja de la jerarquía de herencia del modelo. Demuestra
 * POLIMORFISMO al implementar de manera distinta {@code getTipo()}
 * y {@code getDetallesAdicionales()} en comparación con
 * {@link ProductoPerecible}.</p>
 *
 * @author  Equipo Proyecto 4 - Ingeniería de Sistemas UNMSM
 * @version 1.0
 */
public class ProductoGeneral extends Producto {

    // ----------------------------------------------------------------
    // CAMPOS PROPIOS DEL SUBTIPO (ENCAPSULAMIENTO)
    // ----------------------------------------------------------------

    /** Marca comercial del producto. */
    private String marca;

    /** Modelo o referencia del fabricante. */
    private String modelo;

    /** País de origen/fabricación. */
    private String paisOrigen;

    /** Peso en kilogramos por unidad. */
    private double pesoPorUnidad;

    /** Garantía en meses ofrecida al cliente. */
    private int garantiaMeses;

    // ----------------------------------------------------------------
    // CONSTRUCTOR
    // ----------------------------------------------------------------

    /**
     * Constructor completo de ProductoGeneral.
     *
     * @param codigo         Código único del producto.
     * @param nombre         Nombre del producto.
     * @param categoria      Categoría (ej. "Electrónico", "Ropa", "Herramienta").
     * @param precioUnitario Precio unitario en soles.
     * @param cantidadStock  Stock inicial.
     * @param stockMinimo    Umbral mínimo de stock para alertas.
     * @param marca          Marca comercial del producto.
     * @param modelo         Modelo o referencia del fabricante.
     * @param paisOrigen     País de fabricación.
     * @param pesoPorUnidad  Peso por unidad en kilogramos.
     * @param garantiaMeses  Garantía ofrecida en meses.
     */
    public ProductoGeneral(String codigo, String nombre, String categoria,
                           double precioUnitario, int cantidadStock, int stockMinimo,
                           String marca, String modelo, String paisOrigen,
                           double pesoPorUnidad, int garantiaMeses) {
        // HERENCIA - OOP: invoca al constructor de la clase padre Producto
        super(codigo, nombre, categoria, precioUnitario, cantidadStock, stockMinimo);
        this.marca         = marca;
        this.modelo        = modelo;
        this.paisOrigen    = paisOrigen;
        this.pesoPorUnidad = pesoPorUnidad;
        this.garantiaMeses = garantiaMeses;
    }

    // ----------------------------------------------------------------
    // IMPLEMENTACIÓN DE MÉTODOS ABSTRACTOS (POLIMORFISMO - OOP)
    // ----------------------------------------------------------------

    /**
     * Identifica este producto como de tipo "General".
     *
     * @return La cadena {@code "General (No Perecible)"}.
     */
    @Override
    public String getTipo() {
        return "General (No Perecible)";
    }

    /**
     * Retorna los detalles adicionales del producto general para
     * ser incluidos en el método {@code toString()} heredado del padre.
     *
     * @return Cadena formateada con campos de marca, modelo y garantía.
     */
    @Override
    public String getDetallesAdicionales() {
        return String.format(
            "│  [General] Marca:    %-20s │%n" +
            "│  [General] Modelo:   %-20s │%n" +
            "│  [General] Origen:   %-20s │%n" +
            "│  [General] Peso:     %-17.2f kg │%n" +
            "│  [General] Garantia: %-17d mes │%n",
            marca, modelo, paisOrigen, pesoPorUnidad, garantiaMeses
        );
    }

    // ----------------------------------------------------------------
    // GETTERS Y SETTERS
    // ----------------------------------------------------------------

    /** @return Marca comercial del producto. */
    public String getMarca() { return marca; }

    /** @param marca Nueva marca. */
    public void setMarca(String marca) { this.marca = marca; }

    /** @return Modelo o referencia del fabricante. */
    public String getModelo() { return modelo; }

    /** @param modelo Nuevo modelo. */
    public void setModelo(String modelo) { this.modelo = modelo; }

    /** @return País de origen. */
    public String getPaisOrigen() { return paisOrigen; }

    /** @param paisOrigen Nuevo país de origen. */
    public void setPaisOrigen(String paisOrigen) { this.paisOrigen = paisOrigen; }

    /** @return Peso por unidad en kilogramos. */
    public double getPesoPorUnidad() { return pesoPorUnidad; }

    /** @param pesoPorUnidad Nuevo peso por unidad. */
    public void setPesoPorUnidad(double pesoPorUnidad) { this.pesoPorUnidad = pesoPorUnidad; }

    /** @return Garantía en meses. */
    public int getGarantiaMeses() { return garantiaMeses; }

    /** @param garantiaMeses Nueva garantía en meses. */
    public void setGarantiaMeses(int garantiaMeses) { this.garantiaMeses = garantiaMeses; }
}
