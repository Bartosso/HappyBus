package com.bartosso.bot.command.impl.AdminMenu.EmployeeMenu.Coordinators;

import com.bartosso.bot.command.impl.AdminMenu.EmployeeMenu.AbstractAddEmployeeViaContactCommand;
import com.bartosso.bot.entity.ProjectEntities.Coordinator;
import org.telegram.telegrambots.api.objects.Contact;

public class AddNewCoordinatorCommand extends AbstractAddEmployeeViaContactCommand {
    @Override
    protected void addNewContactToDB(Contact contact) {
        factory.getCoordinatorDao().addNew(new Coordinator(contact));
    }
}
