package com.proyect.Social_action_networkks.servicio;

import com.proyect.Social_action_networkks.modelo.Proyecto;
import com.proyect.Social_action_networkks.repository.ProyectoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ProyectoService {

    private final ProyectoRepository proyectoRepository;

    @Autowired
    public ProyectoService(ProyectoRepository proyectoRepository) {
        this.proyectoRepository = proyectoRepository;
    }
    

    // 🔹 Listar todos los proyectos
    public List<Proyecto> obtenerTodos() {
        return proyectoRepository.findAll();
    }

    // 🔹 Buscar proyecto por ID (devuelve null si no existe)
    public Optional<Proyecto> obtenerPorId(String id) {
    return proyectoRepository.findById(id);
}


    // 🔹 Guardar o actualizar proyecto
    public Proyecto guardar(Proyecto proyecto) {
        if (proyecto.getNombre() == null || proyecto.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del proyecto no puede estar vacío");
        }
        return proyectoRepository.save(proyecto);
    }

    // 🔹 Eliminar proyecto por ID
    public void eliminar(String id) {
        proyectoRepository.deleteById(id);
    }

    // 🔹 Listar proyectos de una fundación específica
    public List<Proyecto> obtenerPorFundacion(String fundacionId) {
        List<Proyecto> proyectos = proyectoRepository.findByFundacionId(fundacionId);
        return proyectos != null ? proyectos : List.of(); // lista vacía si no hay proyectos
    }

    // 🔹 Marcar un proyecto como terminado
    public void terminarProyecto(String id) {
        Proyecto proyecto = obtenerPorId(id).orElse(null);
        if (proyecto != null) {
            proyecto.setEstado("TERMINADO");
            proyecto.setFechaFin(new Date());
            proyectoRepository.save(proyecto);
        }
    }

    // 🔍 Buscar proyectos por nombre (coincidencia parcial)
    public List<Proyecto> buscarPorNombre(String nombre) {
        return proyectoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    // 🔍 Buscar proyectos por nombre y fundación
    public List<Proyecto> buscarPorNombreYFundacion(String nombre, String fundacionId) {
        return proyectoRepository.findByNombreContainingIgnoreCaseAndFundacionId(nombre, fundacionId);
    }
}
