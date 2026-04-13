 const input = document.getElementById("card-cvv");
  input.addEventListener("input", () => {
    // Elimina cualquier carácter que no sea número
    input.value = input.value.replace(/\D/g, '');

    // Limita a 3 caracteres (por si se pegó texto)
    if (input.value.length > 3) {
      input.value = input.value.slice(0, 3);
    }

    // Muestra el valor actual en el label
    label.textContent = input.value;
  });
                