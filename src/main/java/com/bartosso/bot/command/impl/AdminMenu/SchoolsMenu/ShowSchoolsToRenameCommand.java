package com.bartosso.bot.command.impl.AdminMenu.SchoolsMenu;

import com.bartosso.bot.command.Command;
import com.bartosso.bot.command.impl.CoordinatorMenu.ToSchool.ShowListsThings.ShowListsCommand;


@SuppressWarnings("Duplicates")
public class ShowSchoolsToRenameCommand extends ShowListsCommand {
    @Override
    public Command getSelectBusCommand(long schoolId, long constructorChatId) {
        return new RenameSchoolCommand(chatId,schoolId);
    }
}
