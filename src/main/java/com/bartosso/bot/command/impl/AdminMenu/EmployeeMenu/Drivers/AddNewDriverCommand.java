package com.bartosso.bot.command.impl.AdminMenu.EmployeeMenu.Drivers;

import com.bartosso.bot.command.impl.AdminMenu.EmployeeMenu.AbstractAddEmployeeViaContactCommand;
import com.bartosso.bot.entity.ProjectEntities.Driver;
import org.telegram.telegrambots.api.objects.Contact;


public class AddNewDriverCommand extends AbstractAddEmployeeViaContactCommand {

    @Override
    protected void addNewContactToDB(Contact contact) {
        factory.getDriverDao().addNew(new Driver(contact));
    }
}
