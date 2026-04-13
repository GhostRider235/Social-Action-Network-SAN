
document.addEventListener('DOMContentLoaded', function () {
    const container = document.getElementById('volunteer-summary');
    const filterPendingButton = document.getElementById('filter-pending');
    const clearAllButton = document.getElementById('clear-all');

    let volunteerList = JSON.parse(localStorage.getItem('volunteerList') || '[]');

    // Función para renderizar todas las solicitudes
    const renderSolicitudes = (list = volunteerList) => {
        container.innerHTML = ''; // Limpiar antes de volver a pintar
        if (list.length === 0) {
            container.innerHTML = '<p>No hay solicitudes disponibles.</p>';
            return;
        }

        list.forEach((vol, index) => {
            const card = document.createElement('div');
            card.style.border = '1px solid #ccc';
            card.style.padding = '1rem';
            card.style.marginBottom = '1rem';
            card.innerHTML = `
                <h3>${vol.name}</h3>
                <p><strong>Correo:</strong> ${vol.email}</p>
                <p><strong>Teléfono:</strong> ${vol.phone}</p>
                <p><strong>Disponibilidad:</strong> ${vol.availability}</p>
                <p><strong>Intereses:</strong> ${vol.interests}</p>
                <p><strong>Estado:</strong> ${vol.status}</p>
                ${vol.status === 'pendiente' ? `
                    <button onclick="cambiarEstado(${index}, 'aprobado')">Aprobar</button>
                    <button onclick="cambiarEstado(${index}, 'rechazado')">Rechazar</button>
                    <button onclick="eliminarSolicitud(${index})">Eliminar</button>
                ` : `<p style="color: ${vol.status === 'aprobado' ? 'green' : 'red'}"><strong>Solicitud ${vol.status}</strong></p>`}
            `;
            container.appendChild(card);
        });
    };

    // Función para cambiar el estado (aprobar o rechazar)
    window.cambiarEstado = (index, nuevoEstado) => {
        volunteerList[index].status = nuevoEstado;
        localStorage.setItem('volunteerList', JSON.stringify(volunteerList));
        renderSolicitudes();
    };

    // Función para eliminar una solicitud
    window.eliminarSolicitud = (index) => {
        volunteerList.splice(index, 1); // Eliminar la solicitud de la lista
        localStorage.setItem('volunteerList', JSON.stringify(volunteerList));
        renderSolicitudes();
    };

    // Función para filtrar solicitudes pendientes
    filterPendingButton.addEventListener('click', () => {
        const pendientes = volunteerList.filter(vol => vol.status === 'pendiente');
        renderSolicitudes(pendientes); // Renderizar solo las solicitudes pendientes
    });

    // Función para eliminar todas las solicitudes
    clearAllButton.addEventListener('click', () => {
        volunteerList = [];
        localStorage.setItem('volunteerList', JSON.stringify(volunteerList));
        renderSolicitudes();
    });

    // Renderizar las solicitudes al cargar
    renderSolicitudes();
});