package com.akshathsaipittala.streamspace.utils;

import java.util.UUID;

public final class HelperFunctions {

    // Define a method to generate a unique movieCode (e.g., using UUID)
    public static String generateUniqueCode() {
        return UUID.randomUUID().toString();
    }

    private HelperFunctions() {}

}
