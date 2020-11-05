package com.example.mosis_ispit.addon;

public class User {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String number;

    public User(String user, String fn, String ln, String em, String pass, String numb) {
        this.username = user;
        this.firstName = fn;
        this.lastName = ln;
        this.email = em;
        this.password = pass;
        this.number = numb;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
