package com.proyect.Social_action_networkks.Config;

import com.proyect.Social_action_networkks.repository.UsuarioRepository;
import com.proyect.Social_action_networkks.servicio.UsuarioServicio;
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

@Configuration
@EnableWebSecurity
public class configuration {

    private final UsuarioRepository repository;

    public configuration(UsuarioRepository repository) {
        this.repository = repository;
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
    public UserDetailsService  userDetailsService() {
        return usuario -> repository.findByEmail(usuario).orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
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
                .authorizeHttpRequests(request -> request

                        // rutas publicas
                        .requestMatchers("/index").permitAll()
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/register").permitAll()
                        .requestMatchers("/fundacion/login").permitAll()
                        .requestMatchers("/fundacion/register").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/css/**").permitAll()
                        .requestMatchers("/js/**").permitAll()
                        .requestMatchers("/images/**").permitAll()
                        .requestMatchers("/uploads/**").permitAll()


                        // rutas privadas
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/usuario/**").hasRole("USER")
                        .requestMatchers("/fundacion/proyectos/**").hasRole("FUNDACION")
                        .requestMatchers("/fundacion/dashboard/**").hasRole("FUNDACION")
                        .requestMatchers("/fundacion_dashboard").hasRole("FUNDACION")
                        .anyRequest().authenticated()
                )

                .formLogin(login -> login
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                )

                // cierre se sesion
                .logout(out  -> out
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout"))
                .build();
    }

}
