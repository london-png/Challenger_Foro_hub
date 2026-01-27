package med.voll.ForoHub.generarclave;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class VerificarHash {
    public static void main(String[] args) {
        // El hash que tienes en tu base de datos
        String hashEnBD = "$2a$10$wpjZKcGAQ7aTp.jRcH4Q2eqNLvpg1/0cNhnPBdoXEWu0Hxnjnsjru";
        // La contraseña que estás enviando desde Insomnia
        String contrasenaPlano = "123456";

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        boolean coincide = encoder.matches(contrasenaPlano, hashEnBD);

        System.out.println("¿La contraseña '" + contrasenaPlano + "' coincide con el hash en la BD?");
        System.out.println("Resultado: " + (coincide ? "¡SÍ! El hash es correcto." : "¡NO! El hash no corresponde a esa contraseña."));
    }
}
