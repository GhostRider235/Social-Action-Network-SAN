package com.spring.app.social_action_network.models;

import org.jspecify.annotations.Nullable;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Document(collation = "Users")
public class Usuario implements UserDetails {

    /*
    Nota: hasta ahora se le asigno al usuario estas variables por lo que a futuro se recomienda agregarle mas atributos
    pd: esto es lo minimo para el sistema de seguridad
    */


    private String email;
    private String passwordUser;
    private String authority;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordUser() {
        return passwordUser;
    }

    public void setPasswordUser(String passwordUser) {
        this.passwordUser = passwordUser;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public Usuario() {
    }

    // Rol del usuario
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String r =this.authority.toUpperCase();
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_"+r));
    }

    // Contraseña
    @Override
    public @Nullable String getPassword() {
        return this.getPasswordUser();
    }

    // Usuario
    @Override
    public String getUsername() {
        return this.getEmail();
    }
}
