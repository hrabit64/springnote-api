package com.springnote.api.security.captcha;


public record RecaptchaResponse(
        boolean success,
        double score,
        String action,
        String challengeTs,
        String hostname,
        String[] errorCodes
) {

}
