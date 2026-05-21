package com.proyect.Social_action_networkks.controllers;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proyect.Social_action_networkks.dto.DonacionDTO;
import com.proyect.Social_action_networkks.modelo.Donacion;
import com.proyect.Social_action_networkks.modelo.Fundacion;
import com.proyect.Social_action_networkks.modelo.Proyecto;
import com.proyect.Social_action_networkks.modelo.Usuario;
import com.proyect.Social_action_networkks.servicio.DonacionService;
import com.proyect.Social_action_networkks.servicio.FundacionService;
import com.proyect.Social_action_networkks.servicio.ProyectoService;
import com.proyect.Social_action_networkks.servicio.UsuarioServicio;

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
public String aprobarDonacion(@PathVariable("id") String id) {

    Optional<Donacion> donacionOpt = donacionService.obtenerPorId(id);

    if (donacionOpt.isPresent()) {

        Donacion donacion = donacionOpt.get();

        // Evitar aprobar dos veces
        if ("APROBADA".equalsIgnoreCase(donacion.getEstado())) {
            return "redirect:/proyectos/ver/" + donacion.getProyectoId();
        }

        donacion.setEstado("APROBADA");

        // Buscar fundación
        Optional<Fundacion> fundacionOpt =
                fundacionService.obtenerPorId(donacion.getFundacionId());

        if (fundacionOpt.isPresent()) {

            Fundacion fundacion = fundacionOpt.get();

            // Si fondos es null
            if (fundacion.getFondos() == null) {
                fundacion.setFondos(BigDecimal.ZERO);
            }

            // Sumar donación
            fundacion.setFondos(
                    fundacion.getFondos().add(donacion.getMonto())
            );

            // Guardar fundación
            fundacionService.guardar(fundacion);
        }

        // Guardar donación
        donacionService.guardar(donacion);

        return "redirect:/proyectos/ver/" + donacion.getProyectoId();
    }

    return "redirect:/";
}

    //Rechazar donacion
    @PostMapping("/rechazar/{id}")
public String rechazarDonacion(@PathVariable("id") String id) {

    Optional<Donacion> donacionOpt = donacionService.obtenerPorId(id);

    if (donacionOpt.isPresent()) {

        Donacion donacion = donacionOpt.get();

        donacion.setEstado("RECHAZADA");

        // DEVOLVER DINERO AL USUARIO
        Usuario usuario = usuarioService
                .obtenerPorId(donacion.getUsuarioId())
                .orElse(null);

        if (usuario != null) {

            usuario.setSaldo(
                    usuario.getSaldo().add(donacion.getMonto())
            );

            usuarioService.guardar(usuario);
        }

        // Guardar donación
        donacionService.guardar(donacion);

        return "redirect:/proyectos/ver/" + donacion.getProyectoId();
    }

    return "redirect:/";
}
@PostMapping("/guardar")
@ResponseBody
public Map<String, Object> guardarDonacion(@RequestBody DonacionDTO donacionDTO,
                                            Principal principal) {

    Map<String, Object> response = new HashMap<>();

    if (principal == null) {
        response.put("success", false);
        response.put("mensaje", "Usuario no autenticado");
        return response;
    }

    Usuario usuario = usuarioService
            .findByEmail(principal.getName())
            .orElse(null);

    if (usuario == null) {
        response.put("success", false);
        response.put("mensaje", "Usuario no registrado");
        return response;
    }

    if (usuario.getSaldo().compareTo(donacionDTO.getMonto()) < 0) {
        response.put("success", false);
        response.put("mensaje", "Saldo insuficiente");
        return response;
    }

    // descontar saldo
    usuario.setSaldo(usuario.getSaldo().subtract(donacionDTO.getMonto()));
    usuarioService.guardar(usuario);

    Optional<Fundacion> fundacionOpt =
            fundacionService.obtenerPorId(donacionDTO.getFundacionId());

    Optional<Proyecto> proyectoOpt =
            proyectoService.obtenerPorId(donacionDTO.getProyectoId());

    Donacion donacion = new Donacion();
    donacion.setUsuarioId(usuario.getId());
    donacion.setProyectoId(donacionDTO.getProyectoId());
    donacion.setFundacionId(donacionDTO.getFundacionId());
    donacion.setMonto(donacionDTO.getMonto());
    donacion.setDescripcion(donacionDTO.getDescripcion());
    donacion.setFecha(new Date());
    donacion.setEstado("PENDIENTE");

    donacion.setNombreUsuario(usuario.getNombre());
    donacion.setCorreoUsuario(usuario.getEmail());
    donacion.setTelefonoUsuario(usuario.getTelefono());

    fundacionOpt.ifPresent(f -> {
        donacion.setNombreFundacion(f.getNombre());
        donacion.setFundacion(f);
    });

    proyectoOpt.ifPresent(p -> {
        donacion.setNombreProyecto(p.getNombre());
        donacion.setProyecto(p);
    });

    donacion.setUsuario(usuario);

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
