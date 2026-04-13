package com.proyect.Social_action_networkks.dto;

import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProyectoDTO {
    private String id;
    private String nombre;
    private String descripcion;
    private Date fechaInicio;
    private Date fechaFin;
    private boolean recibirDonaciones;
    private String fundacionId; // Para asociar con Fundacion
}
