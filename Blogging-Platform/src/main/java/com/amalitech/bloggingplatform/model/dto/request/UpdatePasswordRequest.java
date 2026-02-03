package com.amalitech.bloggingplatform.model.dto.request;

public class UpdatePasswordRequest {
    private String password;

    public UpdatePasswordRequest() {}
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
