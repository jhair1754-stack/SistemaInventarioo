package estructuras;

import modelos.Transaccion;

/**
 *  * Nodo individual de la pila enlazada {@link PilaAuditoria}.
 * Cada nodo almacena una {@link Transaccion} y mantiene una
 * referencia al nodo que estaba en el tope ANTES de él.
 * 
 * @author Equipo 2
 * @version Beta
 */
public class NodoPila {

    //  CAMPOS DEL NODO

    /**
     * La transacción almacenada en este nodo.
     * Es la "carga útil" del eslabón de la cadena.
     */
    Transaccion dato;

    /**
     * REFERENCIA/PUNTERO al nodo anterior (el que estaba en el tope
     * antes de que este nodo fuera apilado con {@code push}).
     * 
     * Vale {@code null} cuando es el primer (y único) nodo de la pila,
     * o el fondo de la misma.
     */
    NodoPila siguiente;

    //  CONSTRUCTOR

    /**
     * Crea un nuevo nodo de pila con la transacción dada.
     * El puntero {@code siguiente} se inicializa en {@code null}
     * (no tiene sucesor hasta que se apile).
     * 
     * @param dato La transacción a almacenar en este nodo.
     */
    public NodoPila(Transaccion dato) {
        this.dato = dato;    // Carga útil: la transacción de auditoría
        this.siguiente = null;    // Puntero: null hasta que se asigne en push()
    }

    //  ACCESORES

    /**
     * Retorna la transacción almacenada en este nodo.
     * 
     * @return La {@link Transaccion} almacenada.
     */
    public Transaccion getDato() { return dato; }
}
