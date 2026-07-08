package estructuras;

import excepciones.CodigoProductoDuplicadoException;
import excepciones.ProductoNoEncontradoException;
import modelos.Producto;

/**
 * Árbol Binario de Búsqueda (BST) que organiza y recupera
 * {@link Producto}s por su campo {@code codigo} de forma eficiente.
 *
 * <h2>Operaciones disponibles</h2>
 * 
 *   {@link #insertar(Producto)}           – O(log n) amortizado.
 *   {@link #buscar(String)}               – O(log n) amortizado.
 *   {@link #existe(String)}               – O(log n), sin excepción.
 *   {@link #eliminar(String)}             – O(log n) amortizado.
 *   {@link #recorrerInorden()}            – O(n), lista ordenada.
 *   {@link #recorrerPreorden()}           – O(n).
 *   {@link #obtenerProductosCriticos()}   – O(n).
 *   {@link #obtenerAltura()}              – O(n).
 * 
@author Equipo 2
 * @version Beta
 */
public class ArbolBinarioBusqueda {

    //  RAÍZ DEL ÁRBOL

    /**
     * REFERENCIA a la raíz del árbol.
     *
     * Nodo principal del árbol.
 * 
Vale {@code null} cuando el árbol está completamente vacío.
     */
    private NodoArbol raiz;

    /** Número de productos almacenados en el árbol. */
    private int tamanio;

    //  CONSTRUCTOR

    /**
     * Crea un árbol binario de búsqueda vacío.
     * La raíz se inicializa en {@code null} (árbol vacío).
     */
    public ArbolBinarioBusqueda() {
        this.raiz = null;    // Árbol vacío: ningún nodo existe aún
        this.tamanio = 0;
    }

    /**
     * Inserta un nuevo producto en el árbol, respetando la propiedad BST.
     * Si la raíz es null, el nuevo nodo será la raíz.
     *   Comparar el código del nuevo producto con el nodo actual:
     *     
     *       Código menor va a la izquierda.
     *       Código mayor va a la derecha.
     *       Código igual lanza excepción.
     * Al llegar a un puntero {@code null}, insertar aquí el nuevo nodo.
     * @param producto Producto a insertar (código único requerido).
     * @throws CodigoProductoDuplicadoException si el código ya existe.
     */
    public void insertar(Producto producto) {
        this.raiz = insertarRec(this.raiz, producto);
        this.tamanio++;
    }

    /**
     * Auxiliar recursivo para {@link #insertar(Producto)}.
     *
     * Navega el árbol hasta encontrar la posición correcta (hoja nula)
     * y devuelve la nueva raíz del sub-árbol modificado.
 * 
 * @param nodo     Nodo evaluado en esta llamada recursiva.
     * @param producto Producto que se quiere insertar.
     * @return La raíz del sub-árbol tras la inserción.
     */
    private NodoArbol insertarRec(NodoArbol nodo, Producto producto) {

        //  CASO BASE 
        // Insertar nuevo nodo aquí
        if (nodo == null) {
            return new NodoArbol(producto);  // Crear nodo hoja
        }

        //  COMPARACIÓN DE CÓDIGOS 
        int cmp = producto.getCodigo()
                          .compareToIgnoreCase(nodo.dato.getCodigo());

        if (cmp < 0) {
            // Navegar a la izquierda
            // Actualizamos el puntero izquierdo con el resultado recursivo
            nodo.izquierdo = insertarRec(nodo.izquierdo, producto);

        } else if (cmp > 0) {
            // Navegar a la derecha
            nodo.derecho = insertarRec(nodo.derecho, producto);

        } else {
            // Manejar duplicado
            this.tamanio--;   // Corregimos porque insertar() ya sumó 1
            throw new CodigoProductoDuplicadoException(producto.getCodigo());
        }

        return nodo;  // Devolver el nodo actual sin cambios (solo su hijo cambió)
    }

