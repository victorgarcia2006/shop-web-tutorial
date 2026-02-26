// ID de la categoría que se está editando o eliminando
let categoriaIdActual = null;

// ===== INICIALIZACIÓN =====
document.addEventListener('DOMContentLoaded', function () {
    verificarSesion(function () {
        cargarCategorias();
    });
});

// ===== CARGAR CATEGORÍAS =====
function cargarCategorias() {
    fetch('/api/categorias')
        .then(function(response) { return response.json(); })
        .then(function(data) {
            if (!data.exito) {
                window.location.href = 'index.html';
                return;
            }
            renderizarTabla(data.categorias);
        })
        .catch(function(error) {
            console.error('Error al cargar categorías:', error);
        });
}

function renderizarTabla(categorias) {
    const tbody = document.getElementById('tabla-categorias');

    if (categorias.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="3" style="text-align:center; color:#aaa;">
                    No hay categorías registradas
                </td>
            </tr>`;
        return;
    }

    tbody.innerHTML = categorias.map(function(c) {
        return `
            <tr>
                <td>${c.id}</td>
                <td>${c.nombre}</td>
                <td>
                    <button class="btn-editar"
                        onclick="abrirModalEditar(${c.id}, '${c.nombre}')">U</button>
                    <button class="btn-eliminar"
                        onclick="abrirModalEliminar(${c.id})">D</button>
                </td>
            </tr>`;
    }).join('');
}

// ===== MODAL NUEVO =====
function abrirModalNuevo() {
    categoriaIdActual = null;
    document.getElementById('modal-titulo').textContent = 'Nueva categoría';
    limpiarModal();
    document.getElementById('modal-categoria').classList.remove('oculto');
}

// ===== MODAL EDITAR =====
// Aquí no necesitamos hacer fetch porque ya tenemos el nombre en la tabla
function abrirModalEditar(id, nombre) {
    categoriaIdActual = id;
    document.getElementById('modal-titulo').textContent = 'Actualizar categoría';
    limpiarModal();
    document.getElementById('cat-nombre').value = nombre;
    document.getElementById('modal-categoria').classList.remove('oculto');
}

function cerrarModalCategoria() {
    document.getElementById('modal-categoria').classList.add('oculto');
    limpiarModal();
}

function limpiarModal() {
    document.getElementById('cat-nombre').value = '';
    document.getElementById('cat-nombre').classList.remove('error');
    document.getElementById('modal-error').classList.add('oculto');
}

// ===== GUARDAR (CREAR O ACTUALIZAR) =====
function guardarCategoria() {
    const nombre  = document.getElementById('cat-nombre').value.trim();
    const errorEl = document.getElementById('modal-error');

    errorEl.classList.add('oculto');
    document.getElementById('cat-nombre').classList.remove('error');

    // Validación
    if (nombre === '') {
        document.getElementById('cat-nombre').classList.add('error');
        errorEl.textContent = 'El nombre no puede estar vacío';
        errorEl.classList.remove('oculto');
        return;
    }

    const params = new URLSearchParams();
    params.append('nombre', nombre);

    // Si categoriaIdActual tiene valor → actualizar, si no → crear
    const url    = categoriaIdActual ? `/api/categorias/${categoriaIdActual}` : '/api/categorias';
    const metodo = categoriaIdActual ? 'PUT' : 'POST';

    fetch(url, {
        method: metodo,
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: params
    })
        .then(function(response) { return response.json(); })
        .then(function(data) {
            if (data.exito) {
                cerrarModalCategoria();
                cargarCategorias(); // Refresca la tabla sin recargar la página
            } else {
                errorEl.textContent = data.mensaje;
                errorEl.classList.remove('oculto');
            }
        })
        .catch(function(error) {
            console.error('Error al guardar categoría:', error);
            errorEl.textContent = 'Error de conexión';
            errorEl.classList.remove('oculto');
        });
}

// ===== MODAL ELIMINAR =====
function abrirModalEliminar(id) {
    categoriaIdActual = id;
    document.getElementById('eliminar-id-confirmacion').value = '';
    document.getElementById('eliminar-error').classList.add('oculto');
    document.getElementById('modal-eliminar').classList.remove('oculto');
}

function cerrarModalEliminar() {
    document.getElementById('modal-eliminar').classList.add('oculto');
    categoriaIdActual = null;
}

function confirmarEliminar() {
    const idConfirmado = document.getElementById('eliminar-id-confirmacion').value.trim();
    const errorEl      = document.getElementById('eliminar-error');

    errorEl.classList.add('oculto');

    // Validar que ingresó algo
    if (idConfirmado === '') {
        errorEl.textContent = 'Por favor ingresa el ID de la categoría';
        errorEl.classList.remove('oculto');
        return;
    }

    // Validar que el ID coincide
    if (parseInt(idConfirmado) !== categoriaIdActual) {
        errorEl.textContent = 'El ID ingresado no coincide';
        errorEl.classList.remove('oculto');
        return;
    }

    fetch(`/api/categorias/${categoriaIdActual}`, {
        method: 'DELETE'
    })
        .then(function(response) { return response.json(); })
        .then(function(data) {
            if (data.exito) {
                cerrarModalEliminar();
                cargarCategorias(); // Refresca la tabla sin recargar la página
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