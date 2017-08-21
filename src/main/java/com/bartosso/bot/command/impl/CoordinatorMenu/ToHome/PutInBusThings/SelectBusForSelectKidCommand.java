package com.bartosso.bot.command.impl.CoordinatorMenu.ToHome.PutInBusThings;

import com.bartosso.bot.Bot;
import com.bartosso.bot.command.impl.CoordinatorMenu.ToSchool.ShowListsThings.SelectBusCommand;
import com.bartosso.bot.command.impl.CoordinatorMenu.ToSchool.ShowListsThings.SelectKidCommand;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

public class SelectBusForSelectKidCommand extends SelectBusCommand {
    private ShowListsOfSchoolsForEveningCommand showListsOfSchoolsForEveningCommand;
    SelectBusForSelectKidCommand(long school_id, long constructorChatId, ShowListsOfSchoolsForEveningCommand showListsOfSchoolsForEveningCommand) {
        super(school_id, constructorChatId);
        this.showListsOfSchoolsForEveningCommand = showListsOfSchoolsForEveningCommand;
    }

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
//        if (update.hasCallbackQuery()) {
//            if (update.getCallbackQuery().getData().equals(buttonDao.getButtonText(10))) {
//                return true;
//            }
//        }
        return super.execute(update, bot);
    }



    @Override
    public SelectKidCommand getSelectKidCommand(long busId, long constructorChatId) {
        return new SelectKidForPutInBus(busId,constructorChatId, showListsOfSchoolsForEveningCommand);
    }
}
