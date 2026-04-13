// ===== MENU =====
document.addEventListener("DOMContentLoaded", () => {
  const menuButton = document.getElementById("menu-toggle");
  const nav = document.getElementById("main-nav");

  if (menuButton) {
    menuButton.addEventListener("click", () => {
      nav.classList.toggle("active");
    });
  }

  // ENTER para enviar mensaje
  const input = document.getElementById("inputMensaje");
  if (input) {
    input.addEventListener("keypress", function(e) {
      if (e.key === "Enter") {
        enviarMensaje();
      }
    });
  }
});

// ===== CHATBOT =====
let chatAbierto = false;

function toggleChat() {
    const chat = document.getElementById("chat-window");
    chatAbierto = !chatAbierto;
    chat.style.display = chatAbierto ? "flex" : "none";
}

function enviarMensaje() {
    let input = document.getElementById("inputMensaje");
    let mensaje = input.value.trim();

    if (!mensaje) return;

    let mensajesDiv = document.getElementById("mensajes");

    // Mensaje del usuario
    mensajesDiv.innerHTML += `
        <div class="mensaje usuario">${mensaje}</div>
    `;

    input.value = "";
    mensajesDiv.scrollTop = mensajesDiv.scrollHeight;

    // Indicador "escribiendo..."
    let typing = document.createElement("div");
    typing.className = "mensaje ia";
    typing.innerText = "Escribiendo...";
    mensajesDiv.appendChild(typing);

    mensajesDiv.scrollTop = mensajesDiv.scrollHeight;

    // PETICIÓN AL BACKEND
    fetch("/chat", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            usuario: "usuario1",
            contenido: mensaje
        })
    })
    .then(res => {
        if (!res.ok) {
            throw new Error("Error del servidor");
        }
        return res.json(); 
    })
    .then(data => {
        typing.remove();

        // Tomar la última respuesta del chatbot
        let ultimaRespuesta = data[data.length - 1];

        if (!ultimaRespuesta || !ultimaRespuesta.contenido) {
            mensajesDiv.innerHTML += `
                <div class="mensaje ia">
                    🤖 No pude generar respuesta.
                </div>
            `;
        } else {
            mensajesDiv.innerHTML += `
                <div class="mensaje ia">
                    ${ultimaRespuesta.contenido}
                </div>
            `;
        }

        mensajesDiv.scrollTop = mensajesDiv.scrollHeight;
    })
    .catch(error => {
        typing.remove();

        mensajesDiv.innerHTML += `
            <div class="mensaje ia error">
                ⚠️ Error al conectar con el servidor
            </div>
        `;

        console.error("Error:", error);
        mensajesDiv.scrollTop = mensajesDiv.scrollHeight;
    });
}