package com.bartosso.bot.command.impl.CoordinatorMenu.ToHome.ReadyToGoBuses;

import com.bartosso.bot.command.impl.CoordinatorMenu.ToSchool.ShowListsThings.TransplantCommand;
import com.bartosso.bot.entity.ProjectEntities.Bus;
import com.bartosso.bot.entity.ProjectEntities.Entity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TransplantFromEveningBusCommand extends TransplantCommand {

    TransplantFromEveningBusCommand(long constructorChatId, List<Entity> kidList) {
        super(constructorChatId, kidList);
    }

    @Override
    protected List<Entity> getBuses() {
        //noinspection unchecked
        return factory.getBusesDao().getAllBusesWhoCanDoEveningRoute();
    }

    @Override
    protected void changeBus(long newBusId) throws SQLException {
        Bus bus = busesDao.getBusByChildInsideInEvening(kidId);
        ArrayList<Long> oldBusKids = bus.getTo_home_kids();
        oldBusKids.removeIf(aLong -> aLong==kidId);
        busesDao.updateEveningRoute(oldBusKids.toArray(new Long[oldBusKids.size()]),bus.getId());
        bus     = busesDao.getBusById(newBusId);
        ArrayList<Long> newBusKids;
        //noinspection Duplicates
        if (bus.getTo_home_kids() != null) {
            newBusKids = bus.getTo_home_kids();
            newBusKids.add(kidId);
        } else {
            newBusKids = new ArrayList<>();
            newBusKids.add(kidId);
        }
        busesDao.updateEveningRoute(newBusKids.toArray(new Long[newBusKids.size()]),bus.getId());
        busesDao.setLastCoordinatorId(chatId,bus.getId());
    }
}
