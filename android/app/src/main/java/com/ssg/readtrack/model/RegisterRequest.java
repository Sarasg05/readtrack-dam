package com.ssg.readtrack.model;

public class RegisterRequest {
    public String username;
    public String password;

    public RegisterRequest(String username, String password){
        this.username = username;
        this.password = password;
    }
}
