package com.proyect.Social_action_networkks.controllers;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.proyect.Social_action_networkks.modelo.Usuario;
import com.proyect.Social_action_networkks.servicio.UsuarioServicio;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioRestController {

    @Autowired
    private UsuarioServicio usuarioService;

    private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    // 🔹 Obtener todos
    @GetMapping
    public List<Usuario> obtenerTodos() {
        return usuarioService.obtenerTodos();
    }

    // 🔹 Obtener por ID
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerPorId(
            @PathVariable String id) {

        return usuarioService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 🔹 Registrar usuario
    @PostMapping
    public ResponseEntity<?> guardar(
            @RequestBody Usuario usuario) {

        if (usuarioService.findByEmail(usuario.getEmail())
                .isPresent()) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("El correo ya está registrado");
        }

        usuario.setContrasena(
                passwordEncoder.encode(
                        usuario.getContrasena()
                )
        );

        if (usuario.getSaldo() == null) {
            usuario.setSaldo(BigDecimal.ZERO);
        }

        if (usuario.getRol() == null) {
            usuario.setRol("USER");
        }

        Usuario usuarioGuardado =
                usuarioService.guardar(usuario);

        return ResponseEntity.ok(usuarioGuardado);
    }

    // 🔹 Actualizar
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizar(
            @PathVariable String id,
            @RequestBody Usuario usuario) {

        Optional<Usuario> opt =
                usuarioService.obtenerPorId(id);

        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Usuario existente = opt.get();

        if (usuario.getNombre() != null) {
            existente.setNombre(usuario.getNombre());
        }

        if (usuario.getEmail() != null) {
            existente.setEmail(usuario.getEmail());
        }

        if (usuario.getTelefono() != null) {
            existente.setTelefono(usuario.getTelefono());
        }

        if (usuario.getContrasena() != null) {
            existente.setContrasena(
                    passwordEncoder.encode(
                            usuario.getContrasena()
                    )
            );
        }

        Usuario actualizado =
                usuarioService.guardar(existente);

        return ResponseEntity.ok(actualizado);
    }

    // 🔹 Eliminar
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable String id) {

        if (usuarioService.obtenerPorId(id).isPresent()) {

            usuarioService.eliminar(id);

            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    // 🔹 Obtener saldo
    @GetMapping("/saldo")
    public ResponseEntity<?> obtenerSaldo(
            Principal principal) {

        Usuario usuario = usuarioService
                .findByEmail(principal.getName())
                .orElse(null);

        if (usuario == null) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("No autenticado");
        }

        return ResponseEntity.ok(usuario.getSaldo());
    }
}