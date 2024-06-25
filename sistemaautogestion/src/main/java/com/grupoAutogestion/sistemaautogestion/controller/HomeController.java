package com.grupoAutogestion.sistemaautogestion.controller;

import org.springframework.ui.Model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grupoAutogestion.sistemaautogestion.model.Course;
import com.grupoAutogestion.sistemaautogestion.model.Student;
import com.grupoAutogestion.sistemaautogestion.model.UserEvent;
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
import java.util.Collections;

@Controller
public class HomeController {

    private static final String API_GESTION_BASE_URL = "https://poo2024.unsada.edu.ar/sistema_gestion/";
    private static final String API_AUTOGESTION_BASE_URL = "https://poo2024.unsada.edu.ar/sistema_autogestion/";
    private static final String API_LOG_URL = "http://poo-dev.unsada.edu.ar:8080/sistema_autogestion/logs/";
    
    private RestTemplate restTemplate = new RestTemplate();
    
    // METEDOS PARA EL HOME
    
    //Traer todos los cursos
    @GetMapping("/home")
    public String getHome(Model model) {      
        String url = API_GESTION_BASE_URL + "courses";
        ResponseEntity<Course[]> response = restTemplate.getForEntity(url, Course[].class);
        
        Course[] courses = response.getBody();
        model.addAttribute("courses", courses);
        
        String studentId = getUser(); 
        model.addAttribute("studentId", studentId);
        return "/home"; // Nombre de la vista 
    }

    // Dar de alta un curso
    @PostMapping("/courses/enroll/alta")
    public String enrollStudent(@RequestParam String courseId, @RequestParam String studentId) {
        //String url = API_AUTOGESTION_BASE_URL + "courses/" + courseId + "/enroll/" + studentId;
        
        //Doy de alta en un curso al alumno
        //restTemplate.postForObject(url, null, Void.class);
        
        //Envio el evento del usurio con el tiempo 
        UserEvent event = new UserEvent(studentId,courseId, "alta");
        restTemplate.postForObject(API_LOG_URL, event, Void.class);
        return "redirect:/home"; 
    }

    //Metodo para dar de baja un curso
    @PostMapping("/courses/unenroll/baja") //Esto es un delete despues cuando tenga la API de cursos
    public String unenrollStudent(@RequestParam String courseId,@RequestParam String studentId) {
        //String url = API_AUTOGESTION_BASE_URL + "courses/" + courseId + "/enroll/" + studentId;
        //restTemplate.delete(url);
        
        //Envio el evento del usurio con el tiempo 
        UserEvent event = new UserEvent(studentId,courseId, "baja");
        restTemplate.postForObject(API_LOG_URL, event, Void.class);
        
        return "redirect:/myCourses";
    }
    
    //METODOS PARA CURSOS DE UN AUMNO
    
    //Traer los cursos de un alumno
    @GetMapping("/myCourses")
    public String getCourses(Model model) {
        String studentId = getUser();
        if ("Not Found".equals(studentId)) {
            // Si el id no se encuentra
            return "error";
        }

        String url = API_GESTION_BASE_URL + "students/" + "student2"; //Students2 se cambiaria por studentId
        ResponseEntity<Student> response = restTemplate.getForEntity(url, Student.class);
        Student student = response.getBody();

        if (student.getCourses() == null) {
            student.setCourses(Collections.emptyList());
        }

        model.addAttribute("student", student);
        model.addAttribute("courses", student.getCourses());

        return "myCourses"; 
    }

    //Traer el id del usuario desde la api
    private String getUser(){
        try {
            Authentication autentificacion = SecurityContextHolder.getContext().getAuthentication();
            String respuestaAPI = autentificacion.getDetails().toString();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(respuestaAPI);
            return jsonNode.get("userId").asText();
        } catch (IOException e) {
            return "Not Found";
        }
    };
}
