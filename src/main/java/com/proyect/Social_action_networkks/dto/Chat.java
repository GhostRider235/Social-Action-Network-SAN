package com.proyect.Social_action_networkks.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Chat {

    private String usuario;
    private String tipo;  
    private String contenido;
    private LocalDateTime fecha = LocalDateTime.now();

    public Chat(String tipo, String contenido) {
        this.tipo = tipo;
        this.contenido = contenido;
    }
}
