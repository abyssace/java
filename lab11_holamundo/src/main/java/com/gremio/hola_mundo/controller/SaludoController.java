package com.gremio.hola_mundo.controller;

import com.gremio.hola_mundo.Aventurero;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SaludoController {

    @GetMapping("/saludo")
    public String saludo(Model model) {

        // Aquí preparamos los datos (el Model)
        String mensaje = "¡Hola Mundo desde Spring Boot!";
        String gremio = "Gran Gremio de Aventureros";
        int nivel = 1;

        // Los enviamos a la vista
        model.addAttribute("mensaje", mensaje);
        model.addAttribute("gremio", gremio);
        model.addAttribute("nivel", nivel);

        return "saludo";   // busca templates/saludo.html
    }

    @GetMapping("/aventurero")
    public String aventurero(Model model) {
        Aventurero aventurero = new Aventurero("Aragorn", "Guerrero", 20);

        model.addAttribute("aventurero", aventurero);

        return "aventurero";   // busca templates/aventurero.html
    }
}