package com.example.thepwnedgame.users;

public class User {

    private String username;
    private String email;
    private Integer userId;

    public User(String username, Integer userId, String email) {
        this.username = username;
        this.userId = userId;
        this.email = email;
    }


    public String getUsername() {
        return username;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

}
