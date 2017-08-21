package com.bartosso.bot.command.impl.CoordinatorMenu.ToHome.ReadyToGoBuses;

import com.bartosso.bot.Bot;
import com.bartosso.bot.command.Command;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

public class DeleteEveningListCommand extends Command {
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        return false;
    }
}
