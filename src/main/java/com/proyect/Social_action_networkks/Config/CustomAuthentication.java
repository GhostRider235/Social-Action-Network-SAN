package com.proyect.Social_action_networkks.Config;

import com.proyect.Social_action_networkks.modelo.Fundacion;
import com.proyect.Social_action_networkks.repository.FundacionRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthentication
        implements AuthenticationFailureHandler {

    private final FundacionRepository fundacionRepository;

    public CustomAuthentication(
            FundacionRepository fundacionRepository) {

        this.fundacionRepository = fundacionRepository;
    }

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception)

            throws IOException, ServletException {

        String email = request.getParameter("email");

        Fundacion fundacion =
                fundacionRepository
                .findByCorreo(email)
                .orElse(null);

        if (fundacion != null) {

            if ("PENDIENTE".equalsIgnoreCase(
                    fundacion.getEstado())) {

                response.sendRedirect(
                        "/login?pendiente=true");

                return;
            }

            if ("RECHAZADA".equalsIgnoreCase(
                    fundacion.getEstado())) {

                response.sendRedirect(
                        "/login?rechazada=true");

                return;
            }
        }

        if (exception instanceof BadCredentialsException) {

            response.sendRedirect(
                    "/login?error=true");

            return;
        }

        if (exception instanceof DisabledException) {

            response.sendRedirect(
                    "/login?disabled=true");

            return;
        }

        response.sendRedirect("/login?error=true");
    }
}