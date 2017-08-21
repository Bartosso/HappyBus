package com.bartosso.bot.command.impl.ParentsMenu;

import com.bartosso.bot.Bot;
import com.bartosso.bot.command.Command;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

public class ShowAboutCommand extends Command {
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        chatId = update.getMessage().getChatId();
       sendMessageByIdWithPhoto(bot, 4);
       return true;
    }
}
