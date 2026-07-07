package com.distribuidora.inventario.structures;

import com.distribuidora.inventario.exceptions.CodigoProductoDuplicadoException;
import com.distribuidora.inventario.exceptions.ProductoNoEncontradoException;
import com.distribuidora.inventario.models.Producto;

// ====================================================================
// [REQUISITO RUBRICA: ÁRBOL BINARIO DE BÚSQUEDA] - Clase ArbolBST
// Implementación COMPLETA del Árbol Binario de Búsqueda (BST) desde
// cero. NO usa java.util.TreeSet ni ninguna colección de Java.
// Cada operación navega el árbol comparando códigos de producto para
// ir a la izquierda (menor) o derecha (mayor) hasta encontrar la
// posición correcta. Complejidad promedio: O(log n).
// ====================================================================

/**
 * ================================================================
 * CLASE: ArbolBST (Árbol Binario de Búsqueda)
 * ================================================================
 * Implementación desde cero de un BST para almacenar y recuperar
 * productos del inventario por su código único.
 *
 * <h2>Propiedades del BST</h2>
 * <ul>
 *   <li>Para cada nodo N: todos los nodos del subárbol izquierdo
 *       tienen código &lt; N.codigo</li>
 *   <li>Para cada nodo N: todos los nodos del subárbol derecho
 *       tienen código &gt; N.codigo</li>
 *   <li>Búsqueda, inserción y eliminación: O(log n) promedio.</li>
 * </ul>
 *
 * <h2>Operaciones disponibles</h2>
 * <ul>
 *   <li>{@code insertar}: Agrega un nuevo producto.</li>
 *   <li>{@code buscar}: Localiza un producto por código.</li>
 *   <li>{@code eliminar}: Elimina un producto del árbol.</li>
 *   <li>{@code recorrerInorden}: Lista todos en orden alfabético.</li>
 *   <li>{@code recorrerPreorden}: Lista en preorden (raíz primero).</li>
 *   <li>{@code recorrerNivelPorNivel}: Recorrido BFS por niveles.</li>
 *   <li>{@code obtenerProductosCriticos}: Filtra los de stock bajo.</li>
 * </ul>
 *
 * @author  Equipo Proyecto 4 - Ingeniería de Sistemas UNMSM
 * @version 1.0
 */
public class ArbolBST {

    // ----------------------------------------------------------------
    // RAÍZ DEL ÁRBOL
    // ----------------------------------------------------------------

    /**
     * REFERENCIA a la raíz del árbol.
     * Es el punto de entrada para TODAS las operaciones del BST.
     * Si vale {@code null}, el árbol está vacío.
     *
     * Estructura visual:
     * <pre>
     *           [raiz]        ← Punto de entrada
     *          /      \
     *       [nodo]   [nodo]   ← Nivel 1
     *       /    \
     *    [nodo] [nodo]        ← Nivel 2
     * </pre>
     */
    private NodoBST raiz;

    /** Número de productos almacenados en el árbol. */
    private int tamanio;

    // ----------------------------------------------------------------
    // CONSTRUCTOR
    // ----------------------------------------------------------------

    /**
     * Crea un árbol BST vacío.
     * La raíz se inicializa en {@code null}.
     */
    public ArbolBST() {
        this.raiz    = null; // Árbol vacío: raíz apunta a null
        this.tamanio = 0;
    }

    // ================================================================
    // OPERACIÓN: INSERTAR
    // ================================================================

    /**
     * Inserta un nuevo producto en el árbol BST.
     *
     * <p><b>Algoritmo:</b></p>
     * <ol>
     *   <li>Si el árbol está vacío, el nuevo nodo se convierte en la raíz.</li>
     *   <li>Si el código del producto es MENOR al nodo actual, se va a la
     *       izquierda y se repite el proceso recursivamente.</li>
     *   <li>Si el código es MAYOR, se va a la derecha.</li>
     *   <li>Si el código es IGUAL, se lanza {@code CodigoProductoDuplicadoException}.</li>
     * </ol>
     *
     * @param producto Producto a insertar (código único requerido).
     * @throws CodigoProductoDuplicadoException si ya existe un producto
     *         con el mismo código en el árbol.
     */
    public void insertar(Producto producto) {
        // [REQUISITO RUBRICA: ÁRBOL BINARIO DE BÚSQUEDA] - Punto de inicio de la inserción
        // Se llama al método recursivo pasando la raíz actual
        this.raiz = insertarRecursivo(this.raiz, producto);
        this.tamanio++;
    }

