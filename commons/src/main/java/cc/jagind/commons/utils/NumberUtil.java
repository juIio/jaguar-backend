package cc.jagind.commons.utils;

import java.util.Random;

public class NumberUtil {

    private static final Random RANDOM = new Random();

    public static String generateVerificationCode() {
        return generateVerificationCode(6);
    }

    public static String generateVerificationCode(int length) {
        char[] chars = new char[length];

        for (int i = 0; i < length; i++) {
            chars[i] = (char) (RANDOM.nextInt(26) + 97);
        }

        return new String(chars);
    }

}
