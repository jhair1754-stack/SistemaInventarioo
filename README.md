# PROYECTO 4: Sistema de Control de Inventario y Red Logística

> **Universidad Nacional Mayor de San Marcos (UNMSM)**  
> **Facultad de Ingeniería de Sistemas e Informática (FISI)**  
> **Escuela Profesional de Ingeniería de Sistemas**  
> **Asignatura:** Estructura de Datos (2026-I)  
> **Proyecto:** Sistema de Control de Inventario y Red Logística para una Empresa Distribuidora  
> **Equipo de Trabajo:** 9 Integrantes  

---

## 📌 Descripción del Proyecto

El **Sistema de Control de Inventario y Red Logística** es una solución integral orientada a objetos desarrollada en Java 17 con **Interfaz Gráfica (GUI) en Java Swing** para empresas distribuidoras. Permite la administración eficiente de productos, auditoría inmutable de movimientos de stock y el cálculo óptimo de rutas de distribución entre almacenes.

Todas las estructuras de datos principales (**Árbol Binario de Búsqueda**, **Pila Enlazada** y **Grafo con Lista de Adyacencia**) han sido **implementadas desde cero sin utilizar colecciones del framework `java.util`** para el almacenamiento interno de nodos. El sistema opera **100% en memoria RAM** cumpliendo con los requerimientos académicos estrictos.

---

## 🚀 Restricciones Técnicas y Arquitectura

1. **Lenguaje & Entorno:** Java JDK 17 o superior.
2. **Gestor de Construcción:** Estructura modular estándar **Maven** (`pom.xml`).
3. **Interfaz Gráfica:** Java Swing (`JFrame`, `JDialog`, `JTable`, `JOptionPane`) con formularios profesionales.
4. **Persistencia Dinámica:** Sin bases de datos ni archivos en disco. Todos los datos residen dinámicamente en RAM.
5. **Arquitectura por Almacén:** Cada almacén posee su propio `ArbolBinarioBusqueda` y su propia `PilaAuditoria`, aislando los datos de inventario y auditoría por nodo logístico.
6. **Paradigma de Programación:** Orientación a Objetos (OOP) impecable:
   - **Abstracción:** Modelo claro de dominio con entidades `Producto`, `Almacen`, `Transaccion`.
   - **Encapsulamiento:** Campos privados con validaciones mediante getters y setters.
   - **Polimorfismo:** Métodos genéricos de representación y comportamiento específico.
7. **Manejo Controlado de Excepciones:** Definición e integración de excepciones personalizadas capturadas visualmente con `JOptionPane`.

---

## 📊 Estructuras de Datos Implementadas desde Cero

Las estructuras de datos cuentan con comentarios explicativos explícitos en formato JavaDoc y etiquetas de rúbrica en el código fuente:

| Estructura | Clases | Uso y Complejidad | Comentario en Código |
|---|---|---|---|
| **Árbol Binario de Búsqueda (BST)** | `NodoArbol`, `ArbolBinarioBusqueda` | Organización e inserción de productos por código único. Búsqueda y eliminación en $O(\log n)$ promedio. Recorrido inorden para listado alfabético. Cada `Almacen` posee su propia instancia. | `// [REQUISITO RUBRICA: ÁRBOL BINARIO DE BÚSQUEDA]` |
| **Pila (Stack)** | `NodoPila`, `PilaAuditoria` | Registro de auditoría con disciplina LIFO (Last In, First Out). Cada movimiento (entrada/salida) genera un `push` de un objeto `Transaccion`. Cada `Almacen` posee su propia instancia. | `// [REQUISITO RUBRICA: PILAS]` |
| **Grafo No Dirigido con Pesos** | `Arista`, `GrafoLogistico` | Mapeo de la red de almacenes (vértices) y rutas (aristas con pesos en km). Implementa la **Lista de Adyacencia** y el **Algoritmo de Dijkstra** para la ruta más corta. | `// [REQUISITO RUBRICA: GRAFOS]` |
| **Lista Enlazada** | `ListaEnlazada` | Estructura auxiliar genérica para poblar `JTable` y transferir datos entre capas sin depender de `java.util.ArrayList`. | — |

---

## 📂 Estructura de Paquetes del Proyecto