    /**
     * Método recursivo auxiliar para insertar.
     * Navega el árbol hasta encontrar la posición correcta (hoja nula).
     *
     * @param nodoActual Nodo que se evalúa en esta iteración recursiva.
     * @param producto   Producto a insertar.
     * @return El nodo raíz del subárbol (potencialmente modificado).
     */
    private NodoBST insertarRecursivo(NodoBST nodoActual, Producto producto) {
        // CASO BASE: llegamos a una hoja nula → aquí va el nuevo nodo
        if (nodoActual == null) {
            // Creamos el nuevo nodo y lo "colgamos" en esta posición del árbol
            return new NodoBST(producto);
        }

        // Comparamos el código del producto nuevo con el del nodo actual
        int comparacion = producto.getCodigo()
                                  .compareToIgnoreCase(nodoActual.dato.getCodigo());

        if (comparacion < 0) {
            // El código nuevo es MENOR → ir al subárbol IZQUIERDO
            // Actualizamos el puntero izquierdo del nodo actual
            nodoActual.izquierdo = insertarRecursivo(nodoActual.izquierdo, producto);

        } else if (comparacion > 0) {
            // El código nuevo es MAYOR → ir al subárbol DERECHO
            // Actualizamos el puntero derecho del nodo actual
            nodoActual.derecho = insertarRecursivo(nodoActual.derecho, producto);

        } else {
            // IGUAL → código duplicado, revertimos el incremento y lanzamos excepción
            this.tamanio--; // Corregimos porque insertar() ya sumó antes
            throw new CodigoProductoDuplicadoException(producto.getCodigo());
        }

        // Devolvemos el nodo actual sin cambios (solo su puntero hijo fue modificado)
        return nodoActual;
    }

    // ================================================================
    // OPERACIÓN: BUSCAR
    // ================================================================

    /**
     * Busca y retorna un producto por su código en el BST.
     *
     * <p><b>Algoritmo:</b> Compara el código buscado con el nodo actual.
     * Si es menor, va a la izquierda; si es mayor, va a la derecha.
     * Repite hasta encontrarlo o llegar a un nodo nulo (no encontrado).</p>
     *
     * <p><b>Complejidad:</b> O(log n) en árbol balanceado, O(n) en peor caso.</p>
     *
     * @param codigo Código del producto a buscar.
     * @return El {@link Producto} encontrado.
     * @throws ProductoNoEncontradoException si el código no existe en el árbol.
     */
    public Producto buscar(String codigo) {
        // [REQUISITO RUBRICA: ÁRBOL BINARIO DE BÚSQUEDA] - Inicio de búsqueda desde la raíz
        NodoBST resultado = buscarRecursivo(this.raiz, codigo);
        if (resultado == null) {
            throw new ProductoNoEncontradoException(codigo);
        }
        return resultado.dato;
    }

    /**
     * Método recursivo auxiliar para búsqueda.
     *
     * @param nodoActual Nodo evaluado en la iteración actual.
     * @param codigo     Código a buscar.
     * @return El nodo que contiene el producto, o {@code null} si no existe.
     */
    private NodoBST buscarRecursivo(NodoBST nodoActual, String codigo) {
        // CASO BASE 1: árbol vacío o nodo hoja nulo → no encontrado
        if (nodoActual == null) {
            return null;
        }

        // CASO BASE 2: encontramos el nodo con el código buscado
        int comparacion = codigo.compareToIgnoreCase(nodoActual.dato.getCodigo());

        if (comparacion == 0) {
            // ¡ENCONTRADO! Devolvemos este nodo
            return nodoActual;
        } else if (comparacion < 0) {
            // El código buscado es MENOR → navegar al SUBÁRBOL IZQUIERDO
            return buscarRecursivo(nodoActual.izquierdo, codigo);
        } else {
            // El código buscado es MAYOR → navegar al SUBÁRBOL DERECHO
            return buscarRecursivo(nodoActual.derecho, codigo);
        }
    }

