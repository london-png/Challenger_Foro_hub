package med.voll.ForoHub.controller;

import med.voll.ForoHub.topico.DatosRegistroTopico;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController //un componente que maneja las solicitudes HTTP entrantes y devuelve respuestas, típicamente en formato JSON o XML.
@RequestMapping("/topicos") //para que Spring sepa que es un controller y va a atender topicos
public class TopicoController {

    @PostMapping //e utiliza para mapear métodos de un controlador a solicitudes HTTP POST.
    // creamos el metodo para topico
    public void registrar(@RequestBody DatosRegistroTopico datos) {
        System.out.println(datos); // es de tipo DTO

    }
}
