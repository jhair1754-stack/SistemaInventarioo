package modelos;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Clase base que representa un producto del inventario de la
 * empresa distribuidora. Aplica encapsulamiento estricto.
 *
 * Sirve como tipo unificado para el Árbol Binario de Búsqueda
 * (se ordena por {@code codigo}) y como dato en la pila de
 * auditoría a través de {@link Transaccion}.
 * 
 * @author Equipo 2
 * @version Beta
 */
public class Producto {

    //  CAMPOS (ENCAPSULAMIENTO - OOP)

    /** Código alfanumérico único. Es la clave de comparación en el BST. */
    private String codigo;

    /** Nombre comercial del producto. */
    private String nombre;

    /** Categoría del producto (ej. "Electrónico", "Alimento"). */
    private String categoria;

    /** Precio unitario en soles. */
    private double precioUnitario;

    /** Unidades disponibles en el inventario. */
    private int cantidadStock;

    /** Umbral mínimo: si {@code cantidadStock} cae debajo, se emite alerta. */
    private int stockMinimo;

    /**
     * Tipo del producto: {@code "GENERAL"} o {@code "PERECIBLE"}.
     * Permite polimorfismo básico sin subclases separadas para simplificar
     * la integración con el BST genérico.
     */
    private String tipo;

    /** Fecha de vencimiento (solo relevante si {@code tipo == "PERECIBLE"}). */
    private LocalDate fechaVencimiento;

    /** Marca comercial (solo para {@code tipo == "GENERAL"}). */
    private String marca;

    /** Momento exacto del registro en el sistema. */
    private final LocalDateTime fechaRegistro;

    //  CONSTRUCTORES

    /**
     * Constructor para producto General (no perecible).
 * 
 * @param codigo         Código único (clave BST).
     * @param nombre         Nombre del producto.
     * @param categoria      Categoría comercial.
     * @param precioUnitario Precio unitario (S/).
     * @param cantidadStock  Stock inicial.
     * @param stockMinimo    Stock mínimo de alerta.
     * @param marca          Marca comercial.
     */
    public Producto(String codigo, String nombre, String categoria,
                    double precioUnitario, int cantidadStock,
                    int stockMinimo, String marca) {
        this.codigo = codigo.toUpperCase().trim();
        this.nombre = nombre;
        this.categoria = categoria;
        this.precioUnitario = precioUnitario;
        this.cantidadStock = cantidadStock;
        this.stockMinimo = stockMinimo;
        this.marca = marca;
        this.tipo = "GENERAL";
        this.fechaVencimiento = null;
        this.fechaRegistro = LocalDateTime.now();
    }

    /**
     * Constructor para producto Perecible.
 * 
 * @param codigo           Código único (clave BST).
     * @param nombre           Nombre del producto.
     * @param categoria        Categoría comercial.
     * @param precioUnitario   Precio unitario (S/).
     * @param cantidadStock    Stock inicial.
     * @param stockMinimo      Stock mínimo de alerta.
     * @param fechaVencimiento Fecha límite de consumo.
     */
    public Producto(String codigo, String nombre, String categoria,
                    double precioUnitario, int cantidadStock,
                    int stockMinimo, LocalDate fechaVencimiento) {
        this.codigo = codigo.toUpperCase().trim();
        this.nombre = nombre;
        this.categoria = categoria;
        this.precioUnitario = precioUnitario;
        this.cantidadStock = cantidadStock;
        this.stockMinimo = stockMinimo;
        this.fechaVencimiento = fechaVencimiento;
        this.tipo = "PERECIBLE";
        this.marca = "N/A";
        this.fechaRegistro = LocalDateTime.now();
    }

    //  LÓGICA DE NEGOCIO

    /**
     * Indica si el stock actual está por debajo del umbral mínimo.
 * 
 * @return {@code true} si {@code cantidadStock < stockMinimo}.
     */
    public boolean estaEnStockCritico() {
        return this.cantidadStock < this.stockMinimo;
    }

    /**
     * Indica si un producto perecible ya superó su fecha de vencimiento.
 * 
 * @return {@code true} si la fecha de hoy es posterior a {@code fechaVencimiento}.
     *         Para productos no perecibles retorna siempre {@code false}.
     */
    public boolean estaVencido() {
        if (!"PERECIBLE".equals(tipo) || fechaVencimiento == null) return false;
        return LocalDate.now().isAfter(fechaVencimiento);
    }