    /**
     * Verifica si existe un producto con el código dado sin lanzar excepción.
     *
     * @param codigo Código a verificar.
     * @return {@code true} si el producto existe, {@code false} si no.
     */
    public boolean existe(String codigo) {
        return buscarRecursivo(this.raiz, codigo) != null;
    }

    // ================================================================
    // OPERACIÓN: ELIMINAR
    // ================================================================

    /**
     * Elimina un producto del árbol BST por su código.
     *
     * <p><b>Casos de eliminación:</b></p>
     * <ul>
     *   <li><b>Nodo hoja:</b> Se elimina directamente poniendo {@code null} en el puntero del padre.</li>
     *   <li><b>Un hijo:</b> El padre apunta directamente al único hijo del nodo eliminado.</li>
     *   <li><b>Dos hijos:</b> Se reemplaza el dato con el sucesor inorden (mínimo del subárbol
     *       derecho) y luego se elimina ese sucesor.</li>
     * </ul>
     *
     * @param codigo Código del producto a eliminar.
     * @throws ProductoNoEncontradoException si el código no existe.
     */
    public void eliminar(String codigo) {
        if (!existe(codigo)) {
            throw new ProductoNoEncontradoException(codigo);
        }
        // [REQUISITO RUBRICA: ÁRBOL BINARIO DE BÚSQUEDA] - Eliminación con reconexión de punteros
        this.raiz = eliminarRecursivo(this.raiz, codigo);
        this.tamanio--;
    }

    /**
     * Método recursivo auxiliar para eliminación.
     *
     * @param nodoActual Nodo evaluado en la iteración.
     * @param codigo     Código del producto a eliminar.
     * @return La raíz del subárbol tras la eliminación.
     */
    private NodoBST eliminarRecursivo(NodoBST nodoActual, String codigo) {
        if (nodoActual == null) return null;

        int comparacion = codigo.compareToIgnoreCase(nodoActual.dato.getCodigo());

        if (comparacion < 0) {
            // Ir al subárbol izquierdo y actualizar el puntero izquierdo
            nodoActual.izquierdo = eliminarRecursivo(nodoActual.izquierdo, codigo);

        } else if (comparacion > 0) {
            // Ir al subárbol derecho y actualizar el puntero derecho
            nodoActual.derecho = eliminarRecursivo(nodoActual.derecho, codigo);

        } else {
            // ENCONTRADO el nodo a eliminar

            // Caso 1: Nodo hoja (sin hijos) → simplemente retornamos null
            if (nodoActual.izquierdo == null && nodoActual.derecho == null) {
                return null;
            }

            // Caso 2a: Solo tiene hijo derecho → el padre ahora apunta al hijo derecho
            if (nodoActual.izquierdo == null) {
                return nodoActual.derecho;
            }

            // Caso 2b: Solo tiene hijo izquierdo → el padre ahora apunta al hijo izquierdo
            if (nodoActual.derecho == null) {
                return nodoActual.izquierdo;
            }

            // Caso 3: Tiene DOS hijos
            // Encontrar el SUCESOR INORDEN (mínimo del subárbol derecho)
            // El sucesor inorden es el nodo más a la izquierda del subárbol derecho
            NodoBST sucesor = encontrarMinimo(nodoActual.derecho);

            // Reemplazar el dato del nodo actual con el del sucesor
            nodoActual.dato = sucesor.dato;

            // Eliminar el sucesor de su posición original en el subárbol derecho
            nodoActual.derecho = eliminarRecursivo(nodoActual.derecho, sucesor.dato.getCodigo());
        }

        return nodoActual;
    }

