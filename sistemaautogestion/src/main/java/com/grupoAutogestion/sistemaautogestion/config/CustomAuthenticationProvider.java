package com.grupoAutogestion.sistemaautogestion.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.web.exchanges.HttpExchange.Principal;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private RestTemplate restTemplate;

    private static final String loginAPIURL = "http://poo-dev.unsada.edu.ar:4000/sistema_login/login";
    private static final String authorizeAPIURL = "http://poo-dev.unsada.edu.ar:4000/sistema_login/authorize";

    @Override
    public Authentication authenticate(Authentication autentificacion) throws AuthenticationException {
        String username = autentificacion.getName();
        String password = autentificacion.getCredentials().toString();

        HttpHeaders headersLogin = new HttpHeaders();
        headersLogin.setContentType(MediaType.APPLICATION_JSON);
        String jsonBodyLogin = String.format("{\"username\": \"%s\", \"password\": \"%s\"}", username, password);
        HttpEntity<String> requestLogin = new HttpEntity<>(jsonBodyLogin, headersLogin);

        try {
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(loginAPIURL, requestLogin, String.class);

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                String respuestaLogin = responseEntity.getBody();
                //Testeo
                System.out.println(respuestaLogin);

                HttpHeaders headersAuthorize = new HttpHeaders();
                headersAuthorize.setContentType(MediaType.APPLICATION_JSON);

                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(respuestaLogin);

                String jsonBodyAuthorize = String.format("{\"token\": \"%s\", \"systemId\": \"AUTOGESTION\"}", jsonNode.get("token").asText());
                HttpEntity<String> requestAuthorize = new HttpEntity<>(jsonBodyAuthorize, headersAuthorize);

                try{
                    ResponseEntity<String> responseEntityAuthorize = restTemplate.postForEntity(authorizeAPIURL, requestAuthorize, String.class);
                    if (responseEntityAuthorize.getStatusCode().is2xxSuccessful()) {
                        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
                        String respuestaAuthorize = responseEntityAuthorize.getBody();
                        JsonNode jsonNode1 = objectMapper.readTree(respuestaAuthorize);

                        System.out.println(respuestaAuthorize);
                        if (jsonNode1.get("authorized").asBoolean() == true){
                            autentificacion = new UsernamePasswordAuthenticationToken(username, password, authorities);
                            ((UsernamePasswordAuthenticationToken) autentificacion).setDetails(respuestaLogin);
                            return autentificacion;
                        }

                    }
                }
                catch(Exception e){
                    throw new BadCredentialsException("Username o Password invalidos");
                }
                
            } else {
                throw new BadCredentialsException("Username o Password invalidos");
            }
        } catch (Exception e) {
            throw new BadCredentialsException("Error", e);
        }
        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
