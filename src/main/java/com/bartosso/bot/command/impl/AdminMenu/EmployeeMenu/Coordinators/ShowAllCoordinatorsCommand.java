package com.bartosso.bot.command.impl.AdminMenu.EmployeeMenu.Coordinators;

import com.bartosso.bot.command.impl.AdminMenu.AbstractShowListCommand;
import com.bartosso.bot.entity.ProjectEntities.Entity;

import java.util.List;

public class ShowAllCoordinatorsCommand extends AbstractShowListCommand {
    @Override
    protected List<Entity> getEntitiesForList() {
        //noinspection unchecked
        return factory.getCoordinatorDao().getAllCoordinators();
    }
}
