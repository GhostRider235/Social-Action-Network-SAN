package com.proyect.Social_action_networkks.Config;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.proyect.Social_action_networkks.modelo.Usuario;
import com.proyect.Social_action_networkks.repository.UsuarioRepository;

@Component
public class AdminInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminInitializer(UsuarioRepository usuarioRepository,
                            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {

        String emailAdmin = "admin@correo.com";

        // Verificar si el admin ya existe
        if (usuarioRepository.findByEmail(emailAdmin).isEmpty()) {

            Usuario admin = new Usuario();

            admin.setNombre("Administrador");
            admin.setEmail(emailAdmin);
            admin.setTipo("ADMIN");
            admin.setTelefono("0000000000");
            admin.setFechaRegistro(new Date());

            // contraseña encriptada
            admin.setContrasena(
                    passwordEncoder.encode("admin123")
            );

            admin.setRol("ADMIN");

            admin.setSaldo(BigDecimal.ZERO);

            admin.setVoluntarioIds(new ArrayList<>());
            admin.setDonacionIds(new ArrayList<>());

            usuarioRepository.save(admin);

            System.out.println("ADMIN CREADO CORRECTAMENTE");
        }
    }
}