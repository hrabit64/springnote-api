package com.springnote.api.utils.time;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

@Component
public class TimeHelper {
    
    private final Clock clock = Clock.systemDefaultZone();

    public LocalDateTime nowTime() {
        return LocalDateTime.now(clock);
    }

    public LocalDate nowDate() {
        return LocalDate.now(clock);
    }

}
