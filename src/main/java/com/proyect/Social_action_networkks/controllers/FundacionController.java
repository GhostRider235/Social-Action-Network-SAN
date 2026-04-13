package com.proyect.Social_action_networkks.controllers;

import com.proyect.Social_action_networkks.dto.FundacionDTO;
import com.proyect.Social_action_networkks.modelo.Donacion;
import com.proyect.Social_action_networkks.modelo.Fundacion;
import com.proyect.Social_action_networkks.modelo.Proyecto;
import com.proyect.Social_action_networkks.modelo.Voluntario;
import com.proyect.Social_action_networkks.repository.DonacionRepository;
import com.proyect.Social_action_networkks.repository.FundacionRepository;
import com.proyect.Social_action_networkks.repository.ProyectoRepository;
import com.proyect.Social_action_networkks.repository.UsuarioRepository;
import com.proyect.Social_action_networkks.repository.VoluntarioRepository;
import com.proyect.Social_action_networkks.servicio.ProyectoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/fundacion")
public class FundacionController {

    @Autowired
    private FundacionRepository fundacionRepository;

    @Autowired
    private ProyectoService proyectoService;

    @Autowired
    private DonacionRepository donacionRepository;

    @Autowired
    private ProyectoRepository proyectoRepository;

    @Autowired
    private VoluntarioRepository voluntarioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Mostrar formulario de registro
    @GetMapping("/register")
    public String mostrarFormularioFundacion(Model model) {
        model.addAttribute("fundacion", new FundacionDTO());
        return "register_fundacion";
    }

    // Procesar registro
    @PostMapping("/register")
    public String registrarFundacion(@ModelAttribute FundacionDTO dto, Model model) {
        if (fundacionRepository.findByCorreo(dto.getCorreo()).isPresent()) {
            model.addAttribute("error", "El correo ya está registrado");
            return "register_fundacion";
        }

        if (!dto.getContrasena().equals(dto.getConfirmarContrasena())) {
            model.addAttribute("error", "Las contraseñas no coinciden");
            return "register_fundacion";
        }

        Fundacion fundacion = new Fundacion();
        fundacion.setNombre(dto.getNombre());
        fundacion.setDescripcion(dto.getDescripcion());
        fundacion.setContacto(dto.getContacto());
        fundacion.setUbicacion(dto.getUbicacion());
        fundacion.setCorreo(dto.getCorreo());
        fundacion.setEstado("PENDIENTE");

        // Encriptar contraseña
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        fundacion.setContrasena(encoder.encode(dto.getContrasena()));

        // Subida de logo
        try {
            if (dto.getLogo() != null && !dto.getLogo().isEmpty()) {
                fundacion.setLogo(dto.getLogo().getBytes());
            }
        } catch (Exception e) {
            model.addAttribute("error", "Error al subir el logo");
            return "register_fundacion";
        }

        fundacionRepository.save(fundacion);
        return "redirect:/fundacion/login";
    }

    // Mostrar login
    @GetMapping("/login")
    public String mostrarLoginFundacion() {
        return "login_fundacion";
    }

