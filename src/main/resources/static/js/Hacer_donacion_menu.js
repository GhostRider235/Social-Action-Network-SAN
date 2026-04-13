    document.addEventListener('DOMContentLoaded', function () {
        const menuToggle = document.getElementById('menu-toggle');
        const mainNav = document.getElementById('main-nav');
        const loginLink = document.getElementById('login-link');

        menuToggle.addEventListener('click', function () {
            mainNav.classList.toggle('active');
        });

        const usuarioAuth = localStorage.getItem('usuario_auth');

        if (usuarioAuth) {
            loginLink.textContent = 'Cerrar sesión';
        } else {
            loginLink.textContent = 'Login';
            loginLink.href = 'login';
        }

        loginLink.addEventListener('click', function (e) {
            if (loginLink.textContent === "Cerrar sesión") {
                e.preventDefault();
                localStorage.removeItem('usuario_auth');
                localStorage.removeItem('id');
                location.reload();
            }
        });
    });