package com.proyect.Social_action_networkks.controllers;

import com.proyect.Social_action_networkks.dto.DonacionDTO;
import com.proyect.Social_action_networkks.modelo.Donacion;
import com.proyect.Social_action_networkks.modelo.Fundacion;
import com.proyect.Social_action_networkks.modelo.Proyecto;
import com.proyect.Social_action_networkks.modelo.Usuario;
import com.proyect.Social_action_networkks.servicio.DonacionService;
import com.proyect.Social_action_networkks.servicio.FundacionService;
import com.proyect.Social_action_networkks.servicio.ProyectoService;
import com.proyect.Social_action_networkks.servicio.UsuarioServicio;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/donaciones")
public class DonacionController {

    @Autowired
    private DonacionService donacionService;

    @Autowired
    private UsuarioServicio usuarioService;

    @Autowired
    private FundacionService fundacionService;

    @Autowired
    private ProyectoService proyectoService;

    // Aprobar donación (redirección a detalle proyecto)
    @PostMapping("/aprobar/{id}")
    public String aprobarDonacion(@PathVariable String id) {
        Optional<Donacion> donacionOpt = donacionService.obtenerPorId(id);
        if (donacionOpt.isPresent()) {
            Donacion donacion = donacionOpt.get();
            donacion.setEstado("APROBADA");
            donacionService.guardar(donacion);
            // ✅ Cambiado:
            return "redirect:/proyectos/ver/" + donacion.getProyectoId();
        }
        return "redirect:/"; // fallback
    }

    //Rechazar donacion
    @PostMapping("/rechazar/{id}")
    public String rechazarDonacion(@PathVariable String id) {
        Optional<Donacion> donacionOpt = donacionService.obtenerPorId(id);
        if (donacionOpt.isPresent()) {
            Donacion donacion = donacionOpt.get();
            donacion.setEstado("RECHAZADA");
            donacionService.guardar(donacion);
            // ✅ Cambiado:
            return "redirect:/proyectos/ver/" + donacion.getProyectoId();
        }
        return "redirect:/"; // fallback
    }

    // Guardar donación (opcional para formulario con AJAX)
@PostMapping("/guardar")
@ResponseBody
public Map<String, Object> guardarDonacion(@RequestBody DonacionDTO donacionDTO, HttpSession session) {
    Map<String, Object> response = new HashMap<>();
    Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

    if (usuario == null) {
        response.put("success", false);
        response.put("mensaje", "Usuario no logueado");
        return response;
    }

    if (usuario.getSaldo().compareTo(donacionDTO.getMonto()) < 0) {
        response.put("success", false);
        response.put("mensaje", "Saldo insuficiente");
        return response;
    }

    // Descontar saldo
    usuario.setSaldo(usuario.getSaldo().subtract(donacionDTO.getMonto()));
    usuarioService.guardar(usuario);
    session.setAttribute("usuarioLogueado", usuario);

    // Buscar entidades relacionadas
    Optional<Fundacion> fundacionOpt = fundacionService.obtenerPorId(donacionDTO.getFundacionId());
    Optional<Proyecto> proyectoOpt = proyectoService.obtenerPorId(donacionDTO.getProyectoId());

    Fundacion fundacion = fundacionOpt.orElse(null);
    Proyecto proyecto = proyectoOpt.orElse(null);

    // Crear donación
    Donacion donacion = new Donacion();
    donacion.setUsuarioId(usuario.getId());
    donacion.setProyectoId(donacionDTO.getProyectoId());
    donacion.setFundacionId(donacionDTO.getFundacionId());
    donacion.setMonto(donacionDTO.getMonto());
    donacion.setDescripcion(donacionDTO.getDescripcion());
    donacion.setFecha(new Date());
    donacion.setEstado("PENDIENTE");

    // ✅ Setear datos del usuario
    donacion.setNombreUsuario(usuario.getNombre());
    donacion.setCorreoUsuario(usuario.getEmail());
    donacion.setTelefonoUsuario(usuario.getTelefono());

    // ✅ Setear datos de la fundación y proyecto (si existen)
    if (fundacion != null) {
        donacion.setNombreFundacion(fundacion.getNombre());
        donacion.setFundacion(fundacion);
    }

    if (proyecto != null) {
        donacion.setNombreProyecto(proyecto.getNombre());
        donacion.setProyecto(proyecto);
    }

    donacion.setUsuario(usuario);

    // Guardar donación
    donacionService.guardar(donacion);

    response.put("success", true);
    response.put("donacionId", donacion.getId());
    return response;
}




    // Convertir Donacion → DonacionDTO
    private DonacionDTO convertirADTO(Donacion donacion) {
        DonacionDTO dto = new DonacionDTO();
        dto.setId(donacion.getId());
        dto.setMonto(donacion.getMonto());
        dto.setFecha(donacion.getFecha());
        dto.setDescripcion(donacion.getDescripcion());
        dto.setEstado(donacion.getEstado());
        dto.setUsuarioId(donacion.getUsuarioId());
        dto.setFundacionId(donacion.getFundacionId());
        dto.setProyectoId(donacion.getProyectoId());
        dto.setNombreUsuario(donacion.getNombreUsuario());
        dto.setTelefonoUsuario(donacion.getTelefonoUsuario());
        dto.setCorreoUsuario(donacion.getCorreoUsuario());
        dto.setNombreFundacion(donacion.getNombreFundacion());
        dto.setNombreProyecto(donacion.getNombreProyecto());
        return dto;
    }
}
