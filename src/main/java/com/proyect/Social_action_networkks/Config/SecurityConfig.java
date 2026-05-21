package com.proyect.Social_action_networkks.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.proyect.Social_action_networkks.modelo.Fundacion;
import com.proyect.Social_action_networkks.repository.FundacionRepository;
import com.proyect.Social_action_networkks.repository.UsuarioRepository;
import com.proyect.Social_action_networkks.servicio.UsuarioServicio;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UsuarioRepository repository;

    private final FundacionRepository fundacionRepository;

    private final CustomAuthentication failureHandler;

    public SecurityConfig(UsuarioRepository repository, FundacionRepository fundacionRepository, CustomAuthentication failureHandler) { 
        this.repository = repository;
        this.fundacionRepository = fundacionRepository;
        this.failureHandler = failureHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


    @Bean
    public UsuarioServicio usuarioServicio() {
        return new UsuarioServicio(repository,passwordEncoder());
    }


    @Bean
public UserDetailsService userDetailsService() {

    return email -> {

        // Buscar usuario normal
        var usuario = repository.findByEmail(email);

        if (usuario.isPresent()) {
            return usuario.get();
        }

        // Buscar fundación
        var fundacion = fundacionRepository.findByCorreo(email);

        if (fundacion.isPresent()) {
            return fundacion.get();
        }

        throw new UsernameNotFoundException("Usuario no encontrado");
    };
}

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception{
        return configuration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() throws Exception{
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    return http

        .csrf(csrf -> csrf.disable())

        .authorizeHttpRequests(request -> request

            // públicas
            .requestMatchers("/", "/index").permitAll()

            .requestMatchers("/login").permitAll()
            .requestMatchers("/register").permitAll()

            .requestMatchers("/fundacion/register").permitAll()

            .requestMatchers("/css/**").permitAll()
            .requestMatchers("/js/**").permitAll()
            .requestMatchers("/images/**").permitAll()
            .requestMatchers("/uploads/**").permitAll()
            .requestMatchers("/chat/**").permitAll()

            .requestMatchers("/fundacion/*/logo").permitAll()

            // privadas
            .requestMatchers("/admin/**").hasRole("ADMIN")

            .requestMatchers("/usuario/**")
            .hasAnyRole("USER", "ADMIN")

            .requestMatchers("/fundacion/**")
            .hasRole("FUNDACION")

            .anyRequest().authenticated()
        )

        .formLogin(login -> login

            .loginPage("/login")

            .loginProcessingUrl("/login")

            .usernameParameter("email")

            .passwordParameter("contrasena")

            .failureHandler(failureHandler)

            .successHandler((request, response, authentication) -> {

    var authorities = authentication.getAuthorities();

    if (authorities.stream()
            .anyMatch(a -> a.getAuthority()
            .equals("ROLE_ADMIN"))) {

        response.sendRedirect("/admin_dashboard");
    }

    else if (authorities.stream()
            .anyMatch(a -> a.getAuthority()
            .equals("ROLE_FUNDACION"))) {

        Fundacion fundacion =
                fundacionRepository
                .findByCorreo(authentication.getName())
                .orElse(null);

        if (fundacion != null) {

            response.sendRedirect(
                    "/fundacion/dashboard/"
                            + fundacion.getId()
            );

        } else {

            response.sendRedirect("/login");
        }
    }

    // 🔹 USER
    else {

        response.sendRedirect("/usuario/dashboard");
    }
    }).permitAll()
        )
        .logout(logout -> logout

            .logoutUrl("/logout")

            .logoutSuccessUrl("/")

            .permitAll()
        )
        .build();
}

}
