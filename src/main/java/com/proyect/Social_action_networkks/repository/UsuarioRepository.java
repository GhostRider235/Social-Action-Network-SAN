package com.proyect.Social_action_networkks.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.proyect.Social_action_networkks.modelo.Usuario;

import java.util.List;
import java.util.Optional;


@Repository
public interface UsuarioRepository extends MongoRepository<Usuario, String> {
    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByEmailAndContrasena(String email, String contrasena); // "contrasena" sin la ñ
        List<Usuario> findByIdIn(List<String> ids);
        List<Usuario> findAllById(Iterable<String> ids);


}