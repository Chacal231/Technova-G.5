package com.example.demo;
import org.mindrot.jbcrypt.BCrypt;

public class GeneradorHash {
     public static void main(String[] args) {
        String[] passwords = {"admin123", "oficina123", "cliente123"};
        
        for (String pwd : passwords) {
            String hash = BCrypt.hashpw(pwd, BCrypt.gensalt());
            System.out.println("Texto: " + pwd + " → Hash: " + hash);
        }
    }
}
