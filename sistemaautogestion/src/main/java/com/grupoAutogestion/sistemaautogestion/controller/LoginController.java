/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.grupoAutogestion.sistemaautogestion.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication autenticacion = SecurityContextHolder.getContext().getAuthentication();
        if (autenticacion != null){
            new SecurityContextLogoutHandler().logout(request, response, autenticacion);
        }
        return "redirect:/login";
    }
}
