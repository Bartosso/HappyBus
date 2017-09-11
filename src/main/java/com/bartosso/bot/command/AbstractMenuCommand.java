package com.bartosso.bot.command;

import com.bartosso.bot.Bot;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.Map;


public abstract class AbstractMenuCommand extends Command {
    private Command                  command;
    private boolean                  runningCommand;
    private Map<String, Integer>     buttonsCommandMap = getButtonsCommandMap();
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (runningCommand){
            if (command.execute(update, bot)){
                showMenu(bot,update);
                command        = null;
                runningCommand = false;
            }
            return false;
        }
        if (update.hasMessage()){
            chatId = update.getMessage().getChatId();
            if (update.getMessage().hasText()){
            String messageText = update.getMessage().getText();
              if (buttonsCommandMap.containsKey(messageText)){
                  command = CommandFactory.getCommand(buttonsCommandMap.get(messageText));
                  return selectMenu(command,update,bot);
              }
              if (messageText.equals(buttonDao.getButtonText(10))){
                 deleteMessages(bot);
                 return true;
              }
            }
            showMenu(bot,update);
        }
        return false;

    }

    protected abstract Map<String, Integer> getButtonsCommandMap();

    protected abstract int getMessageMenuTextId();

    protected abstract int getKeyboardId();

    private void showMenu(Bot bot,Update update) throws SQLException, TelegramApiException {
        deleteMessages(bot);
        if (chatId==0){
            chatId = update.getMessage().getChatId();
        }
        sendMessageByIdWithKeyboard(bot, getMessageMenuTextId(), getKeyboardId());
    }

    private boolean selectMenu(Command command,Update update, Bot bot) throws SQLException, TelegramApiException {
        if (command.execute(update, bot)){
            deleteMessages(bot);
            showMenu(bot,update);
            return false;
        } else {
            deleteMessages(bot);
            runningCommand = true;
            return false; }
    }

}
