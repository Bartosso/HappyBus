package com.bartosso.bot.entity.ProjectEntities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
public class SickLeave implements Entity {

    private       long      id;
    private final long      childId;
    private final LocalDate sickDate;
    private final int       placeInTheBus;
    private final long      busId;


    @Override
    public String getTextToButton() {
        return sickDate.toString();
    }

    @Override
    public long getId() {
        return id;
    }
}
