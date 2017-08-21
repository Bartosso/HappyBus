package com.bartosso.bot.command.impl.AdminMenu.INFOMenu;

import com.bartosso.bot.command.impl.AdminMenu.EmployeeMenu.AbstractAddEmployeeViaContactCommand;
import org.telegram.telegrambots.api.objects.Contact;

public class AddManagerCommand extends AbstractAddEmployeeViaContactCommand {
    @Override
    protected void addNewContactToDB(Contact contact) {
        factory.getManagerDao().insertNewManager(contact);
    }
}
