package com.springnote.api.utils.formatter;

public class ExceptionMessageFormatter {

    public static String createItemNotFoundMessage(String name, String item) {
        return "( " + name + " ) 에 해당하는 " + item + "(이)가 존재하지 않습니다.";
    }

    public static String createItemAlreadyExistMessage(String name, String item) {
        return "( " + name + " ) 에 해당하는 " + item + "(이)가 이미 존재합니다.";
    }

    public static String createFailedVerifyMessage(String name, String role) {
        return "( " + name + " ) 는 ( " + role + " ) 을(를) 만족하지 않습니다.";
    }

    public static String createBadPermissionMessage() {
        return "권한이 없습니다.";
    }

}
