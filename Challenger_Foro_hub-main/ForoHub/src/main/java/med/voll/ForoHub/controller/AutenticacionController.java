package med.voll.ForoHub.controller;

import jakarta.validation.Valid;
import med.voll.ForoHub.domain.usuario.DatosAutenticacion;
import med.voll.ForoHub.domain.usuario.Usuario;
import med.voll.ForoHub.infra.security.DatosTokenJWT;
import med.voll.ForoHub.infra.security.TokenService;
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

    @Autowired // sea inyectado con inyeccion de dependencias
    private TokenService tokenService;

    //creamos un metodo donde recivimos todos los datos que nos va a enviar nuestro frontend
    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping
    public ResponseEntity iniciarSecion(@RequestBody @Valid DatosAutenticacion datos) {


        var autenticationToken = new UsernamePasswordAuthenticationToken(datos.login(), datos.contrasena());
        var autenticacion = authenticationManager.authenticate(autenticationToken);

        //vamos a convertir el token service en un DTO que es el que se le devuelve al usuario
        var tokenJWT = tokenService.generarToken((Usuario)autenticacion.getPrincipal());

        return ResponseEntity.ok(new DatosTokenJWT(tokenJWT));
    }


}