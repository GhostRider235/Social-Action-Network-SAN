package com.proyect.Social_action_networkks.controllers;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.proyect.Social_action_networkks.modelo.Recarga;
import com.proyect.Social_action_networkks.modelo.Usuario;
import com.proyect.Social_action_networkks.repository.RecargaRepository;
import com.proyect.Social_action_networkks.repository.UsuarioRepository;

@Controller
@RequestMapping("/recarga")
public class RecargaController {

    @Autowired
    private RecargaRepository recargaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // 🔹 Mostrar formulario
    @GetMapping
    public String mostrarFormulario() {
        return "recarga";
    }

    // 🔹 Enviar recarga
    @PostMapping("/enviar")
public String enviarRecarga(
        @RequestParam("monto") BigDecimal monto,

        @RequestParam(value = "descripcion", required = false)
        String descripcion,

        @RequestParam("comprobantePdf")
        MultipartFile comprobanteFile,

        Principal principal,

        RedirectAttributes redirectAttributes)

        throws IOException {

        Usuario usuario = usuarioRepository
                .findByEmail(principal.getName())
                .orElse(null);

        if (usuario == null) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Debes iniciar sesión para recargar."
            );

            return "redirect:/login";
        }

        // 🔹 Guardar archivo
        String nombreArchivo =
                System.currentTimeMillis()
                + "_"
                + comprobanteFile.getOriginalFilename();

        File carpeta = new File(
                System.getProperty("user.dir")
                + "/uploads/recargas"
        );

        carpeta.mkdirs();

        File destino = new File(carpeta, nombreArchivo);

        comprobanteFile.transferTo(destino);

        // 🔹 Crear recarga
        Recarga recarga = new Recarga();

        recarga.setUsuarioId(usuario.getId());
        recarga.setMonto(monto);
        recarga.setDescripcion(descripcion);
        recarga.setFecha(new Date());
        recarga.setEstado("PENDIENTE");
        recarga.setComprobantePdf(nombreArchivo);

        recargaRepository.save(recarga);

        redirectAttributes.addFlashAttribute(
                "mensajeExito",
                "✅ Solicitud enviada correctamente"
        );

        return "redirect:/usuario/dashboard";
    }

    // 🔹 Vista admin: recargas pendientes
    @GetMapping("/pendientes")
    public String listarPendientes(
            Model model,
            Principal principal) {

        Usuario usuario = usuarioRepository
                .findByEmail(principal.getName())
                .orElse(null);

        // 🔒 Validación
        if (usuario == null ||
            !"ADMIN".equals(usuario.getRol())) {

            return "redirect:/login";
        }

        List<Recarga> recargasPendientes =
                recargaRepository.findByEstado("PENDIENTE");

        model.addAttribute(
                "recargasPendientes",
                recargasPendientes
        );

        return "recargas_admin";
    }

    // 🔹 Aprobar recarga
    @PostMapping("/aprobar/{id}")
    public String aprobar(
            @PathVariable("id") String id,
            Principal principal) {

        Usuario usuario = usuarioRepository
                .findByEmail(principal.getName())
                .orElse(null);

        if (usuario == null ||
            !"ADMIN".equals(usuario.getRol())) {

            return "redirect:/login";
        }

        recargaRepository.findById(id).ifPresent(recarga -> {

            Usuario user = usuarioRepository
                    .findById(recarga.getUsuarioId())
                    .orElse(null);

            if (user != null) {

                user.setSaldo(
                        user.getSaldo()
                        .add(recarga.getMonto())
                );

                usuarioRepository.save(user);

                recarga.setEstado("APROBADA");

                recargaRepository.save(recarga);
            }
        });

        return "redirect:/recarga/pendientes";
    }

    // 🔹 Rechazar recarga
    @PostMapping("/rechazar/{id}")
    public String rechazar(
            @PathVariable("id") String id,
            Principal principal) {

        Usuario usuario = usuarioRepository
                .findByEmail(principal.getName())
                .orElse(null);

        if (usuario == null ||
            !"ADMIN".equals(usuario.getRol())) {

            return "redirect:/login";
        }

        recargaRepository.findById(id).ifPresent(recarga -> {

            recarga.setEstado("RECHAZADA");

            recargaRepository.save(recarga);
        });

        return "redirect:/recarga/pendientes";
    }
}