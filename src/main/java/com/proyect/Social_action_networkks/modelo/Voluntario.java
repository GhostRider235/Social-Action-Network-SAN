package com.proyect.Social_action_networkks.modelo;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Document(collection = "voluntarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Voluntario {

    @Id
    private String id;

    private String usuarioId;      // Quién se postuló
    private String proyectoId;     // En qué proyecto participa
    @Indexed
    private String fundacionId;    // A qué fundación pertenece el proyecto (útil para consultas)
    private Date fechaAsignacion = new Date(); // Se registra automáticamente la fecha
    @Indexed
    private String estado = "Pendiente"; // Puede ser "Pendiente", "Aprobado" o "Rechazado"
    private String comentario; // Mensaje opcional del voluntario o nota de la fundación
    
    private String nombre;
    private String email;
    private String telefono;

}
