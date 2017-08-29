package com.bartosso.bot.Util.Reminder;

import lombok.extern.slf4j.Slf4j;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Timer;

@Slf4j
public class Reminder {
    private Timer timer = new Timer(true);



    public Reminder() {
        setCheckEveryNightDb(LocalDate.now().atTime(0,5,0));
    }

    void setCheckEveryNightDb(LocalDateTime lDT) {
        log.info("Next check db task set to " + lDT.toString());

        CheckEveryNightDB checkEveryNightDbTask = new CheckEveryNightDB(this);
        timer.schedule(checkEveryNightDbTask, Date.from(lDT.atZone(ZoneId.systemDefault()).toInstant()));
    }
}
