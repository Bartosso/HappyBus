package com.bartosso.bot.command.impl.AdminMenu.SchoolsMenu;

import com.bartosso.bot.command.impl.AdminMenu.AbstractShowListCommand;
import com.bartosso.bot.entity.ProjectEntities.Entity;

import java.util.List;

public class ShowAllSchoolsCommand extends AbstractShowListCommand {

    @Override
    protected List<Entity> getEntitiesForList() {
        //noinspection unchecked
        return factory.getSchoolDao().getAllSchools();
    }
}
