package com.proyect.Social_action_networkks.servicio;

import com.proyect.Social_action_networkks.modelo.Voluntario;
import com.proyect.Social_action_networkks.repository.VoluntarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VoluntarioService {

    @Autowired
    private VoluntarioRepository voluntarioRepository;

    public List<Voluntario> obtenerTodos() {
        return voluntarioRepository.findAll();
    }

    public Optional<Voluntario> obtenerPorId(String id) {
        return voluntarioRepository.findById(id);
    }

    public Voluntario guardar(Voluntario voluntario) {
        return voluntarioRepository.save(voluntario);
    }

    public void eliminar(String id) {
        voluntarioRepository.deleteById(id);
    }

    public List<Voluntario> obtenerPorProyecto(String proyectoId) {
        return voluntarioRepository.findByProyectoId(proyectoId);
    }
    public boolean existePorUsuarioYProyecto(String usuarioId, String proyectoId) {
        return voluntarioRepository.existsByUsuarioIdAndProyectoId(usuarioId, proyectoId);
    }
    
    


}