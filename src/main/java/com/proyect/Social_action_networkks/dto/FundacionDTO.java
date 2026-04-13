package com.proyect.Social_action_networkks.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.*;
import jakarta.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FundacionDTO {
    private String id;
    private String nombre;
    private String descripcion;
    @Pattern(regexp = "\\d{10}", message = "El teléfono debe tener exactamente 10 números")

    private String contacto;
    private String ubicacion;
    private String estado;
    private String correo;
    private String contrasena;
    private String confirmarContrasena;
    private MultipartFile logo;

}