function ordenarSelect() {
    const select = document.getElementById("create-fundacionId");
    const opciones = Array.from(select.options);
    opciones.sort((a, b) => a.text.localeCompare(b.text, 'es', { sensitivity: 'base' }));
    select.innerHTML = "";
    opciones.forEach(opcion => select.add(opcion));
}
