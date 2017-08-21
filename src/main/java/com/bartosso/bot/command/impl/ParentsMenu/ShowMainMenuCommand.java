package com.bartosso.bot.command.impl.ParentsMenu;

import com.bartosso.bot.Bot;
import com.bartosso.bot.command.AbstractMenuCommand;
import com.bartosso.bot.command.Command;
import com.bartosso.bot.command.impl.AdminMenu.ShowMainAdminMenuCommand;
import com.bartosso.bot.command.impl.CoordinatorMenu.ShowCoordinatorMenuCommand;
import com.bartosso.bot.command.impl.DriverMenu.ShowDriverMenu;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;


import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ShowMainMenuCommand extends AbstractMenuCommand {
    @Override
    protected Map<String, Integer> getButtonsCommandMap() {
        Map<String, Integer> commandMap = new HashMap<>();
        try {
            commandMap.put(buttonDao.getButtonText(2), 2);
            commandMap.put(buttonDao.getButtonText(3), 3);
            commandMap.put(buttonDao.getButtonText(4), 4);
            commandMap.put(buttonDao.getButtonText(5), 5);
            commandMap.put(buttonDao.getButtonText(6), 6);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return commandMap;
    }

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (update.hasMessage()){
            chatId = update.getMessage().getChatId();
            if (update.getMessage().hasText()){
                if (update.getMessage().getText().equals(buttonDao.getButtonText(23))){
                    deleteMessages(bot);
                    Command command = new ShowMainAdminMenuCommand();
                    bot.getConversation(chatId)
                            .setCommand(command);
                    return command.execute(update, bot);
                }
                if (update.getMessage().getText().equals(buttonDao.getButtonText(47))){
                    deleteMessages(bot);
                    Command command = new ShowCoordinatorMenuCommand();
                    bot.getConversation(chatId)
                            .setCommand(command);
                    return command.execute(update, bot);
                }
                if (update.getMessage().getText().equals(buttonDao.getButtonText(52))){
                    deleteMessages(bot);
                    Command command = new ShowDriverMenu();
                    bot.getConversation(chatId)
                            .setCommand(command);
                    return command.execute(update, bot);
                }
            }
        }
        return super.execute(update, bot);
        }



    @Override
    protected int getMessageMenuTextId() {
        return 1;
    }

    @Override
    protected int getKeyboardId() {
        return 1;
    }

}
