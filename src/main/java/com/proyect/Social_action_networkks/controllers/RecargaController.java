package com.proyect.Social_action_networkks.controllers;

import com.proyect.Social_action_networkks.modelo.Recarga;
import com.proyect.Social_action_networkks.modelo.Usuario;
import com.proyect.Social_action_networkks.repository.RecargaRepository;
import com.proyect.Social_action_networkks.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/recarga")
public class RecargaController {

    @Autowired
    private RecargaRepository recargaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private HttpSession session;

    // Mostrar formulario
    @GetMapping
    public String mostrarFormulario() {
        return "recarga";
    }

    // Enviar recarga
    @PostMapping("/enviar")
public String enviarRecarga(@RequestParam BigDecimal monto,
                            @RequestParam(required = false) String descripcion,
                            @RequestParam("comprobantePdf") MultipartFile comprobanteFile,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) throws IOException {

    Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
    if (usuario == null) {
        redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para recargar.");
        return "redirect:/login";
    }

    // Guardar archivo en el servidor
    String nombreArchivo = System.currentTimeMillis() + "_" + comprobanteFile.getOriginalFilename();
    File carpeta = new File(System.getProperty("user.dir") + "/uploads/recargas");
    carpeta.mkdirs();
    File destino = new File(carpeta, nombreArchivo);
    comprobanteFile.transferTo(destino);

    Recarga recarga = new Recarga();
    recarga.setUsuarioId(usuario.getId());
    recarga.setMonto(monto);
    recarga.setDescripcion(descripcion);
    recarga.setFecha(new Date());
    recarga.setEstado("PENDIENTE");
    recarga.setComprobantePdf(nombreArchivo); // 🔹 Guardar nombre del PDF
    recargaRepository.save(recarga);

    // 🔄 Refrescar usuario en sesión
    session.setAttribute("usuarioLogueado", usuarioRepository.findById(usuario.getId()).orElse(usuario));

    redirectAttributes.addFlashAttribute("mensajeExito",
            "✅ Tu solicitud de recarga por $" + monto + " ha sido enviada al administrador.");
    return "redirect:/index";
}

    // 🔹 Vista para admin: lista de recargas pendientes
    @GetMapping("/pendientes")
public String listarPendientes(Model model, HttpSession session) {

    Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

    // 🔒 Validación de seguridad
    if (usuario == null || !"ADMIN".equals(usuario.getRol())) {
        return "redirect:/login";
    }

    List<Recarga> recargasPendientes = recargaRepository.findByEstado("PENDIENTE");
    model.addAttribute("recargasPendientes", recargasPendientes);

    return "recargas_admin";
}


    // 🔹 Admin aprueba
    @PostMapping("/aprobar/{id}")
public String aprobar(@PathVariable String id, HttpSession session) {

    Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

    if (usuario == null || !"ADMIN".equals(usuario.getRol())) {
        return "redirect:/login";
    }

    recargaRepository.findById(id).ifPresent(recarga -> {
        Usuario user = usuarioRepository.findById(recarga.getUsuarioId()).orElse(null);
        if (user != null) {
            user.setSaldo(user.getSaldo().add(recarga.getMonto()));
            usuarioRepository.save(user);

            recarga.setEstado("APROBADA");
            recargaRepository.save(recarga);
        }
    });

    return "redirect:/recarga/pendientes";
}



    @PostMapping("/rechazar/{id}")
public String rechazar(@PathVariable String id, HttpSession session) {

    Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

    if (usuario == null || !"ADMIN".equals(usuario.getRol())) {
        return "redirect:/login";
    }

    recargaRepository.findById(id).ifPresent(recarga -> {
        recarga.setEstado("RECHAZADA");
        recargaRepository.save(recarga);
    });

    return "redirect:/recarga/pendientes";
}
}