```text
com.distribuidora.inventario/
├── Main.java                        # Punto de entrada, carga de datos demo y lanzamiento de GUI
├── models/                          # Entidades de dominio
│   ├── Producto.java                # Modelo unificado de producto (General y Perecible)
│   ├── Almacen.java                 # Vértice de la red logística (con BST y Pila propios)
│   └── Transaccion.java             # Registro inmutable para auditoría LIFO
├── structures/                      # Estructuras de datos desde cero
│   ├── NodoArbol.java               # Nodo del BST (punteros izquierdo/derecho)
│   ├── ArbolBinarioBusqueda.java    # BST completo (insertar, buscar, eliminar, inorden)
│   ├── NodoPila.java                # Nodo de la Pila (puntero siguiente)
│   ├── PilaAuditoria.java           # Pila LIFO (push, pop, peek, toList)
│   ├── ListaEnlazada.java           # Lista enlazada genérica auxiliar
│   ├── Arista.java                  # Conexión con peso entre almacenes
│   └── GrafoLogistico.java          # Grafo con lista de adyacencia y Dijkstra
├── exceptions/                      # Excepciones personalizadas
│   ├── StockInsuficienteException.java
│   ├── ProductoNoEncontradoException.java
│   ├── AlmacenNoEncontradoException.java
│   ├── CodigoProductoDuplicadoException.java
│   └── FechaVencimientoInvalidaException.java
├── services/                        # Capa de servicios y lógica de negocio
│   ├── InventarioService.java       # Orquestador del BST y PilaAuditoria por almacén
│   └── LogisticaService.java        # Orquestador del Grafo y rutas Dijkstra
└── ui/                              # Interfaz gráfica (Java Swing)
    ├── FrmPrincipal.java            # Dashboard principal con selector de almacén activo
    ├── FrmGestionProductos.java     # CRUD de productos con filtro por categoría
    ├── FrmControlStock.java         # Entradas/salidas de inventario con filtro
    ├── FrmLogistica.java            # Gestión de almacenes, rutas y Dijkstra
    └── FrmReportes.java             # Reporte de vencidos e historial de auditoría
```

---

## ⚠️ Excepciones Personalizadas

- `StockInsuficienteException`: Lanzada cuando se solicita retirar más unidades de las disponibles en el stock actual.
- `ProductoNoEncontradoException`: Lanzada cuando una búsqueda por código no retorna ningún nodo en el BST.
- `AlmacenNoEncontradoException`: Lanzada al hacer referencia a un ID de almacén inexistente en el Grafo.
- `CodigoProductoDuplicadoException`: Lanzada si se intenta insertar en el BST un producto con un código ya existente.
- `FechaVencimientoInvalidaException`: Lanzada si se intenta registrar un producto perecible con fecha de vencimiento anterior a hoy.

---

## 🖥️ Interfaz Gráfica (GUI - Java Swing)

La interfaz gráfica del sistema está construida con **Java Swing**, utilizando formularios (`JFrame`, `JDialog`) con diseño profesional claro. Para asegurar que todos los colores y componentes personalizados se rendericen correctamente en cualquier sistema operativo (especialmente en Windows), el sistema fuerza la inicialización del **Look & Feel "Metal" (CrossPlatform)**.

### Paleta de Colores
- **Fondo principal:** Gris perla `#F5F7FA` — limpio y neutro.
- **Paneles y tarjetas:** Blanco `#FFFFFF` — máxima legibilidad.
- **Acento (botones primarios):** Azul profesional `#2563EB`.
- **Botones de acción positiva:** Verde `#16A34A` (entradas, registros).
- **Botones de acción destructiva:** Rojo `#DC2626` (eliminar, salidas).
- **Texto primario:** Gris oscuro `#1E293B`.
- **Texto secundario:** Gris medio `#64748B`.
- **Bordes:** Gris claro `#E2E8F0`.

### Tipografía
- **Fuente principal:** Segoe UI (Bold / Plain) en tamaños 11–17pt.
- **Fuente monoespaciada (resultados):** Consolas 12pt.

### Formularios
1. **`FrmPrincipal`** — Dashboard con logo UNMSM, título, selector de almacén activo y 4 botones de navegación con iconos y hover interactivo.
2. **`FrmGestionProductos`** — Formulario CRUD con autogeneración de código, filtro por categorías reales y tabla expandida de 7 columnas: `Código | Nombre | Categoría | Precio (S/) | Stock Actual | Stock Mín. | Fecha Venc.`. Para productos estándar (no perecibles), la columna de fecha de vencimiento muestra `"-"`.
3. **`FrmControlStock`** — Tabla de stock con filtro por categoría y panel inferior para registrar entradas/salidas con validación inmediata.
4. **`FrmLogistica`** — Tres secciones: Nuevo Almacén, Conectar Rutas y Optimización de Rutas Logísticas. Los campos de origen y destino usan **listas desplegables (`JComboBox`)** que se cargan automáticamente con los almacenes del grafo. El resultado de la ruta óptima se muestra en un **panel inferior dedicado** con formato de flechas (`ALM-01 ➔ ALM-03 ➔ ALM-05 | Distancia Total: 45.00 km`).
5. **`FrmReportes`** — Tres pestañas: **Inventario Completo** (con polimorfismo: GENERAL muestra Marca, PERECIBLE muestra Fecha Venc.), **Productos Perecibles Vencidos** e **Historial de Movimientos (Pila LIFO)**.

