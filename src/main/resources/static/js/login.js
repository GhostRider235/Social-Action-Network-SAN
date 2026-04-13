document.addEventListener('DOMContentLoaded', function () {

    // ===========================
    // CAMBIO ENTRE USUARIO Y FUNDACIÓN
    // ===========================
    const container = document.querySelector(".container");
    const switchBtn = document.getElementById("switchBtn");
    const infoText = document.getElementById("infoText");
    const userForm = document.getElementById("loginUsuarioForm");
    const foundationForm = document.getElementById("loginFundacionForm");

    let modoFundacion = false;

    switchBtn.addEventListener("click", () => {
        modoFundacion = !modoFundacion;
        container.classList.toggle("fundacion-mode");

        if (modoFundacion) {
            infoText.textContent = "¿Deseas ingresar como usuario?";
            switchBtn.textContent = "Iniciar como Usuario";
            userForm.classList.remove("active");
            foundationForm.classList.add("active");
        } else {
            infoText.textContent = "¿Deseas ingresar como fundación?";
            switchBtn.textContent = "Iniciar como Fundación";
            foundationForm.classList.remove("active");
            userForm.classList.add("active");
        }
    });

    // ===========================
    // VALIDACIÓN Y LOGIN USUARIO
    // ===========================
    const form = userForm; // tu formulario de usuario
    const emailInput = document.getElementById('email');
    const contrasenaInput = document.getElementById('contrasena');

    function showError(input, message) {
        const errorElement = document.getElementById(`${input.id}-error`);
        errorElement.textContent = message;
        errorElement.style.display = 'block';
    }

    function hideError(input) {
        const errorElement = document.getElementById(`${input.id}-error`);
        errorElement.style.display = 'none';
    }

    function validateForm() {
        let isValid = true;

        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(emailInput.value)) {
            showError(emailInput, "Por favor ingrese un email válido");
            isValid = false;
        } else {
            hideError(emailInput);
        }

        if (contrasenaInput.value.length < 6) {
            showError(contrasenaInput, "La contraseña debe tener al menos 6 caracteres");
            isValid = false;
        } else {
            hideError(contrasenaInput);
        }

        return isValid;
    }

    form.addEventListener('submit', function(e) {
        e.preventDefault();

        if (!validateForm()) return;

        const formData = {
            email: emailInput.value,
            contrasena: contrasenaInput.value
        };

        fetch('http://localhost:8080/usuarios/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(formData),
        })
        .then(response => {
            if (!response.ok) throw new Error('Usuario no encontrado');
            return response.json();
        })
        .then(data => {
            localStorage.setItem("usuario_auth", "claro_que_yes");
            localStorage.setItem("id", data.id);

            if (data.tipo === 'admin') window.location.href = "/admin_dashboard";
            else if (data.tipo === 'user') window.location.href = "/user-dashboard";
            else window.location.href = "/";
        })
        .catch(error => {
            alert(error.message);
        });
    });

});
