package com.proyect.Social_action_networkks.controllers;

import com.proyect.Social_action_networkks.modelo.Fundacion;
import com.proyect.Social_action_networkks.modelo.Usuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping({"", "/", "/index"})
    public String index(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        Fundacion fundacion = (Fundacion) session.getAttribute("fundacionLogueada");

        if (usuario != null) {
            // ✅ Enviamos el usuario logueado al modelo
            model.addAttribute("usuarioLogueado", usuario);

            if ("ADMIN".equalsIgnoreCase(usuario.getTipo())) {
                return "redirect:/admin_dashboard";
            } else {
                return "index"; // Carga la plantilla index.html
            }
        }

        if (fundacion != null) {
            model.addAttribute("fundacionLogueada", fundacion);

            if ("APROBADA".equalsIgnoreCase(fundacion.getEstado())) {
                return "fundacion_dashboard";
            } else {
                model.addAttribute("mensaje", "Tu cuenta aún no ha sido aprobada");
                return "index";
            }
        }

        return "index"; // Para visitantes
    }
}
