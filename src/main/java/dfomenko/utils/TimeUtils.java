package dfomenko.utils;

import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.ParseException;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;

@Component
public class TimeUtils {

    public Calendar getCalendarFromDateString(final String newDateString, final Timestamp timeSource) throws ParseException {
        // TODO: 21.05.2023 Проверить на Exceptions
        if (newDateString == null) {
            throw new NullPointerException("Source string \"newDateString\" is null.");
        }

        String[] values = newDateString.split("-");
        if (values.length != 3) {
            throw new ParseException("Source String not in format \"yyyy-MM-dd\", but present " + newDateString, 0);
        }

        int newYear = Integer.parseInt(values[0]);
        int newMonth = Integer.parseInt(values[1]);
        int newDay = Integer.parseInt(values[2]);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeSource.getTime());

        if (newYear < 1970) {
            throw new ParseException("Source String year must be greater then 1970, but present " + newYear, 0);
        }

        if ((newMonth < 1) || (newMonth > 12)) {
            throw new ParseException("Source String month must be in range 1..12, but present " + newMonth, 5);
        }

        newMonth -= 1; // Month starts from 0 - !!

        calendar.set(Calendar.YEAR, newYear);
        calendar.set(Calendar.MONTH, newMonth);

        int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        if ((newDay < 1) || (newDay > maxDays)) {
            throw new ParseException("Source String day must be in range [1.." + String.valueOf(maxDays) + "], but present " + newDay, 0);
        }

        calendar.set(Calendar.DAY_OF_MONTH, newDay);

        return calendar;
    }

    public Calendar getCalendarFromDateString(final String dateString) throws ParseException {
        // TODO: 21.05.2023 Проверить на Exceptions
        return getCalendarFromDateString(dateString, new Timestamp(0));
    }

    public Timestamp getTimestampFromDateString(final String newDateString, final Timestamp timeSource) throws ParseException {
        // TODO: 21.05.2023 Проверить на Exceptions
        return new Timestamp(getCalendarFromDateString(newDateString, timeSource).getTimeInMillis());
    }

    public Timestamp getTimestampFromDateString(final String dateString) throws ParseException {
        // TODO: 21.05.2023 Проверить на Exceptions
        return getTimestampFromDateString(dateString, new Timestamp(0));
    }


    public Calendar getCalendarFromTimeString(final String newTimeString, final Timestamp dateSource) throws ParseException {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateSource.getTime());
// TODO: 21.05.2023  Exceptions???
        if (newTimeString == null) {
            throw new NullPointerException("Source string \"newTimeString\" is null.");
        }

        String[] values = newTimeString.split(":");
        if (values.length != 2) {
            throw new ParseException("Source String not in format \"HH:mm\", but present " + newTimeString, 0);
        }

        int newHours = Integer.parseInt(values[0]);
        int newMinutes = Integer.parseInt(values[1]);

        if ((newHours < 0) || (newHours > 23)) {
            throw new ParseException("Source String not in values of \"HH:mm\". Present " + newTimeString, 0);
        }

        calendar.set(Calendar.HOUR_OF_DAY, newHours);
        calendar.set(Calendar.MINUTE, newMinutes);

        return calendar;
    }

    public Calendar getCalendarFromTimeString(final String newTimeString) throws ParseException {
        return getCalendarFromTimeString(newTimeString, new Timestamp(0));
    }

    public Timestamp getTimestampFromTimeString(final String newTimeString, final Timestamp dateSource) throws ParseException {
        return new Timestamp(getCalendarFromTimeString(newTimeString, dateSource).getTimeInMillis());
    }

    public Timestamp getTimestampFromTimeString(final String newTimeString) throws ParseException {
        return new Timestamp(getCalendarFromTimeString(newTimeString).getTimeInMillis());
    }

    public String getDateString(Timestamp sourceDate) {
        return sourceDate.toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault()));
    }

    public String getTimeString(Timestamp sourceTime) {
        return sourceTime.toLocalDateTime().format(DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault()));
    }


    public String getDateStringNotEarlierThenNow(final String sourceDateString) throws ParseException {
        Timestamp sourceDate = getTimestampFromDateString(sourceDateString);
        Timestamp nowTime = getTimeNow();
        if (nowTime.after(sourceDate)) {
            return getDateString(nowTime);
        }
        return sourceDateString;
    }

    public String getTimeStringNotEarlierThenNow(final String sourceTimeString) throws ParseException {
        Timestamp sourceTime = getTimestampFromTimeString(sourceTimeString);
        Timestamp nowTime = getTimeNow();
        if (nowTime.after(sourceTime)) {
            return getDateString(nowTime);
        }
        return sourceTimeString;
    }


    public Timestamp getTimeNow() {
        return new Timestamp(System.currentTimeMillis());
    }

    public String getDateNowString() {
        return getDateString(getTimeNow());
    }

    public Timestamp getDateNow() {
        return new Timestamp(System.currentTimeMillis());
    }

    public String getTimeNowString() {
        return getTimeString(getTimeNow());
    }

}
