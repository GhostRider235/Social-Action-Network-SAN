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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.proyect.Social_action_networkks.repository.FundacionRepository;
import com.proyect.Social_action_networkks.repository.UsuarioRepository;

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

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private FundacionRepository fundacionRepository;

    public VoluntarioController(
            VoluntarioService voluntarioService) {

        this.voluntarioService = voluntarioService;
    }

    // ✅ Obtener todos los voluntarios (JSON)
    @GetMapping
    @ResponseBody
    public List<VoluntarioDTO> obtenerTodos() {

        return voluntarioService.obtenerTodos()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // ✅ Obtener voluntario por ID (JSON)
    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<VoluntarioDTO> obtenerPorId(
            @PathVariable("id") String id) {

        return voluntarioService.obtenerPorId(id)
                .map(v -> ResponseEntity.ok(
                        convertirADTO(v)))
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ Obtener voluntarios por proyecto (JSON)
    @GetMapping("/proyecto/{proyectoId}")
    @ResponseBody
    public List<VoluntarioDTO> obtenerPorProyecto(
            @PathVariable("proyectoId") String proyectoId) {

        return voluntarioService
                .obtenerPorProyecto(proyectoId)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // ✅ Postulación de voluntario
    @PostMapping
    @ResponseBody
    public ResponseEntity<?> guardar(
            @RequestBody VoluntarioDTO voluntarioDTO,
            Principal principal) {

        Usuario usuario = usuarioRepository
        .findByEmail(principal.getName())
        .orElse(null);

        if (usuario == null) {

            return ResponseEntity
                    .status(401)
                    .body("⚠️ Debes iniciar sesión para postularte.");
        }

        // 🔒 Verificar si ya está postulado
        if (voluntarioService.existePorUsuarioYProyecto(
                usuario.getId(),
                voluntarioDTO.getProyectoId())) {

            return ResponseEntity
                    .badRequest()
                    .body("Ya estás postulado a este proyecto.");
        }

        // 🔒 Buscar proyecto
        Optional<Proyecto> proyectoOpt =
                proyectoService.obtenerPorId(
                        voluntarioDTO.getProyectoId());

        if (proyectoOpt.isEmpty()) {

            return ResponseEntity
                    .badRequest()
                    .body("❌ El proyecto no fue encontrado.");
        }

        Proyecto proyecto = proyectoOpt.get();

        // 🔹 Crear voluntario
        Voluntario voluntario = new Voluntario();

        voluntario.setUsuarioId(usuario.getId());

        voluntario.setProyectoId(
                voluntarioDTO.getProyectoId()
        );

        voluntario.setFundacionId(
                proyecto.getFundacionId()
        );

        voluntario.setComentario(
                voluntarioDTO.getComentario()
        );

        voluntario.setFechaAsignacion(
                voluntarioDTO.getFechaAsignacion()
        );

        voluntario.setEstado("Pendiente");

        Voluntario guardado =
                voluntarioService.guardar(voluntario);

        return ResponseEntity.ok(
                convertirADTO(guardado)
        );
    }

    // ✅ Actualizar postulación
    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> actualizar(
            @PathVariable("id") String id,
            @RequestBody VoluntarioDTO voluntarioDTO) {

        Optional<Voluntario> voluntarioOpt =
                voluntarioService.obtenerPorId(id);

        if (voluntarioOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Voluntario voluntario = voluntarioOpt.get();

        voluntario.setComentario(
                voluntarioDTO.getComentario()
        );

        voluntario.setFechaAsignacion(
                voluntarioDTO.getFechaAsignacion()
        );

        Voluntario actualizado =
                voluntarioService.guardar(voluntario);

        return ResponseEntity.ok(
                convertirADTO(actualizado)
        );
    }

    @PostMapping("/aprobar/{id}")
public String aprobarVoluntario(
        @PathVariable("id") String id) {

    Voluntario voluntario =
            voluntarioRepository.findById(id)
            .orElse(null);

    if (voluntario == null) {
        return "redirect:/proyectos/todos";
    }

    // Evitar aprobar dos veces
    if ("Aprobado".equalsIgnoreCase(voluntario.getEstado())) {
        return "redirect:/proyectos/ver/"
                + voluntario.getProyectoId();
    }

    voluntario.setEstado("Aprobado");

    voluntarioRepository.save(voluntario);

    // 🔹 Buscar fundación
    Fundacion fundacion =
            fundacionRepository.findById(
                    voluntario.getFundacionId())
            .orElse(null);

    if (fundacion != null) {

    // Si es null
    if (fundacion.getCantidadVoluntarios() == null) {
        fundacion.setCantidadVoluntarios(0);
    }

    // Sumar +1
    fundacion.setCantidadVoluntarios(
            fundacion.getCantidadVoluntarios() + 1
    );

    fundacionRepository.save(fundacion);
}

    return "redirect:/proyectos/ver/"
            + voluntario.getProyectoId();
}

    @PostMapping("/rechazar/{id}")
    public String rechazarVoluntario(
            @PathVariable("id") String id) {

        Voluntario voluntario =
                voluntarioRepository.findById(id)
                .orElse(null);

        if (voluntario == null) {
            return "redirect:/proyectos/todos";
        }

        voluntario.setEstado("Rechazado");

        voluntarioRepository.save(voluntario);

        return "redirect:/proyectos/ver/"
                + voluntario.getProyectoId();
    }

    // ✅ Eliminar voluntario
    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<String> eliminarVoluntario(
            @PathVariable("id") String id) {

        if (!voluntarioRepository.existsById(id)) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Voluntario no encontrado");
        }

        voluntarioRepository.deleteById(id);

        return ResponseEntity.ok(
                "Eliminado correctamente"
        );
    }

    // 🔹 Conversión entidad → DTO
    private VoluntarioDTO convertirADTO(
            Voluntario voluntario) {

        VoluntarioDTO dto = new VoluntarioDTO();

        dto.setId(voluntario.getId());
        dto.setUsuarioId(voluntario.getUsuarioId());
        dto.setProyectoId(voluntario.getProyectoId());
        dto.setFundacionId(voluntario.getFundacionId());
        dto.setFechaAsignacion(
                voluntario.getFechaAsignacion());

        dto.setComentario(
                voluntario.getComentario());

        dto.setEstado(
                voluntario.getEstado());

        // 🔹 Datos usuario
        Optional<Usuario> usuarioOpt =
                Optional.ofNullable(
                        usuarioService.findById(
                                voluntario.getUsuarioId()
                        )
                );

        usuarioOpt.ifPresentOrElse(u -> {

            dto.setNombreUsuario(u.getNombre());

            dto.setTelefonoUsuario(
                    u.getTelefono() != null
                            ? u.getTelefono()
                            : "No disponible"
            );

            dto.setCorreoUsuario(
                    u.getEmail() != null
                            ? u.getEmail()
                            : "No disponible"
            );

        }, () -> {

            dto.setNombreUsuario("Desconocido");
            dto.setTelefonoUsuario("No disponible");
            dto.setCorreoUsuario("No disponible");
        });

        // 🔹 Datos proyecto
        Optional<Proyecto> proyectoOpt =
                proyectoService.obtenerPorId(
                        voluntario.getProyectoId());

        dto.setNombreProyecto(
                proyectoOpt.map(Proyecto::getNombre)
                        .orElse("Desconocido")
        );

        // 🔹 Datos fundación
        Optional<Fundacion> fundacionOpt =
                fundacionService.obtenerPorId(
                        voluntario.getFundacionId());

        dto.setNombreFundacion(
                fundacionOpt.map(Fundacion::getNombre)
                        .orElse("Desconocida")
        );

        return dto;
    }
}