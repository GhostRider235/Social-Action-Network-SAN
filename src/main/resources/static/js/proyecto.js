// Resaltar fila seleccionada y mostrar alerta con nombre del proyecto
document.addEventListener("DOMContentLoaded", () => {
    const rows = document.querySelectorAll("tbody tr");

    rows.forEach(row => {
        row.addEventListener("click", () => {
            const nombre = row.querySelector("td").innerText;
            alert("Has seleccionado el proyecto: " + nombre);
        });
    });
});
