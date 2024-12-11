package com.example.smartliving.Handlers;

public class DeleteFamilyUserHandler {
    private String first_name, last_name, email, key, profile_img;

    DeleteFamilyUserHandler(){} //empty constructor

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setProfile_img(String profile_img) {
        this.profile_img = profile_img;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getEmail() {
        return email;
    }

    public String getKey() {
        return key;
    }

    public String getProfile_img() {
        return profile_img;
    }
}
