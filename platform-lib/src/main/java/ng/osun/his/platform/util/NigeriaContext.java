package ng.osun.his.platform.util;

import lombok.Getter;

import java.time.ZoneId;
import java.util.Currency;
import java.util.Locale;

/**
 * Nigeria-specific context constants.
 */
@Getter
public enum NigeriaContext {
    INSTANCE;

    public static final ZoneId TIMEZONE = ZoneId.of("Africa/Lagos");
    public static final Currency CURRENCY = Currency.getInstance("NGN");
    public static final Locale LOCALE = Locale.forLanguageTag("en-NG");

    private final String phonePrefix = "+234";
    private final String countryCode = "NG";

    /**
     * Validate Nigerian phone number.
     */
    public boolean isValidPhoneNumber(String phone) {
        if (phone == null || phone.isEmpty()) {
            return false;
        }
        // Remove spaces and dashes
        phone = phone.replaceAll("[\\s-]", "");
        // Check if starts with +234
        if (phone.startsWith("+234")) {
            return phone.length() == 14; // +234 123 456 7890
        }
        // Check if starts with 0 (local format)
        if (phone.startsWith("0")) {
            return phone.length() == 11;
        }
        return false;
    }

    /**
     * Validate NIN format (placeholder - implement actual validation).
     */
    public boolean isValidNIN(String nin) {
        if (nin == null || nin.isEmpty()) {
            return false;
        }
        // Placeholder: NIN is 11 digits
        return nin.matches("\\d{11}");
    }
}

