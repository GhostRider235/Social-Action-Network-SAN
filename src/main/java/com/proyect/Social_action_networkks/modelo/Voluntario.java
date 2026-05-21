package com.proyect.Social_action_networkks.modelo;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "voluntarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Voluntario {

    @Id
    private String id;

    private String usuarioId;      
    private String proyectoId;     
    @Indexed
    private String fundacionId;    
    private Date fechaAsignacion = new Date(); 
    @Indexed
    private String estado = "Pendiente";
    private String comentario; 
    private String nombre;
    private String email;
    private String telefono;

}
