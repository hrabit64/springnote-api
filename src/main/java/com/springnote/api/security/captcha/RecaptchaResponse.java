package com.springnote.api.security.captcha;

//{
//        "success": true|false,
//        "challenge_ts": timestamp,  // timestamp of the challenge load (ISO format yyyy-MM-dd'T'HH:mm:ssZZ)
//        "hostname": string,         // the hostname of the site where the reCAPTCHA was solved
//        "error-codes": [...]        // optional
//        }
public record RecaptchaResponse(
        boolean success,
        String challenge_ts,
        String hostname,
        String[] error_codes
) {

}
