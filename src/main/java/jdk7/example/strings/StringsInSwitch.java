package jdk7.example.strings;

/**
 * Strings in switch Statements (switch 语句中的字符串).
 * <p/>
 * The Java compiler generates generally more efficient bytecode from switch statements
 * that use String objects than from chained if-then-else statements.
 *
 * @author xingle
 * @since 2016年07月09日 10:44
 */
public class StringsInSwitch {

    /**
     * use a String object in the expression of a switch statement:
     */
    public String getTypeOfDayWithSwitchStatement(String dayOfWeek) {
        String typeOfDay;
        switch (dayOfWeek) {
            case "Monday": // 区别点
                typeOfDay = "Start of work week";
                break;
            case "Tuesday":
            case "Wednesday":
            case "Thursday":
                typeOfDay = "Midweek";
                break;
            case "Friday":
                typeOfDay = "End of work week";
                break;
            case "Saturday":
            case "Sunday":
                typeOfDay = "Weekend";
                break;
            default:
                throw new IllegalArgumentException("Invalid day of the week: " + dayOfWeek);
        }
        return typeOfDay;
    }

}
