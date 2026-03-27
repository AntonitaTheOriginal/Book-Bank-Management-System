package com.bbms.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Loads and provides application-wide configuration values.
 */
public class AppConfig {

    private static final Properties props = new Properties();

    static {
        try (InputStream is = AppConfig.class.getClassLoader()
                .getResourceAsStream("database.properties")) {
            props.load(is);
        } catch (IOException e) {
            throw new RuntimeException("Cannot load application config", e);
        }
    }

    public static double getFineRatePerDay()   { return Double.parseDouble(props.getProperty("app.fine.rate", "1.0")); }
    public static double getMaxFine()          { return Double.parseDouble(props.getProperty("app.fine.max", "50.0")); }
    public static int    getGraceDays()        { return Integer.parseInt(props.getProperty("app.fine.grace.days", "2")); }
    public static int    getStudentLoanDays()  { return Integer.parseInt(props.getProperty("app.loan.days.student", "120")); }
    public static int    getFacultyLoanDays()  { return Integer.parseInt(props.getProperty("app.loan.days.faculty", "120")); }
    public static int    getMaxBooksStudent()  { return Integer.parseInt(props.getProperty("app.max.books.student", "5")); }
    public static int    getMaxBooksFaculty()  { return Integer.parseInt(props.getProperty("app.max.books.faculty", "10")); }
    public static double getMaxFineThreshold() { return Double.parseDouble(props.getProperty("app.max.fine.threshold", "50.0")); }

    public static String getMailHost()     { return props.getProperty("mail.smtp.host"); }
    public static String getMailPort()     { return props.getProperty("mail.smtp.port"); }
    public static String getMailUser()     { return props.getProperty("mail.username"); }
    public static String getMailPassword() { return props.getProperty("mail.password"); }
    public static String getMailFrom()     { return props.getProperty("mail.from"); }
}
