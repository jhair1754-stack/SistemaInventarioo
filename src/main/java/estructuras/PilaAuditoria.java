package estructuras;

import modelos.Transaccion;

// ====================================================================
// [REQUISITO RUBRICA: PILAS] - Clase PilaAuditoria
// ====================================================================
// Implementación COMPLETA de una Pila (Stack) LIFO desde cero
// usando nodos enlazados (NodoPila).
// NO usa java.util.Stack, java.util.Deque ni ninguna colección JDK.
//
// PRINCIPIO LIFO (Last In, First Out):
//   El ÚLTIMO elemento apilado (push) es el PRIMERO en salir (pop).
//   Esto es ideal para auditoría: el movimiento más reciente
//   siempre está en el TOPE de la pila.
//
// DIAGRAMA:
//
//  Estado inicial (vacío):  tope → null
//
//  push(TRX-1):  tope → [TRX-1] → null
//  push(TRX-2):  tope → [TRX-2] → [TRX-1] → null
//  push(TRX-3):  tope → [TRX-3] → [TRX-2] → [TRX-1] → null
//
//  pop():  retorna TRX-3, tope → [TRX-2] → [TRX-1] → null
//  peek(): retorna TRX-2 SIN modificar la pila
//
// ====================================================================

/**
 * ============================================================
 * ESTRUCTURA: PilaAuditoria
 * ============================================================
 * Pila LIFO de {@link Transaccion}es implementada desde cero
 * con nodos enlazados ({@link NodoPila}).
 *
 * <p>Registra todos los movimientos de inventario (entradas,
 * salidas, registros) en orden cronológico inverso: el más
 * reciente siempre está en el tope.</p>
 *
 * <h2>Operaciones – Complejidades</h2>
 * <ul>
 *   <li>{@link #push(Transaccion)} – O(1): agrega al tope.</li>
 *   <li>{@link #pop()}             – O(1): extrae del tope.</li>
 *   <li>{@link #peek()}            – O(1): consulta el tope.</li>
 *   <li>{@link #toList()}          – O(n): copia toda la pila.</li>
 * </ul>
 *
 * @author  Equipo Proyecto 4 – Ingeniería de Sistemas UNMSM
 * @version 2.0
 */
public class PilaAuditoria {

    // ─────────────────────────────────────────────────────────
    //  TOPE DE LA PILA
    // ─────────────────────────────────────────────────────────

    /**
     * REFERENCIA/PUNTERO al nodo en el TOPE de la pila.
     *
     * <p>Es el único punto de acceso a toda la pila. Siempre
     * apunta al elemento apilado más recientemente.</p>
     *
     * <pre>
     *  tope → [NodoMásReciente] → [Anterior] → ... → [Primero] → null
     * </pre>
     *
     * Vale {@code null} cuando la pila está completamente vacía.
     */
    private NodoPila tope;

    /** Número de transacciones actualmente en la pila. */
    private int tamanio;

    // ─────────────────────────────────────────────────────────
    //  CONSTRUCTOR
    // ─────────────────────────────────────────────────────────

    /**
     * Crea una pila de auditoría vacía.
     * El puntero {@code tope} se inicializa en {@code null}.
     */
    public PilaAuditoria() {
        // [REQUISITO RUBRICA: PILAS] - Inicialización de la pila vacía
        this.tope    = null;  // Pila vacía: ningún nodo existe aún
        this.tamanio = 0;
    }

    // ====================================================================
    // [REQUISITO RUBRICA: PILAS] - OPERACIÓN: PUSH (apilar)
    // ====================================================================

    /**
     * Apila una nueva transacción en el tope de la pila.
     *
     * <h3>Algoritmo PUSH — paso a paso</h3>
     * <pre>
     *  Antes:  tope → [A] → [B] → null
     *
     *  1. Crear nuevoNodo con dato = transaccion
     *  2. nuevoNodo.siguiente = tope    →   [nuevoNodo] → [A] → [B] → null
     *  3. tope = nuevoNodo              →   tope apunta a [nuevoNodo]
     *
     *  Después: tope → [nuevoNodo] → [A] → [B] → null
     * </pre>
     *
     * <p><b>Complejidad: O(1)</b> — no recorre la cadena.</p>
     *
     * @param transaccion La transacción de inventario a apilar.
     */
    public void push(Transaccion transaccion) {
        // [REQUISITO RUBRICA: PILAS] - Operación PUSH: agrega al tope
        NodoPila nuevoNodo   = new NodoPila(transaccion); // Paso 1: crear nodo
        nuevoNodo.siguiente  = this.tope;                 // Paso 2: enlazar con el tope actual
        this.tope            = nuevoNodo;                 // Paso 3: el nuevo nodo ES el nuevo tope
        this.tamanio++;
    }

