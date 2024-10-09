package com.springnote.api.utils.time;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Getter
@Component
public class TimeHelper {

    private final Clock clock = Clock.systemDefaultZone();

    public LocalDateTime nowTime() {
        return LocalDateTime.now(clock);
    }

    public LocalDate nowDate() {
        return LocalDate.now(clock);
    }

    public ZoneOffset getZoneOffset() {
        return clock.getZone().getRules().getOffset(clock.instant());
    }
}
