package com.proyect.Social_action_networkks.modelo;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Document(collection = "fundaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Fundacion {
    @Id
    private String id;
    private String nombre;
    private String descripcion;
    private String contacto;
    private String correo;
    private String contrasena;
    private String ubicacion;
    private String estado; //Pendiente, Aparobado, Rechazado
    private byte[] logo; 
    private List<Proyecto> proyectoIds;
    private List<Donacion> donacioneIds;
}
