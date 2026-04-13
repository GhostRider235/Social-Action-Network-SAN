package com.proyect.Social_action_networkks.servicio;

import com.proyect.Social_action_networkks.modelo.Donacion;
import com.proyect.Social_action_networkks.repository.DonacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DonacionService {

    @Autowired
    private DonacionRepository donacionRepository;

    public List<Donacion> obtenerTodas() {
        return donacionRepository.findAll();
    }

    public Optional<Donacion> obtenerPorId(String id) {
        return donacionRepository.findById(id);
    }

    public Donacion guardar(Donacion donacion) {
        return donacionRepository.save(donacion);
    }

    public List<Donacion> obtenerPorProyecto(String proyectoId) {
        return donacionRepository.findByProyectoId(proyectoId);
    }

    public void eliminar(String id) {
        donacionRepository.deleteById(id);
    }

    public List<Donacion> obtenerPorUsuario(String usuarioId) {
        return donacionRepository.findByUsuarioId(usuarioId);
    }
}
