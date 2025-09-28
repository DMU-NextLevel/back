package NextLevel.demo.util;

import NextLevel.demo.exception.CustomException;
import NextLevel.demo.exception.ErrorCode;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class StringUtil {

    public static final String PHONE_NUMBER_FORMAT = "^010-\\d{4}-\\d{4}$";
    public static final String AREA_NUMBER_FORMAT = "^\\d{2,3}-\\d{3,4}-\\d{4}$";
    private static final String NO_FORMAT = "^\\d{11}$";

    public static LocalDate toLocalDate(String date) {
        try {
            // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd")); //LocalDateTime.of(new SimpleDateFormat("yyyy-MM-dd").parse(expired));
        }catch (Exception e) {
            e.printStackTrace();
            throw new CustomException(ErrorCode.ERROR_EXPIRED_DATE_CONVERSION, date);
        }
    }

    public static String setGetterName(String columnName) {
        String firstChar = columnName.substring(0, 1).toUpperCase();
        return "set" + firstChar + columnName.substring(1);
    }

    public static String getFormattedNumber(String number, String format) {
        if(number == null || number.length() == 0)
            return null;
        if(number.matches(NO_FORMAT)) {
            return String.format("%s-%s-%s",
                number.substring(0, 3),
                number.substring(3, 7),
                number.substring(7));
        }

        if(number.matches(format)) {
            return number;
        }

        throw new CustomException(ErrorCode.INVALID_NUMBER_FORMAT, number);
    }
}
