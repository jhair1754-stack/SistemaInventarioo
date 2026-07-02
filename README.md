# PROYECTO 4: Sistema de Control de Inventario y Red Logística

> **Universidad Nacional Mayor de San Marcos (UNMSM)**  
> **Facultad de Ingeniería de Sistemas e Informática (FISI)**  
> **Escuela Profesional de Ingeniería de Sistemas**  
> **Asignatura:** Estructura de Datos (2026-I)  
> **Proyecto:** Sistema de Control de Inventario y Red Logística para una Empresa Distribuidora  
> **Equipo de Trabajo:** 9 Integrantes  

---

## 📌 Descripción del Proyecto

El **Sistema de Control de Inventario y Red Logística** es una solución integral orientada a objetos desarrollada en Java 17 para empresas distribuidoras. Permite la administración eficiente de productos, auditoría inmutable de movimientos de stock y el cálculo óptimo de rutas de distribución entre almacenes.

Todas las estructuras de datos principales (**Árbol Binario de Búsqueda**, **Pila Enlazada** y **Grafo con Lista de Adyacencia**) han sido **implementadas desde cero sin utilizar colecciones del framework `java.util`** para el almacenamiento interno de nodos. El sistema opera **100% en memoria RAM** cumpliendo con los requerimientos académicos estrictos.

---

## 🚀 Restricciones Técnicas y Arquitectura

1. **Lenguaje & Entorno:** Java JDK 17 o superior.
2. **Gestor de Construcción:** Estructura modular estándar **Maven** (`pom.xml`).
3. **Persistencia Dinámica:** Sin bases de datos ni archivos en disco. Todos los datos residen dinámicamente en RAM.
4. **Paradigma de Programación:** Orientación a Objetos (OOP) impecable:
   - **Abstracción:** Modelo claro de dominio con entidades `Producto`, `Almacen`, `Transaccion`.
   - **Encapsulamiento:** Campos privados con validaciones mediante getters y setters.
   - **Polimorfismo:** Métodos genéricos de representación y comportamiento específico.
5. **Manejo Controlado de Excepciones:** Definición e integración de excepciones personalizadas en puntos críticos de la aplicación.

---

## 📊 Estructuras de Datos Implementadas desde Cero

Las estructuras de datos cuentan con comentarios explicativos explícitos en formato JavaDoc y etiquetas de rúbrica en el código fuente:

| Estructura | Clases | Uso y Complejidad | Comentario en Código |
|---|---|---|---|
| **Árbol Binario de Búsqueda (BST)** | `NodoArbol`, `ArbolBinarioBusqueda` | Organización e inserción de productos por código único. Búsqueda y eliminación en $O(\log n)$ promedio. Recorrido inorden para listado alfabético. | `// [REQUISITO RUBRICA: ÁRBOL BINARIO DE BÚSQUEDA]` |
| **Pila (Stack)** | `NodoPila`, `PilaAuditoria` | Registro de auditoría con disciplina LIFO (Last In, First Out). Cada movimiento (entrada/salida) genera un `push` de un objeto `Transaccion`. | `// [REQUISITO RUBRICA: PILAS]` |
| **Grafo No Dirigido con Pesos** | `Arista`, `GrafoLogistico` | Mapeo de la red de almacenes (vértices) y rutas (aristas con pesos en km). Implementa la **Lista de Adyacencia** y el **Algoritmo de Dijkstra** para la ruta más corta. | `// [REQUISITO RUBRICA: GRAFOS]` |

---

## 📂 Estructura de Paquetes del Proyecto

