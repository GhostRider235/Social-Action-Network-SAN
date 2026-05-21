package com.proyect.Social_action_networkks.controllers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
public String listarProyectos(
        @PathVariable("fundacionId") String fundacionId,
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
public String formularioNuevoProyecto(
        @PathVariable("fundacionId") String fundacionId, Model model) {
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
public String editarProyecto(
        @PathVariable("id") String id, Model model) {
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
public String terminarProyecto(
        @PathVariable("id") String id, Model model) {
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
public String verProyecto(
        @PathVariable("id") String id,
        Model model,
        HttpSession session) {

    Proyecto proyecto = proyectoService
            .obtenerPorId(id)
            .orElse(null);

    if (proyecto == null) {
        model.addAttribute(
                "error",
                "El proyecto no fue encontrado"
        );

        return "error";
    }

    // 🔹 Donaciones con usuarios, excluyendo rechazadas
    List<Donacion> donaciones =
            donacionRepository
            .findByProyectoId(id)
            .stream()
            .filter(d ->
                    !"RECHAZADA"
                    .equalsIgnoreCase(d.getEstado()))
            .collect(Collectors.toList());

    Map<Donacion, Usuario> donacionesConUsuarios =
            new LinkedHashMap<>();

    for (Donacion donacion : donaciones) {

        usuarioRepository
                .findById(donacion.getUsuarioId())
                .ifPresent(usuario ->
                        donacionesConUsuarios.put(
                                donacion,
                                usuario
                        )
                );
    }

    // 🔹 Voluntarios pendientes o aprobados
    List<Voluntario> voluntariosList =
            voluntarioService
            .obtenerPorProyecto(id)
            .stream()
            .filter(v ->
                    v.getEstado()
                    .equalsIgnoreCase("Pendiente")
                    ||
                    v.getEstado()
                    .equalsIgnoreCase("Aprobado"))
            .collect(Collectors.toList());

    // 🔹 Convertir a DTO
    List<VoluntarioDTO> voluntarios =
            voluntariosList
            .stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());

    // 🔹 Cantidad de voluntarios aprobados
    long cantidadVoluntarios =
            voluntarioRepository
            .countByFundacionIdAndEstado(
                    proyecto.getFundacionId(),
                    "Aprobado"
            );
    // 🔹 Cantidad de donaciones aprobadas
    long cantidadDonaciones =
        donacionRepository
        .countByFundacionIdAndEstado(
                proyecto.getFundacionId(),
                "APROBADA"
        );

// 🔹 Total recaudado
    BigDecimal totalRecaudado = donacionRepository
        .findByFundacionIdAndEstado(
                proyecto.getFundacionId(),
                "APROBADA"
        )
        .stream()
        .map(Donacion::getMonto)
        .reduce(BigDecimal.ZERO, BigDecimal::add);        

    // 🔹 Enviar datos al HTML
    model.addAttribute("proyecto", proyecto);

    model.addAttribute(
            "donacionesConUsuarios",
            donacionesConUsuarios
    );

    model.addAttribute(
            "voluntarios",
            voluntarios
    );

    model.addAttribute(
            "cantidadVoluntarios",
            cantidadVoluntarios
    );

    model.addAttribute(
        "cantidadDonaciones",
        cantidadDonaciones
);

    model.addAttribute(
        "totalRecaudado",
        totalRecaudado
);

    return "detalle_proyecto";
}


// 🔹 Aprobar voluntario
    @PostMapping("/voluntarios/aprobar/{id}")
public String aprobarVoluntario(
        @PathVariable("id") String id) {
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
public String rechazarVoluntario(
        @PathVariable("id") String id) {
    Voluntario voluntario = voluntarioService.obtenerPorId(id).orElse(null);
    if (voluntario != null) {
        voluntario.setEstado("Rechazado");
        voluntarioService.guardar(voluntario);
        return "redirect:/proyectos/ver/" + voluntario.getProyectoId();
    }
    return "redirect:/proyectos/todos";
}

    @PostMapping("/voluntarios/eliminar/{id}")
public String eliminarVoluntario(
        @PathVariable("id") String id, RedirectAttributes redirectAttributes) {
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
public String agregarVoluntario(
        @RequestParam("proyectoId") String proyectoId,
        @RequestParam("usuarioId") String usuarioId, Model model) {
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
public String listarTodosLosProyectos(

        @RequestParam(name = "busqueda", required = false)
        String busqueda,

        @RequestParam(name = "filtro", required = false)
        String filtro,

        @RequestParam(name = "orden", required = false)
        String orden,

        Model model,
        HttpSession session) {

    // ✅ Usuario sesión
    Usuario usuarioSesion =
            (Usuario) session.getAttribute("usuarioLogueado");

    if (usuarioSesion != null) {

        Usuario usuarioActualizado =
                usuarioRepository
                .findById(usuarioSesion.getId())
                .orElse(usuarioSesion);

        session.setAttribute(
                "usuarioLogueado",
                usuarioActualizado
        );

        model.addAttribute(
                "usuario",
                usuarioActualizado
        );
    }

    // ✅ Obtener todos los proyectos
    List<Proyecto> proyectos =
            proyectoService.obtenerTodos();

    // ✅ Buscar por proyecto o fundación
    if (busqueda != null &&
        !busqueda.trim().isEmpty()) {

        String texto =
                busqueda.toLowerCase();

        proyectos = proyectos.stream()
                .filter(proyecto -> {

                    boolean coincideProyecto =
                            proyecto.getNombre() != null &&
                            proyecto.getNombre()
                            .toLowerCase()
                            .contains(texto);

                    boolean coincideFundacion =
                            fundacionRepository
                            .findById(proyecto.getFundacionId())
                            .map(f ->
                                f.getNombre() != null &&
                                f.getNombre()
                                .toLowerCase()
                                .contains(texto)
                            )
                            .orElse(false);

                    return coincideProyecto
                            || coincideFundacion;
                })
                .collect(Collectors.toList());
    }

    // ✅ ORDENAMIENTO AUTOMÁTICO
    if (orden != null &&
        !orden.isEmpty()) {

        switch (orden) {

            case "masReciente":

                proyectos.sort((a, b) ->
                        b.getFechaInicio()
                        .compareTo(a.getFechaInicio()));

                break;

            case "masDonaciones":

                proyectos.sort((a, b) -> {

                    BigDecimal totalA =
                            donacionRepository
                            .findByProyectoId(a.getId())
                            .stream()
                            .filter(d ->
                                    "APROBADA"
                                    .equalsIgnoreCase(d.getEstado()))
                            .map(Donacion::getMonto)
                            .reduce(BigDecimal.ZERO,
                                    BigDecimal::add);

                    BigDecimal totalB =
                            donacionRepository
                            .findByProyectoId(b.getId())
                            .stream()
                            .filter(d ->
                                    "APROBADA"
                                    .equalsIgnoreCase(d.getEstado()))
                            .map(Donacion::getMonto)
                            .reduce(BigDecimal.ZERO,
                                    BigDecimal::add);

                    return totalB.compareTo(totalA);
                });

                break;

            case "masVoluntarios":

                proyectos.sort((a, b) -> {

                    long totalA =
                            voluntarioRepository
                            .findByProyectoId(a.getId())
                            .stream()
                            .filter(v ->
                                    "Aprobado"
                                    .equalsIgnoreCase(v.getEstado()))
                            .count();

                    long totalB =
                            voluntarioRepository
                            .findByProyectoId(b.getId())
                            .stream()
                            .filter(v ->
                                    "Aprobado"
                                    .equalsIgnoreCase(v.getEstado()))
                            .count();

                    return Long.compare(totalB, totalA);
                });

                break;
        }
    }

    // ✅ FILTROS ESPECIALES
    if (filtro != null &&
        !filtro.isEmpty() &&
        !proyectos.isEmpty()) {

        Proyecto proyectoResultado = null;

        switch (filtro) {

            case "masDonaciones":

                proyectoResultado =
                        proyectos.stream()
                        .max((p1, p2) -> {

                            BigDecimal total1 =
                                    donacionRepository
                                    .findByProyectoId(p1.getId())
                                    .stream()
                                    .filter(d ->
                                            "APROBADA"
                                            .equalsIgnoreCase(d.getEstado()))
                                    .map(Donacion::getMonto)
                                    .reduce(BigDecimal.ZERO,
                                            BigDecimal::add);

                            BigDecimal total2 =
                                    donacionRepository
                                    .findByProyectoId(p2.getId())
                                    .stream()
                                    .filter(d ->
                                            "APROBADA"
                                            .equalsIgnoreCase(d.getEstado()))
                                    .map(Donacion::getMonto)
                                    .reduce(BigDecimal.ZERO,
                                            BigDecimal::add);

                            return total1.compareTo(total2);
                        })
                        .orElse(null);

                break;

            case "menosDonaciones":

                proyectoResultado =
                        proyectos.stream()
                        .min((p1, p2) -> {

                            BigDecimal total1 =
                                    donacionRepository
                                    .findByProyectoId(p1.getId())
                                    .stream()
                                    .filter(d ->
                                            "APROBADA"
                                            .equalsIgnoreCase(d.getEstado()))
                                    .map(Donacion::getMonto)
                                    .reduce(BigDecimal.ZERO,
                                            BigDecimal::add);

                            BigDecimal total2 =
                                    donacionRepository
                                    .findByProyectoId(p2.getId())
                                    .stream()
                                    .filter(d ->
                                            "APROBADA"
                                            .equalsIgnoreCase(d.getEstado()))
                                    .map(Donacion::getMonto)
                                    .reduce(BigDecimal.ZERO,
                                            BigDecimal::add);

                            return total1.compareTo(total2);
                        })
                        .orElse(null);

                break;

            case "masVoluntarios":

                proyectoResultado =
                        proyectos.stream()
                        .max((p1, p2) -> {

                            long total1 =
                                    voluntarioRepository
                                    .findByProyectoId(p1.getId())
                                    .stream()
                                    .filter(v ->
                                            "Aprobado"
                                            .equalsIgnoreCase(v.getEstado()))
                                    .count();

                            long total2 =
                                    voluntarioRepository
                                    .findByProyectoId(p2.getId())
                                    .stream()
                                    .filter(v ->
                                            "Aprobado"
                                            .equalsIgnoreCase(v.getEstado()))
                                    .count();

                            return Long.compare(total1, total2);
                        })
                        .orElse(null);

                break;

            case "menosVoluntarios":

                proyectoResultado =
                        proyectos.stream()
                        .min((p1, p2) -> {

                            long total1 =
                                    voluntarioRepository
                                    .findByProyectoId(p1.getId())
                                    .stream()
                                    .filter(v ->
                                            "Aprobado"
                                            .equalsIgnoreCase(v.getEstado()))
                                    .count();

                            long total2 =
                                    voluntarioRepository
                                    .findByProyectoId(p2.getId())
                                    .stream()
                                    .filter(v ->
                                            "Aprobado"
                                            .equalsIgnoreCase(v.getEstado()))
                                    .count();

                            return Long.compare(total1, total2);
                        })
                        .orElse(null);

                break;
        }

        if (proyectoResultado != null) {

            proyectos = List.of(proyectoResultado);

        } else {

            proyectos = new ArrayList<>();
        }
    }

    // ✅ MAPAS
    Map<String, String> fundacionIdANombre =
            new HashMap<>();

    Map<String, String> fundacionIdALogo =
            new HashMap<>();

    Map<String, BigDecimal> dineroPorProyecto =
            new HashMap<>();

    Map<String, Integer> voluntariosPorProyecto =
            new HashMap<>();

    // ✅ RECORRER PROYECTOS
    for (Proyecto proyecto : proyectos) {

        // 🔹 Fundación
        fundacionRepository
                .findById(proyecto.getFundacionId())
                .ifPresent(f -> {

                    fundacionIdANombre.put(
                            proyecto.getFundacionId(),
                            f.getNombre()
                    );

                    if (f.getLogo() != null &&
                        !f.getLogo().isEmpty()) {

                        fundacionIdALogo.put(
                                proyecto.getFundacionId(),
                                "/uploads/logos/" + f.getLogo()
                        );

                    } else {

                        fundacionIdALogo.put(
                                proyecto.getFundacionId(),
                                "/img/logo_fundacion_default.png"
                        );
                    }
                });

        // 🔹 Donaciones aprobadas
        BigDecimal totalDonaciones =
                donacionRepository
                .findByProyectoId(proyecto.getId())
                .stream()
                .filter(d ->
                        "APROBADA"
                        .equalsIgnoreCase(d.getEstado()))
                .map(Donacion::getMonto)
                .reduce(BigDecimal.ZERO,
                        BigDecimal::add);

        dineroPorProyecto.put(
                proyecto.getId(),
                totalDonaciones
        );

        // 🔹 Voluntarios aprobados
        int totalVoluntarios =
                (int) voluntarioRepository
                .findByProyectoId(proyecto.getId())
                .stream()
                .filter(v ->
                        "Aprobado"
                        .equalsIgnoreCase(v.getEstado()))
                .count();

        voluntariosPorProyecto.put(
                proyecto.getId(),
                totalVoluntarios
        );
    }

    // ✅ ENVIAR DATOS
    model.addAttribute("proyectos", proyectos);

    model.addAttribute(
            "fundacionIdANombre",
            fundacionIdANombre
    );

    model.addAttribute(
            "fundacionIdALogo",
            fundacionIdALogo
    );

    model.addAttribute(
            "dineroPorProyecto",
            dineroPorProyecto
    );

    model.addAttribute(
            "voluntariosPorProyecto",
            voluntariosPorProyecto
    );

    model.addAttribute(
            "busqueda",
            busqueda
    );

    model.addAttribute(
            "filtro",
            filtro
    );

    model.addAttribute(
            "orden",
            orden
    );

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
