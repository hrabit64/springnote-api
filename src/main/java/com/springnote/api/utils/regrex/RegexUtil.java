package com.springnote.api.utils.regrex;

public class RegexUtil {

    public final static String PASSWORD_REGEX = "^(?=.*[a-zA-Z0-9!@#$%&*])[\\w!@#$%&*]{4,20}$";
    public final static String URL_REGEX = "^(https?:\\/\\/)?(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)?$|^$";
}
