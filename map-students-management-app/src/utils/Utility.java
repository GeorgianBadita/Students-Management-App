package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Class for utility methods
 */
public class Utility {
    /**
     * Current Week getter static method
     * @return - the current week
     */
    public static Integer getCurrWeek(){
        SimpleDateFormat myFormat = new SimpleDateFormat("dd MM yyyy");
        String yearStartStr = "01 10 2018";
        String nowStr = myFormat.format(new Date());

        // System.out.println(yearStartStr);
        // System.out.println(nowStr);

        int days = 0;
        try {

            Date yearStart =  myFormat.parse(yearStartStr);
            Date now = myFormat.parse(nowStr);
            long diff = now.getTime() - yearStart.getTime();
            days = Math.toIntExact(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));

        } catch (ParseException e) {
            e.printStackTrace();
        }
        int weeks = days/7 + 1;
        if(weeks <= 12){
            return weeks;
        }
        else if(weeks == 13 || weeks == 14){
            return 0;
        }
        return weeks - 2;
    }

    /**
     * Returns number of weeks between date1 and date2
     * @param date1 - Date type object
     * @param date2 - Date type object
     * @return - integer number of weeks between date1 and date2
     */
    public static long numWeeksBetweenTwoDates(Date date1, Date date2){
        LocalDate date1ToLocal = date1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate date2ToLocal = date2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return ChronoUnit.WEEKS.between(date1ToLocal, date2ToLocal);
    }

    /**
     * Function to convert a local date to a date
     * @param ldate - locaDate object
     * @return - date object
     */
    public static Date convertLocalDateTodate(LocalDate ldate){
        Calendar c = Calendar.getInstance();
        c.set(ldate.getYear(), ldate.getMonthValue() - 1, ldate.getDayOfMonth());
        return c.getTime();
    }

    /**
     * Function to get the content of a file as string
     * @param path - path to the file
     * @return string containing the file
     */
    public static String getFileContent(String path) {
        StringBuilder str = new StringBuilder();
        try(BufferedReader br = new BufferedReader(new FileReader(new File(path)))) {
            String line;
            while((line = br.readLine()) != null){
                str.append(line + "\n");
            }
            return str.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Function which checks if the ith bit in a number is one
     * @param nr -number to check
     * @param i - bit position
     * @return - true if the ith bit in the number is 1, false otherwise
     */
    public static boolean ithBitOne(Integer nr, int i){
        return (nr & (1 << i)) != 0;
    }
}
