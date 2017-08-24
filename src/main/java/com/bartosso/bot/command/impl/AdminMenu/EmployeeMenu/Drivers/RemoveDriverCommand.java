package com.bartosso.bot.command.impl.AdminMenu.EmployeeMenu.Drivers;


import com.bartosso.bot.Bot;
import com.bartosso.bot.command.impl.AdminMenu.AbstractShowAndRemoveCommand;
import com.bartosso.bot.entity.ProjectEntities.Entity;

import java.util.List;


public class RemoveDriverCommand extends AbstractShowAndRemoveCommand {

    @Override
    protected List<Entity> getEntitiesToShow() {
        //noinspection unchecked
        return factory.getDriverDao().getAll();
    }

    @Override
    protected void deleteEntityFromDb(int idToDelete) {
        factory.getDriverDao().deleteDriver(idToDelete);
        factory.getBusesDao().removeDriverFromBuses(idToDelete);
    }

    @Override
    protected void sendMessagesOnDelete(Bot bot) {

    }
}
