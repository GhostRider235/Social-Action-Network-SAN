package com.proyect.Social_action_networkks.servicio;

import com.proyect.Social_action_networkks.dto.UsuarioDTO;
import com.proyect.Social_action_networkks.modelo.Usuario;
import com.proyect.Social_action_networkks.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServicio {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Encriptador de contraseñas
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Usuario registrarUsuario(UsuarioDTO usuarioDTO) {
        Usuario usuario = new Usuario();
        usuario.setNombre(usuarioDTO.getNombre());
        usuario.setEmail(usuarioDTO.getEmail());
        usuario.setTelefono(usuarioDTO.getTelefono());


        // Encriptar contraseña antes de guardar
        String passwordEncriptada = passwordEncoder.encode(usuarioDTO.getContrasena());
        usuario.setContrasena(passwordEncriptada);

        // Asignar rol/tipo por defecto
        usuario.setTipo("usuario");

        return usuarioRepository.save(usuario);
    }

    public Usuario guardar(Usuario usuario) {
        String passwordEncriptada = passwordEncoder.encode(usuario.getContrasena());
        usuario.setContrasena(passwordEncriptada);
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> obtenerPorId(String id) {
        return usuarioRepository.findById(id);
    }

    public void eliminar(String id) {
        usuarioRepository.deleteById(id);
    }

    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public Optional<Usuario> autenticar(String email, String contrasena) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
        if (usuario.isPresent() && passwordEncoder.matches(contrasena, usuario.get().getContrasena())) {
            return usuario;
        }
        return Optional.empty();
    }

    public Usuario findById(String id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    public boolean esAdmin(String idUsuario) {
        return usuarioRepository.findById(idUsuario)
                .map(u -> "admin".equalsIgnoreCase(u.getTipo()))
                .orElse(false);
    }
}
