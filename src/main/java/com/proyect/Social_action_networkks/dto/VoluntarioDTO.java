package com.proyect.Social_action_networkks.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoluntarioDTO {
    private String id;
    private String usuarioId;
    private String proyectoId;
    private String fundacionId;   // ✅ Agregar para referencia
    private Date fechaAsignacion;
    private String nombreUsuario;
    private String telefonoUsuario;  // ✅ opcional
    private String correoUsuario;    // ✅ opcional
    private String nombreProyecto;
    private String nombreFundacion;  // ✅ Agregar para mostrar
    private String comentario;
    private String estado;
}
