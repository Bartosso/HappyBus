package com.bartosso.bot.command.impl.ParentsMenu.PersonalAreaMenu;

import com.bartosso.bot.Bot;
import com.bartosso.bot.command.Command;
import org.telegram.telegrambots.api.methods.send.SendDocument;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;

public class FeedbackCommand extends Command {
    private boolean            expectNewValue;
    private int                step;
    private String             text;
    private String             document;
    private User               user;
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (update.hasMessage()){
            if (update.getMessage().hasText()){
                if (update.getMessage().getText().equals(buttonDao.getButtonText(10))){
                    step     = 0;
                    text     = null;
                    document = null;
                    deleteMessages(bot);
                    return true;
                }
            }
        }
        if (expectNewValue){
            switch (step){
                case 0:
                    if (update.hasMessage()){
                        if (update.getMessage().hasText()){
                            text = update.getMessage().getText();
                            step = 1;
                            break;
                        } else {
                            sendErrorMessageForFormat(bot);
                            return false;
                        }
                    }  else {
                        sendErrorMessageForFormat(bot);
                        return false;
                    }
                case 1:
                    if (update.hasMessage()){
                        if (update.getMessage().hasDocument()){
                            document = update.getMessage().getDocument().getFileId();
                            step     = 2;
                            break;
                        }
                    } else {
                        if (update.hasCallbackQuery()){
                            if (update.getCallbackQuery().getData().equals(buttonDao.getButtonText(15))){
                                document = null;
                                step     = 2;
                                break;
                            } else {sendErrorMessageForFormat(bot);
                            return false;}
                        } else {
                            sendErrorMessageForFormat(bot);
                            return false;
                        }
                    }
            }

        }
         if (update.hasMessage()){
             user   = update.getMessage().getFrom();
             chatId = user.getId();
             if (!factory.getParentDao().isRegistred(chatId)) {
                 sendMessageByIdWithKeyboard(bot,68,3);
                 return false;
             }
         }
         if (step == 0){
         sendMessageById(bot,11);
         sendMessageById(bot,12);
         expectNewValue = true;
         return false;
         }
         if (step == 1){
         sendMessageByIdWithKeyboard(bot,13,6);
         return false;
         }
         if (step == 2){try {
            bot.execute(new SendMessage(factory.getManagerDao().getManagerChatId(), getMessageText(text)));
            if (document!=null){
            bot.sendDocument(new SendDocument().setDocument(document).setChatId(factory.getManagerDao().getManagerChatId()));}
            sendMessageByIdWithKeyboard(bot,14, 3);}
            catch (org.springframework.dao.EmptyResultDataAccessException e){
                sendMessageByIdWithKeyboard(bot,14, 3);
            }
             expectNewValue      = false;
             step                = 0;
             text                = null;
             document            = null;
             messagesIdsToDelete = new ArrayList<>();
         }

            return false;
    }

    private String getMessageText(String text){
        StringBuilder sb = new StringBuilder();
        sb.append("Сообщение от пользователя: ").append(user.getFirstName());
        if (user.getLastName() != null){
            sb.append(" ").append(user.getFirstName());
        }
        if (user.getUserName() != null){
            sb.append(" @").append(user.getLastName());
        }
        sb.append("\n").append(parentDao.getParentByChatId(chatId).getPhone());
        sb.append("\n").append(text);
        return sb.toString();
    }

}
