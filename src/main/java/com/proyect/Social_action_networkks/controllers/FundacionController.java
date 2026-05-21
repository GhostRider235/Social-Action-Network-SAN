package com.proyect.Social_action_networkks.controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

@Controller
@RequestMapping("/fundacion")
public class FundacionController {

    @Autowired
    private FundacionRepository fundacionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

    fundacion.setRol("FUNDACION");

    fundacion.setContrasena(
    passwordEncoder.encode(dto.getContrasena())
);

    try {

    if (dto.getLogo() != null
            && !dto.getLogo().isEmpty()) {

        // 🔹 Nombre único
        String nombreArchivo =
                System.currentTimeMillis()
                + "_"
                + dto.getLogo().getOriginalFilename();

        // 🔹 Carpeta uploads/logos
        File carpeta = new File(
                System.getProperty("user.dir")
                + "/uploads/logos"
        );

        // 🔹 Crear carpeta automáticamente
        carpeta.mkdirs();

        // 🔹 Archivo destino
        File destino =
                new File(carpeta, nombreArchivo);

        // 🔹 Guardar imagen
        dto.getLogo().transferTo(destino);

        // 🔹 Guardar SOLO nombre en Mongo
        fundacion.setLogo(nombreArchivo);
    }

} catch (Exception e) {

    model.addAttribute(
            "error",
            "Error al subir el logo"
    );

    return "register_fundacion";
}

    fundacionRepository.save(fundacion);

    return "redirect:/login";
}


    // Dashboard de la fundación
    @GetMapping("/dashboard/{id}")
public String dashboardFundacion(
        @PathVariable("id") String id,
        java.security.Principal principal,
        Model model) {

    Fundacion fundacion = fundacionRepository
            .findByCorreo(principal.getName())
            .orElse(null);

    if (fundacion == null ||
        !fundacion.getId().equals(id)) {

        return "redirect:/login";
    }

    model.addAttribute("fundacion", fundacion);

    List<Proyecto> proyectos =
            proyectoRepository.findByFundacionId(id);

    if (proyectos == null) {
        proyectos = new ArrayList<>();
    }

    model.addAttribute("proyectos", proyectos);
    model.addAttribute("nuevoProyecto", new Proyecto());

    return "fundacion_dashboard";
}


    // Crear nuevo proyecto
    @PostMapping("/proyectos/nuevo")
public String crearProyecto(
        @RequestParam("nombre") String nombre,
        @RequestParam("descripcion") String descripcion,
        java.security.Principal principal,
        Model model) {

    Fundacion fundacion = fundacionRepository
            .findByCorreo(principal.getName())
            .orElse(null);

    if (fundacion == null) {
        return "redirect:/login";
    }

    Proyecto proyecto = new Proyecto();

    proyecto.setNombre(nombre);
    proyecto.setDescripcion(descripcion);

    proyecto.setFundacionId(fundacion.getId());

    proyectoService.guardar(proyecto);

    return "redirect:/fundacion/dashboard/" + fundacion.getId();
}

    // Eliminar proyecto (solo si está terminado)
    @PostMapping("/proyectos/eliminar/{id}")
public String eliminarProyecto(@PathVariable("id") String id, Model model) {
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
public String verDonacionesFundacion(
        @PathVariable("id") String id,
        java.security.Principal principal,
        Model model) {

    Fundacion fundacion = fundacionRepository
            .findByCorreo(principal.getName())
            .orElse(null);

    if (fundacion == null ||
        !fundacion.getId().equals(id)) {

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
public String verTodosLosVoluntarios(
        @PathVariable("idFundacion") String idFundacion,
        java.security.Principal principal,
        Model model) {

    Fundacion fundacion = fundacionRepository
            .findByCorreo(principal.getName())
            .orElse(null);

    if (fundacion == null ||
        !fundacion.getId().equals(idFundacion)) {

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

    model.addAttribute("fundacion", fundacion);
    model.addAttribute("voluntariosPorProyecto", voluntariosPorProyecto);
    model.addAttribute("totalVoluntarios", totalVoluntarios);

    return "fundacion_voluntarios";
}


}
