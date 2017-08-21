package com.bartosso.bot.command;

import com.bartosso.bot.Bot;
import com.bartosso.bot.dao.DaoFactory;
import com.bartosso.bot.dao.impl.*;

import com.bartosso.bot.entity.Message;
import org.springframework.stereotype.Component;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.api.objects.Update;

import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;


/**
 * Created by Yerassyl_Turlygazhy on 11/27/2016.
 */
@Component
public abstract class Command {
    protected long id;
    @SuppressWarnings("WeakerAccess")
    protected long messageId;

    protected DaoFactory         factory             = DaoFactory.getFactory();
    protected MessageDao         messageDao          = factory.getMessageDao();
    protected KeyboardMarkUpDao  keyboardMarkUpDao   = factory.getKeyboardMarkUpDao();
    protected ButtonDao          buttonDao           = factory.getButtonDao();
    protected ParentDao          parentDao           = factory.getParentDao();
    protected ArrayList<Integer> messagesIdsToDelete = new ArrayList<>();
    protected long               chatId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public abstract boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException;


    protected void sendMessageByIdWithKeyboard(Bot bot, long messageId, long keyboardId) throws SQLException, TelegramApiException {
        Message message = messageDao.getMessage(messageId);
        SendMessage sendMessage = message.getSendMessage().setChatId(chatId);
        sendMessage.setReplyMarkup(keyboardMarkUpDao.select(keyboardId));
        messagesIdsToDelete.add(bot.execute(sendMessage).getMessageId());
    }

    protected void sendMessageById(Bot bot, long messageId) throws SQLException, TelegramApiException {
        Message message = messageDao.getMessage(messageId);
        SendMessage sendMessage = message.getSendMessage().setChatId(chatId);
        messagesIdsToDelete.add(bot.execute(sendMessage).getMessageId());
    }

    protected void sendMessageByIdWithPhoto(Bot bot, @SuppressWarnings("SameParameterValue") long messageId) throws SQLException, TelegramApiException {
        Message message = messageDao.getMessage(messageId);
        SendMessage sendMessage = message.getSendMessage().setChatId(chatId);
        if (message.getSendPhoto()!=null){
          messagesIdsToDelete.add(bot.sendPhoto(message.getSendPhoto().setChatId(chatId)).getMessageId());
        }
        messagesIdsToDelete.add(bot.execute(sendMessage).getMessageId());

    }


    protected void sendErrorMessageForFormat(Bot bot) throws SQLException, TelegramApiException {
    messagesIdsToDelete.add(bot.execute(messageDao.getMessage(5).getSendMessage().setChatId(chatId)
    .setReplyMarkup(keyboardMarkUpDao.select(3)))
            .getMessageId());
    }

    protected void sendMessageWithCustomKeyboard(Bot bot, long messageId, ReplyKeyboard keyboard) throws SQLException, TelegramApiException {
        messagesIdsToDelete.add(bot.execute(messageDao.getMessage(messageId).getSendMessage().setChatId(chatId)
        .setReplyMarkup(keyboard)).getMessageId());
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    protected void deleteMessages(Bot bot){
        messagesIdsToDelete.forEach(message -> {
            try {
                bot.execute(new DeleteMessage(chatId, message));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        });
        messagesIdsToDelete = new ArrayList<>();
    }

    protected void sendNothingFound(Bot bot) throws SQLException, TelegramApiException {
        deleteMessages(bot);
        sendMessageByIdWithKeyboard(bot,23,3);
    }

    public void addNewMessageToDelete(int messageId){
        messagesIdsToDelete.add(messageId);
    }

}
