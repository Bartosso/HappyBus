package com.bartosso.bot.command.impl.AdminMenu.EmployeeMenu.Drivers;

import com.bartosso.bot.Bot;
import com.bartosso.bot.Util.CustomKeyboardUtil;
import com.bartosso.bot.command.Command;
import com.bartosso.bot.command.impl.CoordinatorMenu.ToSchool.ShowListsThings.ShowListsCommand;
import com.bartosso.bot.entity.ProjectEntities.Entity;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SelectSchoolToShowDriversCommand extends ShowListsCommand {
    @Override
    public Command getSelectBusCommand(long schoolId, long constructorChatId) {
        return new ShowAllDriversCommand(schoolId,constructorChatId);
    }

    @Override
    protected void showSchools(Bot bot) throws SQLException, TelegramApiException {
        List list = factory.getSchoolDao().getAllSchools();
        page = 0;
        deleteMessages(bot);
        if (list==null){
            list = new ArrayList();
        }
        //noinspection unchecked
        list.add(0, new Entity() {
            @Override
            public String getTextToButton() {
                return "Без школы";
            }

            @Override
            public long getId() {
                return -404;
            }
        });
        String message = messageDao.getMessage(37).getSendMessage().getText();
        //noinspection unchecked
        schoolPages             = CustomKeyboardUtil.getPagesWithEntitiesAsButtonText(list,"school");
        SendMessage sendMessage = new SendMessage(chatId,message).setReplyMarkup(schoolPages.get(page));
        messageWithKeyboardId   = bot.execute(sendMessage).getMessageId();
        messagesIdsToDelete.add(messageWithKeyboardId);
    }
}
