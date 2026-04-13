package com.proyect.Social_action_networkks.controllers;

import com.proyect.Social_action_networkks.dto.VoluntarioDTO;
import com.proyect.Social_action_networkks.modelo.Fundacion;
import com.proyect.Social_action_networkks.modelo.Proyecto;
import com.proyect.Social_action_networkks.modelo.Usuario;
import com.proyect.Social_action_networkks.modelo.Voluntario;
import com.proyect.Social_action_networkks.servicio.FundacionService;
import com.proyect.Social_action_networkks.servicio.ProyectoService;
import com.proyect.Social_action_networkks.servicio.UsuarioServicio;
import com.proyect.Social_action_networkks.servicio.VoluntarioService;
import com.proyect.Social_action_networkks.repository.VoluntarioRepository;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/voluntarios")
public class VoluntarioController {

    private final VoluntarioService voluntarioService;

    @Autowired
    private UsuarioServicio usuarioService;

    @Autowired
    private ProyectoService proyectoService;

    @Autowired
    private FundacionService fundacionService;

    @Autowired
    private VoluntarioRepository voluntarioRepository;


    public VoluntarioController(VoluntarioService voluntarioService) {
        this.voluntarioService = voluntarioService;
    }

    // ✅ Obtener todos los voluntarios (JSON)
    @GetMapping
    @ResponseBody
    public List<VoluntarioDTO> obtenerTodos() {
        return voluntarioService.obtenerTodos().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // ✅ Obtener voluntario por ID (JSON)
    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<VoluntarioDTO> obtenerPorId(@PathVariable String id) {
        return voluntarioService.obtenerPorId(id)
                .map(v -> ResponseEntity.ok(convertirADTO(v)))
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ Obtener voluntarios por proyecto (JSON)
    @GetMapping("/proyecto/{proyectoId}")
    @ResponseBody
    public List<VoluntarioDTO> obtenerPorProyecto(@PathVariable String proyectoId) {
        return voluntarioService.obtenerPorProyecto(proyectoId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // ✅ Postulación de voluntario (JSON)
    @PostMapping
    @ResponseBody
    public ResponseEntity<?> guardar(@RequestBody VoluntarioDTO voluntarioDTO, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return ResponseEntity.status(401).body("⚠️ Debes iniciar sesión para postularte.");
        }

        if (voluntarioService.existePorUsuarioYProyecto(usuario.getId(), voluntarioDTO.getProyectoId())) {
            return ResponseEntity.badRequest().body("Ya estás postulado a este proyecto.");
        }

        Optional<Proyecto> proyectoOpt = proyectoService.obtenerPorId(voluntarioDTO.getProyectoId());
        if (proyectoOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("❌ El proyecto no fue encontrado.");
        }

        Proyecto proyecto = proyectoOpt.get();

        Voluntario voluntario = new Voluntario();
        voluntario.setUsuarioId(usuario.getId());
        voluntario.setProyectoId(voluntarioDTO.getProyectoId());
        voluntario.setFundacionId(proyecto.getFundacionId());
        voluntario.setComentario(voluntarioDTO.getComentario());
        voluntario.setFechaAsignacion(voluntarioDTO.getFechaAsignacion());

        Voluntario guardado = voluntarioService.guardar(voluntario);
        return ResponseEntity.ok(convertirADTO(guardado));
    }

    // ✅ Actualizar postulación (JSON)
    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> actualizar(@PathVariable String id, @RequestBody VoluntarioDTO voluntarioDTO) {
        Optional<Voluntario> voluntarioOpt = voluntarioService.obtenerPorId(id);
        if (voluntarioOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Voluntario voluntario = voluntarioOpt.get();
        voluntario.setComentario(voluntarioDTO.getComentario());
        voluntario.setFechaAsignacion(voluntarioDTO.getFechaAsignacion());

        Voluntario actualizado = voluntarioService.guardar(voluntario);
        return ResponseEntity.ok(convertirADTO(actualizado));
    }

    // ✅ APROBAR VOLUNTARIO
@PostMapping("/aprobar/{id}")
    public String aprobarVoluntario(@PathVariable String id) {
        Voluntario voluntario = voluntarioRepository.findById(id).orElse(null);
        if (voluntario != null) {
            voluntario.setEstado("Aprobado");
            voluntarioRepository.save(voluntario);
        }
        return "redirect:/detalle_proyecto/" + voluntario.getProyectoId();
    }

    @PostMapping("/rechazar/{id}")
    public String rechazarVoluntario(@PathVariable String id) {
        Voluntario voluntario = voluntarioRepository.findById(id).orElse(null);
        if (voluntario != null) {
            voluntario.setEstado("Rechazado");
            voluntarioRepository.save(voluntario);
        }
        return "redirect:/detalle_proyecto/" + voluntario.getProyectoId();
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<String> eliminarVoluntario(@PathVariable String id) {
        if (!voluntarioRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Voluntario no encontrado");
        }
        voluntarioRepository.deleteById(id);
        return ResponseEntity.ok("Eliminado correctamente");
    }


    // 🔹 Conversión entidad → DTO (enriquecido)
    private VoluntarioDTO convertirADTO(Voluntario voluntario) {
        VoluntarioDTO dto = new VoluntarioDTO();
        dto.setId(voluntario.getId());
        dto.setUsuarioId(voluntario.getUsuarioId());
        dto.setProyectoId(voluntario.getProyectoId());
        dto.setFundacionId(voluntario.getFundacionId());
        dto.setFechaAsignacion(voluntario.getFechaAsignacion());
        dto.setComentario(voluntario.getComentario());
        dto.setEstado(voluntario.getEstado());

        // Datos del usuario
        Optional<Usuario> usuarioOpt = Optional.ofNullable(usuarioService.findById(voluntario.getUsuarioId()));
        usuarioOpt.ifPresentOrElse(u -> {
            dto.setNombreUsuario(u.getNombre());
            dto.setTelefonoUsuario(u.getTelefono() != null ? u.getTelefono() : "No disponible");
            dto.setCorreoUsuario(u.getEmail() != null ? u.getEmail() : "No disponible");
        }, () -> {
            dto.setNombreUsuario("Desconocido");
            dto.setTelefonoUsuario("No disponible");
            dto.setCorreoUsuario("No disponible");
        });

        // Datos del proyecto
        Optional<Proyecto> proyectoOpt = proyectoService.obtenerPorId(voluntario.getProyectoId());
        dto.setNombreProyecto(proyectoOpt.map(Proyecto::getNombre).orElse("Desconocido"));

        // Datos de la fundación
        Optional<Fundacion> fundacionOpt = fundacionService.obtenerPorId(voluntario.getFundacionId());
        dto.setNombreFundacion(fundacionOpt.map(Fundacion::getNombre).orElse("Desconocida"));

        return dto;
    }
}
