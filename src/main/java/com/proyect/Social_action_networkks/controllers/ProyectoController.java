package com.proyect.Social_action_networkks.controllers;

import com.proyect.Social_action_networkks.dto.VoluntarioDTO;
import com.proyect.Social_action_networkks.modelo.Donacion;
import com.proyect.Social_action_networkks.modelo.Proyecto;
import com.proyect.Social_action_networkks.modelo.Usuario;
import com.proyect.Social_action_networkks.modelo.Voluntario;
import com.proyect.Social_action_networkks.repository.DonacionRepository;
import com.proyect.Social_action_networkks.repository.FundacionRepository;
import com.proyect.Social_action_networkks.repository.UsuarioRepository;
import com.proyect.Social_action_networkks.repository.VoluntarioRepository;
import com.proyect.Social_action_networkks.servicio.ProyectoService;
import com.proyect.Social_action_networkks.servicio.VoluntarioService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/proyectos")
public class ProyectoController {

    @Autowired
    private ProyectoService proyectoService;

    @Autowired
    private VoluntarioService voluntarioService;

    @Autowired
    private DonacionRepository donacionRepository;

    @Autowired
    private FundacionRepository fundacionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private VoluntarioRepository voluntarioRepository;

    // 🔹 Listar proyectos de una fundación
    @GetMapping("/fundacion/{fundacionId}")
    public String listarProyectos(@PathVariable String fundacionId,
                                  @RequestParam(name = "busqueda", required = false) String busqueda,
                                  Model model) {
        List<Proyecto> proyectos = (busqueda != null && !busqueda.trim().isEmpty())
                ? proyectoService.buscarPorNombreYFundacion(busqueda, fundacionId)
                : proyectoService.obtenerPorFundacion(fundacionId);

        model.addAttribute("proyectos", proyectos);
        model.addAttribute("fundacionId", fundacionId);
        model.addAttribute("busqueda", busqueda);
        return "lista_proyectos";
    }

    // 🔹 Formulario nuevo proyecto
    @GetMapping("/nuevo/{fundacionId}")
    public String formularioNuevoProyecto(@PathVariable String fundacionId, Model model) {
        Proyecto proyecto = new Proyecto();
        proyecto.setFundacionId(fundacionId);
        proyecto.setVoluntariosIds(new ArrayList<>());
        model.addAttribute("proyecto", proyecto);
        return "nuevo_proyecto";
    }

    // 🔹 Guardar proyecto
    @PostMapping("/guardar")
    public String guardarProyecto(@Valid @ModelAttribute Proyecto proyecto, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "nuevo_proyecto";
        }

        proyecto.setEstado("ACTIVO");
        if (proyecto.getFechaInicio() == null) {
            proyecto.setFechaInicio(new Date());
        }

        if (proyecto.getVoluntariosIds() == null) {
            proyecto.setVoluntariosIds(new ArrayList<>());
        }

        proyectoService.guardar(proyecto);
        return "redirect:/proyectos/fundacion/" + proyecto.getFundacionId();
    }

    // 🔹 Editar proyecto
    @GetMapping("/editar/{id}")
    public String editarProyecto(@PathVariable String id, Model model) {
        Proyecto proyecto = proyectoService.obtenerPorId(id).orElse(null);
        if (proyecto == null) {
            model.addAttribute("error", "El proyecto no fue encontrado");
            return "error";
        }
        model.addAttribute("proyecto", proyecto);
        return "editar_proyecto";
    }

    // 🔹 Actualizar proyecto
    @PostMapping("/actualizar")
    public String actualizarProyecto(@Valid @ModelAttribute Proyecto proyecto, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "editar_proyecto";
        }

        Proyecto existente = proyectoService.obtenerPorId(proyecto.getId()).orElse(null);
        if (existente == null) {
            model.addAttribute("error", "El proyecto no existe");
            return "error";
        }

        proyecto.setVoluntariosIds(existente.getVoluntariosIds() != null ? existente.getVoluntariosIds() : new ArrayList<>());
        proyectoService.guardar(proyecto);
        return "redirect:/proyectos/fundacion/" + proyecto.getFundacionId();
    }

    // 🔹 Terminar proyecto
    @PostMapping("/terminar/{id}")
    public String terminarProyecto(@PathVariable String id, Model model) {
        Proyecto proyecto = proyectoService.obtenerPorId(id).orElse(null);
        if (proyecto == null) {
            model.addAttribute("error", "El proyecto no fue encontrado");
            return "error";
        }

        if (!"ACTIVO".equalsIgnoreCase(proyecto.getEstado())) {
            model.addAttribute("error", "Solo los proyectos activos pueden ser terminados");
            return "error";
        }

        proyecto.setEstado("TERMINADO");
        proyecto.setFechaFin(new Date());
        proyectoService.guardar(proyecto);
        return "redirect:/proyectos/fundacion/" + proyecto.getFundacionId();
    }

    // 🔹 Ver detalles del proyecto (solo fundación, mostrando voluntarios en espera o aprobados)
