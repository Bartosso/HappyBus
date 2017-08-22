package com.bartosso.bot;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yerassyl_Turlygazhy on 11/24/2016.
 */
public class Bot extends TelegramLongPollingBot {
    private Map<Long, Conversation> conversations = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(Bot.class);



    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if (message == null) {
            message = update.getCallbackQuery().getMessage();
        }
        String title = message.getChat().getTitle();
        if (title != null) {

            return;
        }
        Conversation conversation = getConversation(update);
        try {
            conversation.handleUpdate(update, this);
        } catch (SQLException | TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private Conversation getConversation(Update update) {
        Message message = update.getMessage();
        if (message == null) {
            message = update.getCallbackQuery().getMessage();
        }
        Long chatId = message.getChatId();
        Conversation conversation = conversations.get(chatId);
        if (conversation == null) {
            String username = "@" + message.getFrom().getUserName();
            if (username.equals("@null")){
                username = message.getFrom().getFirstName();
            }
            logger.info("Создана новая беседа для '{}', логин "+ username, chatId);
            conversation = new Conversation();
            conversations.put(chatId, conversation);
        }
        return conversation;
    }


    public Conversation getConversation(Long chatId) {
        Conversation conversation = conversations.get(chatId);
        if (conversation == null) {
            logger.info("Создана новая беседа для '{}'", chatId);
            conversation = new Conversation();
            conversations.put(chatId, conversation);
        }
        return conversation;
    }

    public String getBotUsername() {
        return "";
    }

    public String getBotToken() {
        return "";
    }
}
