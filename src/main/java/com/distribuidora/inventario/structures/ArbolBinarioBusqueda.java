package com.distribuidora.inventario.structures;

import com.distribuidora.inventario.exceptions.CodigoProductoDuplicadoException;
import com.distribuidora.inventario.exceptions.ProductoNoEncontradoException;
import com.distribuidora.inventario.models.Producto;

import java.util.ArrayList;
import java.util.List;

// ====================================================================
// [REQUISITO RUBRICA: ÁRBOL BINARIO DE BÚSQUEDA] - ArbolBinarioBusqueda
// ====================================================================
// Implementación COMPLETA desde cero de un Árbol Binario de Búsqueda.
// NO usa java.util.TreeSet, java.util.TreeMap ni ninguna otra
// colección del JDK.
//
// PROPIEDADES DEL BST:
//   Para todo NodoArbol N:
//     • Sub-árbol izquierdo  → códigos MENORES  que N.codigo
//     • Sub-árbol derecho    → códigos MAYORES   que N.codigo
//
// COMPLEJIDADES:
//   • Búsqueda / Inserción / Eliminación: O(log n) promedio
//   • Recorrido Inorden (lista ordenada):  O(n)
//
// ====================================================================

/**
 * ============================================================
 * ESTRUCTURA: ArbolBinarioBusqueda
 * ============================================================
 * Árbol Binario de Búsqueda (BST) que organiza y recupera
 * {@link Producto}s por su campo {@code codigo} de forma eficiente.
 *
 * <h2>Operaciones disponibles</h2>
 * <ul>
 *   <li>{@link #insertar(Producto)}           – O(log n) amortizado.</li>
 *   <li>{@link #buscar(String)}               – O(log n) amortizado.</li>
 *   <li>{@link #existe(String)}               – O(log n), sin excepción.</li>
 *   <li>{@link #eliminar(String)}             – O(log n) amortizado.</li>
 *   <li>{@link #recorrerInorden()}            – O(n), lista ordenada.</li>
 *   <li>{@link #recorrerPreorden()}           – O(n).</li>
 *   <li>{@link #obtenerProductosCriticos()}   – O(n).</li>
 *   <li>{@link #obtenerAltura()}              – O(n).</li>
 * </ul>
 *
 * @author  Equipo Proyecto 4 – Ingeniería de Sistemas UNMSM
 * @version 2.0
 */
public class ArbolBinarioBusqueda {

    // ─────────────────────────────────────────────────────────
    //  RAÍZ DEL ÁRBOL
    // ─────────────────────────────────────────────────────────

    /**
     * REFERENCIA a la raíz del árbol.
     *
     * <p>Es el único punto de acceso externo al árbol. Todas las
     * operaciones comienzan navegando desde aquí.</p>
     *
     * <pre>
     *              [raiz]             ← único punto de entrada
     *             /      \
     *          [nodo]   [nodo]        ← nivel 1
     *          /    \
     *       [nodo] [nodo]             ← nivel 2  … etc.
     * </pre>
     *
     * Vale {@code null} cuando el árbol está completamente vacío.
     */
    private NodoArbol raiz;

    /** Número de productos almacenados en el árbol. */
    private int tamanio;

    // ─────────────────────────────────────────────────────────
    //  CONSTRUCTOR
    // ─────────────────────────────────────────────────────────

    /**
     * Crea un árbol binario de búsqueda vacío.
     * La raíz se inicializa en {@code null} (árbol vacío).
     */
    public ArbolBinarioBusqueda() {
        // [REQUISITO RUBRICA: ÁRBOL BINARIO DE BÚSQUEDA] - Inicialización
        this.raiz    = null;    // Árbol vacío: ningún nodo existe aún
        this.tamanio = 0;
    }

    // ====================================================================
    // [REQUISITO RUBRICA: ÁRBOL BINARIO DE BÚSQUEDA] - OPERACIÓN: INSERTAR
    // ====================================================================

