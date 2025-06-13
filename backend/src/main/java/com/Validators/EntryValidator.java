package com.Validators;

public class EntryValidator extends RuntimeException {
    public EntryValidator(String message) {
        super(message);
    }

    public static boolean validateInput(String name, int age, String c1, String c2) {
        if (name == null || name.isEmpty()) {
            throw new EntryValidator("Name cannot be empty.");
        }
        if (age < 6 || age > 15) {
            throw new EntryValidator("Age should be between 6 and 15.");
        }
        if (c1.isEmpty() && c2.isEmpty()) {
            throw new EntryValidator("At least one competition should be selected.");
        }
        if (!"Empty".equals(c1) && c1.equals(c2)) {
            throw new EntryValidator("Competitions should be different.");
        }
        return true;
    }

    public static boolean validateNameAndAge(String name, int age) {
        if (name == null || name.isEmpty()) {
            throw new EntryValidator("Name cannot be empty.");
        }
        if (age < 6 || age > 15) {
            throw new EntryValidator("Age should be between 6 and 15.");
        }
        return true;
    }
}
