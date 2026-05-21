// ================= CHAT ADMIN =================

let chatAbierto = false;

// ================= TOGGLE CHAT =================

function toggleChat() {

    const chat =
        document.getElementById("admin-chat-window");

    chatAbierto = !chatAbierto;

    chat.style.display =
        chatAbierto ? "flex" : "none";
}

// ================= ENTER =================

document.addEventListener("DOMContentLoaded", () => {

    const input =
        document.getElementById("admin-inputMensaje");

    if (input) {

        input.addEventListener("keypress", e => {

            if (e.key === "Enter") {

                enviarMensaje();
            }
        });
    }
});

// ================= ENVIAR MENSAJE =================

async function enviarMensaje() {

    const input =
        document.getElementById("admin-inputMensaje");

    const mensaje = input.value.trim();

    if (!mensaje) return;

    const mensajesDiv =
        document.getElementById("admin-mensajes");

    // ================= MENSAJE ADMIN =================

    mensajesDiv.innerHTML += `
        <div class="admin-message admin-user">
            ${escapeHTML(mensaje)}
        </div>
    `;

    input.value = "";

    scrollChat();

    // ================= LOADING =================

    const typing = document.createElement("div");

    typing.className =
        "admin-message admin-bot";

    typing.innerHTML = `
        <span class="typing-dot"></span>
        <span class="typing-dot"></span>
        <span class="typing-dot"></span>
    `;

    mensajesDiv.appendChild(typing);

    scrollChat();

    try {

        // ================= FETCH =================

        const response = await fetch("/admin/chat", {

            method: "POST",

            headers: {
                "Content-Type": "text/plain"
            },

            body: mensaje
        });

        typing.remove();

        // ================= ERROR SERVIDOR =================

        if (!response.ok) {

            mensajesDiv.innerHTML += `
                <div class="admin-message admin-bot">
                    Error del servidor.
                </div>
            `;

            return;
        }

        const respuesta = await response.text();

        // ================= RESPUESTA IA =================

        mensajesDiv.innerHTML += `
            <div class="admin-message admin-bot">
                ${marked.parse(
                    escapeHTML(respuesta)
                )}
            </div>
        `;

        scrollChat();

    } catch (error) {

        typing.remove();

        mensajesDiv.innerHTML += `
            <div class="admin-message admin-bot">
                Error al conectar con el servidor.
            </div>
        `;

        console.error(error);

        scrollChat();
    }
}

// ================= AUTOSCROLL =================

function scrollChat() {

    const chatBody =
        document.getElementById("admin-chat-body");

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