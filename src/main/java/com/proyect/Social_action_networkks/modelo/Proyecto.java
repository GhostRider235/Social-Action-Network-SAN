package com.proyect.Social_action_networkks.modelo;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;

@Document(collection = "proyectos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Proyecto {

    @Id
    private String id;

    @NotBlank
    private String nombre;
    @NotBlank
    private String descripcion;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date fechaInicio = new Date();
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date fechaFin;
    private String fundacionId;
    private Boolean recibirDonaciones;
    private List<String> voluntariosIds;
    private String estado = "ACTIVO"; // opcional
    private String nombreFundacion;
    private Long duracion; // en días
}


