package com.grupoAutogestion.sistemaautogestion.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
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

    private static final String APIURL = "https://poo2024.unsada.edu.ar/sistema_login/login";

    @Override
    public Authentication authenticate(Authentication autentificacion) throws AuthenticationException {
        String username = autentificacion.getName();
        String password = autentificacion.getCredentials().toString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String jsonBody = String.format("{\"username\": \"%s\", \"password\": \"%s\"}", username, password);
        HttpEntity<String> request = new HttpEntity<>(jsonBody, headers);

        try {
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(APIURL, request, String.class);

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                String respuesta = responseEntity.getBody();
                List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));

                autentificacion = new UsernamePasswordAuthenticationToken(username, password, authorities);
                ((UsernamePasswordAuthenticationToken) autentificacion).setDetails(respuesta);
                //Testeo
                //System.out.println(respuesta);
                return autentificacion;
            } else {
                throw new BadCredentialsException("Username o Password invalidos");
            }
        } catch (Exception e) {
            throw new BadCredentialsException("Error", e);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
