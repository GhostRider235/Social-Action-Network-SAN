package com.proyect.Social_action_networkks.repository;

import com.proyect.Social_action_networkks.modelo.Proyecto;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProyectoRepository extends MongoRepository<Proyecto, String> {

    // 🔹 Buscar proyectos por el ID de la fundación
    List<Proyecto> findByFundacionId(String fundacionId);

    // 🔹 Buscar proyectos por nombre (ignorando mayúsculas/minúsculas)
    List<Proyecto> findByNombreContainingIgnoreCase(String nombre);

    // 🔹 Buscar proyectos por nombre y fundación
    List<Proyecto> findByNombreContainingIgnoreCaseAndFundacionId(String nombre, String fundacionId);
}