@GetMapping("/ver/{id}")
public String verProyecto(@PathVariable String id, Model model, HttpSession session) {
    Proyecto proyecto = proyectoService.obtenerPorId(id).orElse(null);
    if (proyecto == null) {
        model.addAttribute("error", "El proyecto no fue encontrado");
        return "error";
    }

    // Donaciones con usuarios, excluyendo las rechazadas
    List<Donacion> donaciones = donacionRepository.findByProyectoId(id).stream()
            .filter(d -> !"RECHAZADA".equalsIgnoreCase(d.getEstado())) // <-- filtra rechazadas
            .collect(Collectors.toList());

    Map<Donacion, Usuario> donacionesConUsuarios = new LinkedHashMap<>();
    for (Donacion donacion : donaciones) {
        usuarioRepository.findById(donacion.getUsuarioId())
                .ifPresent(usuario -> donacionesConUsuarios.put(donacion, usuario));
    }

    // 🔹 Filtrar solo voluntarios en espera o aprobados
    List<Voluntario> voluntariosList = voluntarioService.obtenerPorProyecto(id).stream()
            .filter(v -> v.getEstado().equalsIgnoreCase("Pendiente") ||
                         v.getEstado().equalsIgnoreCase("Aprobado"))
            .collect(Collectors.toList());

    // Convertir a DTO
    List<VoluntarioDTO> voluntarios = voluntariosList.stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());

    model.addAttribute("proyecto", proyecto);
    model.addAttribute("donacionesConUsuarios", donacionesConUsuarios);
    model.addAttribute("voluntarios", voluntarios);

    // ✅ Ya no se valida el rol, porque esta vista es exclusiva de fundaciones
    return "detalle_proyecto";
}


// 🔹 Aprobar voluntario
@PostMapping("/voluntarios/aprobar/{id}")
public String aprobarVoluntario(@PathVariable String id) {
    Voluntario voluntario = voluntarioService.obtenerPorId(id).orElse(null);
    if (voluntario != null) {
        voluntario.setEstado("Aprobado");
        voluntarioService.guardar(voluntario);
        return "redirect:/proyectos/ver/" + voluntario.getProyectoId();
    }
    return "redirect:/proyectos/todos";
}

// 🔹 Rechazar voluntario
@PostMapping("/voluntarios/rechazar/{id}")
public String rechazarVoluntario(@PathVariable String id) {
    Voluntario voluntario = voluntarioService.obtenerPorId(id).orElse(null);
    if (voluntario != null) {
        voluntario.setEstado("Rechazado");
        voluntarioService.guardar(voluntario);
        return "redirect:/proyectos/ver/" + voluntario.getProyectoId();
    }
    return "redirect:/proyectos/todos";
}

@PostMapping("/voluntarios/eliminar/{id}")
public String eliminarVoluntario(@PathVariable String id, RedirectAttributes redirectAttributes) {
    try {
        // Busca el voluntario por ID
        Optional<Voluntario> voluntarioOpt = voluntarioRepository.findById(id);
        if (voluntarioOpt.isPresent()) {
            Voluntario voluntario = voluntarioOpt.get();

            // Lo eliminamos del repositorio
            voluntarioRepository.delete(voluntario);

            redirectAttributes.addFlashAttribute("mensajeExito", "Voluntario eliminado correctamente.");
        } else {
            redirectAttributes.addFlashAttribute("mensajeError", "El voluntario no existe.");
        }
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("mensajeError", "Error al eliminar el voluntario: " + e.getMessage());
    }

    // Redirige nuevamente al detalle del proyecto
    return "redirect:/proyectos/ver/" + id;
}


    // 🔹 Agregar voluntario a proyecto
    @PostMapping("/agregar-voluntario")
    public String agregarVoluntario(@RequestParam String proyectoId, @RequestParam String usuarioId, Model model) {
        Proyecto proyecto = proyectoService.obtenerPorId(proyectoId).orElse(null);
        if (proyecto == null) {
            model.addAttribute("error", "Proyecto no encontrado");
            return "error";
        }

        if (proyecto.getVoluntariosIds() == null) {
            proyecto.setVoluntariosIds(new ArrayList<>());
        }

        if (!proyecto.getVoluntariosIds().contains(usuarioId)) {
            proyecto.getVoluntariosIds().add(usuarioId);
            proyectoService.guardar(proyecto);
        }

        return "redirect:/proyectos/ver/" + proyectoId;
    }

    // 🔹 Mostrar proyectos en el index
    @GetMapping("/inicio")
    public String mostrarIndex(Model model) {
        List<Proyecto> proyectos = proyectoService.obtenerTodos();
        model.addAttribute("proyectos", proyectos);
        return "index";
    }

    // 🔹 Listar todos los proyectos
    @GetMapping("/todos")
