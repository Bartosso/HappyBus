package com.bartosso.bot.command.impl.CoordinatorMenu.ToHome.ReadyToGoBuses;


import com.bartosso.bot.command.impl.CoordinatorMenu.ToSchool.ShowListsThings.AddNewKidToMorningBusCommand;
import com.bartosso.bot.entity.ProjectEntities.Entity;

import java.util.List;

public class AddNewKidToEveningBus extends AddNewKidToMorningBusCommand {
    AddNewKidToEveningBus(long chatId, long busId) {
        super(chatId, busId);
    }

    @Override
    public List<Entity> getAvailableKids() {
        return factory.getKidsDao().getAllKidsWhoNotInAnyEveningBus();
    }

    @Override
    protected void addNewKidToThisBus(long kidId) {
        List<Long> kidsInThisBus = factory.getBusesDao().getBusById(busId).getTo_home_kids();
        kidsInThisBus.add(kidId);
        factory.getBusesDao().updateEveningRoute(kidsInThisBus.toArray(new Long[kidsInThisBus.size()]),busId);
    }
}
