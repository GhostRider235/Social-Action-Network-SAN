package com.proyect.Social_action_networkks.modelo;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Document(collection = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    @Id
    private String id;
    private String nombre;
    private String email;
    private String tipo;
    private String telefono;
    private Date fechaRegistro;
    private String contrasena;
    private List<String> voluntarioIds;
    private List<String> donacionIds;
    private String rol;
    private BigDecimal saldo = BigDecimal.ZERO; // saldo inicial 0
}
