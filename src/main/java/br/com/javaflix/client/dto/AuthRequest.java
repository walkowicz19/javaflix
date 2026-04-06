package br.com.javaflix.client.dto;

public class AuthRequest {
    public String identity; // email ou username
    public String password;
    
    public AuthRequest() {}
    
    public AuthRequest(String identity, String password) {
        this.identity = identity;
        this.password = password;
    }
}

// Made with Bob