    // Procesar login
    @PostMapping("/login")
public String loginFundacion(@RequestParam String correo,
                             @RequestParam String contrasena,
                             HttpSession session,
                             Model model) {

    Fundacion fundacion = fundacionRepository.findByCorreo(correo).orElse(null);
    if (fundacion == null) {
        model.addAttribute("error", "Credenciales incorrectas");
        return "login_fundacion";
    }

    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    if (!encoder.matches(contrasena, fundacion.getContrasena())) {
        model.addAttribute("error", "Credenciales incorrectas");
        return "login_fundacion";
    }

    // 🔹 Validar el estado antes de permitir el ingreso
    if (fundacion.getEstado() == null || fundacion.getEstado().equalsIgnoreCase("PENDIENTE")) {
        model.addAttribute("error", "Tu cuenta aún no ha sido validada por el administrador.");
        return "login_fundacion";
    }

    if (fundacion.getEstado().equalsIgnoreCase("RECHAZADA")) {
        model.addAttribute("error", "Tu registro fue rechazado por el administrador.");
        return "login_fundacion";
    }

    // ✅ Solo entra si está aprobada
    if (fundacion.getEstado().equalsIgnoreCase("APROBADA")) {
        session.setAttribute("fundacionLogueada", fundacion);
        return "redirect:/fundacion/dashboard/" + fundacion.getId();
    }

    model.addAttribute("error", "Estado de cuenta no válido. Contacta con soporte.");
    return "login_fundacion";
}
    // Obtener el logo de una fundación
    @GetMapping("/{id}/logo")
    public ResponseEntity<byte[]> obtenerLogo(@PathVariable String id) {
        Optional<Fundacion> fundacionOpt = fundacionRepository.findById(id);
        if (fundacionOpt.isPresent() && fundacionOpt.get().getLogo() != null) {
            byte[] logo = fundacionOpt.get().getLogo();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG); // o IMAGE_JPEG si tus logos son jpg

            return new ResponseEntity<>(logo, headers, HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Dashboard de la fundación
    @GetMapping("/dashboard/{id}")
public String dashboardFundacion(@PathVariable String id, HttpSession session, Model model) {
    Fundacion fundacionSesion = (Fundacion) session.getAttribute("fundacionLogueada");
    
    if (fundacionSesion == null || !fundacionSesion.getId().equals(id)) {
        return "redirect:/fundacion/login";
    }

    model.addAttribute("fundacion", fundacionSesion);

    List<Proyecto> proyectos = proyectoRepository.findByFundacionId(id);
    if (proyectos == null) proyectos = new ArrayList<>();

    model.addAttribute("proyectos", proyectos);
    model.addAttribute("nuevoProyecto", new Proyecto());

    return "fundacion_dashboard";
}


    // Crear nuevo proyecto
    @PostMapping("/proyectos/nuevo")
    public String crearProyecto(@RequestParam String nombre,
                                @RequestParam String descripcion,
                                @RequestParam String fundacionId,
                                Model model) {

        Fundacion fundacion = fundacionRepository.findById(fundacionId).orElse(null);
        if (fundacion == null) {
            model.addAttribute("error", "Fundación no encontrada");
            return "error";
        }

        Proyecto proyecto = new Proyecto();
        proyecto.setNombre(nombre);
        proyecto.setDescripcion(descripcion);
        proyecto.setFundacionId(fundacionId);

        proyectoService.guardar(proyecto);
        return "redirect:/fundacion/dashboard/" + fundacionId;
    }

    // Logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/fundacion/login";
    }

    // Eliminar proyecto (solo si está terminado)
    @PostMapping("/proyectos/eliminar/{id}")
    public String eliminarProyecto(@PathVariable String id, Model model) {
        Proyecto proyecto = proyectoService.obtenerPorId(id).orElse(null);
        if (proyecto == null) {
            model.addAttribute("error", "Proyecto no encontrado");
            return "error";
        }

        if (!"TERMINADO".equalsIgnoreCase(proyecto.getEstado())) {
            model.addAttribute("error", "Solo se pueden eliminar proyectos terminados");
            return "error";
        }

        proyectoService.eliminar(id);
        return "redirect:/fundacion/dashboard/" + proyecto.getFundacionId();
    }

    @GetMapping("/donaciones/{id}")
public String verDonacionesFundacion(@PathVariable String id, HttpSession session, Model model) {
    Fundacion fundacionSesion = (Fundacion) session.getAttribute("fundacionLogueada");

    // Si no hay sesión o no coincide con el ID → redirigir al index
    if (fundacionSesion == null || !fundacionSesion.getId().equals(id)) {
        return "redirect:/index";
    }

    Fundacion fundacion = fundacionRepository.findById(id).orElse(null);
    if (fundacion == null) {
        return "redirect:/index";
    }

    // Obtener los proyectos de la fundación
    List<Proyecto> proyectos = proyectoRepository.findByFundacionId(id);

    // Map con las donaciones de cada proyecto
    Map<Proyecto, List<Donacion>> donacionesPorProyecto = new LinkedHashMap<>();
    Map<String, Double> totalDonacionesPorProyecto = new HashMap<>();

    for (Proyecto proyecto : proyectos) {
        List<Donacion> donaciones = donacionRepository.findByProyectoId(proyecto.getId());
        donacionesPorProyecto.put(proyecto, donaciones);

        double total = donaciones.stream()
        .mapToDouble(d -> d.getMonto().doubleValue())
        .sum();

        totalDonacionesPorProyecto.put(proyecto.getId(), total);
    }

    model.addAttribute("fundacion", fundacion);
    model.addAttribute("donacionesPorProyecto", donacionesPorProyecto);
    model.addAttribute("totalDonacionesPorProyecto", totalDonacionesPorProyecto);

    return "fundacion_donaciones";
}


@GetMapping("/voluntarios-todos/{idFundacion}")
public String verTodosLosVoluntarios(@PathVariable String idFundacion, HttpSession session, Model model) {
    // ✅ Validar sesión
    Fundacion fundacionSesion = (Fundacion) session.getAttribute("fundacionLogueada");
    if (fundacionSesion == null || !fundacionSesion.getId().equals(idFundacion)) {
        return "redirect:/index";
    }

    // ✅ Obtener proyectos de la fundación
    List<Proyecto> proyectos = proyectoRepository.findByFundacionId(idFundacion);
    Map<Proyecto, List<Map<String, String>>> voluntariosPorProyecto = new LinkedHashMap<>();
    int totalVoluntarios = 0;

    for (Proyecto proyecto : proyectos) {
        List<Voluntario> voluntarios = voluntarioRepository.findByProyectoId(proyecto.getId());
        List<Map<String, String>> usuariosData = new ArrayList<>();

        for (Voluntario voluntario : voluntarios) {
            Map<String, String> data = new HashMap<>();

            // Buscar usuario asociado
            usuarioRepository.findById(voluntario.getUsuarioId()).ifPresentOrElse(u -> {
                data.put("nombre", u.getNombre() != null ? u.getNombre() : "Desconocido");
                data.put("email", u.getEmail() != null ? u.getEmail() : "No disponible");
                data.put("telefono", u.getTelefono() != null ? u.getTelefono() : "No disponible");
            }, () -> {
                data.put("nombre", "Desconocido");
                data.put("email", "No disponible");
                data.put("telefono", "No disponible");
            });

            usuariosData.add(data);
        }

        voluntariosPorProyecto.put(proyecto, usuariosData);
        totalVoluntarios += usuariosData.size();
    }

    // ✅ Pasar todo al modelo
    model.addAttribute("fundacion", fundacionSesion);
    model.addAttribute("voluntariosPorProyecto", voluntariosPorProyecto);
    model.addAttribute("totalVoluntarios", totalVoluntarios);

    return "fundacion_voluntarios";
}


}
