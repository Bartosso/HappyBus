package com.bartosso.bot.command.impl.CoordinatorMenu.ToHome.PutInBusThings;

import com.bartosso.bot.command.Command;
import com.bartosso.bot.entity.ProjectEntities.Entity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class ShowListsOfSchoolsForEveningCommand extends com.bartosso.bot.command.impl.CoordinatorMenu.ToSchool.ShowListsThings.ShowListsCommand {
    @Setter
    @Getter
    protected List<Entity> busListForKidSelect;


    @Override
    public Command getSelectBusCommand(long schoolId, long constructorChatId) {
        return new SelectBusForSelectKidCommand(schoolId,constructorChatId,this);
    }
}
