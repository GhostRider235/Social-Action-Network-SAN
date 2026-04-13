package com.proyect.Social_action_networkks.repository;

import com.proyect.Social_action_networkks.modelo.Fundacion;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository

public interface FundacionRepository extends MongoRepository<Fundacion, String> {
    // Métodos específicos de Fundación pueden añadirse aquí
    Optional<Fundacion> findByCorreo(String correo);
    List<Fundacion> findByEstado(String estado);
}