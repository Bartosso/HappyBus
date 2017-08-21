package com.bartosso.bot.command.impl.AdminMenu.SchoolsMenu;

import com.bartosso.bot.Bot;
import com.bartosso.bot.command.Command;
import com.bartosso.bot.entity.ProjectEntities.School;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

public class AddNewSchoolCommand extends Command {
    private boolean creatingSchool;
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (update.hasMessage()){
            chatId = update.getMessage().getChatId();
            if (update.getMessage().hasText()){
                if (update.getMessage().getText().equals(buttonDao.getButtonText(10))){
                    deleteMessages(bot);
                    return true;
                }
                if (creatingSchool){
                    School school = new School(update.getMessage().getText());
                    factory.getSchoolDao().addNew(school);
                    creatingSchool = false;
                    sendMessageByIdWithKeyboard(bot,20,3);
                    return false;
                }
            }
            sendMessageByIdWithKeyboard(bot, 25, 3);
            creatingSchool = true;
            return false;
        } else sendErrorMessageForFormat(bot);
        return false;
    }
}
