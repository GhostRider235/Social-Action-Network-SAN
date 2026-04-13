package com.proyect.Social_action_networkks.controllers;

import com.proyect.Social_action_networkks.dto.UsuarioDTO;
import com.proyect.Social_action_networkks.servicio.UsuarioServicio;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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


    // Procesar login de usuarios normales
    @PostMapping("/login")
public String procesarLoginUsuario(@RequestParam String email,
                                   @RequestParam String contrasena,
                                   Model model,
                                   HttpSession session) {
    return usuarioServicio.autenticar(email, contrasena)
        .map(usuario -> {
            session.setAttribute("usuarioLogueado", usuario);
            session.setAttribute("usuarioId", usuario.getId());
            session.setAttribute("tipoUsuario", usuario.getTipo()); // ✅ Aquí agregas esta línea

            if ("admin".equalsIgnoreCase(usuario.getTipo())) {
                return "redirect:/admin_dashboard";
            } else {
                return "redirect:/";
            }
        })
        .orElseGet(() -> {
            model.addAttribute("error", "Usuario no encontrado o contraseña incorrecta");
            return "login";
        });
}
    // Cerrar sesión
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
