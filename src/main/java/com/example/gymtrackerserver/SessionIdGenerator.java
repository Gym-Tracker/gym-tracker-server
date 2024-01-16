package com.example.gymtrackerserver;

import java.security.SecureRandom;

public class SessionIdGenerator {
    private final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private final String NUMBERS = "0123456789";
    private final String tokens = UPPERCASE + LOWERCASE + NUMBERS;

    public String generateId() {
        SecureRandom random = new SecureRandom();
        StringBuilder randomTokens = new StringBuilder();

        for (int i = 0; i < 128; i++) {
            int randomInt = random.nextInt((tokens.length()));
            randomTokens.append(tokens.charAt(randomInt));
        }

        return(randomTokens.toString());
    }
}