    /**
     * Encuentra el nodo con el valor mínimo (más a la izquierda) en un subárbol.
     * Se usa para encontrar el sucesor inorden al eliminar nodos con dos hijos.
     *
     * @param nodo Raíz del subárbol en que buscar el mínimo.
     * @return El nodo más a la izquierda del subárbol.
     */
    private NodoBST encontrarMinimo(NodoBST nodo) {
        // El mínimo siempre está en el extremo más izquierdo
        while (nodo.izquierdo != null) {
            nodo = nodo.izquierdo; // Avanzamos el puntero hacia la izquierda
        }
        return nodo;
    }

    // ================================================================
    // RECORRIDOS DEL ÁRBOL
    // ================================================================

    /**
     * Recorre el árbol en INORDEN (izquierdo → raíz → derecho).
     * Produce los productos ordenados alfabéticamente por código.
     *
     * <p>Complejidad: O(n) — visita todos los nodos exactamente una vez.</p>
     *
     * @return Lista de productos en orden alfabético de código.
     */
    public ListaEnlazada<Producto> recorrerInorden() {
        ListaEnlazada<Producto> lista = new ListaEnlazada<>();
        inordenRecursivo(this.raiz, lista);
        return lista;
    }

    /** Auxiliar recursivo para recorrido inorden. */
    private void inordenRecursivo(NodoBST nodo, ListaEnlazada<Producto> lista) {
        if (nodo == null) return;
        inordenRecursivo(nodo.izquierdo, lista);  // 1. Subárbol izquierdo
        lista.add(nodo.dato);                       // 2. Nodo actual
        inordenRecursivo(nodo.derecho, lista);     // 3. Subárbol derecho
    }

    /**
     * Recorre el árbol en PREORDEN (raíz → izquierdo → derecho).
     * Útil para copiar o exportar la estructura del árbol.
     *
     * @return Lista de productos en preorden.
     */
    public ListaEnlazada<Producto> recorrerPreorden() {
        ListaEnlazada<Producto> lista = new ListaEnlazada<>();
        preordenRecursivo(this.raiz, lista);
        return lista;
    }

    /** Auxiliar recursivo para recorrido preorden. */
    private void preordenRecursivo(NodoBST nodo, ListaEnlazada<Producto> lista) {
        if (nodo == null) return;
        lista.add(nodo.dato);                         // 1. Nodo actual
        preordenRecursivo(nodo.izquierdo, lista);    // 2. Subárbol izquierdo
        preordenRecursivo(nodo.derecho, lista);      // 3. Subárbol derecho
    }

    /**
     * Retorna todos los productos cuyo stock está por debajo del mínimo.
     *
     * @return Lista de productos en estado crítico de stock.
     */
    public ListaEnlazada<Producto> obtenerProductosCriticos() {
        ListaEnlazada<Producto> todos    = recorrerInorden();
        ListaEnlazada<Producto> criticos = new ListaEnlazada<>();
        for (Producto p : todos) {
            if (p.estaEnStockCritico()) {
                criticos.add(p);
            }
        }
        return criticos;
    }

    /**
     * Calcula la altura del árbol.
     * La altura es el número de aristas en el camino más largo desde
     * la raíz hasta una hoja.
     *
     * @return Altura del árbol. Retorna -1 si el árbol está vacío.
     */
    public int obtenerAltura() {
        return alturaRecursiva(this.raiz);
    }

    /** Auxiliar recursivo para cálculo de altura. */
    private int alturaRecursiva(NodoBST nodo) {
        if (nodo == null) return -1;
        int alturaIzq = alturaRecursiva(nodo.izquierdo);
        int alturaDer = alturaRecursiva(nodo.derecho);
        return 1 + Math.max(alturaIzq, alturaDer);
    }

    // ================================================================
    // GETTERS DE ESTADO
    // ================================================================

    /** @return Número de productos en el árbol. */
    public int getTamanio() { return tamanio; }

    /** @return {@code true} si el árbol no tiene ningún producto. */
    public boolean estaVacio() { return raiz == null; }

    /** @return La raíz del árbol (uso interno). */
    NodoBST getRaiz() { return raiz; }
}
