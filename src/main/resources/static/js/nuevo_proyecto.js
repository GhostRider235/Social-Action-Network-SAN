$(document).ready(function () {
    const apiUrl = "/proyectos";

    // ==========================
    // Cargar proyectos
    // ==========================
    function cargarProyectos() {
        $.get(apiUrl, function (data) {
            let tbody = $("#proyectos-table tbody");
            tbody.empty();

            if (data.length === 0) {
                $("#no-proyectos-message").show();
            } else {
                $("#no-proyectos-message").hide();
                data.forEach(proyecto => {
                    tbody.append(`
                        <tr>
                            <td>${proyecto.id}</td>
                            <td>${proyecto.nombre}</td>
                            <td>${proyecto.descripcion}</td>
                            <td>${proyecto.fechaInicio ? proyecto.fechaInicio.split("T")[0] : ""}</td>
                            <td>${proyecto.fechaFin ? proyecto.fechaFin.split("T")[0] : ""}</td>
                            <td>${proyecto.recibeDonaciones ? "Sí" : "No"}</td>
                            <td>
                                <button class="edit-btn" data-id="${proyecto.id}">Editar</button>
                                <button class="delete-btn" data-id="${proyecto.id}">Eliminar</button>
                            </td>
                        </tr>
                    `);
                });
            }
        });
    }

    cargarProyectos();

    // ==========================
    // Abrir modal crear
    // ==========================
    $("#create-btn").click(function () {
        $("#createModal").show();
    });

    // ==========================
    // Cerrar modales
    // ==========================
    $(".close-btn, .close-modal").click(function () {
        $(".modal").hide();
    });

    // ==========================
    // Crear proyecto
    // ==========================
    $("#createForm").submit(function (e) {
        e.preventDefault();
        let formData = new FormData(this);

        let proyecto = {
            nombre: formData.get("nombre"),
            descripcion: formData.get("descripcion"),
            fechaInicio: formData.get("fechaInicio"),
            fechaFin: formData.get("fechaFin"),
            recibeDonaciones: formData.get("recibeDonaciones") !== null
        };

        $.ajax({
            url: apiUrl,
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify(proyecto),
            success: function () {
                $("#createModal").hide();
                cargarProyectos();
            }
        });
    });

    // ==========================
    // Editar proyecto
    // ==========================
    $(document).on("click", ".edit-btn", function () {
        let id = $(this).data("id");

        $.get(apiUrl + "/" + id, function (proyecto) {
            let form = $("#editForm");
            form.find("[name=id]").val(proyecto.id);
            form.find("[name=nombre]").val(proyecto.nombre);
            form.find("[name=descripcion]").val(proyecto.descripcion);
            form.find("[name=fechaInicio]").val(proyecto.fechaInicio.split("T")[0]);
            form.find("[name=fechaFin]").val(proyecto.fechaFin.split("T")[0]);
            form.find("[name=recibeDonaciones]").prop("checked", proyecto.recibeDonaciones);

            $("#editModal").show();
        });
    });

    $("#editForm").submit(function (e) {
        e.preventDefault();
        let formData = new FormData(this);
        let id = formData.get("id");

        let proyecto = {
            id: id,
            nombre: formData.get("nombre"),
            descripcion: formData.get("descripcion"),
            fechaInicio: formData.get("fechaInicio"),
            fechaFin: formData.get("fechaFin"),
            recibeDonaciones: formData.get("recibeDonaciones") !== null
        };

        $.ajax({
            url: apiUrl + "/" + id,
            type: "PUT",
            contentType: "application/json",
            data: JSON.stringify(proyecto),
            success: function () {
                $("#editModal").hide();
                cargarProyectos();
            }
        });
    });

    // ==========================
    // Eliminar proyecto
    // ==========================
    $(document).on("click", ".delete-btn", function () {
        let id = $(this).data("id");
        $("#deleteId").val(id);
        $("#deleteModal").show();
    });

    $("#confirmDelete").click(function () {
        let id = $("#deleteId").val();

        $.ajax({
            url: apiUrl + "/" + id,
            type: "DELETE",
            success: function () {
                $("#deleteModal").hide();
                cargarProyectos();
            }
        });
    });
});
