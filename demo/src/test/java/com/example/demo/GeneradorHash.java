package com.example.demo;
import org.mindrot.jbcrypt.BCrypt;

public class GeneradorHash {
    public static void main(String[] args) {
        String passwordAdmin = "admin";
        String passwordCliente = "1234";

        String hashAdmin = BCrypt.hashpw(passwordAdmin, BCrypt.gensalt());
        String hashCliente = BCrypt.hashpw(passwordCliente, BCrypt.gensalt());
        System.out.println("Hash Generados:");
        System.out.println("Hash para admin: " + hashAdmin);
        System.out.println("Hash para cliente: " + hashCliente);
        // Copiar estos Hash para el MySQL
    }
}
