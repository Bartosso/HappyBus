package com.bartosso.bot.command.impl.DriverMenu;

import com.bartosso.bot.Bot;
import com.bartosso.bot.command.AbstractMenuCommand;
import com.bartosso.bot.command.Command;
import com.bartosso.bot.command.impl.ParentsMenu.ShowMainMenuCommand;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ShowDriverMenu extends AbstractMenuCommand {
    @Override
    protected Map<String, Integer> getButtonsCommandMap() {
        Map<String, Integer> map = new HashMap<>();
        try {
            map.put(buttonDao.getButtonText(41),47);
            map.put(buttonDao.getButtonText(42),48);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (update.hasMessage()) {
            chatId = update.getMessage().getChatId();
        }
        if (!factory.getDriverDao().isDriver(chatId)){
            Command command = new ShowMainMenuCommand();
            command.addNewMessageToDelete(bot.execute(new SendMessage(chatId,messageDao.getMessage(63).getSendMessage().getText())
            .setReplyMarkup(keyboardMarkUpDao.select(1))).getMessageId());
            bot.getConversation(chatId)
                    .setCommand(command);
            return false;
        }
        return super.execute(update, bot);
    }

    @Override
    protected int getMessageMenuTextId() {
        return 62;
    }

    @Override
    protected int getKeyboardId() {
        return 16;
    }
}
