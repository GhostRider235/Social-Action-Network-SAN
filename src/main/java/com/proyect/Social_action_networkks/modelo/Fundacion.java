package com.proyect.Social_action_networkks.modelo;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "fundaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Fundacion implements UserDetails {

    @Id
    private String id;

    private String nombre;
    private String descripcion;
    private String contacto;
    private String correo;
    private String contrasena;
    private String ubicacion;
    private BigDecimal fondos = BigDecimal.ZERO;
    private Integer cantidadVoluntarios = 0;

    // PENDIENTE, APROBADA, RECHAZADA
    private String estado;

    private String rol;

    private String logo;

    private List<String> proyectoIds;
    private List<String> donacionIds;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        String autoridad = this.getRol().toUpperCase();

        return Collections.singleton(
                new SimpleGrantedAuthority("ROLE_" + autoridad)
        );
    }

    @Override
    public String getPassword() {
        return this.contrasena;
    }

    @Override
    public String getUsername() {
        return this.correo;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return "APROBADA".equalsIgnoreCase(this.estado);
    }
}