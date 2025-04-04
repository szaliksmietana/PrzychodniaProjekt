package pl.example.przychodniafx;

public class PeselValidator {

    private final byte[] PESEL = new byte[11];
    private boolean valid = false;

    public PeselValidator(String PESELNumber) {
        if (PESELNumber.length() != 11 || !PESELNumber.matches("\\d+")) {
            valid = false;
        } else {
            for (int i = 0; i < 11; i++) {
                PESEL[i] = Byte.parseByte(PESELNumber.substring(i, i + 1));
            }
            valid = checkSum() && checkMonth() && checkDay();
        }
    }

    public boolean isValid() {
        return valid;
    }

    private boolean checkSum() {
        int sum = 1 * PESEL[0] + 3 * PESEL[1] + 7 * PESEL[2] + 9 * PESEL[3] +
                1 * PESEL[4] + 3 * PESEL[5] + 7 * PESEL[6] + 9 * PESEL[7] +
                1 * PESEL[8] + 3 * PESEL[9];
        sum = (10 - (sum % 10)) % 10;
        return sum == PESEL[10];
    }

    private boolean checkMonth() {
        int month = getBirthMonth();
        return month > 0 && month < 13;
    }

    private boolean checkDay() {
        int day = getBirthDay();
        int month = getBirthMonth();
        int year = getBirthYear();

        if (day < 1) return false;
        if (month == 2) {
            return (leapYear(year) && day <= 29) || (!leapYear(year) && day <= 28);
        }
        return (month == 4 || month == 6 || month == 9 || month == 11) ? day <= 30 : day <= 31;
    }

    private boolean leapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }

    private int getBirthYear() {
        int year = 10 * PESEL[0] + PESEL[1];
        int month = 10 * PESEL[2] + PESEL[3];

        if (month > 80) return year + 1800;
        if (month > 60) return year + 2200;
        if (month > 40) return year + 2100;
        if (month > 20) return year + 2000;
        return year + 1900;
    }

    private int getBirthMonth() {
        int month = 10 * PESEL[2] + PESEL[3];
        if (month > 80) return month - 80;
        if (month > 60) return month - 60;
        if (month > 40) return month - 40;
        if (month > 20) return month - 20;
        return month;
    }

    private int getBirthDay() {
        return 10 * PESEL[4] + PESEL[5];
    }


    public boolean matchesBirthDate(String date) {
        try {
            String[] parts = date.split("-");
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int day = Integer.parseInt(parts[2]);
            return year == getBirthYear() &&
                    month == getBirthMonth() &&
                    day == getBirthDay();
        } catch (Exception e) {
            return false;
        }
    }
    public boolean matchesGender(Character gender) {
        if (!valid || gender == null) return false;

        int genderDigit = PESEL[9]; // 10. cyfra PESEL

        if (gender == 'M') {
            return genderDigit % 2 == 1; // nieparzysta dla mężczyzny
        } else if (gender == 'K') {
            return genderDigit % 2 == 0; // parzysta dla kobiety
        }

        return false; // nieoczekiwany przypadek
    }





}
