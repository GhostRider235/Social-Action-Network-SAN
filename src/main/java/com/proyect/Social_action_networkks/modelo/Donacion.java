package com.proyect.Social_action_networkks.modelo;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "donaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Donacion {

    @Id
    private String id;

    private BigDecimal monto;
    @Field("fecha")
private Date fecha = new Date();

    private String descripcion;
    private String usuarioId;    // quién donó
    private String proyectoId;   // ✅ a qué proyecto fue
    private String fundacionId;  // a qué fundación pertenece el proyecto

    private String nombreUsuario;    
    private String correoUsuario;
    private String telefonoUsuario;

    private String nombreFundacion;
    private String nombreProyecto;
    private String estado;

    @DBRef
    private Usuario usuario;

    @DBRef
    private Fundacion fundacion;

    @DBRef
    private Proyecto proyecto;
    
}
