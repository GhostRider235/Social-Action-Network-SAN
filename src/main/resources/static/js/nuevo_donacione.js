
    document.addEventListener('DOMContentLoaded', function () {
        const API_URL = 'http://localhost:8080';

        // Load donations
        function loadDonaciones() {
            fetch(`${API_URL}/donaciones`)
                .then(response => response.json())
                .then(donaciones => {
                    const tableBody = document.querySelector('#donaciones-table tbody');
                    const noDonacionesMessage = document.getElementById('no-donaciones-message');

                    tableBody.innerHTML = '';

                    if (donaciones.length === 0) {
                        noDonacionesMessage.style.display = 'block';
                    } else {
                        noDonacionesMessage.style.display = 'none';
                        donaciones.forEach(donacion => {
                            const row = `
                                    <tr>
                                        <td>${donacion.id}</td>
                                        <td>${donacion.monto}</td>
                                        <td>${donacion.fecha}</td>
                                        <td>${donacion.descripcion}</td>
                                        <td>${donacion.usuario.nombre}</td>
                                        <td>${donacion.fundacion.nombre}</td>
                                        <td class="action-btns">
                                            <button class="btn btn-warning edit-btn" data-id="${donacion.id}">
                                                <i class='bx bx-edit-alt'></i> Editar
                                            </button>
                                            <button class="btn btn-danger delete-btn" data-id="${donacion.id}">
                                                <i class='bx bx-trash'></i> Eliminar
                                            </button>
                                        </td>
                                    </tr>
                                `;
                            tableBody.insertAdjacentHTML('beforeend', row);
                        });
                    }
                })
                .catch(error => {
                    console.error('Error al cargar las donaciones:', error);
                    alert('Error al cargar las donaciones.');
                });
        }

        // Modal functions
        function openModal(modalId) {
            document.getElementById(modalId).style.display = 'block';
        }

        function closeModal(modalId) {
            document.getElementById(modalId).style.display = 'none';
        }

        // Create donation
        document.getElementById('create-btn').addEventListener('click', () => {
            document.getElementById('createForm').reset();
            openModal('createModal');
        });

        document.getElementById('createForm').addEventListener('submit', function (e) {
            e.preventDefault();
            const formData = {
                monto: parseFloat(this.monto.value),
                fecha: this.fecha.value,
                descripcion: this.descripcion.value,
                usuarioId: parseInt(this.usuarioId.value),
                fundacionId: parseInt(this.fundacionId.value)
            };

            fetch(`${API_URL}/donaciones`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(formData),
            })
                .then(response => response.json())
                .then(() => {
                    closeModal('createModal');
                    loadDonaciones();
                    alert('Donación añadida exitosamente!');
                })
                .catch(error => {
                    console.error('Error al añadir la donación:', error);
                    alert('Error al añadir la donación.');
                });
        });

        // Edit donation
        document.addEventListener('click', function (e) {
            if (e.target.classList.contains('edit-btn')) {
                const id = e.target.dataset.id;
                fetch(`${API_URL}/donaciones/26`)
                    .then(response => response.json())
                    .then(donacion => {
                        const form = document.getElementById('editForm');
                        form.id.value = donacion.id;
                        form.monto.value = donacion.monto;
                        form.fecha.value = donacion.fecha;
                        form.descripcion.value = donacion.descripcion;
                        form.usuarioId.value = donacion.usuario.id;
                        form.fundacionId.value = donacion.fundacion.id;
                        openModal('editModal');
                    })
                    .catch(error => {
                        console.error('Error al cargar los datos de la donación:', error);
                        alert('Error al cargar los datos de la donación.');
                    });
            }
        });

        document.getElementById('editForm').addEventListener('submit', function (e) {
            e.preventDefault();
            const id = this.id.value;
            const formData = {
                id: parseInt(id),
                monto: parseFloat(this.monto.value),
                fecha: this.fecha.value,
                descripcion: this.descripcion.value,
                usuarioId: parseInt(this.usuarioId.value),
                fundacionId: parseInt(this.fundacionId.value)
            };

            fetch(`${API_URL}/donaciones/${id}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(formData),
            })
                .then(response => response.json())
                .then(() => {
                    closeModal('editModal');
                    loadDonaciones();
                    alert('Donación actualizada exitosamente!');
                })
                .catch(error => {
                    console.error('Error al actualizar la donación:', error);
                    alert('Error al actualizar la donación.');
                });
        });

        // Delete donation
        document.addEventListener('click', function (e) {
            if (e.target.classList.contains('delete-btn')) {
                const id = e.target.dataset.id;
                document.getElementById('deleteId').value = id;
                openModal('deleteModal');
            }
        });

        document.getElementById('confirmDelete').addEventListener('click', function () {
            const id = document.getElementById('deleteId').value;
            fetch(`${API_URL}/donaciones/${id}`, {
                method: 'DELETE',
            })
                .then(() => {
                    closeModal('deleteModal');
                    loadDonaciones();
                    alert('Donación eliminada exitosamente!');
                })
                .catch(error => {
                    console.error('Error al eliminar la donación:', error);
                    alert('Error al eliminar la donación.');
                });
        });

        // Close modal events
        document.querySelectorAll('.close-btn, .close-modal').forEach(element => {
            element.addEventListener('click', function () {
                const modalId = this.closest('.modal').id;
                closeModal(modalId);
            });
        });

        // Close modal when clicking outside
        document.querySelectorAll('.modal').forEach(modal => {
            modal.addEventListener('click', function (e) {
                if (e.target === this) {
                    closeModal(this.id);
                }
            });
        });

        // Initial load
        loadDonaciones();
    });

    function logout() {
        localStorage.removeItem('usuario_auth');
        window.location.href = '/login';
        localStorage.removeItem('id');
    }