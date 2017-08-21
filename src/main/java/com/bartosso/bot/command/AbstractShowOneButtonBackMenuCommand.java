package com.bartosso.bot.command;

import com.bartosso.bot.Bot;
import com.bartosso.bot.entity.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;


public abstract class AbstractShowOneButtonBackMenuCommand extends Command {
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (update.hasMessage()){
            if (update.getMessage().getText().equals(buttonDao.getButtonText(10))){
                deleteMessages(bot);
                return true;
            }
            if (update.getMessage().getText().equals(buttonDao.getButtonText(getButtonOnCallId()))){
                chatId = update.getMessage().getChatId();
                sendInfo(bot,chatId);
                return false;
            }
        }
        return false;
    }


    private void sendInfo(Bot bot, long chatId) throws SQLException, TelegramApiException {
        Message message = messageDao.getMessage(getMessageIdToShow());
        if (message.getSendPhoto()!=null){
            messagesIdsToDelete.add(bot.sendPhoto(message.getSendPhoto().setChatId(chatId)).getMessageId());
        }
        sendMessageByIdWithKeyboard(bot, getMessageIdToShow(),3);
    }

    protected abstract int getMessageIdToShow();

    protected abstract int getButtonOnCallId();


}
