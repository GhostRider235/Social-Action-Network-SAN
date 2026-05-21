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
    // VALIDACIONES VISUALES
    // ===========================
    const emailInput = document.getElementById('email');
    const contrasenaInput = document.getElementById('contrasena');

    function showError(input, message) {

        const errorElement =
            document.getElementById(`${input.id}-error`);

        errorElement.textContent = message;
        errorElement.style.display = 'block';
    }

    function hideError(input) {

        const errorElement =
            document.getElementById(`${input.id}-error`);

        errorElement.style.display = 'none';
    }

    // Validación en tiempo real
    emailInput.addEventListener("input", () => {

        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

        if (!emailRegex.test(emailInput.value)) {
            showError(emailInput, "Correo inválido");
        } else {
            hideError(emailInput);
        }
    });

    contrasenaInput.addEventListener("input", () => {

        if (contrasenaInput.value.length < 6) {
            showError(contrasenaInput,
                "Mínimo 6 caracteres");
        } else {
            hideError(contrasenaInput);
        }
    });

});