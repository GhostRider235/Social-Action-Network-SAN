package com.proyect.Social_action_networkks.Config;

import java.util.Optional;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.proyect.Social_action_networkks.modelo.Fundacion;
import com.proyect.Social_action_networkks.repository.FundacionRepository;

@Component
public class FundacionTools {

    @Autowired
    private FundacionRepository fundacionRepository;

    @Tool(
        name = "aprobarFundacion",
        description = "Aprueba una fundación por correo"
    )
    public String aprobarFundacion(String correoFundacion) {

        Optional<Fundacion> fundacionOpt =
                fundacionRepository.findByCorreo(correoFundacion);

        if (fundacionOpt.isEmpty()) {
            return "No encontré la fundación con correo: " + correoFundacion;
        }

        Fundacion fundacion = fundacionOpt.get();

        fundacion.setEstado("APROBADA");

        fundacionRepository.save(fundacion);

        return "La fundación "
                + fundacion.getNombre()
                + " fue aprobada correctamente";
    }

    @Tool(
        name = "rechazarFundacion",
        description = "Rechaza una fundación por correo"
    )
    public String rechazarFundacion(String correoFundacion) {

        Optional<Fundacion> fundacionOpt =
                fundacionRepository.findByCorreo(correoFundacion);

        if (fundacionOpt.isEmpty()) {
            return "No encontré la fundación con correo: " + correoFundacion;
        }

        Fundacion fundacion = fundacionOpt.get();

        fundacion.setEstado("RECHAZADA");

        fundacionRepository.save(fundacion);

        return "La fundación "
                + fundacion.getNombre()
                + " fue rechazada";
    }
}