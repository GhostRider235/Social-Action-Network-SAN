package com.proyect.Social_action_networkks.repository;
import com.proyect.Social_action_networkks.modelo.Voluntario;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoluntarioRepository extends MongoRepository<Voluntario, String> {
    List<Voluntario> findByProyectoId(String proyectoId);
    boolean existsByUsuarioIdAndProyectoId(String usuarioId, String proyectoId);
    // ✅ Opcional: Buscar todos los voluntarios de un usuario
    List<Voluntario> findByUsuarioId(String usuarioId);
    List<Voluntario> findByFundacionId(String fundacionId);



    // Métodos específicos de Voluntario pueden añadirse aquí
}