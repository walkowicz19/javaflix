package br.com.javaflix.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserRecord {
    public String id;
    
    @JsonProperty("collectionId")
    public String collectionId;
    
    @JsonProperty("collectionName")
    public String collectionName;
    
    public String created;
    public String updated;
    public String username;
    public String email;
    public boolean verified;
    public String name;
    public int idade;
    
    @JsonProperty("tipo_assinatura")
    public String tipoAssinatura;
}

// Made with Bob
