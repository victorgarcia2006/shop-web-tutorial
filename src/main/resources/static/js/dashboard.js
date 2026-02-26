// Al cargar la página verificamos sesión y cargamos estadísticas
document.addEventListener('DOMContentLoaded', function () {
    verificarSesionYCargar();
});

function verificarSesionYCargar() {
    // Primero verificamos si hay sesión activa
    fetch('/api/auth/sesion')
        .then(function(response) {
            return response.json();
        })
        .then(function(data) {
            if (!data.activa) {
                // No hay sesión → redirigir al login
                window.location.href = 'index.html';
                return;
            }

            // Hay sesión → mostrar nombre de usuario y cargar estadísticas
            document.getElementById('nombre-usuario').textContent = data.usuario;
            cargarEstadisticas();
        })
        .catch(function(error) {
            console.error('Error al verificar sesión:', error);
            window.location.href = 'index.html';
        });
}

function cargarEstadisticas() {
    fetch('/api/dashboard/estadisticas')
        .then(function(response) {
            return response.json();
        })
        .then(function(data) {
            if (!data.exito) {
                window.location.href = 'index.html';
                return;
            }
            renderizarCards(data);
        })
        .catch(function(error) {
            console.error('Error al cargar estadísticas:', error);
        });
}

function renderizarCards(data) {
    const contenedor = document.getElementById('cards-estadisticas');

    contenedor.innerHTML = `
        <div class="card">
            <span class="icono">
                <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon icon-tabler icons-tabler-outline icon-tabler-shopping-cart"><path stroke="none" d="M0 0h24v24H0z" fill="none"/><path d="M4 19a2 2 0 1 0 4 0a2 2 0 1 0 -4 0" /><path d="M15 19a2 2 0 1 0 4 0a2 2 0 1 0 -4 0" /><path d="M17 17h-11v-14h-2" /><path d="M6 5l14 1l-1 7h-13" /></svg>
            </span>
            <span class="numero">${data.totalProductos}</span>
            <span class="etiqueta">Productos</span>
        </div>
        <div class="card">
            <span class="icono">
                <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon icon-tabler icons-tabler-outline icon-tabler-package"><path stroke="none" d="M0 0h24v24H0z" fill="none"/><path d="M12 3l8 4.5l0 9l-8 4.5l-8 -4.5l0 -9l8 -4.5" /><path d="M12 12l8 -4.5" /><path d="M12 12l0 9" /><path d="M12 12l-8 -4.5" /><path d="M16 5.25l-8 4.5" /></svg>
            </span>
            <span class="numero">${data.totalCategorias}</span>
            <span class="etiqueta">Categorías</span>
        </div>
    `;
}

function logout() {
    fetch('/api/auth/logout', {
        method: 'POST'
    })
        .then(function(response) {
            return response.json();
        })
        .then(function(data) {
            if (data.exito) {
                window.location.href = 'index.html';
            }
        })
        .catch(function(error) {
            console.error('Error al cerrar sesión:', error);
        });
}