    /**
     * Aumenta el stock del producto.
 * 
 * @param cantidad Unidades a añadir (debe ser &gt; 0).
     * @throws IllegalArgumentException si {@code cantidad} ≤ 0.
     */
    public void aumentarStock(int cantidad) {
        if (cantidad <= 0)
            throw new IllegalArgumentException(
                "La cantidad a ingresar debe ser positiva. Recibido: " + cantidad);
        this.cantidadStock += cantidad;
    }

    /**
     * Disminuye el stock del producto.
     * No valida si hay suficiente stock; esa responsabilidad
     * pertenece al servicio que llama a este método.
 * 
 * @param cantidad Unidades a retirar (debe ser &gt; 0).
     * @throws IllegalArgumentException si {@code cantidad} ≤ 0.
     */
    public void disminuirStock(int cantidad) {
        if (cantidad <= 0)
            throw new IllegalArgumentException(
                "La cantidad a retirar debe ser positiva. Recibido: " + cantidad);
        this.cantidadStock -= cantidad;
    }

    //  GETTERS Y SETTERS

    /** Obtiene el valor. @return Código único del producto. */
    public String getCodigo() { return codigo; }
    /** Establece el valor. @param c Nuevo código. */
    public void setCodigo(String c) { this.codigo = c.toUpperCase().trim(); }

    /** Obtiene el valor. @return Nombre del producto. */
    public String getNombre() { return nombre; }
    /** Establece el valor. @param n Nuevo nombre. */
    public void setNombre(String n) { this.nombre = n; }

    /** Obtiene el valor. @return Categoría del producto. */
    public String getCategoria() { return categoria; }
    /** Establece el valor. @param c Nueva categoría. */
    public void setCategoria(String c){ this.categoria = c; }

    /** Obtiene el valor. @return Precio unitario en S/. */
    public double getPrecioUnitario() { return precioUnitario; }
    /** Establece el valor. @param p Nuevo precio unitario. */
    public void setPrecioUnitario(double p) { this.precioUnitario = p; }

    /** Obtiene el valor. @return Cantidad actual en stock. */
    public int getCantidadStock() { return cantidadStock; }
    /** Establece el valor. @param s Nuevo valor directo del stock. */
    public void setCantidadStock(int s) { this.cantidadStock = s; }

    /** Obtiene el valor. @return Umbral mínimo de stock. */
    public int getStockMinimo() { return stockMinimo; }
    /** Establece el valor. @param min Nuevo umbral mínimo. */
    public void setStockMinimo(int min) { this.stockMinimo = min; }

    /** Obtiene el valor. @return Tipo del producto ({@code "GENERAL"} o {@code "PERECIBLE"}). */
    public String getTipo() { return tipo; }

    /** Obtiene el valor. @return Marca comercial (solo General). */
    public String getMarca() { return marca; }
    /** Establece el valor. @param m Nueva marca. */
    public void setMarca(String m) { this.marca = m; }

    /** Obtiene el valor. @return Fecha de vencimiento (solo Perecible), o {@code null}. */
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    /** Establece el valor. @param f Nueva fecha de vencimiento. */
    public void setFechaVencimiento(LocalDate f){ this.fechaVencimiento = f; }

    /** Obtiene el valor. @return Fecha y hora de registro en el sistema. */
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }

    //  REPRESENTACIÓN EN CADENA

    /**
     * Retorna una ficha completa del producto para mostrar en consola.
 * 
 * @return Cadena formateada con todos los campos del producto.
     */
    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String alerta = estaEnStockCritico() ? "  *** STOCK CRITICO ***" : "";
        String venc = (fechaVencimiento != null)
            ? "  Vencimiento: " + fechaVencimiento : "";
        String marcaStr = !"N/A".equals(marca)
            ? "  Marca:       " + marca : "";
        return String.format(
            "%n" +
            "  Codigo    : %-27s%n" +
            "  Tipo      : %-27s%n" +
            "  Nombre    : %-27s%n" +
            "  Categoria : %-27s%n" +
            "  Precio    : S/ %-24.2f%n" +
            "  Stock     : %-27d%n" +
            "  StockMin  : %-27d%n" +
            "  Registrado: %-27s%n" +
            "%s%s%s" +
            "",
            codigo, tipo, nombre, categoria, precioUnitario,
            cantidadStock, stockMinimo, fmt.format(fechaRegistro),
            venc.isEmpty()   ? "" : String.format("  %-40s%n", venc.trim()),
            marcaStr.isEmpty()? "" : String.format("  %-40s%n", marcaStr.trim()),
            alerta.isEmpty() ? "" : String.format("  %-40s%n", alerta.trim())
        );
    }
}
