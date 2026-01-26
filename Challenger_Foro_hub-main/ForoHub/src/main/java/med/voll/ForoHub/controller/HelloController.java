package med.voll.ForoHub.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/London")
public class HelloController {
    //va estar respondiedo al @GetMapping
    @GetMapping

    //creamos el metodo
    public String London () {
        return "como estan en el dia de hoy";
    }
}
