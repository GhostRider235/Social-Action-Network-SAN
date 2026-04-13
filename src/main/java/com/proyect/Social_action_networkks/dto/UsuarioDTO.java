package com.proyect.Social_action_networkks.dto;

import lombok.*;
import jakarta.validation.constraints.Pattern;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
    private String id;
    private String nombre;
    private String email;
    private String tipo;
    private String estado;
    @Pattern(regexp = "\\d{10}", message = "El teléfono debe tener exactamente 10 números")
    private String telefono;
    private Date fechaRegistro;
    private String confirmarContrasena; // ✅ este campo faltaba
    private String contrasena; // Incluye este campo si es necesario para la transferencia
    private String rol;//
}