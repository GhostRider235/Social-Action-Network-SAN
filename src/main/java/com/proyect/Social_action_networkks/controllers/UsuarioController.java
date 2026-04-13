package com.proyect.Social_action_networkks.controllers;

import com.proyect.Social_action_networkks.dto.LoginRequest;
import com.proyect.Social_action_networkks.modelo.Usuario;
import com.proyect.Social_action_networkks.servicio.UsuarioServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioServicio usuarioService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 🔹 Obtener todos los usuarios
    @GetMapping
    public List<Usuario> obtenerTodos() {
        return usuarioService.obtenerTodos();
    }

    // 🔹 Obtener usuario por ID
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerPorId(@PathVariable String id) {
        return usuarioService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 🔹 Registrar nuevo usuario
    @PostMapping
    public ResponseEntity<?> guardar(@RequestBody Usuario usuario) {
        if (usuarioService.findByEmail(usuario.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("El correo electrónico ya está registrado.");
        }

        // Encriptar contraseña
        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));

        // Inicializar saldo y rol por defecto
        if (usuario.getSaldo() == null) usuario.setSaldo(BigDecimal.ZERO);
        if (usuario.getRol() == null) usuario.setRol("USUARIO");

        Usuario usuarioGuardado = usuarioService.guardar(usuario);
        return ResponseEntity.ok(usuarioGuardado);
    }

    // 🔹 Actualizar usuario parcialmente
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizar(@PathVariable String id, @RequestBody Usuario usuario) {
        Optional<Usuario> opt = usuarioService.obtenerPorId(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        Usuario existente = opt.get();

        // Solo actualizamos campos permitidos
        if (usuario.getNombre() != null) existente.setNombre(usuario.getNombre());
        if (usuario.getEmail() != null) existente.setEmail(usuario.getEmail());
        if (usuario.getTelefono() != null) existente.setTelefono(usuario.getTelefono());
        if (usuario.getContrasena() != null)
            existente.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        // No tocamos saldo ni rol directamente

        Usuario actualizado = usuarioService.guardar(existente);
        return ResponseEntity.ok(actualizado);
    }

    // 🔹 Eliminar usuario
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        if (usuarioService.obtenerPorId(id).isPresent()) {
            usuarioService.eliminar(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // 🔹 Login
    @PostMapping("/login")
    public ResponseEntity<?> autenticar(@RequestBody LoginRequest loginRequest, HttpSession session) {
        Optional<Usuario> usuarioOpt = usuarioService.findByEmail(loginRequest.getEmail());

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();

            if (!passwordEncoder.matches(loginRequest.getContrasena(), usuario.getContrasena())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas.");
            }

            // Guardar usuario en sesión
            session.setAttribute("usuarioLogueado", usuario);
            session.setAttribute("usuarioId", usuario.getId());

            return ResponseEntity.ok(usuario);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Credenciales incorrectas.");
    }

    // 🔹 Logout
    @PostMapping("/logout")
    public ResponseEntity<String> cerrarSesion(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Sesión cerrada correctamente.");
    }

    // 🔹 Verificar sesión
    @GetMapping("/session")
    public ResponseEntity<?> verificarSesion(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No hay usuario logueado.");
        }
        return ResponseEntity.ok(usuario);
    }

    // 🔹 Obtener saldo del usuario logueado
    // ✅ Obtener saldo del usuario logueado
@GetMapping("/saldo")
public ResponseEntity<?> obtenerSaldo(HttpSession session) {
    Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
    if (usuario == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No hay usuario logueado.");
    }
    return ResponseEntity.ok(usuario.getSaldo());
}

}
