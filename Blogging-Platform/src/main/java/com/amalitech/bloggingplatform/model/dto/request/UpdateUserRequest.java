package com.amalitech.bloggingplatform.model.dto.request;

public class UpdateUserRequest {
    private String id;
    private String email;
    private String username;
    private String password;

    public UpdateUserRequest() {
    }
    public UpdateUserRequest(String id, String email, String username, String password) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.password = password;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getEmail() {
        return email;
    }
    public String getUsername() {
        return username;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getPassword() {
        return password;
    }
}
