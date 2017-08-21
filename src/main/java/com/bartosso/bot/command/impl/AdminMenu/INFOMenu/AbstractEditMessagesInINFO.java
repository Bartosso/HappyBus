package com.bartosso.bot.command.impl.AdminMenu.INFOMenu;

import com.bartosso.bot.Bot;
import com.bartosso.bot.command.Command;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

public abstract class AbstractEditMessagesInINFO extends Command {
    private int     step = 0;
    private boolean expectNewValue;
    private String  newText;
    private String  photo;
    @SuppressWarnings("Duplicates")
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (update.hasMessage()){
            chatId = update.getMessage().getChatId();
            if (update.getMessage().hasText()){
                if (update.getMessage().getText().equals(buttonDao.getButtonText(10))){
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
                            newText = update.getMessage().getText();
                            step = 1;
                            break;
                        }}
                        sendErrorMessageForFormat(bot);
                        return false;
                    case 1:
                        if (update.hasCallbackQuery()){
                            if (update.getCallbackQuery().getData().equals(buttonDao.getButtonText(15))){
                                photo = null;
                                step = 2;
                                break;
                            }
                        }
                        if (update.hasMessage()){
                            if (update.getMessage().getPhoto()!=null){
                                photo = update.getMessage().getPhoto().get(update.getMessage().getPhoto().size()-1).getFileId();
                                step = 2;
                                break;
                            }
                        }
                        sendErrorMessageForFormat(bot);
                        return false;
                }
            }
            if (step==0){
                deleteMessages(bot);
                sendMessageByIdWithKeyboard(bot,32,3);
                expectNewValue = true;
                return false;
            }
            if (step==1){
                deleteMessages(bot);
                sendMessageByIdWithKeyboard(bot,34,6);
                return false;
            }
            if (step==2){
                deleteMessages(bot);
                sendMessageByIdWithKeyboard(bot,20,3);
                messageDao.updateMessage(newText,photo,getMessageId());
                step = 0;
                return false;
            }

       return false;
    }
    protected abstract long getMessageId();
}
