package estructuras;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Implementación de una lista simplemente enlazada genérica
 * que reemplaza por completo a java.util.ArrayList y java.util.List.
 * Implementa Iterable para ser usada fácilmente en bucles for-each
 * y llenar tablas JTable.
 * 
 * @param <T> Tipo de elemento a almacenar.
 * @author Equipo 2
 */
public class ListaEnlazada<T> implements Iterable<T> {

    private NodoLista<T> cabeza;
    private int tamanio;

    /**
     * Constructor.
     */
    public ListaEnlazada() {
        this.cabeza = null;
        this.tamanio = 0;
    }

    /**
     * Agrega un elemento al final de la lista.
     * @param elemento El elemento a agregar.
     */
    public void add(T elemento) {
        NodoLista<T> nuevoNodo = new NodoLista<>(elemento);
        if (cabeza == null) {
            cabeza = nuevoNodo;
        } else {
            NodoLista<T> actual = cabeza;
            while (actual.siguiente != null) {
                actual = actual.siguiente;
            }
            actual.siguiente = nuevoNodo;
        }
        tamanio++;
    }

    /**
     * Obtiene el elemento en la posición indicada.
     * @param indice Índice del elemento (0 a tamanio-1).
     * @return El elemento en ese índice.
     */
    public T get(int indice) {
        if (indice < 0 || indice >= tamanio) {
            throw new IndexOutOfBoundsException("Índice fuera de rango: " + indice);
        }
        NodoLista<T> actual = cabeza;
        for (int i = 0; i < indice; i++) {
            actual = actual.siguiente;
        }
        return actual.dato;
    }
    
    /**
     * Reemplaza el elemento en la posición indicada.
     * @param indice Índice del elemento.
     * @param elemento Nuevo elemento.
     */
    public void set(int indice, T elemento) {
        if (indice < 0 || indice >= tamanio) {
            throw new IndexOutOfBoundsException("Índice fuera de rango: " + indice);
        }
        NodoLista<T> actual = cabeza;
        for (int i = 0; i < indice; i++) {
            actual = actual.siguiente;
        }
        actual.dato = elemento;
    }

    /**
     * Elimina el elemento en la posición indicada.
     * @param indice Índice del elemento a eliminar.
     */
    public void remove(int indice) {
        if (indice < 0 || indice >= tamanio) {
            throw new IndexOutOfBoundsException("Índice fuera de rango: " + indice);
        }
        if (indice == 0) {
            cabeza = cabeza.siguiente;
        } else {
            NodoLista<T> anterior = cabeza;
            for (int i = 0; i < indice - 1; i++) {
                anterior = anterior.siguiente;
            }
            anterior.siguiente = anterior.siguiente.siguiente;
        }
        tamanio--;
    }

    /**
     * Elimina la primera ocurrencia del elemento especificado.
     * @param elemento Elemento a eliminar.
     * @return true si se eliminó, false si no se encontró.
     */
    public boolean remove(T elemento) {
        if (cabeza == null) return false;

        if ((elemento == null && cabeza.dato == null) || (elemento != null && elemento.equals(cabeza.dato))) {
            cabeza = cabeza.siguiente;
            tamanio--;
            return true;
        }

        NodoLista<T> actual = cabeza;
        while (actual.siguiente != null) {
            if ((elemento == null && actual.siguiente.dato == null) || (elemento != null && elemento.equals(actual.siguiente.dato))) {
                actual.siguiente = actual.siguiente.siguiente;
                tamanio--;
                return true;
            }
            actual = actual.siguiente;
        }
        return false;
    }

    /**
     * Verifica si el elemento existe en la lista.
     */
    /**
     * Verifica si contiene el elemento.
     * @param elemento Elemento a buscar.
     * @return true si lo contiene.
     */
    public boolean contains(T elemento) {
        NodoLista<T> actual = cabeza;
        while (actual != null) {
            if ((elemento == null && actual.dato == null) || (elemento != null && elemento.equals(actual.dato))) {
                return true;
            }
            actual = actual.siguiente;
        }
        return false;
    }

    /**
     * Retorna el número de elementos en la lista.
     */
    /**
     * Obtiene el tamano.
     * @return Numero de elementos.
     */
    public int size() {
        return tamanio;
    }

    /**
     * Verifica si la lista está vacía.
     */
    /**
     * Verifica si esta vacia.
     * @return true si esta vacia.
     */
    public boolean isEmpty() {
        return tamanio == 0;
    }

    /**
     * Vacía la lista.
     */
    public void clear() {
        cabeza = null;
        tamanio = 0;
    }
    
    /**
     * Permite iterar sobre la lista.
     */
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private NodoLista<T> actual = cabeza;

            @Override
            public boolean hasNext() {
                return actual != null;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                T dato = actual.dato;
                actual = actual.siguiente;
                return dato;
            }
        };
    }

    /**
     * Nodo interno de la lista enlazada.
     */
    private static class NodoLista<T> {
        T dato;
        NodoLista<T> siguiente;

        NodoLista(T dato) {
            this.dato = dato;
            this.siguiente = null;
        }
    }
}
