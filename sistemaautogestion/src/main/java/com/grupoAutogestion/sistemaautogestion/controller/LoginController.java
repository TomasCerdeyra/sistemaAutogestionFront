/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.grupoAutogestion.sistemaautogestion.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;

@Controller
public class LoginController {

    private String[] integrantesGrupo2 = {"Tomas Cerdeyra","Franco Leszkiewicz","Facundo Potes","Tobias Posse", "Matias Lopez","Martin Aguilera","Gino D'Agostino","Juan Cruz Dauberte","Martín Peralta"};

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping("/login/informacion")
    public String showInformationPage(Model model) {
        model.addAttribute("integrantesGrupo2", integrantesGrupo2);
        return "informacion"; 
    }

    //Testeo con una vista la conexion con la API login
    @GetMapping("/testLogs") //Remplazar por la vista 2.Cursos
    public String showResultPage(Model model) {
        Authentication autentificacion = SecurityContextHolder.getContext().getAuthentication();
        String respuestaAPI = autentificacion.getDetails().toString();
        model.addAttribute("autentificacion", respuestaAPI);
        return "testLogs";
    }
}
