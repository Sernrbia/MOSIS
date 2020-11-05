package com.example.mosis_ispit.addon;

public class Validator {

    public static void isEmpty(String field, String nameOfField) throws IllegalArgumentException {
        if (field == null || field.isEmpty())
            throw new IllegalArgumentException("Field " + nameOfField + " can't be blank.");
    }

    public static void checkMatch(String password, String confirmPassword) throws IllegalArgumentException {
        if(!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Passwords don't match!");
        }
    }

}
