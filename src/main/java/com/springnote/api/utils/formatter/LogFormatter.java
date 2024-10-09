package com.springnote.api.utils.formatter;

public class LogFormatter {

    public static String createExceptionString(String ExceptionName, String message) {
        return "Raise ( " + ExceptionName + " ) -  Message : ( " + message + " )";
    }


}