```text
com.distribuidora.inventario/
├── Main.java                        # Punto de entrada y carga de datos de demostración
├── models/                          # Entidades de dominio
│   ├── Producto.java                # Modelo unificado de producto (General y Perecible)
│   ├── Almacen.java                 # Vértice de la red logística
│   └── Transaccion.java             # Registro inmutable para auditoría LIFO
├── structures/                      # Estructuras de datos desde cero
│   ├── NodoArbol.java               # Nodo del BST (punteros izquierdo/derecho)
│   ├── ArbolBinarioBusqueda.java    # BST completo (insertar, buscar, eliminar, inorden)
│   ├── NodoPila.java                # Nodo de la Pila (puntero siguiente)
│   ├── PilaAuditoria.java           # Pila LIFO (push, pop, peek, toList)
│   ├── Arista.java                  # Conexión con peso entre almacenes
│   └── GrafoLogistico.java          # Grafo con lista de adyacencia y Dijkstra
├── exceptions/                      # Excepciones personalizadas
│   ├── StockInsuficienteException.java
│   ├── ProductoNoEncontradoException.java
│   ├── AlmacenNoEncontradoException.java
│   └── CodigoProductoDuplicadoException.java
├── services/                        # Capa de servicios y lógica de negocio
│   ├── InventarioService.java       # Orquestador del BST y PilaAuditoria
│   └── LogisticaService.java        # Orquestador del Grafo y rutas Dijkstra
└── ui/                              # Interfaz de usuario por consola
    └── MenuConsola.java             # Menú interactivo con submenús y colores ANSI
```

---

## ⚠️ Excepciones Personalizadas

- `StockInsuficienteException`: Lianzada cuando se solicita retirar más unidades de las disponibles en el stock actual.
- `ProductoNoEncontradoException`: Lanzada cuando una búsqueda por código no retorna ningún nodo en el BST.
- `AlmacenNoEncontradoException`: Lanzada al hacer referencia a un ID de almacén inexistente en el Grafo.
- `CodigoProductoDuplicadoException`: Lanzada si se intenta insertar en el BST un producto con un código ya existente.

---

## 💻 Funcionalidades de la Interfaz Interactiva

1. **Gestión de Productos (BST):**
   - Registrar producto nuevo (General o Perecible con fecha de vencimiento).
   - Buscar detalles de producto por código en $O(\log n)$.
   - Eliminar producto del BST reestructurando los punteros del árbol.
   - Listar productos en orden alfabético ascendente mediante recorrido **Inorden**.

2. **Gestión de Stock & Auditoría (Pila):**
   - Registrar **Entrada de Stock**: Incrementa existencias y apila la transacción en la `PilaAuditoria`.
   - Registrar **Salida de Stock**: Disminuye existencias, valida disponibilidades y apila la transacción. Si el stock cae por debajo del umbral mínimo, emite una **Alerta de Stock Crítico** automática.
   - Cambiar de almacén activo para los movimientos.

3. **Reportes y Consultas:**
   - Visualizar historial de movimientos desapilando/recorriendo la pila en orden **LIFO** (más reciente primero).
   - Operación `POP` explícita para retirar la última transacción.
   - Reporte de productos en estado de **Stock Crítico**.
   - Estadísticas de las estructuras (altura del árbol, tamaño de la pila, vértices y aristas).

4. **Red Logística (Grafo & Dijkstra):**
   - Visualización de la **Lista de Adyacencia** completa de la red de distribución.
   - Agregar almacenes (vértices) y conectar almacenes con distancias (aristas).
   - **Calcular Ruta Óptima (Algoritmo de Dijkstra):** Determina la distancia mínima en kilómetros y el camino secuencial entre dos almacenes de la red.

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

> 💡 **Nota de Visualización:** Se recomienda ejecutar en una terminal con soporte para colores ANSI (como **Windows Terminal**, **Git Bash** o terminales de Linux/macOS) para visualizar correctamente la interfaz formateada.

---

## 📝 Verificación del Cumplimiento de la Rúbrica

- [x] **Árbol Binario de Búsqueda:** Implementado desde cero (`NodoArbol` + `ArbolBinarioBusqueda`).
- [x] **Pila LIFO:** Implementada desde cero (`NodoPila` + `PilaAuditoria`).
- [x] **Grafo + Dijkstra:** Implementado desde cero (`Arista` + `GrafoLogistico`).
- [x] **Comentarios de Rúbrica:** Presentes en el código fuente con el formato `// [REQUISITO RUBRICA: ...]`.
- [x] **Persistencia en RAM:** Todo el ciclo de vida gestionado dinámicamente en memoria.
- [x] **Manejo de Excepciones:** `try-catch` estructurados con 4 excepciones personalizadas.
- [x] **Proyecto Maven:** Compatible con JDK 17 y limpio de errores de compilación.
