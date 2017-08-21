package com.bartosso.bot.command.impl.AdminMenu.SchoolsMenu;

import com.bartosso.bot.Bot;
import com.bartosso.bot.command.impl.AdminMenu.AbstractShowAndRemoveCommand;
import com.bartosso.bot.entity.ProjectEntities.Entity;

import java.util.List;

public class RemoveSchoolCommand extends AbstractShowAndRemoveCommand {
    @Override
    protected List<Entity> getEntitiesToShow() {
        //noinspection unchecked
        return factory.getSchoolDao().getAllSchools();
    }

    @Override
    protected void deleteEntityFromDb(int idToDelete) {
        factory.getSchoolDao().deleteSchool(idToDelete);
    }

    @Override
    protected void sendMessagesOnDelete(Bot bot) {

    }
}
