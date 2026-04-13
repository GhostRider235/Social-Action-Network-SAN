package com.proyect.Social_action_networkks.servicio;

import com.proyect.Social_action_networkks.modelo.Fundacion;
import com.proyect.Social_action_networkks.repository.FundacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FundacionService {

    @Autowired
    private FundacionRepository fundacionRepository;

    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public List<Fundacion> obtenerTodas() {
        return fundacionRepository.findAll();
    }

    public Optional<Fundacion> obtenerPorId(String id) {
        return fundacionRepository.findById(id);
    }

    public Fundacion guardar(Fundacion fundacion) {
        return fundacionRepository.save(fundacion);
    }

    public void eliminar(String id) {
        fundacionRepository.deleteById(id);
    }

    public Fundacion findById(String id) {
        return fundacionRepository.findById(id).orElse(null);
    }
    public Optional<Fundacion> autenticar(String correo, String contrasena) {
    Optional<Fundacion> fundacion = fundacionRepository.findByCorreo(correo);
    if (fundacion.isPresent() && passwordEncoder.matches(contrasena, fundacion.get().getContrasena())) {
        return fundacion;
    }
    return Optional.empty();
}


}