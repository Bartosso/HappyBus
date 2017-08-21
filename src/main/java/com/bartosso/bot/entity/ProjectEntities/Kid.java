package com.bartosso.bot.entity.ProjectEntities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Kid implements Entity {
    private long   id;
    private final String name;
    private final String school_id;
    private final String photo;

    @Override
    public String getTextToButton() {
        return name;
    }

    @Override
    public String toString() {
        return "Имя фамилия: " + name;
    }


}
