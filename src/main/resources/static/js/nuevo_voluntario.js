
    document.addEventListener('DOMContentLoaded', function () {
        const API_URL = 'http://localhost:8080';

        // Load volunteers
        function loadVoluntarios() {
            fetch(`${API_URL}/voluntarios`)
                .then(response => response.json())
                .then(voluntarios => {
                    const tableBody = document.querySelector('#voluntarios-table tbody');
                    const noVoluntariosMessage = document.getElementById('no-voluntarios-message');

                    tableBody.innerHTML = '';

                    if (voluntarios.length === 0) {
                        noVoluntariosMessage.style.display = 'block';
                    } else {
                        noVoluntariosMessage.style.display = 'none';
                        voluntarios.forEach(voluntario => {
                            const row = `
                                    <tr>
                                        <td>${voluntario.id}</td>
                                        <td>${voluntario.usuario.nombre}</td>
                                        <td>${voluntario.proyecto.nombre}</td>
                                        <td>${voluntario.fechaAsignacion}</td>
                                        <td class="action-btns">
                                            <button class="btn btn-warning edit-btn" data-id="${voluntario.id}">
                                                <i class='bx bx-edit-alt'></i> Editar
                                            </button>
                                            <button class="btn btn-danger delete-btn" data-id="${voluntario.id}">
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
                    console.error('Error al cargar los voluntarios:', error);
                    alert('Error al cargar los voluntarios.');
                });
        }

        // Load users and projects for select inputs
        function loadUsersAndProjects() {
            Promise.all([
                fetch(`${API_URL}/usuarios`).then(res => res.json()),
                fetch(`${API_URL}/proyectos`).then(res => res.json())
            ]).then(([users, projects]) => {
                const userSelects = document.querySelectorAll('#create-user, #edit-user');
                const projectSelects = document.querySelectorAll('#create-project, #edit-project');

                userSelects.forEach(select => {
                    select.innerHTML = users.map(user => `<option value="${user.id}">${user.nombre}</option>`
                    ).join('');
                });

                projectSelects.forEach(select => {
                    select.innerHTML = projects.map(project =>
                        `<option value="${project.id}">${project.nombre}</option>`
                    ).join('');
                });
            }).catch(error => {
                console.error('Error al cargar usuarios y proyectos:', error);
                alert('Error al cargar datos para el formulario.');
            });
        }

        // Modal functions
        function openModal(modalId) {
            document.getElementById(modalId).style.display = 'block';
        }

        function closeModal(modalId) {
            document.getElementById(modalId).style.display = 'none';
        }

        // Create volunteer
        document.getElementById('create-btn').addEventListener('click', () => {
            document.getElementById('createForm').reset();
            loadUsersAndProjects();
            openModal('createModal');
        });

        document.getElementById('createForm').addEventListener('submit', function (e) {
            e.preventDefault();
            const formData = {
                usuarioId: this.user.value,
                proyectoId: this.project.value,
                fechaAsignacion: this.startDate.value
            };

            fetch(`${API_URL}/voluntarios`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(formData),
            })
                .then(response => response.json())
                .then(() => {
                    closeModal('createModal');
                    loadVoluntarios();
                    alert('Voluntario añadido exitosamente!');
                })
                .catch(error => {
                    console.error('Error al añadir el voluntario:', error);
                    alert('Error al añadir el voluntario.');
                });
        });

        // Edit volunteer
        document.addEventListener('click', function (e) {
            if (e.target.classList.contains('edit-btn')) {
                const id = e.target.dataset.id;
                fetch(`${API_URL}/voluntarios/${id}`)
                    .then(response => response.json())
                    .then(voluntario => {
                        const form = document.getElementById('editForm');
                        form.id.value = voluntario.id;
                        form.user.value = voluntario.usuario.id;
                        form.project.value = voluntario.proyecto.id;
                        form.startDate.value = voluntario.fechaAsignacion;
                        loadUsersAndProjects();
                        openModal('editModal');
                    })
                    .catch(error => {
                        console.error('Error al cargar los datos del voluntario:', error);
                        alert('Error al cargar los datos del voluntario.');
                    });
            }
        });

        document.getElementById('editForm').addEventListener('submit', function (e) {
            e.preventDefault();
            const id = this.id.value;
            const formData = {
                id: id,
                usuarioId: this.user.value,
                proyectoId: this.project.value,
                fechaAsignacion: this.startDate.value
            };

            fetch(`${API_URL}/voluntarios/${id}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(formData),
            })
                .then(response => response.json())
                .then(() => {
                    closeModal('editModal');
                    loadVoluntarios();
                    alert('Voluntario actualizado exitosamente!');
                })
                .catch(error => {
                    console.error('Error al actualizar el voluntario:', error);
                    alert('Error al actualizar el voluntario.');
                });
        });

        // Delete volunteer
        document.addEventListener('click', function (e) {
            if (e.target.classList.contains('delete-btn')) {
                const id = e.target.dataset.id;
                document.getElementById('deleteId').value = id;
                openModal('deleteModal');
            }
        });

        document.getElementById('confirmDelete').addEventListener('click', function () {
            const id = document.getElementById('deleteId').value;
            fetch(`${API_URL}/voluntarios/${id}`, {
                method: 'DELETE',
            })
                .then(() => {
                    closeModal('deleteModal');
                    loadVoluntarios();
                    alert('Voluntario eliminado exitosamente!');
                })
                .catch(error => {
                    console.error('Error al eliminar el voluntario:', error);
                    alert('Error al eliminar el voluntario.');
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
        loadVoluntarios();
    });

    function logout() {
        localStorage.removeItem('usuario_auth');
        window.location.href = '/login';
        localStorage.removeItem('id');
    }