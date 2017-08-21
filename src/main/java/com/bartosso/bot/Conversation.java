package com.bartosso.bot;

import com.bartosso.bot.command.Command;
import com.bartosso.bot.command.impl.ParentsMenu.ShowMainMenuCommand;
import com.bartosso.bot.command.impl.ParentsMenu.ShowNewsCommand;
import com.bartosso.bot.dao.DaoFactory;
import com.bartosso.bot.dao.impl.ButtonDao;
import com.bartosso.bot.dao.impl.KeyboardMarkUpDao;
import com.bartosso.bot.dao.impl.MessageDao;
import com.bartosso.bot.entity.Message;
import com.bartosso.bot.exception.CommandNotFoundException;
import com.bartosso.bot.service.CommandService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;


/**
 * Created by Yerassyl_Turlygazhy on 11/27/2016.
 */
@Component
public class Conversation {
    private CommandService commandService = new CommandService();
    private Command command;
    private DaoFactory factory = DaoFactory.getFactory();
    private MessageDao messageDao = factory.getMessageDao();
    private ButtonDao buttonDao = factory.getButtonDao();
    private KeyboardMarkUpDao keyboardMarkUpDao = factory.getKeyboardMarkUpDao();



    void handleUpdate(Update update, Bot bot) throws SQLException, TelegramApiException {
        org.telegram.telegrambots.api.objects.Message updateMessage = update.getMessage();
        String inputtedText;
        if (updateMessage == null) {
            inputtedText = update.getCallbackQuery().getData();
        } else {
            inputtedText = updateMessage.getText();
        }

        try {
            command = commandService.getCommand(inputtedText);
        } catch (CommandNotFoundException e) {
            if (command == null) {
                command = new ShowMainMenuCommand();
                command.execute(update, bot);
                return;
            }
        }
        boolean commandFinished = command.execute(update, bot);
        if (commandFinished) {
            command = null;
        }
    }

    public void setCommand(Command command) {
        this.command = command;
    }


}
