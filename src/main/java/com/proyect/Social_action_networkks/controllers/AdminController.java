package com.proyect.Social_action_networkks.controllers;

import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.proyect.Social_action_networkks.dto.VoluntarioDTO;
import com.proyect.Social_action_networkks.modelo.Donacion;
import com.proyect.Social_action_networkks.modelo.Fundacion;
import com.proyect.Social_action_networkks.modelo.Proyecto;
import com.proyect.Social_action_networkks.modelo.Recarga;
import com.proyect.Social_action_networkks.modelo.Usuario;
import com.proyect.Social_action_networkks.modelo.Voluntario;
import com.proyect.Social_action_networkks.repository.DonacionRepository;
import com.proyect.Social_action_networkks.repository.FundacionRepository;
import com.proyect.Social_action_networkks.repository.ProyectoRepository;
import com.proyect.Social_action_networkks.repository.RecargaRepository;
import com.proyect.Social_action_networkks.repository.UsuarioRepository;
import com.proyect.Social_action_networkks.servicio.FundacionService;
import com.proyect.Social_action_networkks.servicio.ProyectoService;
import com.proyect.Social_action_networkks.servicio.UsuarioServicio;
import com.proyect.Social_action_networkks.servicio.VoluntarioService;

import jakarta.servlet.http.HttpSession;

@Controller
public class AdminController {

    @Autowired
    private FundacionRepository fundacionRepository;

    @Autowired
    private RecargaRepository recargaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private DonacionRepository donacionRepository;

    @Autowired
    private ProyectoRepository proyectoRepository;



    @Autowired
    private VoluntarioService voluntarioService;

    @Autowired
    private UsuarioServicio usuarioService;

    @Autowired
    private FundacionService fundacionService;

    @Autowired
    private ProyectoService proyectoService;

    @GetMapping("/admin_dashboard")
    public String dashboard(Model model) {
        // Crear un "admin simulado" si no hay sistema de login
        Usuario admin = new Usuario();
        admin.setNombre("Administrador General");
        model.addAttribute("usuario", admin);

        return "admin_dashboard";
    }

    @GetMapping("/admin/fundaciones/pendientes")
    public String fundacionesPendientes(Model model) {
        Usuario admin = new Usuario();
        admin.setNombre("Administrador General");
        model.addAttribute("usuario", admin);

        List<Fundacion> fundacionesPendientes = fundacionRepository.findAll()
                .stream()
                .filter(f -> "PENDIENTE".equalsIgnoreCase(f.getEstado()))
                .toList();

        model.addAttribute("fundacionesPendientes", fundacionesPendientes);
        return "admin_fundaciones_pendientes";
    }

    @GetMapping("/admin/recargas/pendientes")
    public String recargasPendientes(Model model) {
        Usuario admin = new Usuario();
        admin.setNombre("Administrador General");
        model.addAttribute("usuario", admin);

        List<Recarga> recargasPendientes = recargaRepository.findByEstado("PENDIENTE");
        model.addAttribute("recargasPendientes", recargasPendientes);

        return "admin_recargas";
    }


    @PostMapping("/admin/aprobar/{id}")
public String aprobarFundacion(@PathVariable("id") String id) {
        fundacionRepository.findById(id).ifPresent(fundacion -> {
            fundacion.setEstado("APROBADA");
            fundacionRepository.save(fundacion);
        });
        return "redirect:/admin/fundaciones/pendientes";
    }

    @PostMapping("/admin/rechazar/{id}")
public String rechazarFundacion(@PathVariable("id") String id) {
        fundacionRepository.findById(id).ifPresent(fundacion -> {
            fundacion.setEstado("RECHAZADA");
            fundacionRepository.save(fundacion);
        });
        return "redirect:/admin/fundaciones/pendientes";
    }

    @PostMapping("/admin/recarga/aprobar/{id}")
public String aprobarRecarga(@PathVariable("id") String id, HttpSession session) {
    recargaRepository.findById(id).ifPresent(recarga -> {
        Usuario usuario = usuarioRepository.findById(recarga.getUsuarioId()).orElse(null);
        if (usuario != null) {
            usuario.setSaldo(usuario.getSaldo().add(recarga.getMonto()));
            usuarioRepository.save(usuario);

            Usuario usuarioSesion = (Usuario) session.getAttribute("usuarioLogueado");
            if (usuarioSesion != null && usuarioSesion.getId().equals(usuario.getId())) {
                Usuario usuarioActualizado = usuarioRepository.findById(usuario.getId()).orElse(usuario);
                session.setAttribute("usuarioLogueado", usuarioActualizado);
            }
        }
        recarga.setEstado("APROBADA");
        recargaRepository.save(recarga);
    });
    return "redirect:/admin/recargas/pendientes";
}


    @PostMapping("/admin/recarga/rechazar/{id}")
public String rechazarRecarga(@PathVariable("id") String id) {
        recargaRepository.findById(id).ifPresent(recarga -> {
            recarga.setEstado("RECHAZADA");
            recargaRepository.save(recarga);
        });
        return "redirect:/admin/recargas/pendientes";
    }
    @GetMapping("/admin/donaciones")
public String verDonaciones(Model model, Principal principal) {
    if (principal != null) {
        String email = principal.getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
        model.addAttribute("usuario", usuario);
    }

    // Obtener todas las donaciones
    List<Donacion> donaciones = donacionRepository.findAll();

    model.addAttribute("donaciones", donaciones);
    return "admin_donaciones"; // nombre del HTML que crearás
}

@GetMapping("/admin/proyectos")
public String verProyectos(Model model, Principal principal) {
    if (principal != null) {
        String email = principal.getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
        model.addAttribute("usuario", usuario);
    }

    List<Proyecto> proyectos = proyectoRepository.findAll();

    for (Proyecto proyecto : proyectos) {
        if (proyecto.getFundacionId() != null) {
            fundacionRepository.findById(proyecto.getFundacionId())
                .ifPresentOrElse(
                    f -> proyecto.setNombreFundacion(f.getNombre()),
                    () -> proyecto.setNombreFundacion("Fundación no encontrada")
                );
        } else {
            proyecto.setNombreFundacion("Sin fundación");
        }

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


    @GetMapping("/admin/voluntarios")
public String verVoluntarios(Model model) {
    List<VoluntarioDTO> voluntariosDTO = voluntarioService.obtenerTodos()
            .stream()
            .map(this::convertirADTO)
            .filter(v -> "Pendiente".equals(v.getEstado()) || "Aprobado".equals(v.getEstado()))
            .collect(Collectors.toList());

    model.addAttribute("voluntarios", voluntariosDTO);
    return "admin_voluntarios"; // tu template Thymeleaf
}


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