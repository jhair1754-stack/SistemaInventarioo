/**
 * ══════════════════════════════════════════════════════════════════════
 *  DistribuMax — Cliente JavaScript (Fetch API / async-await)
 *  Conecta el frontend HTML con la API REST de Spring Boot.
 * ══════════════════════════════════════════════════════════════════════
 */

// ─── Configuración Base ──────────────────────────────────────────────
const API_BASE = '/api';

// ─── Utilidades HTTP (async/await + fetch) ───────────────────────────

/**
 * Realiza una petición HTTP genérica a la API.
 * @param {string} endpoint - Ruta relativa (ej. '/productos')
 * @param {object} options - Opciones de fetch (method, body, etc.)
 * @returns {Promise<any>} Respuesta parseada como JSON
 */
async function apiRequest(endpoint, options = {}) {
    const url = `${API_BASE}${endpoint}`;
    const config = {
        headers: { 'Content-Type': 'application/json' },
        ...options,
    };

    try {
        const response = await fetch(url, config);

        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.mensaje || `Error ${response.status}: ${response.statusText}`);
        }

        // 204 No Content
        if (response.status === 204) return null;

        return await response.json();
    } catch (error) {
        if (error.message.includes('Failed to fetch') || error.message.includes('NetworkError')) {
            throw new Error('No se pudo conectar con el servidor. Verifica que la API esté corriendo en localhost:8080');
        }
        throw error;
    }
}

// Métodos HTTP convenientes
const api = {
    get: (endpoint) => apiRequest(endpoint),
    post: (endpoint, data) => apiRequest(endpoint, { method: 'POST', body: JSON.stringify(data) }),
    put: (endpoint, data) => apiRequest(endpoint, { method: 'PUT', body: JSON.stringify(data) }),
    delete: (endpoint) => apiRequest(endpoint, { method: 'DELETE' }),
};


// ═══════════════════════════════════════════════════════════════════
//  NAVEGACIÓN ENTRE VISTAS
// ═══════════════════════════════════════════════════════════════════

function showView(viewName) {
    // Ocultar todas las vistas
    document.querySelectorAll('.view-section').forEach(v => v.classList.remove('active'));
    // Desactivar todos los nav-links
    document.querySelectorAll('.nav-link').forEach(n => n.classList.remove('active'));

    // Activar la vista seleccionada
    document.getElementById(`view-${viewName}`).classList.add('active');
    document.getElementById(`nav-${viewName}`).classList.add('active');

    // Cargar datos según la vista
    switch (viewName) {
        case 'dashboard': cargarDashboard(); break;
        case 'productos': cargarProductos(); break;
        case 'movimientos': cargarMovimientos(); break;
        case 'alertas': cargarAlertasFull(); break;
    }
}


// ═══════════════════════════════════════════════════════════════════
//  DASHBOARD
// ═══════════════════════════════════════════════════════════════════

async function cargarDashboard() {
    try {
        // Cargar KPIs
        const resumen = await api.get('/dashboard');
        document.getElementById('kpi-total-productos').textContent = resumen.totalProductos || 0;
        document.getElementById('kpi-total-unidades').textContent = resumen.totalUnidades || 0;
        document.getElementById('kpi-valor-total').textContent = formatCurrency(resumen.valorTotalInventario || 0);
        document.getElementById('kpi-movimientos-hoy').textContent = resumen.movimientosHoy || 0;
        document.getElementById('kpi-stock-bajo').textContent = resumen.productosStockBajo || 0;

        // Cargar últimos movimientos
        const movimientos = await api.get('/movimientos');
        renderUltimosMovimientos(movimientos.slice(0, 8));

        // Cargar alertas del dashboard
        const alertas = await api.get('/dashboard/alertas');
        renderDashboardAlertas(alertas);

        // Actualizar badge de alerta en navbar
        const alertaCount = document.getElementById('alerta-count');
        if (alertas.length > 0) {
            alertaCount.textContent = alertas.length;
            alertaCount.style.display = 'inline';
        } else {
            alertaCount.style.display = 'none';
        }

    } catch (error) {
        showToast('Error al cargar el dashboard: ' + error.message, 'error');
    }
}

