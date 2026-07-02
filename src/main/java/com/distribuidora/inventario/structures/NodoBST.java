package com.distribuidora.inventario.structures;

import com.distribuidora.inventario.models.Producto;

// ====================================================================
// [REQUISITO RUBRICA: ÁRBOL BINARIO DE BÚSQUEDA] - Clase NodoBST
// Cada objeto NodoBST es un nodo del árbol. Contiene el dato
// (un Producto), y dos referencias (punteros) a sus hijos
// izquierdo y derecho. Esta es la unidad fundamental del BST.
// ====================================================================

/**
 * ================================================================
 * CLASE: NodoBST
 * ================================================================
 * Nodo individual del Árbol Binario de Búsqueda (BST).
 * Cada nodo almacena un {@link Producto} como dato y mantiene
 * dos referencias a sus subárboles: izquierdo y derecho.
 *
 * <p>La referencia {@code izquierdo} apunta a nodos con código
 * menor (lexicográficamente) al nodo actual. La referencia
 * {@code derecho} apunta a nodos con código mayor.</p>
 *
 * <p><b>Estructura de un nodo:</b></p>
 * <pre>
 *        [dato: Producto]
 *        /              \
 *  [izquierdo]       [derecho]
 *  (cod < actual)   (cod > actual)
 * </pre>
 *
 * @author  Equipo Proyecto 4 - Ingeniería de Sistemas UNMSM
 * @version 1.0
 */
public class NodoBST {

    // ----------------------------------------------------------------
    // CAMPOS DEL NODO
    // ----------------------------------------------------------------

    /**
     * Dato almacenado en este nodo: el producto del inventario.
     * Es la "carga útil" del nodo.
     */
    Producto dato;

    /**
     * REFERENCIA/PUNTERO al hijo izquierdo.
     * Apunta al subárbol de productos con código MENOR al de este nodo.
     * Vale {@code null} si no tiene hijo izquierdo.
     */
    NodoBST izquierdo;

    /**
     * REFERENCIA/PUNTERO al hijo derecho.
     * Apunta al subárbol de productos con código MAYOR al de este nodo.
     * Vale {@code null} si no tiene hijo derecho.
     */
    NodoBST derecho;

    // ----------------------------------------------------------------
    // CONSTRUCTOR
    // ----------------------------------------------------------------

    /**
     * Crea un nuevo nodo hoja del BST (sin hijos).
     * Inicialmente, ambas referencias hija son {@code null},
     * lo que indica que es un nodo hoja (extremo del árbol).
     *
     * @param producto El producto a almacenar en este nodo.
     */
    public NodoBST(Producto producto) {
        this.dato      = producto;   // Dato del nodo
        this.izquierdo = null;       // Puntero izquierdo: null (sin hijo izq.)
        this.derecho   = null;       // Puntero derecho: null (sin hijo der.)
    }

    // ----------------------------------------------------------------
    // ACCESORES
    // ----------------------------------------------------------------

    /** @return El producto almacenado en este nodo. */
    public Producto getDato() { return dato; }

    /** @param dato Nuevo producto para este nodo. */
    public void setDato(Producto dato) { this.dato = dato; }
}
