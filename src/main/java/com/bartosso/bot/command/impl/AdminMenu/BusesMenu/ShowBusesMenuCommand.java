package com.bartosso.bot.command.impl.AdminMenu.BusesMenu;

import com.bartosso.bot.Bot;
import com.bartosso.bot.command.AbstractMenuCommand;
import com.bartosso.bot.command.Command;
import com.bartosso.bot.command.impl.ParentsMenu.ShowMainMenuCommand;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("Duplicates")
public class ShowBusesMenuCommand extends AbstractMenuCommand {

    @Override
    protected Map<String, Integer> getButtonsCommandMap() {
        Map<String, Integer> map = new HashMap<>();
        try {
            map.put(buttonDao.getButtonText(25),27);
            map.put(buttonDao.getButtonText(26),28);
            map.put(buttonDao.getButtonText(27),29);
            map.put(buttonDao.getButtonText(29),30);
            map.put(buttonDao.getButtonText(30),31);
            map.put(buttonDao.getButtonText(31),32);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (update.hasMessage()) {
            chatId = update.getMessage().getChatId();
            if (update.getMessage().hasText() && update.getMessage().getText().equals(buttonDao.getButtonText(22))) {
                deleteMessages(bot);
                Command command = new ShowMainMenuCommand();
                bot.getConversation(chatId)
                        .setCommand(command);
                return command.execute(update, bot);
            }
        }
        return super.execute(update, bot);
    }

    @Override
    protected int getMessageMenuTextId() {
        return 27;
    }

    @Override
    protected int getKeyboardId() {
        return 11;
    }
}
