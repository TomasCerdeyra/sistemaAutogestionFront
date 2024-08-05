package com.grupoAutogestion.sistemaautogestion.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException;

@Controller
public class HomeController {

    private static final String API_GESTION_BASE_URL = "http://poo-dev.unsada.edu.ar:8082/sistema_gestion/";
    private static final String API_AUTOGESTION_BASE_URL = "https://poo2024.unsada.edu.ar/sistema_autogestion/";
    private static final String API_LOG_URL = "http://poo-dev.unsada.edu.ar:8080/sistema_autogestion/logs/";

    private RestTemplate restTemplate = new RestTemplate();

    // METEDOS PARA EL HOME
    //Traer todos los cursos
    @GetMapping("/home")
    public String getHome(Model model) throws JsonProcessingException {
        String url = API_GESTION_BASE_URL + "courses";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(response.getBody());

        List<Course> courses = new ArrayList<>();

        for (JsonNode node : rootNode) {
            Course course = new Course();
            course.setCourseId(node.get(0).asText().split(": ")[1]);
            course.setCourseName(node.get(1).asText().split(": ")[1]);
            course.setDescription(node.get(2).asText().split(": ")[1]);

            JsonNode studentsNode = node.get(3).get("students");
            List<String> students = new ArrayList<>();
            studentsNode.forEach(student -> students.add(student.asText()));
            course.setStudents(students);

            courses.add(course);
        }

        model.addAttribute("courses", courses);

        String studentId = getUser();
        model.addAttribute("studentId", studentId);
        return "/home"; // Nombre de la vista 
    }

    // Dar de alta un curso
    @PostMapping("/courses/enroll/alta")
    public String enrollStudent(@RequestParam String courseId, @RequestParam String studentId) {
        String url = API_GESTION_BASE_URL + "courses/" + courseId + "/enroll/student" + studentId;

        try {
            // Envio del evento del usuario con el tiempo
            String studnetID = "student" + studentId;  
            UserEvent event = new UserEvent(studnetID, courseId, "alta");
            restTemplate.postForObject(API_LOG_URL, event, Void.class);
            // Intento de alta en el curso
            restTemplate.postForObject(url, null, Void.class);
            return "redirect:/home";
        } catch (Exception e) {
            // Cualquier otro error inesperado
            e.printStackTrace(); // Para depuración
            return "redirect:/home";
        }
    }

    //Metodo para dar de baja un curso
    @PostMapping("/courses/unenroll/baja") //Esto es un delete despues cuando tenga la API de cursos
    public String unenrollStudent(@RequestParam String courseId, @RequestParam String studentId) {
        String url = API_GESTION_BASE_URL + "courses/" + courseId + "/enroll/" + studentId;
        restTemplate.delete(url);

        //Envio el evento del usurio con el tiempo
        UserEvent event = new UserEvent(studentId, courseId, "baja");
        restTemplate.postForObject(API_LOG_URL, event, Void.class);

        return "redirect:/myCourses";
    }

    //METODOS PARA CURSOS DE UN AUMNO
    //Traer los cursos de un alumno
    @GetMapping("/myCourses")
    public String getCourses(Model model) {
        String studentId = getUser();

        String studentUrl = API_GESTION_BASE_URL + "students/student" + studentId;
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper objectMapper = new ObjectMapper();

        // Obtener datos del estudiante
        ResponseEntity<String> studentResponse = restTemplate.getForEntity(studentUrl, String.class);
        List<String> courseIds = new ArrayList<>();
        Student estudiante = new Student();

        try {
            JsonNode rootNode = objectMapper.readTree(studentResponse.getBody());

            for (JsonNode node : rootNode) {
                if (node.isTextual()) {
                    String text = node.asText();
                    if (text.startsWith("studentId: ")) {
                        estudiante.setStudentId(text.replace("studentId: ", ""));
                    } else if (text.startsWith("firstName: ")) {
                        estudiante.setFirstName(text.replace("firstName: ", ""));
                    } else if (text.startsWith("lastName: ")) {
                        estudiante.setLastName(text.replace("lastName: ", ""));
                    }
                } else if (node.isObject() && node.has("courses")) {
                    JsonNode coursesNode = node.get("courses");
                    for (JsonNode courseNode : coursesNode) {
                        courseIds.add(courseNode.asText());
                    }
                    estudiante.setCourses(courseIds);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }

        // Obtener todos los cursos
        String allCoursesUrl = API_GESTION_BASE_URL + "courses";
        ResponseEntity<String> allCoursesResponse = restTemplate.getForEntity(allCoursesUrl, String.class);
        List<Course> allCourses = new ArrayList<>();

        try {
            JsonNode allCoursesNode = objectMapper.readTree(allCoursesResponse.getBody());
            for (JsonNode node : allCoursesNode) {
                Course course = new Course();
                course.setCourseId(node.get(0).asText().split(": ")[1]);
                course.setCourseName(node.get(1).asText().split(": ")[1]);
                course.setDescription(node.get(2).asText().split(": ")[1]);

                JsonNode studentsNode = node.get(3).get("students");
                List<String> students = new ArrayList<>();
                studentsNode.forEach(student -> students.add(student.asText()));
                course.setStudents(students);

                allCourses.add(course);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }

        // Filtrar cursos del estudiante
        List<Course> studentCourses = new ArrayList<>();
        for (String courseId : estudiante.getCourses()) {
            for (Course course : allCourses) {
                if (course.getCourseId().equals(courseId)) {
                    studentCourses.add(course);
                    break;
                }
            }
        }

        System.out.print(studentCourses);

        model.addAttribute("estudiante", estudiante.getStudentId());
        model.addAttribute("courses", studentCourses);

        return "myCourses";
    }

    //Traer el id del usuario desde la api
    private String getUser() {
        try {
            // Obtener el userID del usuario autenticado
            Authentication autentificacion = SecurityContextHolder.getContext().getAuthentication();
            String respuestaAPI = autentificacion.getDetails().toString();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(respuestaAPI);
            String userId = jsonNode.get("userId").asText();

            // Hacer una petición para obtener el studentID
            String url = API_GESTION_BASE_URL + "/users/user" + userId;
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(url, String.class);
            JsonNode responseJson = objectMapper.readTree(response);

            // Obtener el studentID del JSON de respuesta
            return responseJson.get("studentID").asText();
        } catch (IOException e) {
            return "Not Found";
        }
    }
;
}

// //String userId =  jsonNode.get("userId").asText();
           // String url = API_GESTION_BASE_URL + "users/" + userId ;

