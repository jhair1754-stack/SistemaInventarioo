package estructuras;

import modelos.Transaccion;

/**
 * Pila LIFO de {@link Transaccion}es implementada desde cero
 * con nodos enlazados ({@link NodoPila}).
 *
 * Registra todos los movimientos de inventario (entradas,
 * salidas, registros) en orden cronológico inverso: el más
 * reciente siempre está en el tope.
 *
 * <h2>Operaciones – Complejidades</h2>
 * 
 *   {@link #push(Transaccion)} – O(1): agrega al tope.
 *   {@link #pop()}             – O(1): extrae del tope.
 *   {@link #peek()}            – O(1): consulta el tope.
 *   {@link #toList()}          – O(n): copia toda la pila.
 * 
 * @author Equipo 2
 * @version Beta
 */
public class PilaAuditoria {

    //  TOPE DE LA PILA

    /**
     * REFERENCIA/PUNTERO al nodo en el TOPE de la pila.
     *
     * Es el único punto de acceso a toda la pila. Siempre
     * apunta al elemento apilado más recientemente.
     * 
     * Vale {@code null} cuando la pila está completamente vacía.
     */
    private NodoPila tope;

    /** Número de transacciones actualmente en la pila. */
    private int tamanio;

    //  CONSTRUCTOR

    /**
     * Crea una pila de auditoría vacía.
     * El puntero {@code tope} se inicializa en {@code null}.
     */
    public PilaAuditoria() {
        this.tope = null;  // Pila vacía: ningún nodo existe aún
        this.tamanio = 0;
    }

    /**
     * Apila una nueva transacción en el tope de la pila.
     * 
     * @param transaccion La transacción de inventario a apilar.
     */
    public void push(Transaccion transaccion) {
        NodoPila nuevoNodo = new NodoPila(transaccion); // Crear nodo
        nuevoNodo.siguiente = this.tope;                 // Enlazar nodo
        this.tope = nuevoNodo;                 // Actualizar tope
        this.tamanio++;
    }

    /**
     * Desapila y retorna la transacción en el tope de la pila.
     * 
     * @return La {@link Transaccion} que estaba en el tope.
     * @throws IllegalStateException si la pila está vacía.
     */
    public Transaccion pop() {
        if (estaVacia()) {
            throw new IllegalStateException(
                "No se puede desapilar: la pila de auditoria esta vacia.");
        }
        Transaccion dato = this.tope.dato;        // guardar dato del tope
        this.tope = this.tope.siguiente;   // mover tope al nodo anterior
        this.tamanio--;
        return dato;
    }

    /**
     * Retorna la transacción en el tope sin extraerla.
     * 
     * @return La {@link Transaccion} en el tope de la pila.
     * @throws IllegalStateException si la pila está vacía.
     */
    public Transaccion peek() {
        if (estaVacia()) {
            throw new IllegalStateException(
                "No se puede consultar: la pila de auditoria esta vacia.");
        }
        return this.tope.dato;
    }

    //  OPERACIONES AUXILIARES

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
     * sin modificar la pila.
     *
     * Recorre la cadena enlazada desde el {@code tope} hasta {@code null},
     * añadiendo cada transacción a la lista.
     * 
     * @return Lista de transacciones; el elemento 0 es el más reciente.
     */
    public ListaEnlazada<Transaccion> toList() {
        ListaEnlazada<Transaccion> lista = new ListaEnlazada<>();
        NodoPila nodoActual = this.tope;   // Comenzar desde el tope

        // Recorrer la cadena
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
        // Liberar nodos
        this.tope = null;
        this.tamanio = 0;
    }
}
