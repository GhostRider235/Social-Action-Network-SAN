package com.proyect.Social_action_networkks.Config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.proyect.Social_action_networkks.modelo.Usuario;
import com.proyect.Social_action_networkks.modelo.Fundacion;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        HttpSession session = request.getSession(false);
        String path = request.getRequestURI();

        // Si no hay sesión, redirige al login
        if (session == null) {
            response.sendRedirect("/login");
            return false;
        }

        // Detectar si es fundación
        Fundacion fundacion = (Fundacion) session.getAttribute("fundacionLogueada");
        if (fundacion != null) {
            // Solo permitir fundaciones en sus rutas
            if (path.startsWith("/fundacion") && 
                "APROBADA".equalsIgnoreCase(fundacion.getEstado())) {
                return true; // fundación aprobada
            } else {
                response.sendRedirect("/index");
                return false;
            }
        }

        // Detectar si es usuario normal (Admin u otro)
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario != null) {
            String tipo = usuario.getTipo() != null ? usuario.getTipo() : "";

            if (path.startsWith("/admin") && "ADMIN".equalsIgnoreCase(tipo)) {
                return true; // es admin
            }

            // Otros usuarios no pueden entrar a zonas protegidas
            response.sendRedirect("/index");
            return false;
        }

        // Si no hay ningún usuario logueado
        response.sendRedirect("/login");
        return false;
    }
}
