package com.distribuidora.inventario.structures;

// ====================================================================
// [REQUISITO RUBRICA: GRAFOS] - Clase EnlaceRuta (arista del grafo)
// ====================================================================
// Representa una ARISTA (conexión) del grafo logístico.
// Cada EnlaceRuta guarda:
//   • idDestino   → ID del almacén de llegada
//   • distanciaKm → PESO de la arista (distancia o costo)
//   • descripcion → nombre de la vía o ruta
//
// En el grafo NO DIRIGIDO, conectar A→B crea automáticamente B→A.
// ====================================================================

/**
 * ============================================================
 * ESTRUCTURA – ARISTA DEL GRAFO LOGÍSTICO
 * ============================================================
 * Representa una conexión con peso entre dos almacenes dentro
 * del {@link GrafoLogistica}. Se almacena en la lista de
 * adyacencia del vértice origen.
 *
 * @author  Equipo Proyecto 4 – Ingeniería de Sistemas UNMSM
 * @version 2.0
 */
public class EnlaceRuta {

    // ─────────────────────────────────────────────────────────
    //  CAMPOS
    // ─────────────────────────────────────────────────────────

    /**
     * ID del almacén DESTINO de esta conexión.
     * El almacén ORIGEN es el vértice en cuya lista de adyacencia
     * se encuentra esta arista.
     */
    final String idDestino;

    /**
     * PESO de la arista: distancia en kilómetros (o costo de transporte).
     * Usado por el algoritmo de Dijkstra para encontrar la ruta óptima.
     */
    final double distanciaKm;

    /**
     * Descripción de la vía o carretera que une los dos almacenes.
     */
    final String descripcion;

    // ─────────────────────────────────────────────────────────
    //  CONSTRUCTOR
    // ─────────────────────────────────────────────────────────

    /**
     * Crea una arista hacia el almacén destino.
     *
     * @param idDestino   ID del almacén de destino.
     * @param distanciaKm Peso (distancia en km o costo).
     * @param descripcion Descripción de la ruta o vía.
     */
    public EnlaceRuta(String idDestino, double distanciaKm, String descripcion) {
        this.idDestino    = idDestino.toUpperCase().trim();
        this.distanciaKm  = distanciaKm;
        this.descripcion  = descripcion;
    }

    // ─────────────────────────────────────────────────────────
    //  ACCESORES
    // ─────────────────────────────────────────────────────────

    /** @return ID del almacén de destino. */
    public String getIdDestino()    { return idDestino; }

    /** @return Distancia en km (peso de la arista). */
    public double getDistanciaKm()  { return distanciaKm; }

    /** @return Descripción de la ruta. */
    public String getDescripcion()  { return descripcion; }

    @Override
    public String toString() {
        return String.format(" --(%.1f km)-> [%s] via %s",
            distanciaKm, idDestino, descripcion);
    }
}
