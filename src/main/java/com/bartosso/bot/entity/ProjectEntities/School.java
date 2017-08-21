package com.bartosso.bot.entity.ProjectEntities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor()
@AllArgsConstructor
@Getter
@Setter
public class School implements Entity {

    private        long   id;
    private final  String name;

    @Override
    public String getTextToButton() {
        return name;
    }


}
