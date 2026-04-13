package com.proyect.Social_action_networkks.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                // Rutas que deben estar protegidas
                .addPathPatterns(
                        "/admin_dashboard",
                        "/admin/**",
                        "/fundacion_dashboard",
                        "/fundacion/dashboard/**", // protege dashboard de fundación
                        "/fundacion/proyectos/**"   // protege proyectos de fundación
                )
                // Rutas públicas (no requieren login)
                .excludePathPatterns(
                        "/",                       // página raíz
                        "/index",                  // index principal
                        "/login",                  // login general (si existe)
                        "/register",               // registro general (si existe)
                        "/fundacion/login",        // login fundación
                        "/fundacion/register",     // registro fundación
                        "/error",                  // página de error
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/uploads/**"
                );
    }
}
