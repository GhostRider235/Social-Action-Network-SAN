package com.proyect.Social_action_networkks.modelo;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Document(collection = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario implements UserDetails {
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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String autoridad =this.getRol().toUpperCase();
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_"+autoridad));
    }

    @Override
    public String getPassword() {
        return this.getPassword();
    }

    @Override
    public String getUsername() {
        return this.getEmail();
    }
}
