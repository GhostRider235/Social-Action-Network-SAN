package com.proyect.Social_action_networkks.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DonacionDTO {
    private String id;
    private BigDecimal monto;
    private Date fecha;
    private String descripcion;
    private String usuarioId;     // ID del usuario asociado
    private String fundacionId;
    private String proyectoId;    // ID de la fundación asociada

    // Extras para mostrar en la vista
    private String nombreUsuario;   // Nombre del usuario donante
    private String telefonoUsuario;
    private String correoUsuario;

    private String nombreFundacion; // Nombre de la fundación receptora
    private String nombreProyecto;
    private String estado;
}
