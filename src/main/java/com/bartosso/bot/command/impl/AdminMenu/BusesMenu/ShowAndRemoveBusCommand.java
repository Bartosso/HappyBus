package com.bartosso.bot.command.impl.AdminMenu.BusesMenu;

import com.bartosso.bot.Bot;
import com.bartosso.bot.command.impl.AdminMenu.AbstractShowAndRemoveCommand;
import com.bartosso.bot.entity.ProjectEntities.Entity;

import java.util.List;

public class ShowAndRemoveBusCommand extends AbstractShowAndRemoveCommand {
    @Override
    protected List<Entity> getEntitiesToShow() {
        //noinspection unchecked
        return factory.getBusesDao().getAll();
    }

    @Override
    protected void deleteEntityFromDb(int idToDelete) {
        factory.getBusesDao().deleteBus(idToDelete);
    }

    @Override
    protected void sendMessagesOnDelete(Bot bot) {

    }
}
