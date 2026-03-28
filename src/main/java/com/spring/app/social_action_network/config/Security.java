package com.spring.app.social_action_network.config;

import com.spring.app.social_action_network.repositories.UsuarioRepository;
import com.spring.app.social_action_network.services.AuthService;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableAutoConfiguration
public class Security {

    private final UsuarioRepository usuarioRepository;

    public Security(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }


    @Bean
    public AuthService authService() {
        return new AuthService(usuarioRepository,passwordEncoder());
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return usuario -> usuarioRepository.findByEmail(usuario).orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }

    // Bean que proporciona el AuthenticationManager necesario para manejar el login
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception{
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() throws Exception{
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(
                        authorizeRequests -> authorizeRequests
                                .requestMatchers("/auth/**").permitAll()
                                .requestMatchers("/app").authenticated()
                                .anyRequest().authenticated()
                )
                .formLogin(
                        login -> login
                                .loginPage("/auth/login") // Diereccion de la pagina donde se hara el login
                                .loginProcessingUrl("/auth/login") // Direccion en donde hace el proceso del login
                )
                .logout(
                        out -> out
                                .logoutUrl("/auth/logout") // URL para cerrar sesión
                                .logoutSuccessUrl("/auth/login?logout") // Cierre de sesion
                )


                .build();

    }
}
