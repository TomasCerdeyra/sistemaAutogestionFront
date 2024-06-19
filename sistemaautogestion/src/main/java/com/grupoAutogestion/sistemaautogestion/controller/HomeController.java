package com.grupoAutogestion.sistemaautogestion.controller;

import org.springframework.ui.Model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grupoAutogestion.sistemaautogestion.model.Course;
import java.util.Arrays;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.client.RestTemplate;
import java.io.IOException;

@Controller
public class HomeController {

    private static final String API_GESTION_BASE_URL = "https://poo2024.unsada.edu.ar/sistema_gestion/";
    private static final String API_AUTOGESTION_BASE_URL = "https://poo2024.unsada.edu.ar/sistema_autogestion/";

    private RestTemplate restTemplate = new RestTemplate();

    //Metodo para traer todos los cursos
    @GetMapping("/home")
    public String getHome(Model model) {
        String url = API_GESTION_BASE_URL + "courses";
        ResponseEntity<Course[]> response = restTemplate.getForEntity(url, Course[].class);
        
        Course[] courses = response.getBody();
        model.addAttribute("courses", courses);
        String studentId = getUser(); // Reemplaza esto con la lógica para obtener el ID del estudiante
        model.addAttribute("studentId", studentId);
        return "/home"; // Nombre de la vista 
    }
    
    //Metodo para traer los cursos de un alumno

    //Metodo para dar de alta un curso
    @PostMapping("/courses/enroll")
    public String enrollStudent(@RequestParam Long courseId, @RequestParam String studentId) {
        String url = API_AUTOGESTION_BASE_URL + "courses/" + courseId + "/enroll/" + studentId;
        restTemplate.postForObject(url, null, Void.class);
        return "redirect:/home"; // Redirigir a home después de matricular al estudiante
    }

    //Metodo para dar de baja un curso
    @DeleteMapping("/courses/unenroll")
    public String unenrollStudent(@RequestParam Long courseId, @RequestParam String studentId) {
        String url = API_AUTOGESTION_BASE_URL + "courses/" + courseId + "/enroll/" + studentId;
        restTemplate.delete(url);
        return "redirect:/miCourses"; // Redirigir a home después de dar de baja al estudiante
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
