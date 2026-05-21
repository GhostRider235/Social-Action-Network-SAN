package com.proyect.Social_action_networkks.controllers;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.proyect.Social_action_networkks.modelo.Usuario;
import com.proyect.Social_action_networkks.servicio.UsuarioServicio;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioServicio usuarioService;

    // 🔹 Dashboard
    @GetMapping("/dashboard")
    public String dashboardUsuario(
            Principal principal,
            Model model) {

        Usuario usuario = usuarioService
                .findByEmail(principal.getName())
                .orElse(null);

        if (usuario == null) {
            return "redirect:/login";
        }

        model.addAttribute("usuario", usuario);

        return "usuario_dashboard";
    }

    // 🔹 Perfil
    @GetMapping("/perfil")
    public String perfil(
            Principal principal,
            Model model) {

        Usuario usuario = usuarioService
                .findByEmail(principal.getName())
                .orElse(null);

        if (usuario == null) {
            return "redirect:/login";
        }

        model.addAttribute("usuario", usuario);

        return "usuario_perfil";
    }
}