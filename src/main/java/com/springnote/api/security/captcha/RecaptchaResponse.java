package com.springnote.api.security.captcha;

//{
//        "success": true,
//        "challenge_ts": "2025-01-01T11:26:25Z",
//        "hostname": "client-dev.hrabit64.shop",
//        "score": 0.9,
//        "action": "register"
//        }
public record RecaptchaResponse(
        boolean success,
        String challenge_ts,
        String hostname,
        double score,
        String action
) {

}
