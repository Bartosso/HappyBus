package com.bartosso.bot.command.impl.AdminMenu.SchoolKidsMenu;

import com.bartosso.bot.Bot;
import com.bartosso.bot.command.impl.AdminMenu.AbstractShowAndRemoveCommand;
import com.bartosso.bot.entity.ProjectEntities.Entity;

import java.util.List;

public class RemoveChildAndParentsCommand extends AbstractShowAndRemoveCommand {
    @Override
    protected List<Entity> getEntitiesToShow() {
        //noinspection unchecked
        return factory.getKidsDao().getAll();
    }

    @Override
    protected void deleteEntityFromDb(int idToDelete) {
            factory.getKidsDao().killKid(idToDelete);
            factory.getParentDao().killParentsFromChildId(idToDelete);
            factory.getBusesDao().deleteKidFromBus(idToDelete);
    }

    @Override
    protected void sendMessagesOnDelete(Bot bot) {
    }
}