    /**
     * Busca y retorna un producto por su código.
     * 
     * @param codigo Código del producto a buscar.
     * @return El {@link Producto} encontrado.
     * @throws ProductoNoEncontradoException si el código no existe en el árbol.
     */
    public Producto buscar(String codigo) {
        NodoArbol resultado = buscarRec(this.raiz, codigo);
        if (resultado == null) {
            throw new ProductoNoEncontradoException(codigo);
        }
        return resultado.dato;
    }

    /**
     * Auxiliar recursivo para {@link #buscar(String)}.
     * 
     * @param nodo   Nodo evaluado en esta llamada.
     * @param codigo Código a buscar.
     * @return El nodo que contiene el producto, o {@code null} si no existe.
     */
    private NodoArbol buscarRec(NodoArbol nodo, String codigo) {

        // Producto no existe
        if (nodo == null) return null;

        int cmp = codigo.compareToIgnoreCase(nodo.dato.getCodigo());

        if (cmp == 0) {
            // ENCONTRADO: devolver este nodo
            return nodo;

        } else if (cmp < 0) {
            // Buscar en el sub-árbol IZQUIERDO (códigos menores)
            return buscarRec(nodo.izquierdo, codigo);

        } else {
            // Buscar en el sub-árbol DERECHO (códigos mayores)
            return buscarRec(nodo.derecho, codigo);
        }
    }

    /**
     * Verifica si existe un producto con el código dado, sin lanzar excepción.
     * 
     * @param codigo Código a verificar.
     * @return {@code true} si el producto existe.
     */
    public boolean existe(String codigo) {
        return buscarRec(this.raiz, codigo) != null;
    }

    /**
     * Elimina un producto del árbol por su código.
     * Nodo hoja: se pone {@code null} en el puntero del padre.
     *   Un hijo: el padre apunta directamente al único hijo.
     *   Dos hijos: se reemplaza con el sucesor inorden
     *       (mínimo del sub-árbol derecho) y luego se elimina ese sucesor.
     * @param codigo Código del producto a eliminar.
     * @throws ProductoNoEncontradoException si el código no existe.
     */
    public void eliminar(String codigo) {
        if (!existe(codigo)) throw new ProductoNoEncontradoException(codigo);
        this.raiz = eliminarRec(this.raiz, codigo);
        this.tamanio--;
    }

    /**
     * Auxiliar recursivo para {@link #eliminar(String)}.
     * 
     * @param nodo   Nodo evaluado en esta llamada.
     * @param codigo Código del producto a eliminar.
     * @return La raíz del sub-árbol tras la eliminación.
     */
    private NodoArbol eliminarRec(NodoArbol nodo, String codigo) {
        if (nodo == null) return null;

        int cmp = codigo.compareToIgnoreCase(nodo.dato.getCodigo());

        if (cmp < 0) {
            // El nodo a eliminar está en el sub-árbol izquierdo
            nodo.izquierdo = eliminarRec(nodo.izquierdo, codigo);

        } else if (cmp > 0) {
            // El nodo a eliminar está en el sub-árbol derecho
            nodo.derecho = eliminarRec(nodo.derecho, codigo);

        } else {

            // Eliminar nodo hoja
            if (nodo.izquierdo == null && nodo.derecho == null) {
                return null;    // El padre perderá la referencia a este nodo
            }

            // Reemplazar por hijo derecho
            if (nodo.izquierdo == null) {
                return nodo.derecho;
            }

            // Solo tiene hijo izquierdo
            if (nodo.derecho == null) {
                return nodo.izquierdo;
            }

            // Reemplazar con sucesor
            // (el nodo más a la IZQUIERDA dentro del sub-árbol DERECHO)
            NodoArbol sucesor = minimoNodo(nodo.derecho);   // Hallar sucesor
            nodo.dato = sucesor.dato;                        // Copiar dato del sucesor

            // Eliminar el sucesor de su posición original
            nodo.derecho = eliminarRec(nodo.derecho, sucesor.dato.getCodigo());
        }

        return nodo;
    }

