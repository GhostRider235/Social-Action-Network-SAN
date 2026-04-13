package com.proyect.Social_action_networkks.Config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class Encriptador {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "miClave123";
        String encrypted = encoder.encode(rawPassword);

        System.out.println("Encriptada: " + encrypted);
        System.out.println("¿Coincide?: " + encoder.matches("miClave123", encrypted));
    }
}
