package estructuras;

import modelos.Producto;

/**
 *  * Representa un nodo individual dentro del
 * {@link ArbolBinarioBusqueda}. Contiene el {@link Producto}
 * como dato y dos referencias hacia sus sub-árboles hijo.
 *
 * Las referencias {@code izquierdo} y {@code derecho} son
 * los punteros que forman la estructura de árbol en
 * memoria RAM. Cuando valen {@code null}, indican que ese lado
 * del árbol está vacío (nodo hoja).
 * 
 * @author Equipo 2
 * @version Beta
 */
public class NodoArbol {

    //  CAMPOS DEL NODO

    /**
     * El producto almacenado en este nodo.
     * Es la "carga útil" del nodo; su {@code codigo} es la clave
     * de ordenamiento dentro del árbol.
     */
    Producto dato;

    /**
     * Referencia al sub-árbol izquierdo.
     * Todos los nodos de este sub-árbol tienen un código
     * lexicográficamente MENOR al código de este nodo.
     * Vale {@code null} si no existe hijo izquierdo.
     */
    NodoArbol izquierdo;

    /**
     * Referencia al sub-árbol derecho.
     * Todos los nodos de este sub-árbol tienen un código
     * lexicográficamente MAYOR al código de este nodo.
     * Vale {@code null} si no existe hijo derecho.
     */
    NodoArbol derecho;

    //  CONSTRUCTOR

    /**
     * Crea un nuevo nodo hoja del árbol.
     * Ambas referencias hijo se inicializan en {@code null}:
     * el nodo nace sin hijos (es una hoja).
     * 
     * @param producto El producto a almacenar en este nodo.
     */
    public NodoArbol(Producto producto) {
        this.dato = producto;  // Carga útil del nodo
        this.izquierdo = null;      // Sin hijo izquierdo
        this.derecho = null;      // Sin hijo derecho
    }

    //  ACCESOR

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
