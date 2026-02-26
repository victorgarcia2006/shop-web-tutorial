// Verifica sesión y ejecuta un callback si está activa
function verificarSesion(callback) {
    fetch('/api/auth/sesion')
        .then(function(response) {
            return response.json();
        })
        .then(function(data) {
            if (!data.activa) {
                window.location.href = 'index.html';
                return;
            }
            // Mostrar nombre de usuario en navbar
            const nombreEl = document.getElementById('nombre-usuario');
            if (nombreEl) nombreEl.textContent = data.usuario;

            // Ejecutar lo que cada página necesite
            if (callback) callback(data);
        })
        .catch(function() {
            window.location.href = 'index.html';
        });
}

// Logout compartido
function logout() {
    fetch('/api/auth/logout', { method: 'POST' })
        .then(function(response) { return response.json(); })
        .then(function(data) {
            if (data.exito) window.location.href = 'index.html';
        })
        .catch(function(error) {
            console.error('Error al cerrar sesión:', error);
        });
}