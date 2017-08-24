package com.bartosso.bot.command.impl.AdminMenu.SchoolsMenu;

import com.bartosso.bot.Bot;
import com.bartosso.bot.command.Command;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

@SuppressWarnings("Duplicates")
public class RenameSchoolCommand extends Command {
    private         boolean messageSends;
    private final   long    schoolId;

    RenameSchoolCommand(long chatId, long schoolId) {
        this.chatId = chatId;
        this.schoolId=schoolId;
    }

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (!messageSends) {
            messageSends = true;
            sendMessageByIdWithKeyboard(bot,25,3);
            return false;
        }
        if (update.hasMessage()) {
            if (update.getMessage().hasText()) {
                if (update.getMessage().getText().equals(buttonDao.getButtonText(10))) {
                    deleteMessages(bot);
                    return true;
                }
                deleteMessages(bot);
                String newValue = update.getMessage().getText();
                factory.getSchoolDao().renameSchool(schoolId, newValue);
                sendMessageByIdWithKeyboard(bot,20,3);
                return false;
            }
        }
        sendErrorMessageForFormat(bot);
        return false;
    }
}
