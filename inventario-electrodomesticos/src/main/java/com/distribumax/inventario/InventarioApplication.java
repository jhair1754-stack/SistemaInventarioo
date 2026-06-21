package com.distribumax.inventario;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la aplicación.
 * Sistema de Gestión de Inventarios — Distribuidora de Electrodomésticos.
 *
 * Arquitectura: Clean Architecture / N-Tier (estilo ASP.NET)
 *   - domain/       → Capa de Dominio (Entidades, Reglas de Negocio, Puertos)
 *   - application/   → Capa de Aplicación (Casos de Uso, DTOs, Servicios)
 *   - infrastructure/ → Capa de Infraestructura (Persistencia, Configuración)
 *   - presentation/  → Capa de Presentación (REST Controllers, API)
 */
@SpringBootApplication
public class InventarioApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventarioApplication.class, args);
    }

}
