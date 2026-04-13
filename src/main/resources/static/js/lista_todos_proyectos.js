let saldoUsuario = 0;

// Obtener saldo del usuario
fetch("/usuarios/saldo", { credentials: "include" })
  .then(res => res.json())
  .then(data => saldoUsuario = parseFloat(data))
  .catch(err => console.warn(err.message));

const modal = document.getElementById("modal");
const modalContent = document.querySelector(".modal-content");

function abrirModal(tipo, btn) {
  const card = btn.closest(".project-card");
  const nombre = card.querySelector("h3").innerText;
  const descripcion = card.querySelector("p").innerText;
  const fundacion = card.querySelector(".fundacion-nombre").innerText;
  const proyectoId = card.dataset.proyectoId;
  const fundacionId = card.dataset.fundacionId;
  const modalBody = document.getElementById("modal-body");

  if (tipo === "donar") {
    modalBody.innerHTML = `
      <h3>Donar a "${nombre}"</h3>
      <p><strong>Fundación:</strong> ${fundacion}</p>
      <div class="saldo-box">💰 Saldo actual: $${saldoUsuario.toLocaleString()}</div>
      <input id="montoDonacion" type="number" placeholder="Monto a donar" 
             style="width:80%;padding:8px;border-radius:8px;border:1px solid #ccc;"><br><br>
      <button onclick="confirmarDonacion('${proyectoId}', '${fundacionId}')"
        style="background:#28a745;color:white;padding:8px 16px;border:none;border-radius:8px;">Confirmar donación</button>
    `;
  } else {
    modalBody.innerHTML = `
      <h3>Postularse a "${nombre}"</h3>
      <p><strong>Fundación:</strong> ${fundacion}</p>
      <p><strong>Descripción:</strong> ${descripcion}</p>
      <textarea id="comentarioVoluntario" placeholder="Escribe un mensaje..." 
                style="width:90%;padding:8px;border-radius:8px;border:1px solid #ccc;"></textarea><br><br>
      <button onclick="enviarPostulacion('${proyectoId}', '${fundacionId}')"
        style="background:#ffc107;color:#333;padding:8px 16px;border:none;border-radius:8px;">Enviar postulación</button>
    `;
  }

  modal.style.display = "flex";
  void modal.offsetWidth;
  modal.style.opacity = 1;
  modal.classList.add("show");
}

function cerrarModal() {
  modal.style.opacity = 0;
  setTimeout(() => {
    modal.style.display = "none";
    modal.classList.remove("show");
  }, 200);
}

modal.addEventListener("click", e => { if(e.target === modal) cerrarModal(); });
modalContent.addEventListener("click", e => e.stopPropagation());

function confirmarDonacion(proyectoId, fundacionId) {
  const monto = parseFloat(document.getElementById("montoDonacion").value);
  if (!monto || monto <= 0) { alert("Por favor ingresa un monto válido."); return; }
  if (monto > saldoUsuario) { alert("Saldo insuficiente."); return; }

  fetch("/donaciones/guardar", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    credentials: "include",
    body: JSON.stringify({ proyectoId, fundacionId, monto, descripcion: "Donación desde la web" })
  })
  .then(res => res.json())
  .then(data => {
    if(data.success) {
      saldoUsuario -= monto;
      alert(`✅ Donación de $${monto.toLocaleString()} realizada con éxito.`);
      cerrarModal();
    } else {
      alert("❌ Error: " + data.mensaje);
    }
  })
  .catch(err => alert("❌ " + err.message));
}

function enviarPostulacion(proyectoId, fundacionId) {
  const comentario = document.getElementById("comentarioVoluntario").value.trim();
  fetch("/voluntarios", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    credentials: "include",
    body: JSON.stringify({ comentario, proyectoId, fundacionId })
  })
  .then(res => res.json())
  .then(data => { alert("✅ Postulación enviada correctamente."); cerrarModal(); })
  .catch(err => alert("❌ " + err.message));
}
