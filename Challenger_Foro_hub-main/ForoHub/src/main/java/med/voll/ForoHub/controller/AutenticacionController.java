package med.voll.ForoHub.controller;

import jakarta.validation.Valid;
import med.voll.ForoHub.domain.usuario.DatosAutenticacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login") // asignamos la ruta donde va a estar atendiendo ese controler

public class AutenticacionController {
    //creamos un metodo donde recivimos todos los datos que nos va a enviar nuestro frontend

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping
    public ResponseEntity iniciarSecion(@RequestBody @Valid DatosAutenticacion datos) {


        var token = new UsernamePasswordAuthenticationToken(datos.login(), datos.contrasena());
        var autenticacion = authenticationManager.authenticate(token);

        return ResponseEntity.ok().build();
    }


}
