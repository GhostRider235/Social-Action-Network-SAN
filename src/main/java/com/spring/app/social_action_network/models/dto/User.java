package com.spring.app.social_action_network.models.dto;

public class User {

    /*
    * Hasta ahora con este dto se hace el registro en caso de necesitar más campos
    * se recomienda agregarle mas pero tengan en cuenta que cuando se le asignan atributos
    * también se le deben de dar a la clase usuario de la base de datos
    * */

    private String nombre;
    private String correo;
    private String contrasena;
    private String confirmarContrasena;

    public User() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getConfirmarContrasena() {
        return confirmarContrasena;
    }

    public void setConfirmarContrasena(String confirmarContrasena) {
        this.confirmarContrasena = confirmarContrasena;
    }
}
