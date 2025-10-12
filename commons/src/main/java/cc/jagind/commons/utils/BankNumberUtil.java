package cc.jagind.commons.utils;

import java.security.SecureRandom;
import java.util.concurrent.ThreadLocalRandom;

public final class BankNumberUtil {

    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Generate a US-style ABA routing number (9 digits) with a valid checksum.
     */
    public static String generateRoutingNumber() {
        int[] digits = new int[9];

        for (int i = 0; i < 8; i++) {
            digits[i] = RANDOM.nextInt(10);
        }

        int[] weights = {3, 7, 1};
        int sum = 0;
        for (int i = 0; i < 8; i++) {
            sum += digits[i] * weights[i % 3];
        }

        int checkDigit = (10 - (sum % 10)) % 10;
        digits[8] = checkDigit;

        StringBuilder stringBuilder = new StringBuilder(9);
        for (int digit : digits) {
            stringBuilder.append(digit);
        }
        return stringBuilder.toString();
    }

    /**
     * Generate a random bank account number as a numeric string,
     * which avoids leading zeros (so it looks realistic)
     */
    public static String generateAccountNumber() {
        int len = ThreadLocalRandom.current().nextInt(9, 13);
        StringBuilder stringBuilder = new StringBuilder(len);

        stringBuilder.append(1 + RANDOM.nextInt(9));

        for (int i = 1; i < len; i++) {
            stringBuilder.append(RANDOM.nextInt(10));
        }
        return stringBuilder.toString();
    }
}

