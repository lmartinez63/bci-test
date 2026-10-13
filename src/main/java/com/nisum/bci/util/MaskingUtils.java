package com.nisum.bci.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MaskingUtils {

    private static final String MASK = "***";

    /**
     * Masks the local part of an email to avoid writing personal data in the logs
     * @param email - email
     * @return - masked email, e.g. j***@rodriguez.org
     */
    public static String maskEmail(String email) {
        if (email == null || email.isBlank()) {
            return email;
        }
        int atIndex = email.indexOf('@');
        if (atIndex <= 0) {
            return MASK;
        }
        return email.charAt(0) + MASK + email.substring(atIndex);
    }
}
