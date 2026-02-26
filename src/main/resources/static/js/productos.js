// ID del producto que se está editando o eliminando
let productoIdActual = null;

// ===== INICIALIZACIÓN =====
document.addEventListener('DOMContentLoaded', function () {
    verificarSesion(function () {
        cargarCategorias();
        cargarProductos();
    });
});

// ===== CARGAR PRODUCTOS =====
function cargarProductos() {
    fetch('/api/productos')
        .then(function(response) { return response.json(); })
        .then(function(data) {
            if (!data.exito) {
                window.location.href = 'index.html';
                return;
            }
            renderizarTabla(data.productos);
        })
        .catch(function(error) {
            console.error('Error al cargar productos:', error);
        });
}

function renderizarTabla(productos) {
    const tbody = document.getElementById('tabla-productos');

    if (productos.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="6" style="text-align:center; color:#aaa;">
                    No hay productos registrados
                </td>
            </tr>`;
        return;
    }

    tbody.innerHTML = productos.map(function(p) {
        return `
            <tr>
                <td>${p.id}</td>
                <td>${p.nombre} </td>
                <td>$${p.precio.toFixed(2)}</td>
                <td>${p.stock}</td>
                <td>${p.descuento || 0}</td>
                <td>${p.categoria ? p.categoria.nombre : '-'}</td>
                <td>
                    <button class="btn-editar"
                        onclick="abrirModalEditar(${p.id})">U</button>
                    <button class="btn-eliminar"
                        onclick="abrirModalEliminar(${p.id})">D</button>
                </td>
            </tr>`;
    }).join('');
}

// ===== CARGAR CATEGORÍAS PARA EL SELECT =====
function cargarCategorias() {
    fetch('/api/categorias')
        .then(function(response) { return response.json(); })
        .then(function(data) {
            if (!data.exito) return;
            const select = document.getElementById('prod-categoria');
            select.innerHTML = data.categorias.map(function(c) {
                return `<option value="${c.id}">${c.nombre}</option>`;
            }).join('');
        })
        .catch(function(error) {
            console.error('Error al cargar categorías:', error);
        });
}

// ===== MODAL NUEVO =====
function abrirModalNuevo() {
    productoIdActual = null;
    document.getElementById('modal-titulo').textContent = 'Nuevo producto';
    limpiarModalProducto();
    document.getElementById('modal-producto').classList.remove('oculto');
}

// ===== MODAL EDITAR =====
function abrirModalEditar(id) {
    productoIdActual = id;
    document.getElementById('modal-titulo').textContent = 'Actualizar producto';
    limpiarModalProducto();

    // Cargar datos del producto seleccionado via AJAX
    fetch(`/api/productos/${id}`)
        .then(function(response) { return response.json(); })
        .then(function(data) {
            if (!data.exito) {
                mostrarErrorModal('No se pudo cargar el producto');
                return;
            }
            const p = data.producto;
            document.getElementById('prod-nombre').value       = p.nombre;
            document.getElementById('prod-precio').value       = p.precio;
            document.getElementById('prod-stock').value        = p.stock;
            document.getElementById('prod-descuento').value    = p.descuento;
            document.getElementById('prod-observaciones').value = p.observaciones || '';

            // Seleccionar la categoría correcta en el select
            if (p.categoria) {
                document.getElementById('prod-categoria').value = p.categoria.id;
            }

            document.getElementById('modal-producto').classList.remove('oculto');
        })
        .catch(function(error) {
            console.error('Error al cargar producto:', error);
        });
}

function cerrarModalProducto() {
    document.getElementById('modal-producto').classList.add('oculto');
    limpiarModalProducto();
}

function limpiarModalProducto() {
    document.getElementById('prod-nombre').value        = '';
    document.getElementById('prod-precio').value        = '';
    document.getElementById('prod-stock').value         = '';
    document.getElementById('prod-descuento').value     = '';
    document.getElementById('prod-observaciones').value = '';
    document.getElementById('modal-error').classList.add('oculto');

    // Quitar estilos de error
    ['prod-nombre','prod-precio','prod-stock'].forEach(function(id) {
        document.getElementById(id).classList.remove('error');
    });
}

// ===== GUARDAR (CREAR O ACTUALIZAR) =====
function guardarProducto() {
    const nombre        = document.getElementById('prod-nombre').value.trim();
    const precio        = document.getElementById('prod-precio').value.trim();
    const stock         = document.getElementById('prod-stock').value.trim();
    const descuento     = document.getElementById('prod-descuento').value.trim();
    const observaciones = document.getElementById('prod-observaciones').value.trim();
    const categoriaId   = document.getElementById('prod-categoria').value;

    // Validación de campos vacíos
    let valido = true;

    if (nombre === '') {
        document.getElementById('prod-nombre').classList.add('error');
        valido = false;
    }
    if (precio === '') {
        document.getElementById('prod-precio').classList.add('error');
        valido = false;
    }
    if (stock === '') {
        document.getElementById('prod-stock').classList.add('error');
        valido = false;
    }

    if (!valido) {
        mostrarErrorModal('Por favor completa todos los campos obligatorios');
        return;
    }

    const params = new URLSearchParams();
    params.append('nombre', nombre);
    params.append('precio', precio);
    params.append('stock', stock);
    params.append('descuento', descuento);
    params.append('observaciones', observaciones);
    params.append('categoriaId', categoriaId);

    // Si productoIdActual tiene valor → actualizar, si no → crear
    const url    = productoIdActual ? `/api/productos/${productoIdActual}` : '/api/productos';
    const metodo = productoIdActual ? 'PUT' : 'POST';

    fetch(url, {
        method: metodo,
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: params
    })
        .then(function(response) { return response.json(); })
        .then(function(data) {
            if (data.exito) {
                cerrarModalProducto();
                cargarProductos(); // Refresca la tabla sin recargar la página
            } else {
                mostrarErrorModal(data.mensaje);
            }
        })
        .catch(function(error) {
            console.error('Error al guardar producto:', error);
            mostrarErrorModal('Error de conexión');
        });
}

// ===== MODAL ELIMINAR =====
function abrirModalEliminar(id) {
    productoIdActual = id;
    document.getElementById('eliminar-id-confirmacion').value = '';
    document.getElementById('eliminar-error').classList.add('oculto');
    document.getElementById('modal-eliminar').classList.remove('oculto');
}

function cerrarModalEliminar() {
    document.getElementById('modal-eliminar').classList.add('oculto');
    productoIdActual = null;
}

function confirmarEliminar() {
    const idConfirmado = document.getElementById('eliminar-id-confirmacion').value.trim();
    const errorEl      = document.getElementById('eliminar-error');

    errorEl.classList.add('oculto');

    // Validar que ingresó el ID correcto
    if (idConfirmado === '') {
        errorEl.textContent = 'Por favor ingresa el ID del producto';
        errorEl.classList.remove('oculto');
        return;
    }

    if (parseInt(idConfirmado) !== productoIdActual) {
        errorEl.textContent = 'El ID ingresado no coincide';
        errorEl.classList.remove('oculto');
        return;
    }

    fetch(`/api/productos/${productoIdActual}`, {
        method: 'DELETE'
    })
        .then(function(response) { return response.json(); })
        .then(function(data) {
            if (data.exito) {
                cerrarModalEliminar();
                cargarProductos(); // Refresca la tabla sin recargar la página
            } else {
                errorEl.textContent = data.mensaje;
                errorEl.classList.remove('oculto');
            }
        })
        .catch(function(error) {
            console.error('Error al eliminar:', error);
            errorEl.textContent = 'Error de conexión';
            errorEl.classList.remove('oculto');
        });
}

// ===== HELPERS =====
function mostrarErrorModal(mensaje) {
    const errorEl = document.getElementById('modal-error');
    errorEl.textContent = mensaje;
    errorEl.classList.remove('oculto');
}