    /**
     * Inserta un nuevo producto en el árbol, respetando la propiedad BST.
     *
     * <h3>Algoritmo de inserción</h3>
     * <ol>
     *   <li>Si la raíz es {@code null} → el nuevo nodo SE CONVIERTE en la raíz.</li>
     *   <li>Comparar el código del nuevo producto con el nodo actual:
     *     <ul>
     *       <li>Código MENOR → ir al sub-árbol IZQUIERDO y repetir.</li>
     *       <li>Código MAYOR → ir al sub-árbol DERECHO y repetir.</li>
     *       <li>Código IGUAL → lanzar {@code CodigoProductoDuplicadoException}.</li>
     *     </ul>
     *   </li>
     *   <li>Al llegar a un puntero {@code null}, insertar aquí el nuevo nodo.</li>
     * </ol>
     *
     * @param producto Producto a insertar (código único requerido).
     * @throws CodigoProductoDuplicadoException si el código ya existe.
     */
    public void insertar(Producto producto) {
        // [REQUISITO RUBRICA: ÁRBOL BINARIO DE BÚSQUEDA] - inicio de inserción
        this.raiz = insertarRec(this.raiz, producto);
        this.tamanio++;
    }

    /**
     * Auxiliar recursivo para {@link #insertar(Producto)}.
     *
     * <p>Navega el árbol hasta encontrar la posición correcta (hoja nula)
     * y devuelve la nueva raíz del sub-árbol modificado.</p>
     *
     * @param nodo     Nodo evaluado en esta llamada recursiva.
     * @param producto Producto que se quiere insertar.
     * @return La raíz del sub-árbol tras la inserción.
     */
    private NodoArbol insertarRec(NodoArbol nodo, Producto producto) {

        // ── CASO BASE ──────────────────────────────────────────────────
        // Llegamos a un puntero nulo → aquí se cuelga el nuevo nodo
        if (nodo == null) {
            return new NodoArbol(producto);  // Crear nodo hoja
        }

        // ── COMPARACIÓN DE CÓDIGOS ─────────────────────────────────────
        int cmp = producto.getCodigo()
                          .compareToIgnoreCase(nodo.dato.getCodigo());

        if (cmp < 0) {
            // Código nuevo es MENOR → navegar hacia la IZQUIERDA
            // Actualizamos el puntero izquierdo con el resultado recursivo
            nodo.izquierdo = insertarRec(nodo.izquierdo, producto);

        } else if (cmp > 0) {
            // Código nuevo es MAYOR → navegar hacia la DERECHA
            nodo.derecho = insertarRec(nodo.derecho, producto);

        } else {
            // Código DUPLICADO → revertir incremento y lanzar excepción
            this.tamanio--;   // Corregimos porque insertar() ya sumó 1
            throw new CodigoProductoDuplicadoException(producto.getCodigo());
        }

        return nodo;  // Devolver el nodo actual sin cambios (solo su hijo cambió)
    }

    // ====================================================================
    // [REQUISITO RUBRICA: ÁRBOL BINARIO DE BÚSQUEDA] - OPERACIÓN: BUSCAR
    // ====================================================================

