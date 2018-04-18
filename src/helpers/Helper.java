package helpers;

import java.text.Normalizer;
import java.util.regex.Pattern;

public final class Helper {

    private static final Pattern nonCharacterBeginPattern           = Pattern.compile("^[^\\p{L}\\p{Digit}]*",
            Pattern.UNICODE_CHARACTER_CLASS);
    private static final Pattern inCombiningDiacriticalMarksPattern = Pattern
            .compile("\\p{InCombiningDiacriticalMarks}+", Pattern.UNICODE_CHARACTER_CLASS);

    public static String removeNonCharacterBegin(String line) {
        return nonCharacterBeginPattern.matcher(line).replaceAll("");
    }

    public static String unAccent(String line) {
        line = Normalizer.normalize(line, Normalizer.Form.NFD);
        return inCombiningDiacriticalMarksPattern.matcher(line).replaceAll("").replaceAll("Đ", "D").replace("đ", "d");
    }
}
