package estructuras;

import modelos.Producto;

// ====================================================================
// [REQUISITO RUBRICA: ÁRBOL BINARIO DE BÚSQUEDA] - Clase NodoArbol
// ====================================================================
// Unidad atómica del árbol. Cada NodoArbol guarda un Producto
// y dos referencias (punteros) a sus hijos izquierdo y derecho.
//
//          [ NodoArbol ]
//          /            \
//   [izquierdo]     [derecho]
//   (cod < nodo)    (cod > nodo)
//
// ====================================================================

/**
 * ============================================================
 * ESTRUCTURA – NODO DEL ÁRBOL BINARIO DE BÚSQUEDA
 * ============================================================
 * Representa un nodo individual dentro del
 * {@link ArbolBinarioBusqueda}. Contiene el {@link Producto}
 * como dato y dos referencias hacia sus sub-árboles hijo.
 *
 * <p>Las referencias {@code izquierdo} y {@code derecho} son
 * los <i>punteros</i> que forman la estructura de árbol en
 * memoria RAM. Cuando valen {@code null}, indican que ese lado
 * del árbol está vacío (nodo hoja).</p>
 *
 * @author  Equipo Proyecto 4 – Ingeniería de Sistemas UNMSM
 * @version 2.0
 */
public class NodoArbol {

    // ─────────────────────────────────────────────────────────
    //  CAMPOS DEL NODO
    // ─────────────────────────────────────────────────────────

    /**
     * El producto almacenado en este nodo.
     * Es la "carga útil" del nodo; su {@code codigo} es la clave
     * de ordenamiento dentro del árbol.
     */
    Producto dato;

    /**
     * REFERENCIA → Sub-árbol IZQUIERDO.
     * Todos los nodos de este sub-árbol tienen un código
     * lexicográficamente MENOR al código de este nodo.
     * Vale {@code null} si no existe hijo izquierdo.
     */
    NodoArbol izquierdo;

    /**
     * REFERENCIA → Sub-árbol DERECHO.
     * Todos los nodos de este sub-árbol tienen un código
     * lexicográficamente MAYOR al código de este nodo.
     * Vale {@code null} si no existe hijo derecho.
     */
    NodoArbol derecho;

    // ─────────────────────────────────────────────────────────
    //  CONSTRUCTOR
    // ─────────────────────────────────────────────────────────

    /**
     * Crea un nuevo nodo hoja del árbol.
     * Ambas referencias hijo se inicializan en {@code null}:
     * el nodo nace sin hijos (es una hoja).
     *
     * <pre>
     *  Antes de insertar:   null  ← izquierdo
     *  [NodoArbol(dato)]
     *                       null  ← derecho
     * </pre>
     *
     * @param producto El producto a almacenar en este nodo.
     */
    public NodoArbol(Producto producto) {
        this.dato      = producto;  // Carga útil del nodo
        this.izquierdo = null;      // Puntero izq.: null → sin hijo izquierdo
        this.derecho   = null;      // Puntero der.: null → sin hijo derecho
    }

    // ─────────────────────────────────────────────────────────
    //  ACCESOR
    // ─────────────────────────────────────────────────────────

    /**
     * Retorna el producto almacenado en este nodo.
     *
     * @return El {@link Producto} almacenado.
     */
    public Producto getDato() { return dato; }

    /**
     * Reemplaza el producto almacenado en este nodo.
     * Usado durante la operación de eliminación (sucesor inorden).
     *
     * @param dato Nuevo producto.
     */
    public void setDato(Producto dato) { this.dato = dato; }
}
