package com.proyect.Social_action_networkks.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.proyect.Social_action_networkks.modelo.Recarga;
import java.util.List;

public interface RecargaRepository extends MongoRepository<Recarga, String> {
    List<Recarga> findByEstado(String estado);
}