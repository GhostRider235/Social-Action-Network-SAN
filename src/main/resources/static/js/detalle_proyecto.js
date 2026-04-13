// 🎯 Este JS solo maneja confirmaciones visuales, no hace llamadas fetch.

// 🗑️ Confirmar eliminación de voluntario
function confirmarEliminacion() {
    return confirm("¿Estás seguro de eliminar este voluntario?");
}

// 🟩 Confirmar aprobación
function confirmarAprobacion() {
    return confirm("¿Deseas aprobar este voluntario?");
}

// 🟨 Confirmar rechazo
function confirmarRechazo() {
    return confirm("¿Deseas rechazar este voluntario?");
}
