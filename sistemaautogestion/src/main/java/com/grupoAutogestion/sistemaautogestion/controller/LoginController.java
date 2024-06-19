/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.grupoAutogestion.sistemaautogestion.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
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

    //REMPLAZAR POR LA VISTA DE CURSOS
    //Testeo con una vista la conexion con la API login
    @GetMapping("/testLogs") //Remplazar por la vista 2.Cursos
    public String showResultPage(Model model) {
        model.addAttribute("autentificacion", getUser());
        return "testLogs";
    }

    //METODO PARA LA BD (TRASLADAR A SU RESPECTIVO CONTROLADOR)
    private String getUser(){
        try {
            Authentication autentificacion = SecurityContextHolder.getContext().getAuthentication();
            String respuestaAPI = autentificacion.getDetails().toString();
            // Respuesta user1: {"token":"66484954fe42e9f79500097bda2f784fb9dea7c479bc2e6374a3f3491a9f51ea","userId":"user1","3600":3600}
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(respuestaAPI);
            return jsonNode.get("userId").asText();
        } catch (IOException e) {
            return "Not Found";
        }
    };
}