    // ====================================================================
    // [REQUISITO RUBRICA: PILAS] - OPERACIÓN: POP (desapilar)
    // ====================================================================

    /**
     * Desapila y retorna la transacción en el tope de la pila.
     *
     * <h3>Algoritmo POP — paso a paso</h3>
     * <pre>
     *  Antes:  tope → [X] → [A] → [B] → null
     *
     *  1. datoGuardado = tope.dato   (guardamos X)
     *  2. tope = tope.siguiente      (tope avanza → [A])
     *     [X] queda sin referencias → GC lo reclamar
     *
     *  Después: tope → [A] → [B] → null   (X fue extraído)
     * </pre>
     *
     * <p><b>Complejidad: O(1)</b>.</p>
     *
     * @return La {@link Transaccion} que estaba en el tope.
     * @throws IllegalStateException si la pila está vacía.
     */
    public Transaccion pop() {
        // [REQUISITO RUBRICA: PILAS] - Operación POP: extrae del tope
        if (estaVacia()) {
            throw new IllegalStateException(
                "No se puede desapilar: la pila de auditoria esta vacia.");
        }
        Transaccion dato = this.tope.dato;        // Paso 1: guardar dato del tope
        this.tope        = this.tope.siguiente;   // Paso 2: mover tope al nodo anterior
        this.tamanio--;
        return dato;
    }

    // ====================================================================
    // [REQUISITO RUBRICA: PILAS] - OPERACIÓN: PEEK (consultar sin extraer)
    // ====================================================================

    /**
     * Retorna la transacción en el tope <b>sin extraerla</b>.
     *
     * <p><b>Complejidad: O(1)</b>.</p>
     *
     * @return La {@link Transaccion} en el tope de la pila.
     * @throws IllegalStateException si la pila está vacía.
     */
    public Transaccion peek() {
        // [REQUISITO RUBRICA: PILAS] - Operación PEEK: consulta sin modificar la pila
        if (estaVacia()) {
            throw new IllegalStateException(
                "No se puede consultar: la pila de auditoria esta vacia.");
        }
        return this.tope.dato;
    }

    // ─────────────────────────────────────────────────────────
    //  OPERACIONES AUXILIARES
    // ─────────────────────────────────────────────────────────

    /**
     * Indica si la pila no contiene ningún elemento.
     *
     * @return {@code true} si la pila está vacía.
     */
    public boolean estaVacia() {
        return this.tope == null;
    }

    /**
     * Retorna el número de transacciones en la pila.
     *
     * @return Cantidad de elementos apilados.
     */
    public int getTamanio() {
        return tamanio;
    }

    /**
     * Convierte la pila en una lista en orden LIFO (más reciente primero),
     * <b>sin modificar</b> la pila.
     *
     * <p>Recorre la cadena enlazada desde el {@code tope} hasta {@code null},
     * añadiendo cada transacción a la lista.</p>
     *
     * <p><b>Complejidad: O(n)</b>.</p>
     *
     * @return Lista de transacciones; el elemento 0 es el más reciente.
     */
    public ListaEnlazada<Transaccion> toList() {
        // [REQUISITO RUBRICA: PILAS] - Recorrido de la pila sin modificarla
        ListaEnlazada<Transaccion> lista = new ListaEnlazada<>();
        NodoPila          nodoActual = this.tope;   // Comenzar desde el tope

        // Recorrer la cadena: tope → siguiente → siguiente → ... → null
        while (nodoActual != null) {
            lista.add(nodoActual.dato);              // Agregar transacción a la lista
            nodoActual = nodoActual.siguiente;       // Avanzar al siguiente nodo
        }
        return lista;
    }

    /**
     * Vacía la pila, liberando todas las referencias internas.
     * El recolector de basura de Java podrá reclamar los nodos.
     */
    public void vaciar() {
        // Al poner tope en null, ningún nodo es alcanzable → GC los reclamará
        this.tope    = null;
        this.tamanio = 0;
    }
}
