document.addEventListener('DOMContentLoaded', function() {
    const form = document.querySelector('.formulario'); // tu formulario
    const nombreInput = form.querySelector('[name="nombre"]');
    const emailInput = form.querySelector('[name="correo"], [name="email"]'); // según tu formulario
    const contrasenaInput = form.querySelector('[name="contrasena"]');

    function showError(input, message) {
        const errorElement = input.nextElementSibling; // asume que hay un span para errores
        if (errorElement) {
            errorElement.textContent = message;
            errorElement.style.display = 'block';
        }
    }

    function hideError(input) {
        const errorElement = input.nextElementSibling;
        if (errorElement) {
            errorElement.style.display = 'none';
        }
    }

    function validateForm() {
        let isValid = true;

        if (nombreInput.value.length < 3) {
            showError(nombreInput, "El nombre debe tener al menos 3 caracteres");
            isValid = false;
        } else {
            hideError(nombreInput);
        }

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
        if (!validateForm()) {
            e.preventDefault(); // previene enviar si hay errores
        }
        // si todo es válido, el formulario se envía normalmente al backend
    });
});
