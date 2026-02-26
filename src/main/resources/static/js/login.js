// Permite hacer login presionando Enter
document.addEventListener('DOMContentLoaded', function () {
    document.getElementById('password').addEventListener('keypress', function (e) {
        if (e.key === 'Enter') login();
    });
    document.getElementById('username').addEventListener('keypress', function (e) {
        if (e.key === 'Enter') login();
    });
});

function login() {
    const username = document.getElementById('username').value.trim();
    const password = document.getElementById('password').value.trim();
    const btnIngresar = document.getElementById('btn-ingresar');
    const mensajeError = document.getElementById('mensaje-error');

    // Ocultar mensaje previo
    mensajeError.classList.add('oculto');

    // Validación de campos vacíos en el frontend
    if (username === '' || password === '') {
        mostrarError('Por favor ingresa usuario y contraseña');
        return;
    }

    // Deshabilitar botón mientras se procesa
    btnIngresar.disabled = true;
    btnIngresar.textContent = 'Verificando...';

    // Construir los parámetros del formulario
    const params = new URLSearchParams();
    params.append('username', username);
    params.append('password', password);

    // AJAX con fetch — sin recargar la página
    fetch('/api/auth/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: params
    })
        .then(function(response) {
            return response.json();
        })
        .then(function(data) {
            if (data.exito) {
                // Login exitoso → redirigir al dashboard
                window.location.href = 'dashboard.html';
            } else {
                mostrarError(data.mensaje);
            }
        })
        .catch(function(error) {
            mostrarError('Error de conexión. Intenta de nuevo.');
        })
        .finally(function() {
            btnIngresar.disabled = false;
            btnIngresar.textContent = 'Ingresar';
        });
}

function mostrarError(mensaje) {
    const mensajeError = document.getElementById('mensaje-error');
    mensajeError.textContent = mensaje;
    mensajeError.classList.remove('oculto');
}
