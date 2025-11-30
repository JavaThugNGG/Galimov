package hms.util;

import java.util.regex.Pattern;

public class ValidationUtils {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^[+]?[0-9]{10,15}$"
    );

    private static final Pattern ID_PATTERN = Pattern.compile(
            "^[A-Za-z0-9-_]+$"
    );

    public static boolean isValidEmail(String email) {
        return email != null && !email.trim().isEmpty() && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) return false;
        String cleanPhone = phone.replaceAll("[\\s()-]", "");
        return PHONE_PATTERN.matcher(cleanPhone).matches();
    }

    public static boolean isValidId(String id) {
        return id != null && !id.trim().isEmpty() && ID_PATTERN.matcher(id.trim()).matches();
    }

    public static boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty() && name.trim().length() <= 100 && name.trim().length() >= 2;
    }

    public static boolean isValidAge(int age) {
        return age > 0 && age <= 120;
    }

    public static boolean isValidPrice(double price) {
        return price >= 0 && price <= 999999.99;
    }

    public static boolean isValidQuantity(int quantity) {
        return quantity >= 0 && quantity <= 999999;
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6 && password.length() <= 50;
    }

    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }

    public static String sanitizeString(String input) {
        if (input == null) return "";
        return input.trim().replaceAll("[,\n\r]", " ");
    }
}
