package com.proyect.Social_action_networkks.controllers;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.proyect.Social_action_networkks.Config.FundacionTools;
import com.proyect.Social_action_networkks.modelo.Usuario;
import com.proyect.Social_action_networkks.repository.UsuarioRepository;

@RestController
@RequestMapping("/admin/chat")
public class AdminChatController {

    private final ChatClient chatClient;

    private final UsuarioRepository usuarioRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminChatController(
            ChatClient.Builder builder,
            FundacionTools fundacionTools,
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder
    ) {

        this.chatClient = builder
                .defaultTools(fundacionTools)
                .build();

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping
    public String chat(@RequestBody String mensaje) {

        String mensajeLower = mensaje.toLowerCase();

        // =====================================================
        // CREAR USUARIO
        // =====================================================

        if (mensajeLower.startsWith("crear usuario")) {

            try {

                String[] partes = mensaje.split(" ");

                if (partes.length < 7) {

                    return """
                            Formato incorrecto.

                            Usa:

                            crear usuario nombre email telefono contraseña rol
                            """;
                }

                String nombre = partes[2];

                String email = partes[3];

                String telefono = partes[4];

                String contrasena = partes[5];

                String rol = partes[6].toUpperCase();

                Optional<Usuario> existente =
                        usuarioRepository.findByEmail(email);

                if (existente.isPresent()) {

                    return "Ya existe un usuario con ese email.";
                }

                Usuario usuario = new Usuario();

                usuario.setNombre(nombre);

                usuario.setEmail(email);

                usuario.setTelefono(telefono);

                usuario.setContrasena(
                        passwordEncoder.encode(contrasena)
                );

                usuario.setRol(rol);

                usuario.setSaldo(BigDecimal.ZERO);

                usuarioRepository.save(usuario);

                return """
                        Usuario creado correctamente.

                        Rol asignado: %s
                        """.formatted(rol);

            } catch (Exception e) {

                return "Error al crear el usuario.";
            }
        }

        // =====================================================
        // ACTUALIZAR ROL
        // =====================================================

        else if (mensajeLower.startsWith("actualizar rol")) {

            try {

                /*
                 FORMATO:

                 actualizar rol email nuevoRol
                 */

                String[] partes = mensaje.split(" ");

                if (partes.length < 4) {

                    return """
                            Formato incorrecto.

                            Usa:

                            actualizar rol email nuevoRol
                            """;
                }

                String email = partes[2];

                String nuevoRol = partes[3].toUpperCase();

                Optional<Usuario> usuarioOpt =
                        usuarioRepository.findByEmail(email);

                if (usuarioOpt.isEmpty()) {

                    return "No existe un usuario con ese email.";
                }

                Usuario usuario = usuarioOpt.get();

                usuario.setRol(nuevoRol);

                usuarioRepository.save(usuario);

                return """
                        Rol actualizado correctamente.

                        Usuario: %s
                        Nuevo rol: %s
                        """.formatted(
                                usuario.getNombre(),
                                nuevoRol
                        );

            } catch (Exception e) {

                return "Error al actualizar el rol.";
            }
        }

        // =====================================================
        // ELIMINAR USUARIO
        // =====================================================

        else if (mensajeLower.startsWith("eliminar usuario")) {

            try {

                String[] partes = mensaje.split(" ");

                if (partes.length < 3) {

                    return """
                            Formato incorrecto.

                            Usa:

                            eliminar usuario email
                            """;
                }

                String email = partes[2];

                Optional<Usuario> usuarioOpt =
                        usuarioRepository.findByEmail(email);

                if (usuarioOpt.isEmpty()) {

                    return "No existe un usuario con ese email.";
                }

                usuarioRepository.delete(usuarioOpt.get());

                return "Usuario eliminado correctamente.";

            } catch (Exception e) {

                return "Error al eliminar usuario.";
            }
        }

        // =====================================================
        // LISTAR USUARIOS
        // =====================================================

        else if (mensajeLower.startsWith("listar usuarios")) {

            var usuarios = usuarioRepository.findAll();

            if (usuarios.isEmpty()) {

                return "No hay usuarios registrados.";
            }

            StringBuilder lista = new StringBuilder();

            for (Usuario u : usuarios) {

                lista.append("""
                        
                        Nombre: %s
                        Email: %s
                        Rol: %s
                        """.formatted(
                                u.getNombre(),
                                u.getEmail(),
                                u.getRol()
                        ));
            }

            return lista.toString();
        }

        // =====================================================
        // CHAT NORMAL ADMIN
        // =====================================================

        return chatClient.prompt()
                .system("""
                    Eres el asistente administrador del sistema.

                    Puedes:
                    - aprobar fundaciones
                    - rechazar fundaciones
                    - crear usuarios
                    - eliminar usuarios
                    - actualizar roles
                    - listar usuarios

                    Roles válidos:
                    - USER
                    - ADMIN

                    Antes de ejecutar acciones críticas,
                    debes pedir confirmación.
                """)
                .user(mensaje)
                .call()
                .content();
    }
}