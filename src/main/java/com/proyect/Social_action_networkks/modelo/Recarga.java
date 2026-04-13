package com.proyect.Social_action_networkks.modelo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Document(collection = "recargas")
public class Recarga {
    @Id
    private String id;
    private String usuarioId;
    private BigDecimal monto;
    private String descripcion;
    private Date fecha;
    private String estado; // PENDIENTE, APROBADA, RECHAZADA
    private String comprobantePdf;
}
