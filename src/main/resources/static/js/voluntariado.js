
    document.addEventListener('DOMContentLoaded', function() {
    const volunteerForm = document.getElementById('volunteer-form');

    // Crear contenedor para el mensaje de éxito
    const successMsg = document.createElement('p');
    successMsg.id = 'form-message';
    successMsg.style.color = 'green';
    successMsg.style.marginTop = '1rem';
    volunteerForm.appendChild(successMsg);

    volunteerForm.addEventListener('submit', function(event) {
        event.preventDefault();

        const name = document.getElementById('volunteer-name').value.trim();
        const email = document.getElementById('volunteer-email').value.trim();
        const phone = document.getElementById('volunteer-phone').value.trim();
        const availability = document.getElementById('volunteer-availability').value;
        const interests = document.getElementById('volunteer-interests').value.trim();

        if (!name || !email || !phone) {
            alert('Por favor, completa todos los campos obligatorios.');
            return;
        }

        // Obtener lista actual de voluntarios
        const volunteers = JSON.parse(localStorage.getItem('volunteerList') || '[]');

        // Agregar nuevo voluntario
        volunteers.push({
            name,
            email,
            phone,
            availability,
            interests,
            status: 'pendiente'
        });

        localStorage.setItem('volunteerList', JSON.stringify(volunteers));

        // Mostrar mensaje de éxito
        successMsg.textContent = '¡Gracias por enviar tu solicitud!';

        // Limpiar el formulario
        volunteerForm.reset();
    });

});