public String listarTodosLosProyectos(@RequestParam(required = false) String nombre,
                                      Model model,
                                      HttpSession session) {
    // ✅ 1. Refrescar usuario logueado desde la base de datos
    Usuario usuarioSesion = (Usuario) session.getAttribute("usuarioLogueado");
    if (usuarioSesion != null) {
        Usuario usuarioActualizado = usuarioRepository.findById(usuarioSesion.getId()).orElse(usuarioSesion);
        session.setAttribute("usuarioLogueado", usuarioActualizado);
        model.addAttribute("usuario", usuarioActualizado);
    }

    // ✅ 2. Obtener proyectos
    List<Proyecto> proyectos = (nombre != null && !nombre.trim().isEmpty())
            ? proyectoService.buscarPorNombre(nombre)
            : proyectoService.obtenerTodos();

    Map<String, String> fundacionIdANombre = new HashMap<>();
    Map<String, String> fundacionIdALogo = new HashMap<>();

    // ✅ 3. Asociar fundaciones con sus logos/nombres
    for (Proyecto proyecto : proyectos) {
        fundacionRepository.findById(proyecto.getFundacionId()).ifPresent(f -> {
            fundacionIdANombre.put(proyecto.getFundacionId(), f.getNombre());
            if (f.getLogo() != null && f.getLogo().length > 0) {
                String logoBase64 = "data:image/png;base64," + Base64.getEncoder().encodeToString(f.getLogo());
                fundacionIdALogo.put(proyecto.getFundacionId(), logoBase64);
            } else {
                fundacionIdALogo.put(proyecto.getFundacionId(), "/img/logo_fundacion_default.png");
            }
        });
    }

    // ✅ 4. Pasar todo al modelo
    model.addAttribute("proyectos", proyectos);
    model.addAttribute("nombre", nombre);
    model.addAttribute("fundacionIdANombre", fundacionIdANombre);
    model.addAttribute("fundacionIdALogo", fundacionIdALogo);

    return "lista_todos_proyectos";
}


    // 🔹 Convertir Voluntario → DTO
    private VoluntarioDTO convertirADTO(Voluntario voluntario) {
        VoluntarioDTO dto = new VoluntarioDTO();
        dto.setId(voluntario.getId());
        dto.setUsuarioId(voluntario.getUsuarioId());
        dto.setProyectoId(voluntario.getProyectoId());
        dto.setFundacionId(voluntario.getFundacionId());
        dto.setFechaAsignacion(voluntario.getFechaAsignacion());
        dto.setComentario(voluntario.getComentario());
        dto.setEstado(voluntario.getEstado());


        Optional<Usuario> usuarioOpt = usuarioRepository.findById(voluntario.getUsuarioId());
        usuarioOpt.ifPresentOrElse(u -> {
            dto.setNombreUsuario(u.getNombre());
            dto.setTelefonoUsuario(u.getTelefono() != null ? u.getTelefono() : "No disponible");
            dto.setCorreoUsuario(u.getEmail() != null ? u.getEmail() : "No disponible");
        }, () -> {
            dto.setNombreUsuario("Desconocido");
            dto.setTelefonoUsuario("No disponible");
            dto.setCorreoUsuario("No disponible");
        });

        return dto;
    }
    // 🔹 Mostrar proyectos en la vista del panel de admin
@GetMapping("/admin")
public String listarProyectosAdmin(Model model) {
    List<Proyecto> proyectos = proyectoService.obtenerTodos();

    for (Proyecto proyecto : proyectos) {
        // Asignar nombre de la fundación
        if (proyecto.getFundacionId() != null) {
            fundacionRepository.findById(proyecto.getFundacionId())
                .ifPresentOrElse(
                    f -> proyecto.setNombreFundacion(f.getNombre()),
                    () -> proyecto.setNombreFundacion("Fundación no encontrada")
                );
        } else {
            proyecto.setNombreFundacion("Sin fundación");
        }

        // Calcular duración
        if (proyecto.getFechaInicio() != null) {
            Date fechaFin = proyecto.getFechaFin() != null ? proyecto.getFechaFin() : new Date();
            long diffDias = (fechaFin.getTime() - proyecto.getFechaInicio().getTime()) / (1000 * 60 * 60 * 24);
            proyecto.setDuracion(diffDias);
        } else {
            proyecto.setDuracion(null);
        }
    }

    model.addAttribute("proyectos", proyectos);
    return "admin_proyectos";
}

}
