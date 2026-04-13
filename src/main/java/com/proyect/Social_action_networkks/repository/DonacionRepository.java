package com.proyect.Social_action_networkks.repository;

import com.proyect.Social_action_networkks.modelo.Donacion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface DonacionRepository extends MongoRepository<Donacion, String> {
    List<Donacion> findByUsuarioId(String usuarioId);
    List<Donacion> findByProyectoId(String proyectoId);
    // Métodos específicos de Donación pueden añadirse aquí
}