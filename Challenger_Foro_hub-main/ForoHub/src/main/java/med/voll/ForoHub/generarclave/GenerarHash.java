package med.voll.ForoHub.generarclave;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenerarHash {
    public static void main(String[] args) {
        String contrasenaPlano = "123456";
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashGenerado = encoder.encode(contrasenaPlano);

        System.out.println("Contraseña en texto plano: " + contrasenaPlano);
        System.out.println("Hash generado: " + hashGenerado);
    }
}