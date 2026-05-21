package com.proyect.Social_action_networkks.controllers;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.proyect.Social_action_networkks.modelo.Usuario;

@Controller
public class HomeController {

    @GetMapping({"", "/", "/index"})
    public String index(Authentication auth, Model model) {

        if (auth != null &&
            auth.isAuthenticated() &&
            !(auth instanceof AnonymousAuthenticationToken)) {

            Usuario usuario = (Usuario) auth.getPrincipal();

            model.addAttribute("usuarioLogueado", usuario);

            if ("ADMIN".equalsIgnoreCase(usuario.getRol())) {
                return "redirect:/admin_dashboard";
            }
        }

        return "index";
    }
}