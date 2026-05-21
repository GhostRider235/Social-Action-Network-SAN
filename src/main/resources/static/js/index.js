// ================= MENU =================

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

// ================= CHATBOT =================

let chatAbierto = false;

function toggleChat() {

    const chat = document.getElementById("chat-window");

    chatAbierto = !chatAbierto;

    chat.style.display = chatAbierto ? "flex" : "none";
}

// ================= ENVIAR MENSAJE =================

async function enviarMensaje() {

    const input = document.getElementById("inputMensaje");

    const mensaje = input.value.trim();

    const usuario =
        document.getElementById("usuarioActual").value;

    if (!mensaje) return;

    const mensajesDiv =
        document.getElementById("mensajes");

    // ================= MENSAJE USUARIO =================

    mensajesDiv.innerHTML += `
        <div class="mensaje usuario">
            ${escapeHTML(mensaje)}
        </div>
    `;

    input.value = "";

    scrollChat();

    // ================= LOADER =================

    const typing = document.createElement("div");

    typing.className = "mensaje ia typing";

    typing.innerHTML = `
        <span></span>
        <span></span>
        <span></span>
    `;

    mensajesDiv.appendChild(typing);

    scrollChat();

    try {

        // ================= FETCH =================

        const response = await fetch("/chat", {

            method: "POST",

            headers: {
                "Content-Type": "application/json"
            },

            body: JSON.stringify({
                usuario: usuario,
                contenido: mensaje
            })

        });

        if (!response.ok) {
            throw new Error("Error del servidor");
        }

        const data = await response.json();

        typing.remove();

        // ================= RESPUESTA IA =================

        const ultimaRespuesta = data[data.length - 1];

        if (!ultimaRespuesta ||
            !ultimaRespuesta.contenido) {

            mensajesDiv.innerHTML += `
                <div class="mensaje ia error">
                    🤖 No pude generar una respuesta.
                </div>
            `;

        } else {

            mensajesDiv.innerHTML += `
                <div class="mensaje ia">
                    ${marked.parse(
                        escapeHTML(
                            ultimaRespuesta.contenido
                        )
                    )}
                </div>
            `;
        }

        scrollChat();

    } catch (error) {

        typing.remove();

        mensajesDiv.innerHTML += `
            <div class="mensaje ia error">
                ⚠️ Error al conectar con el servidor.
            </div>
        `;

        console.error(error);

        scrollChat();
    }
}

// ================= AUTOSCROLL =================

function scrollChat() {

    const chatBody =
        document.getElementById("chat-body");

    chatBody.scrollTop =
        chatBody.scrollHeight;
}

// ================= SEGURIDAD =================

function escapeHTML(text) {

    return text
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;");
}