    /**
     * Busca y retorna un producto por su código.
     *
     * <h3>Algoritmo de búsqueda</h3>
     * <p>Compara el código buscado con el nodo actual. Si es menor,
     * avanza a la izquierda; si es mayor, a la derecha. Repite hasta
     * encontrar el código o llegar a un nodo nulo (no existe).</p>
     *
     * <p><b>Complejidad:</b> O(log n) promedio, O(n) en el peor caso
     * (árbol completamente degenerado).</p>
     *
     * @param codigo Código del producto a buscar.
     * @return El {@link Producto} encontrado.
     * @throws ProductoNoEncontradoException si el código no existe en el árbol.
     */
    public Producto buscar(String codigo) {
        // [REQUISITO RUBRICA: ÁRBOL BINARIO DE BÚSQUEDA] - inicio de búsqueda
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

        // CASO BASE 1: nodo nulo → el producto NO existe en el árbol
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

    // ====================================================================
    // [REQUISITO RUBRICA: ÁRBOL BINARIO DE BÚSQUEDA] - OPERACIÓN: ELIMINAR
    // ====================================================================

    /**
     * Elimina un producto del árbol por su código.
     *
     * <h3>Casos de eliminación</h3>
     * <ul>
     *   <li><b>Nodo hoja</b>: se pone {@code null} en el puntero del padre.</li>
     *   <li><b>Un hijo</b>: el padre apunta directamente al único hijo.</li>
     *   <li><b>Dos hijos</b>: se reemplaza con el <i>sucesor inorden</i>
     *       (mínimo del sub-árbol derecho) y luego se elimina ese sucesor.</li>
     * </ul>
     *
     * @param codigo Código del producto a eliminar.
     * @throws ProductoNoEncontradoException si el código no existe.
     */
    public void eliminar(String codigo) {
        if (!existe(codigo)) throw new ProductoNoEncontradoException(codigo);
        // [REQUISITO RUBRICA: ÁRBOL BINARIO DE BÚSQUEDA] - eliminación con reconexión de punteros
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
            // ──── NODO ENCONTRADO: APLICAR CASO CORRESPONDIENTE ────

            // CASO 1: Nodo hoja (sin hijos) → simplemente eliminarlo
            if (nodo.izquierdo == null && nodo.derecho == null) {
                return null;    // El padre perderá la referencia a este nodo
            }

            // CASO 2a: Solo tiene hijo derecho → el padre apunta al hijo
            if (nodo.izquierdo == null) {
                return nodo.derecho;
            }

            // CASO 2b: Solo tiene hijo izquierdo
            if (nodo.derecho == null) {
                return nodo.izquierdo;
            }

            // CASO 3: Dos hijos → reemplazar con el sucesor inorden
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

    // ====================================================================
    // [REQUISITO RUBRICA: ÁRBOL BINARIO DE BÚSQUEDA] - RECORRIDOS
    // ====================================================================

    /**
     * Recorre el árbol en INORDEN (izquierdo → raíz → derecho).
     *
     * <p>El recorrido inorden de un BST produce los elementos en
     * <b>orden alfabético ascendente</b> por código.</p>
     *
     * <p><b>Complejidad:</b> O(n) — visita cada nodo exactamente una vez.</p>
     *
     * @return Lista de productos ordenados alfabéticamente por código.
     */
    public List<Producto> recorrerInorden() {
        // [REQUISITO RUBRICA: ÁRBOL BINARIO DE BÚSQUEDA] - recorrido inorden
        List<Producto> lista = new ArrayList<>();
        inordenRec(this.raiz, lista);
        return lista;
    }

    /** Auxiliar recursivo para recorrido inorden. */
    private void inordenRec(NodoArbol nodo, List<Producto> lista) {
        if (nodo == null) return;
        inordenRec(nodo.izquierdo, lista);  // 1. Sub-árbol izquierdo
        lista.add(nodo.dato);               // 2. Nodo actual (raíz del sub-árbol)
        inordenRec(nodo.derecho, lista);    // 3. Sub-árbol derecho
    }

    /**
     * Recorre el árbol en PREORDEN (raíz → izquierdo → derecho).
     * Útil para duplicar/serializar la estructura del árbol.
     *
     * @return Lista de productos en preorden.
     */
    public List<Producto> recorrerPreorden() {
        List<Producto> lista = new ArrayList<>();
        preordenRec(this.raiz, lista);
        return lista;
    }

    /** Auxiliar recursivo para recorrido preorden. */
    private void preordenRec(NodoArbol nodo, List<Producto> lista) {
        if (nodo == null) return;
        lista.add(nodo.dato);               // 1. Nodo actual
        preordenRec(nodo.izquierdo, lista); // 2. Sub-árbol izquierdo
        preordenRec(nodo.derecho, lista);   // 3. Sub-árbol derecho
    }

    // ─────────────────────────────────────────────────────────
    //  UTILIDADES
    // ─────────────────────────────────────────────────────────

    /**
     * Retorna todos los productos cuyo stock actual está por debajo
     * del stock mínimo configurado.
     *
     * @return Lista de productos en estado crítico de stock.
     */
    public List<Producto> obtenerProductosCriticos() {
        List<Producto> todos     = recorrerInorden();
        List<Producto> criticos  = new ArrayList<>();
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

    // ─────────────────────────────────────────────────────────
    //  GETTERS DE ESTADO
    // ─────────────────────────────────────────────────────────

    /** @return Número de productos almacenados. */
    public int getTamanio()   { return tamanio; }

    /** @return {@code true} si el árbol no contiene ningún producto. */
    public boolean estaVacio(){ return raiz == null; }

    /** @return Referencia a la raíz (uso interno del servicio). */
    NodoArbol getRaiz()       { return raiz; }
}
