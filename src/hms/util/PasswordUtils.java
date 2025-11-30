package hms.util;

import java.security.MessageDigest;
import java.security.SecureRandom;

public class PasswordUtils {

    public static String hashPassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Пароль не может быть нулевым или пустым.");
        }

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes("UTF-8"));

            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Ошибка хеширования пароля", e);
        }
    }

    public static boolean isStrongPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasLetter = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) {
                hasLetter = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else if (!Character.isWhitespace(c)) {
                hasSpecial = true;
            }
        }

        return hasLetter && hasDigit && hasSpecial;
    }

    public static String generateRandomPassword(int length) {
        if (length < 8) length = 8;

        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < length; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }

        return password.toString();
    }

    public static boolean verifyPassword(String password, String hash) {
        if (password == null || hash == null) return false;
        return hashPassword(password).equals(hash);
    }
}
