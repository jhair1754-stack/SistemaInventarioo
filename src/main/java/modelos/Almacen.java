package modelos;

import estructuras.ArbolBinarioBusqueda;
import estructuras.PilaAuditoria;

/**
 * Representa un nodo (vértice) de la red logística de la empresa.
 * Cada almacén puede ser un centro de acopio, un hub de distribución
 * o una tienda de venta al público.
 *
 * Es el tipo de dato que {@code GrafoLogistica} almacena
 * en cada vértice de su lista de adyacencia.
 * 
 * @author Equipo 2
 * @version Beta
 */
public class Almacen {

    //  ENUM: TIPO DE NODO EN LA RED LOGÍSTICA

    /**
     * Clasificación del almacén dentro de la red logística.
     */
    public enum TipoAlmacen {
        /** Almacén principal de la empresa. */
        CENTRAL,
        /** Centro de distribución regional. */
        REGIONAL,
        /** Punto de venta al público. */
        TIENDA,
        /** Nodo de transbordo o consolidación. */
        HUB
    }

    //  CAMPOS (ENCAPSULAMIENTO)

    /** Identificador único del almacén (clave del vértice en el grafo). */
    private String id;

    /** Nombre descriptivo del almacén. */
    private String nombre;

    /** Dirección o zona geográfica. */
    private String ubicacion;

    /** Tipo de nodo dentro de la red logística. */
    private TipoAlmacen tipo;

    /** Capacidad máxima de almacenamiento en unidades. */
    private int capacidadMaxima;

    /** Árbol de productos específico de este almacén. */
    private ArbolBinarioBusqueda arbolProductos;

    /** Pila de auditoría específica de este almacén. */
    private PilaAuditoria pilaAuditoria;

    //  CONSTRUCTOR

    /**
     * Constructor completo de Almacen.
 * 
 * @param id              Identificador único (ej. {@code "ALM-01"}).
     * @param nombre          Nombre descriptivo.
     * @param ubicacion       Dirección o zona geográfica.
     * @param tipo            Tipo de nodo ({@link TipoAlmacen}).
     * @param capacidadMaxima Capacidad máxima en unidades.
     */
    public Almacen(String id, String nombre, String ubicacion,
                   TipoAlmacen tipo, int capacidadMaxima) {
        this.id = id.toUpperCase().trim();
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.tipo = tipo;
        this.capacidadMaxima = capacidadMaxima;
        this.arbolProductos = new ArbolBinarioBusqueda();
        this.pilaAuditoria = new PilaAuditoria();
    }

    //  GETTERS Y SETTERS

    /** Obtiene el valor. @return ID único del almacén. */
    public String getId() { return id; }
    /** Establece el valor. @param id Nuevo ID. */
    public void setId(String id) { this.id = id.toUpperCase().trim(); }

    /** Obtiene el valor. @return Nombre del almacén. */
    public String getNombre() { return nombre; }
    /** Establece el valor. @param n Nuevo nombre. */
    public void setNombre(String n) { this.nombre = n; }

    /** Obtiene el valor. @return Ubicación del almacén. */
    public String getUbicacion() { return ubicacion; }
    /** Establece el valor. @param u Nueva ubicación. */
    public void setUbicacion(String u){ this.ubicacion = u; }

    /** Obtiene el valor. @return Tipo de almacén. */
    public TipoAlmacen getTipo() { return tipo; }
    /** Establece el valor. @param t Nuevo tipo. */
    public void setTipo(TipoAlmacen t){ this.tipo = t; }

    /** Obtiene el valor. @return Capacidad máxima en unidades. */
    public int getCapacidadMaxima() { return capacidadMaxima; }
    /** Establece el valor. @param c Nueva capacidad máxima. */
    public void setCapacidadMaxima(int c) { this.capacidadMaxima = c; }

    /** Obtiene el valor. @return El árbol de productos de este almacén. */
    public ArbolBinarioBusqueda getArbolProductos() { return arbolProductos; }

    /** Obtiene el valor. @return La pila de auditoría de este almacén. */
    public PilaAuditoria getPilaAuditoria() { return pilaAuditoria; }

    //  REPRESENTACIÓN EN CADENA

    @Override
    public String toString() {
        return String.format("[%s] %-25s | %s | Tipo: %-8s | Cap: %,d ud.",
            id, nombre, ubicacion, tipo, capacidadMaxima);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Almacen a)) return false;
        return id.equalsIgnoreCase(a.id);
    }

    @Override
    public int hashCode() {
        return id.toLowerCase().hashCode();
    }
}
