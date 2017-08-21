package com.bartosso.bot.command.impl.AdminMenu.EmployeeMenu.Coordinators;

import com.bartosso.bot.Bot;
import com.bartosso.bot.command.impl.AdminMenu.AbstractShowAndRemoveCommand;
import com.bartosso.bot.entity.ProjectEntities.Entity;

import java.util.List;

public class RemoveCoordinatorCommand extends AbstractShowAndRemoveCommand {

    @Override
    protected List<Entity> getEntitiesToShow() {
        //noinspection unchecked
        return factory.getCoordinatorDao().getAllCoordinators();
    }

    @Override
    protected void deleteEntityFromDb(int idToDelete) {
        factory.getCoordinatorDao().deleteCoordinator(idToDelete);
    }

    @Override
    protected void sendMessagesOnDelete(Bot bot) {

    }
}
