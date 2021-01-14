package com.example.android.arkanoid;

public class User {
    private String username;
    private String email;

    public User(String userName, String email) {
        this.username = userName;
        this.email = email;
    }


    public String getUsername() {
        return username;
    }

    public void setUsename(String userName) {
        this.username = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
