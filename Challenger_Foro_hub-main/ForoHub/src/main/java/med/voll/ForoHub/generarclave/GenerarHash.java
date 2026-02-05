package med.voll.ForoHub.generarclave;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

//se usa para que ingrese la clave a un usuario y este le retorne la clave encriptada para Algorithm.HMAC256
// que es la que va a ingresar a la tabla usuarios para la creacion de usuarios

public class GenerarHash {
    public static void main(String[] args) {
        String contrasenaPlano = "123456";
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashGenerado = encoder.encode(contrasenaPlano);

        System.out.println("Contraseña en texto plano: " + contrasenaPlano);
        System.out.println("Hash generado: " + hashGenerado);
    }
}

