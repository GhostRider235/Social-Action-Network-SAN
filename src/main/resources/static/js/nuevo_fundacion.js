
    document.addEventListener('DOMContentLoaded', function () {
        const API_URL = 'http://localhost:8080';

        // Load foundations
        function loadFundaciones() {
            fetch(`${API_URL}/fundaciones`)
                .then(response => response.json())
                .then(fundaciones => {
                    const tableBody = document.querySelector('#fundaciones-table tbody');
                    const noFundacionesMessage = document.getElementById('no-fundaciones-message');

                    tableBody.innerHTML = '';

                    if (fundaciones.length === 0) {
                        noFundacionesMessage.style.display = 'block';
                    } else {
                        noFundacionesMessage.style.display = 'none';
                        fundaciones.forEach(fundacion => {
                            const row = `
                                    <tr>
                                        <td>${fundacion.id}</td>
                                        <td>${fundacion.nombre}</td>
                                        <td>${fundacion.descripcion}</td>
                                        <td>${fundacion.contacto}</td>
                                        <td>${fundacion.ubicacion}</td>
                                        <td class="action-btns">
                                            <button class="btn btn-warning edit-btn" data-id="${fundacion.id}">
                                                <i class='bx bx-edit-alt'></i> Editar
                                            </button>
                                            <button class="btn btn-danger delete-btn" data-id="${fundacion.id}">
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
                    console.error('Error al cargar las fundaciones:', error);
                    alert('Error al cargar las fundaciones.');
                });
        }

        // Modal functions
        function openModal(modalId) {
            document.getElementById(modalId).style.display = 'block';
        }

        function closeModal(modalId) {
            document.getElementById(modalId).style.display = 'none';
        }

        // Create foundation
        document.getElementById('create-btn').addEventListener('click', () => {
            document.getElementById('createForm').reset();
            openModal('createModal');
        });

        document.getElementById('createForm').addEventListener('submit', function (e) {
            e.preventDefault();
            const formData = {
                nombre: this.nombre.value,
                descripcion: this.descripcion.value,
                contacto: this.contacto.value,
                ubicacion: this.ubicacion.value
            };

            fetch(`${API_URL}/fundaciones`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(formData),
            })
                .then(response => response.json())
                .then(() => {
                    closeModal('createModal');
                    loadFundaciones();
                    alert('Fundación creada exitosamente!');
                })
                .catch(error => {
                    console.error('Error al crear la fundación:', error);
                    alert('Error al crear la fundación.');
                });
        });

        // Edit foundation
        document.addEventListener('click', function (e) {
            if (e.target.classList.contains('edit-btn')) {
                const id = e.target.dataset.id;
                fetch(`${API_URL}/fundaciones/${id}`)
                    .then(response => response.json())
                    .then(fundacion => {
                        const form = document.getElementById('editForm');
                        form.id.value = fundacion.id;
                        form.nombre.value = fundacion.nombre;
                        form.descripcion.value = fundacion.descripcion;
                        form.contacto.value = fundacion.contacto;
                        form.ubicacion.value = fundacion.ubicacion;
                        openModal('editModal');
                    })
                    .catch(error => {
                        console.error('Error al cargar los datos de la fundación:', error);
                        alert('Error al cargar los datos de la fundación.');
                    });
            }
        });

        document.getElementById('editForm').addEventListener('submit', function (e) {
            e.preventDefault();
            const id = this.id.value;
            const formData = {
                id: id,
                nombre: this.nombre.value,
                descripcion: this.descripcion.value,
                contacto: this.contacto.value,
                ubicacion: this.ubicacion.value
            };

            fetch(`${API_URL}/fundaciones/${id}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(formData),
            })
                .then(response => response.json())
                .then(() => {
                    closeModal('editModal');
                    loadFundaciones();
                    alert('Fundación actualizada exitosamente!');
                })
                .catch(error => {
                    console.error('Error al actualizar la fundación:', error);
                    alert('Error al actualizar la fundación.');
                });
        });

        // Delete foundation
        document.addEventListener('click', function (e) {
            if (e.target.classList.contains('delete-btn')) {
                const id = e.target.dataset.id;
                document.getElementById('deleteId').value = id;
                openModal('deleteModal');
            }
        });

        document.getElementById('confirmDelete').addEventListener('click', function () {
            const id = document.getElementById('deleteId').value;
            fetch(`${API_URL}/fundaciones/${id}`, {
                method: 'DELETE',
            })
                .then(() => {
                    closeModal('deleteModal');
                    loadFundaciones();
                    alert('Fundación eliminada exitosamente!');
                })
                .catch(error => {
                    console.error('Error al eliminar la fundación:', error);
                    alert('Error al eliminar la fundación.');
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
        loadFundaciones();
    });

    function logout() {
        localStorage.removeItem('usuario_auth');
        window.location.href = '/login';
        localStorage.removeItem('id');
    }