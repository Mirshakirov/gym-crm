package com.gymcrm.util;

import lombok.extern.slf4j.Slf4j;
import java.util.Random;
import java.util.function.Function;

@Slf4j
public class UserCredentialsGenerator {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
    private static final int PASSWORD_LENGTH = 10;
    private static final Random RANDOM = new Random();

    public static String generateUsername(String firstName, String lastName, Function<String, Boolean> existingUsernames) {
        if (firstName == null || firstName.trim().isEmpty() || 
            lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name and last name cannot be null or empty");
        }

        String baseUsername = firstName.trim() + "." + lastName.trim();
        String username = baseUsername;
        int counter = 1;

        log.debug("Generating username for {} {}", firstName, lastName);

        while (existingUsernames.apply(username)) {
            username = baseUsername + counter;
            counter++;
            log.debug("Username {} already exists, trying {}", baseUsername, username);
        }

        log.info("Generated username: {}", username);
        return username;
    }

    public static String generatePassword() {
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);
        
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            password.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }

        log.debug("Generated password");
        return password.toString();
    }
}
