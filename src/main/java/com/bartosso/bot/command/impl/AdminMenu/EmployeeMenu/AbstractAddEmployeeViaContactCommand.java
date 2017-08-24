package com.bartosso.bot.command.impl.AdminMenu.EmployeeMenu;

import com.bartosso.bot.Bot;
import com.bartosso.bot.command.Command;
import org.telegram.telegrambots.api.objects.Contact;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

public abstract class AbstractAddEmployeeViaContactCommand extends Command {
    private boolean expectContact;
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (update.hasMessage()){
            if (update.getMessage().hasText()){
                if (update.getMessage().getText().equals(buttonDao.getButtonText(10))){
                    deleteMessages(bot);
                    return true;
                }
            }
            chatId = update.getMessage().getChatId();
            if (expectContact){
                if (update.getMessage().getContact()!=null){
                    if (update.getMessage().getContact().getUserID()==null){
                        sendMessageByIdWithKeyboard(bot,21,3);
                        return false;
                    }
                    try{
                    addNewContactToDB(update.getMessage().getContact());
                    sendMessageByIdWithKeyboard(bot, 20,3);
                    return false;} catch (org.springframework.dao.DuplicateKeyException e){
                        deleteMessages(bot);
                        sendMessageByIdWithKeyboard(bot,83,3);
                        return false;
                    }
                } else sendErrorMessageForFormat(bot);
                return false;
            }
            if (update.getMessage().hasText()){
                sendMessageByIdWithKeyboard(bot, 19,3);
                    expectContact = true;
                    return false;
            }
        }
        if (update.hasCallbackQuery()) chatId = update.getCallbackQuery().getFrom().getId();
        sendErrorMessageForFormat(bot);
        return false;
    }

    protected abstract void addNewContactToDB(Contact contact);

}
