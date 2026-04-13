package com.proyect.Social_action_networkks.Config;

import com.proyect.Social_action_networkks.modelo.Usuario;
import com.proyect.Social_action_networkks.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminInitializer {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner crearAdminYActualizarUsuarios(UsuarioRepository repo) {
        return args -> {
            // Crear admin si no existe
            if (repo.findByEmail("admin@correo.com").isEmpty()) {
                Usuario admin = new Usuario();
                admin.setNombre("Administrador");
                admin.setEmail("admin@correo.com");
                admin.setContrasena(passwordEncoder.encode("admin123"));
                admin.setTipo("admin");
                admin.setFechaRegistro(new Date());
                repo.save(admin);
                System.out.println("Admin creado con contraseña encriptada.");
            }

            // Actualizar contraseñas no encriptadas de usuarios existentes
            List<Usuario> usuarios = repo.findAll();
            for (Usuario u : usuarios) {
                String pass = u.getContrasena();
                // Validar si la contraseña NO parece estar encriptada con BCrypt (no comienza con $2a$ o $2b$)
                if (pass == null || !(pass.startsWith("$2a$") || pass.startsWith("$2b$"))) {
                    System.out.println("Actualizando contraseña del usuario: " + u.getEmail());
                    String encriptada = passwordEncoder.encode(pass == null ? "default123" : pass);
                    u.setContrasena(encriptada);
                    repo.save(u);
                }
            }
            System.out.println("Actualización de contraseñas completada.");
        };
    }
}


