package br.com.javaflix.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserRequest {
    public String username;
    public String email;
    public String password;
    public String passwordConfirm;
    public String name;
    public int idade;
    
    @JsonProperty("tipo_assinatura")
    public String tipoAssinatura;
}

// Made with Bob
