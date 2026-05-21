package com.proyect.Social_action_networkks.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.proyect.Social_action_networkks.dto.UsuarioDTO;
import com.proyect.Social_action_networkks.servicio.UsuarioServicio;

@Controller
public class AuthController {

    @Autowired
    private UsuarioServicio usuarioServicio;

    // Mostrar login general
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // Registro normal de usuario
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("usuario", new UsuarioDTO());
        return "register";
    }

    @PostMapping("/register")
public String procesarRegistro(@ModelAttribute("usuario") UsuarioDTO usuarioDTO, Model model) {
    if (usuarioServicio.findByEmail(usuarioDTO.getEmail()).isPresent()) {
        model.addAttribute("error", "El correo ya está registrado");
        return "register";
    }

    if (!usuarioDTO.getContrasena().equals(usuarioDTO.getConfirmarContrasena())) {
        model.addAttribute("error", "Las contraseñas no coinciden");
        return "register";
    }

    usuarioServicio.registrarUsuario(usuarioDTO);
    return "redirect:/login?success";
}

}
