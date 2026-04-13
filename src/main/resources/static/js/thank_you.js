
        document.addEventListener('DOMContentLoaded', function() {
            // Recuperar los datos del localStorage
            const volunteerData = JSON.parse(localStorage.getItem('volunteerData'));

            if (volunteerData) {
                const summary = `
                    <p><strong>Nombre:</strong> ${volunteerData.name}</p>
                    <p><strong>Correo Electrónico:</strong> ${volunteerData.email}</p>
                    <p><strong>Teléfono:</strong> ${volunteerData.phone}</p>
                    <p><strong>Disponibilidad:</strong> ${volunteerData.availability}</p>
                    <p><strong>Intereses Específicos:</strong> ${volunteerData.interests}</p>
                `;
                document.getElementById('volunteer-summary').innerHTML = summary;

                // Limpiar los datos después de mostrarlos (opcional)
                localStorage.removeItem('volunteerData');
            } else {
                document.getElementById('volunteer-summary').innerHTML = "<p>No se encontraron datos.</p>";
            }
        });