    /**
     * Encuentra el nodo con el valor mínimo dentro de un sub-árbol
     * (el nodo más a la izquierda). Se usa para hallar el sucesor inorden.
     * 
     * @param nodo Raíz del sub-árbol en que buscar el mínimo.
     * @return El nodo con el código lexicográficamente menor.
     */
    private NodoArbol minimoNodo(NodoArbol nodo) {
        // Avanzamos hacia la izquierda hasta llegar a un nodo sin hijo izquierdo
        while (nodo.izquierdo != null) {
            nodo = nodo.izquierdo;  // Puntero avanza hacia la izquierda
        }
        return nodo;
    }

    /**
     * Recorre el árbol en inorden.
     * 
     * @return Lista de productos ordenados alfabéticamente por código.
     */
    public ListaEnlazada<Producto> recorrerInorden() {
        ListaEnlazada<Producto> lista = new ListaEnlazada<>();
        inordenRec(this.raiz, lista);
        return lista;
    }

    /** Auxiliar recursivo para recorrido inorden. */
    private void inordenRec(NodoArbol nodo, ListaEnlazada<Producto> lista) {
        if (nodo == null) return;
        inordenRec(nodo.izquierdo, lista);  // 1. Sub-árbol izquierdo
        lista.add(nodo.dato);               // 2. Nodo actual (raíz del sub-árbol)
        inordenRec(nodo.derecho, lista);    // 3. Sub-árbol derecho
    }

    /**
     * Recorre el árbol en preorden.
     * Útil para duplicar/serializar la estructura del árbol.
     * 
     * @return Lista de productos en preorden.
     */
    public ListaEnlazada<Producto> recorrerPreorden() {
        ListaEnlazada<Producto> lista = new ListaEnlazada<>();
        preordenRec(this.raiz, lista);
        return lista;
    }

    /** Auxiliar recursivo para recorrido preorden. */
    private void preordenRec(NodoArbol nodo, ListaEnlazada<Producto> lista) {
        if (nodo == null) return;
        lista.add(nodo.dato);               // 1. Nodo actual
        preordenRec(nodo.izquierdo, lista); // 2. Sub-árbol izquierdo
        preordenRec(nodo.derecho, lista);   // 3. Sub-árbol derecho
    }

    //  UTILIDADES

    /**
     * Retorna todos los productos cuyo stock actual está por debajo
     * del stock mínimo configurado.
     * 
     * @return Lista de productos en estado crítico de stock.
     */
    public ListaEnlazada<Producto> obtenerProductosCriticos() {
        ListaEnlazada<Producto> todos = recorrerInorden();
        ListaEnlazada<Producto> criticos = new ListaEnlazada<>();
        for (Producto p : todos) {
            if (p.estaEnStockCritico()) criticos.add(p);
        }
        return criticos;
    }

    /**
     * Calcula la altura del árbol (número de aristas en el camino
     * más largo desde la raíz hasta una hoja).
     * 
     * @return Altura del árbol. Retorna {@code -1} si el árbol está vacío.
     */
    public int obtenerAltura() {
        return alturaRec(this.raiz);
    }

    /** Auxiliar recursivo para el cálculo de altura. */
    private int alturaRec(NodoArbol nodo) {
        if (nodo == null) return -1;
        int altIzq = alturaRec(nodo.izquierdo);
        int altDer = alturaRec(nodo.derecho);
        return 1 + Math.max(altIzq, altDer);
    }

    //  GETTERS DE ESTADO

    /** Obtiene el valor. @return Número de productos almacenados. */
    public int getTamanio() { return tamanio; }

    /** Obtiene el valor. @return {@code true} si el árbol no contiene ningún producto. */
    public boolean estaVacio(){ return raiz == null; }

    /** Obtiene el valor. @return Referencia a la raíz (uso interno del servicio). */
    NodoArbol getRaiz() { return raiz; }
}
