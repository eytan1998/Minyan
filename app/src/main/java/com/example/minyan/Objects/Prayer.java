package com.example.minyan.Objects;

public class Prayer {
    private String Email;
    private String Password;

    private String name;

    public Prayer() {
    }

    public Prayer(String email, String password) {
        Email = email;
        Password = password;
        name = email.substring(5);
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Prayer{" +
                "Email='" + Email + '\'' +
                ", Password='" + Password + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