function renderUltimosMovimientos(movimientos) {
    const tbody = document.getElementById('tbody-ultimos-movimientos');
    if (movimientos.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5"><div class="empty-state"><i class="bi bi-inbox"></i><p>No hay movimientos registrados</p></div></td></tr>';
        return;
    }

    tbody.innerHTML = movimientos.map(m => `
        <tr>
            <td>
                <div style="font-weight:600;">${escapeHtml(m.productoNombre)}</div>
                <div style="font-size:0.75rem; color:var(--text-muted);">${escapeHtml(m.productoSku)}</div>
            </td>
            <td><span class="badge-${m.tipoMovimiento.toLowerCase()}">${m.tipoMovimiento}</span></td>
            <td style="font-weight:600;">${m.tipoMovimiento === 'ENTRADA' ? '+' : '-'}${m.cantidad}</td>
            <td>${escapeHtml(m.almacenNombre)}</td>
            <td style="font-size:0.8rem; color:var(--text-muted);">${formatDate(m.fechaMovimiento)}</td>
        </tr>
    `).join('');
}

function renderDashboardAlertas(alertas) {
    const container = document.getElementById('dashboard-alertas-list');
    if (alertas.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <i class="bi bi-check-circle" style="color: var(--accent-green);"></i>
                <p>¡Todo en orden! No hay alertas de stock.</p>
            </div>`;
        return;
    }

    container.innerHTML = alertas.slice(0, 5).map(a => `
        <div class="alert-card">
            <div class="alert-icon"><i class="bi bi-exclamation-triangle-fill"></i></div>
            <div class="alert-info">
                <div class="alert-name">${escapeHtml(a.productoNombre)}</div>
                <div class="alert-detail">${escapeHtml(a.codigoSku)} · ${escapeHtml(a.marcaNombre)}</div>
            </div>
            <div class="alert-deficit">
                ${a.stockActual}/${a.stockMinimo}
                <small>Déficit: ${a.deficit}</small>
            </div>
        </div>
    `).join('');
}


// ═══════════════════════════════════════════════════════════════════
//  PRODUCTOS — CRUD
// ═══════════════════════════════════════════════════════════════════

async function cargarProductos() {
    try {
        const productos = await api.get('/productos');
        renderTablaProductos(productos);
    } catch (error) {
        showToast('Error al cargar productos: ' + error.message, 'error');
    }
}

async function filtrarProductos() {
    const busqueda = document.getElementById('filtro-busqueda').value;
    const categoriaId = document.getElementById('filtro-categoria').value;
    const marcaId = document.getElementById('filtro-marca').value;

    const params = new URLSearchParams();
    if (busqueda) params.append('busqueda', busqueda);
    if (categoriaId) params.append('categoriaId', categoriaId);
    if (marcaId) params.append('marcaId', marcaId);

    try {
        const queryStr = params.toString() ? `?${params.toString()}` : '';
        const productos = await api.get(`/productos${queryStr}`);
        renderTablaProductos(productos);
    } catch (error) {
        showToast('Error al filtrar: ' + error.message, 'error');
    }
}

function limpiarFiltros() {
    document.getElementById('filtro-busqueda').value = '';
    document.getElementById('filtro-categoria').value = '';
    document.getElementById('filtro-marca').value = '';
    cargarProductos();
}

function renderTablaProductos(productos) {
    const tbody = document.getElementById('tbody-productos');
    if (productos.length === 0) {
        tbody.innerHTML = '<tr><td colspan="8"><div class="empty-state"><i class="bi bi-inbox"></i><p>No se encontraron productos</p></div></td></tr>';
        return;
    }

    tbody.innerHTML = productos.map(p => {
        const stockClass = p.stockActual <= 0 ? 'critical' : (p.stockBajo ? 'low' : 'ok');
        const stockIcon = p.stockActual <= 0 ? 'x-circle' : (p.stockBajo ? 'exclamation-circle' : 'check-circle');
        const stockLabel = p.stockActual <= 0 ? 'Sin stock' : (p.stockBajo ? 'Stock bajo' : 'OK');

        return `
        <tr>
            <td><code style="color:var(--accent-cyan); font-size:0.8rem;">${escapeHtml(p.codigoSku)}</code></td>
            <td>
                <div style="font-weight:600;">${escapeHtml(p.nombre)}</div>
                <div style="font-size:0.75rem; color:var(--text-muted);">${escapeHtml(p.modelo || '')}</div>
            </td>
            <td>${escapeHtml(p.marcaNombre)}</td>
            <td>
                <div>${escapeHtml(p.subcategoriaNombre)}</div>
                <div style="font-size:0.7rem; color:var(--text-muted);">${escapeHtml(p.categoriaNombre)}</div>
            </td>
            <td style="font-weight:600;">S/ ${parseFloat(p.precioUnitario).toFixed(2)}</td>
            <td style="font-weight:700;">${p.stockActual} <span style="color:var(--text-muted); font-weight:400; font-size:0.75rem;">/ min ${p.stockMinimo}</span></td>
            <td><span class="badge-stock ${stockClass}"><i class="bi bi-${stockIcon}"></i> ${stockLabel}</span></td>
            <td>
                <div class="d-flex gap-1">
                    <button class="btn btn-edit-small" onclick="abrirModalEditarProducto(${p.id})" title="Editar">
                        <i class="bi bi-pencil"></i>
                    </button>
                    <button class="btn btn-danger-small" onclick="eliminarProducto(${p.id}, '${escapeHtml(p.nombre)}')" title="Eliminar">
                        <i class="bi bi-trash"></i>
                    </button>
                </div>
            </td>
        </tr>`;
    }).join('');
}

// ─── Modal Crear Producto ────────────────────────────────────────────

function abrirModalCrearProducto() {
    document.getElementById('modalProductoTitle').innerHTML = '<i class="bi bi-plus-circle"></i> Nuevo Producto';
    document.getElementById('prod-id').value = '';
    document.getElementById('formProducto').reset();
    document.getElementById('prod-stock-minimo').value = '5';
    new bootstrap.Modal(document.getElementById('modalProducto')).show();
}

async function abrirModalEditarProducto(id) {
    try {
        const producto = await api.get(`/productos/${id}`);
        document.getElementById('modalProductoTitle').innerHTML = '<i class="bi bi-pencil"></i> Editar Producto';
        document.getElementById('prod-id').value = producto.id;
        document.getElementById('prod-sku').value = producto.codigoSku;
        document.getElementById('prod-nombre').value = producto.nombre;
        document.getElementById('prod-modelo').value = producto.modelo || '';
        document.getElementById('prod-marca').value = producto.marcaId;
        document.getElementById('prod-subcategoria').value = producto.subcategoriaId;
        document.getElementById('prod-precio').value = producto.precioUnitario;
        document.getElementById('prod-stock-minimo').value = producto.stockMinimo;
        document.getElementById('prod-peso').value = producto.pesoKg || '';
        document.getElementById('prod-ancho').value = producto.anchoCm || '';
        document.getElementById('prod-alto').value = producto.altoCm || '';
        document.getElementById('prod-profundidad').value = producto.profundidadCm || '';

        new bootstrap.Modal(document.getElementById('modalProducto')).show();
    } catch (error) {
        showToast('Error al cargar producto: ' + error.message, 'error');
    }
}

async function guardarProducto() {
    const id = document.getElementById('prod-id').value;
    const data = {
        codigoSku: document.getElementById('prod-sku').value.trim(),
        nombre: document.getElementById('prod-nombre').value.trim(),
        modelo: document.getElementById('prod-modelo').value.trim() || null,
        marcaId: parseInt(document.getElementById('prod-marca').value),
        subcategoriaId: parseInt(document.getElementById('prod-subcategoria').value),
        precioUnitario: parseFloat(document.getElementById('prod-precio').value),
        stockMinimo: parseInt(document.getElementById('prod-stock-minimo').value),
        pesoKg: parseFloat(document.getElementById('prod-peso').value) || null,
        anchoCm: parseFloat(document.getElementById('prod-ancho').value) || null,
        altoCm: parseFloat(document.getElementById('prod-alto').value) || null,
        profundidadCm: parseFloat(document.getElementById('prod-profundidad').value) || null,
    };

    // Validación básica
    if (!data.codigoSku || !data.nombre || !data.marcaId || !data.subcategoriaId || !data.precioUnitario) {
        showToast('Por favor completa todos los campos obligatorios (*)', 'warning');
        return;
    }

    try {
        if (id) {
            await api.put(`/productos/${id}`, data);
            showToast('Producto actualizado correctamente', 'success');
        } else {
            await api.post('/productos', data);
            showToast('Producto creado correctamente', 'success');
        }

        bootstrap.Modal.getInstance(document.getElementById('modalProducto')).hide();
        cargarProductos();
        cargarDashboard();
    } catch (error) {
        showToast('Error: ' + error.message, 'error');
    }
}

async function eliminarProducto(id, nombre) {
    if (!confirm(`¿Estás seguro de eliminar "${nombre}"?\nEsta acción desactivará el producto.`)) return;

    try {
        await api.delete(`/productos/${id}`);
        showToast(`Producto "${nombre}" eliminado correctamente`, 'success');
        cargarProductos();
        cargarDashboard();
    } catch (error) {
        showToast('Error al eliminar: ' + error.message, 'error');
    }
}


// ═══════════════════════════════════════════════════════════════════
//  MOVIMIENTOS — Entradas y Salidas
// ═══════════════════════════════════════════════════════════════════

async function cargarMovimientos() {
    try {
        const movimientos = await api.get('/movimientos');
        renderTablaMovimientos(movimientos);
    } catch (error) {
        showToast('Error al cargar movimientos: ' + error.message, 'error');
    }
}

async function filtrarMovimientos() {
    const tipo = document.getElementById('filtro-mov-tipo').value;
    const desde = document.getElementById('filtro-mov-desde').value;
    const hasta = document.getElementById('filtro-mov-hasta').value;

    const params = new URLSearchParams();
    if (tipo) params.append('tipo', tipo);
    if (desde) params.append('desde', desde);
    if (hasta) params.append('hasta', hasta);

    try {
        const queryStr = params.toString() ? `?${params.toString()}` : '';
        const movimientos = await api.get(`/movimientos${queryStr}`);
        renderTablaMovimientos(movimientos);
    } catch (error) {
        showToast('Error al filtrar movimientos: ' + error.message, 'error');
    }
}

function renderTablaMovimientos(movimientos) {
    const tbody = document.getElementById('tbody-movimientos');
    if (movimientos.length === 0) {
        tbody.innerHTML = '<tr><td colspan="9"><div class="empty-state"><i class="bi bi-inbox"></i><p>No hay movimientos registrados</p></div></td></tr>';
        return;
    }

    tbody.innerHTML = movimientos.map(m => `
        <tr>
            <td style="color:var(--text-muted);">#${m.id}</td>
            <td style="font-weight:600;">${escapeHtml(m.productoNombre)}</td>
            <td><code style="color:var(--accent-cyan); font-size:0.8rem;">${escapeHtml(m.productoSku)}</code></td>
            <td><span class="badge-${m.tipoMovimiento.toLowerCase()}">${m.tipoMovimiento}</span></td>
            <td style="font-weight:700;">${m.tipoMovimiento === 'ENTRADA' ? '+' : '-'}${m.cantidad}</td>
            <td>${escapeHtml(m.almacenNombre)}</td>
            <td>${m.proveedorNombre ? escapeHtml(m.proveedorNombre) : '<span style="color:var(--text-muted);">—</span>'}</td>
            <td>${m.numeroLote ? escapeHtml(m.numeroLote) : '<span style="color:var(--text-muted);">—</span>'}</td>
            <td style="font-size:0.8rem; color:var(--text-muted);">${formatDate(m.fechaMovimiento)}</td>
        </tr>
    `).join('');
}

// ─── Modal Entrada ───────────────────────────────────────────────────

function abrirModalEntrada() {
    document.getElementById('formEntrada').reset();
    document.getElementById('entrada-cantidad').value = '1';
    cargarSelectsMovimiento();
    new bootstrap.Modal(document.getElementById('modalEntrada')).show();
}

async function registrarEntrada() {
    const data = {
        productoId: parseInt(document.getElementById('entrada-producto').value),
        almacenId: parseInt(document.getElementById('entrada-almacen').value),
        proveedorId: document.getElementById('entrada-proveedor').value
            ? parseInt(document.getElementById('entrada-proveedor').value) : null,
        tipoMovimiento: 'ENTRADA',
        cantidad: parseInt(document.getElementById('entrada-cantidad').value),
        numeroLote: document.getElementById('entrada-lote').value.trim() || null,
        observaciones: document.getElementById('entrada-observaciones').value.trim() || null,
        numerosSerie: parseSerialNumbers(document.getElementById('entrada-series').value),
    };

    if (!data.productoId || !data.almacenId || !data.cantidad) {
        showToast('Por favor completa los campos obligatorios', 'warning');
        return;
    }

    try {
        await api.post('/inventario/entrada', data);
        showToast(`Entrada registrada: +${data.cantidad} unidades`, 'success');
        bootstrap.Modal.getInstance(document.getElementById('modalEntrada')).hide();
        cargarMovimientos();
        cargarDashboard();
    } catch (error) {
        showToast('Error: ' + error.message, 'error');
    }
}

// ─── Modal Salida ────────────────────────────────────────────────────

function abrirModalSalida() {
    document.getElementById('formSalida').reset();
    document.getElementById('salida-cantidad').value = '1';
    cargarSelectsMovimiento();
    new bootstrap.Modal(document.getElementById('modalSalida')).show();
}

async function registrarSalida() {
    const data = {
        productoId: parseInt(document.getElementById('salida-producto').value),
        almacenId: parseInt(document.getElementById('salida-almacen').value),
        tipoMovimiento: 'SALIDA',
        cantidad: parseInt(document.getElementById('salida-cantidad').value),
        observaciones: document.getElementById('salida-observaciones').value.trim() || null,
        numerosSerie: parseSerialNumbers(document.getElementById('salida-series').value),
    };

    if (!data.productoId || !data.almacenId || !data.cantidad) {
        showToast('Por favor completa los campos obligatorios', 'warning');
        return;
    }

    try {
        await api.post('/inventario/salida', data);
        showToast(`Salida registrada: -${data.cantidad} unidades`, 'success');
        bootstrap.Modal.getInstance(document.getElementById('modalSalida')).hide();
        cargarMovimientos();
        cargarDashboard();
    } catch (error) {
        showToast('Error: ' + error.message, 'error');
    }
}


// ═══════════════════════════════════════════════════════════════════
//  ALERTAS
// ═══════════════════════════════════════════════════════════════════

async function cargarAlertasFull() {
    try {
        const alertas = await api.get('/dashboard/alertas');
        const container = document.getElementById('alertas-full-list');

        if (alertas.length === 0) {
            container.innerHTML = `
                <div class="content-card">
                    <div class="empty-state">
                        <i class="bi bi-check-circle" style="color: var(--accent-green);"></i>
                        <p>¡Excelente! No hay productos con stock bajo.</p>
                    </div>
                </div>`;
            return;
        }

        container.innerHTML = `
            <div class="content-card">
                <div class="section-header">
                    <span style="color:var(--text-muted); font-size:0.85rem;">
                        <i class="bi bi-exclamation-triangle" style="color:var(--accent-orange);"></i>
                        ${alertas.length} producto(s) requieren reposición
                    </span>
                </div>
                ${alertas.map(a => `
                    <div class="alert-card">
                        <div class="alert-icon"><i class="bi bi-exclamation-triangle-fill"></i></div>
                        <div class="alert-info">
                            <div class="alert-name">${escapeHtml(a.productoNombre)}</div>
                            <div class="alert-detail">
                                SKU: ${escapeHtml(a.codigoSku)} · ${escapeHtml(a.categoriaNombre)} · ${escapeHtml(a.marcaNombre)}
                            </div>
                        </div>
                        <div class="alert-deficit">
                            ${a.stockActual} / ${a.stockMinimo}
                            <small>Faltan ${a.deficit} unidades</small>
                        </div>
                    </div>
                `).join('')}
            </div>`;
    } catch (error) {
        showToast('Error al cargar alertas: ' + error.message, 'error');
    }
}


// ═══════════════════════════════════════════════════════════════════
//  CARGA DE SELECTS / CATÁLOGOS
// ═══════════════════════════════════════════════════════════════════

async function cargarCatalogos() {
    try {
        const [categorias, subcategorias, marcas, proveedores, almacenes] = await Promise.all([
            api.get('/catalogo/categorias'),
            api.get('/catalogo/subcategorias'),
            api.get('/catalogo/marcas'),
            api.get('/catalogo/proveedores'),
            api.get('/catalogo/almacenes'),
        ]);

        // Filtro de categorías
        const filtroCat = document.getElementById('filtro-categoria');
        categorias.forEach(c => {
            filtroCat.innerHTML += `<option value="${c.id}">${escapeHtml(c.nombre)}</option>`;
        });

        // Filtro de marcas
        const filtroMarca = document.getElementById('filtro-marca');
        marcas.forEach(m => {
            filtroMarca.innerHTML += `<option value="${m.id}">${escapeHtml(m.nombre)}</option>`;
        });

        // Selects del modal producto
        const prodMarca = document.getElementById('prod-marca');
        marcas.forEach(m => {
            prodMarca.innerHTML += `<option value="${m.id}">${escapeHtml(m.nombre)}</option>`;
        });

        const prodSubcat = document.getElementById('prod-subcategoria');
        subcategorias.forEach(s => {
            prodSubcat.innerHTML += `<option value="${s.id}">${escapeHtml(s.nombre)} (${escapeHtml(s.categoriaNombre)})</option>`;
        });

        // Guardar para selects de movimientos
        window._catalogos = { categorias, subcategorias, marcas, proveedores, almacenes };

    } catch (error) {
        showToast('Error al cargar catálogos: ' + error.message, 'error');
    }
}

function cargarSelectsMovimiento() {
    const cat = window._catalogos;
    if (!cat) return;

    // Productos para los selects de entrada/salida
    api.get('/productos').then(productos => {
        ['entrada-producto', 'salida-producto'].forEach(selectId => {
            const select = document.getElementById(selectId);
            select.innerHTML = '<option value="">Seleccionar producto...</option>';
            productos.forEach(p => {
                select.innerHTML += `<option value="${p.id}">${escapeHtml(p.nombre)} (Stock: ${p.stockActual})</option>`;
            });
        });
    });

    // Almacenes
    ['entrada-almacen', 'salida-almacen'].forEach(selectId => {
        const select = document.getElementById(selectId);
        select.innerHTML = '<option value="">Seleccionar...</option>';
        cat.almacenes.forEach(a => {
            select.innerHTML += `<option value="${a.id}">${escapeHtml(a.nombre)}</option>`;
        });
    });

    // Proveedores (solo entrada)
    const provSelect = document.getElementById('entrada-proveedor');
    provSelect.innerHTML = '<option value="">Seleccionar...</option>';
    cat.proveedores.forEach(p => {
        provSelect.innerHTML += `<option value="${p.id}">${escapeHtml(p.razonSocial)}</option>`;
    });
}


// ═══════════════════════════════════════════════════════════════════
//  UTILIDADES
// ═══════════════════════════════════════════════════════════════════

/**
 * Muestra una notificación toast con animación.
 */
function showToast(message, type = 'success') {
    const container = document.getElementById('toastContainer');
    const icons = { success: 'check-circle-fill', error: 'x-circle-fill', warning: 'exclamation-circle-fill' };

    const toast = document.createElement('div');
    toast.className = `toast-notification ${type}`;
    toast.innerHTML = `<i class="bi bi-${icons[type] || icons.success}"></i> ${escapeHtml(message)}`;
    container.appendChild(toast);

    // Auto-remover después de 4 segundos
    setTimeout(() => {
        toast.style.animation = 'slideOutRight 0.3s ease forwards';
        setTimeout(() => toast.remove(), 300);
    }, 4000);
}

/**
 * Formatea un número como moneda peruana (S/).
 */
function formatCurrency(value) {
    const num = parseFloat(value);
    if (num >= 1000000) return `S/ ${(num / 1000000).toFixed(1)}M`;
    if (num >= 1000) return `S/ ${(num / 1000).toFixed(1)}K`;
    return `S/ ${num.toFixed(2)}`;
}

/**
 * Formatea un datetime ISO a formato legible.
 */
function formatDate(isoDate) {
    if (!isoDate) return '—';
    const date = new Date(isoDate);
    return date.toLocaleDateString('es-PE', {
        day: '2-digit', month: 'short', year: 'numeric',
        hour: '2-digit', minute: '2-digit',
    });
}

/**
 * Parsea números de serie del textarea (separados por líneas o comas).
 */
function parseSerialNumbers(text) {
    if (!text || !text.trim()) return null;
    return text.split(/[\n,]+/)
        .map(s => s.trim())
        .filter(s => s.length > 0);
}

/**
 * Escapa HTML para prevenir XSS.
 */
function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}


// ═══════════════════════════════════════════════════════════════════
//  INICIALIZACIÓN
// ═══════════════════════════════════════════════════════════════════

document.addEventListener('DOMContentLoaded', async () => {
    console.log('🚀 DistribuMax — Iniciando aplicación...');

    // Cargar catálogos (para llenar selects)
    await cargarCatalogos();

    // Cargar dashboard por defecto
    await cargarDashboard();

    console.log('✅ Aplicación cargada correctamente');
});