---

## 💻 Funcionalidades

1. **Gestión de Productos (BST):**
   - Registrar producto nuevo (General o Perecible con fecha de vencimiento).
   - Buscar detalles de producto por código en $O(\log n)$.
   - Eliminar producto del BST reestructurando los punteros del árbol.
   - Listar productos en orden alfabético ascendente mediante recorrido **Inorden**.
   - **Filtrar por categoría** (Electrónico, Alimento, Farmacéutico, Hogar/Oficina, Ropa, Herramienta, etc.).

2. **Gestión de Stock & Auditoría (Pila):**
   - Registrar **Entrada de Stock**: Incrementa existencias y apila la transacción en la `PilaAuditoria`.
   - Registrar **Salida de Stock**: Disminuye existencias, valida disponibilidades y apila la transacción. Si el stock cae por debajo del umbral mínimo, emite una **Alerta de Stock Crítico** automática.
   - Cambiar de almacén activo para los movimientos.

3. **Reportes y Consultas:**
   - **Inventario Completo** con polimorfismo: diferencia visualmente productos GENERAL (muestra Marca) y PERECIBLE (muestra Fecha de Vencimiento).
   - Visualizar historial de movimientos recorriendo la pila en orden **LIFO** (más reciente primero).
   - Reporte de productos en estado de **Stock Crítico**.
   - Reporte de productos **perecibles vencidos**.

4. **Red Logística (Grafo & Dijkstra):**
   - Visualización de la **Lista de Adyacencia** completa de la red de distribución.
   - Agregar almacenes (vértices) y conectar almacenes con distancias (aristas) usando **listas desplegables** auto-pobladas.
   - **Calcular Ruta Óptima (Algoritmo de Dijkstra):** Determina la distancia mínima en kilómetros y el camino secuencial entre dos almacenes, con resultado visual en panel dedicado.

---

## 🛠️ Instrucciones de Compilación y Ejecución

### Prerrequisitos
- Tener instalado **Java JDK 17** o superior.
- Tener instalado **Apache Maven 3.6+**.

### 1. Compilación
Desde la raíz del proyecto (donde se ubica el archivo `pom.xml`):

```bash
mvn clean compile
```

### 2. Empaquetado (Generación de JAR)
```bash
mvn package
```

### 3. Ejecución
Para ejecutar el ejecutable JAR generado:

```bash
java -jar target/inventario-sistema.jar
```

O directamente usando el plugin ejecutable de Maven:

```bash
mvn exec:java
```

> 💡 **Nota:** La interfaz gráfica se abrirá automáticamente al ejecutar el sistema. Los datos de demostración (10 productos, 6 almacenes, 7 rutas) se cargan en memoria RAM al inicio.

---

## 📝 Verificación del Cumplimiento de la Rúbrica

- [x] **Árbol Binario de Búsqueda:** Implementado desde cero (`NodoArbol` + `ArbolBinarioBusqueda`). Cada almacén posee su propia instancia.
- [x] **Pila LIFO:** Implementada desde cero (`NodoPila` + `PilaAuditoria`). Cada almacén posee su propia instancia.
- [x] **Grafo + Dijkstra:** Implementado desde cero (`Arista` + `GrafoLogistico`).
- [x] **Lista Enlazada auxiliar:** Implementada desde cero (`ListaEnlazada`) para poblar `JTable` sin depender de `java.util`.
- [x] **Interfaz Gráfica (GUI):** Formularios Swing (`JFrame`, `JDialog`, `JTable`, `JOptionPane`) con diseño profesional.
- [x] **Polimorfismo:** Aplicado en reportes y tablas: productos GENERAL muestran Marca y `"-"` en Fecha Venc.; productos PERECIBLE muestran `"N/A"` en Marca y fecha real.
- [x] **Filtrado por Categoría:** Las categorías del filtro coinciden con las categorías reales de los productos (incluyendo los precargados).
- [x] **Identificadores estandarizados:** Formato `ALM-XX` (2 dígitos) para todos los almacenes.
- [x] **Selección por JComboBox:** Los campos de origen/destino en rutas y Dijkstra usan listas desplegables auto-pobladas del grafo.
- [x] **Comentarios de Rúbrica:** Presentes en el código fuente con el formato `// [REQUISITO RUBRICA: ...]`.
- [x] **Persistencia en RAM:** Todo el ciclo de vida gestionado dinámicamente en memoria.
- [x] **Manejo de Excepciones:** `try-catch` estructurados con 5 excepciones personalizadas mostradas en `JOptionPane`.
- [x] **Proyecto Maven:** Compatible con JDK 17 y limpio de errores de compilación.

