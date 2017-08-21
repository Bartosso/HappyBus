package com.bartosso.bot.command.impl.ParentsMenu;

import com.bartosso.bot.Bot;
import com.bartosso.bot.command.Command;
import org.telegram.telegrambots.api.methods.send.SendContact;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Contact;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;


public class RequestCallCommand extends Command {
    private boolean expectNewValue;
    private String  lastName;
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (update.getMessage().hasText()){
            if (update.getMessage().getText().equals(buttonDao.getButtonText(10))){
                deleteMessages(bot);
                expectNewValue = false;
                return true;
            }}
        if (expectNewValue){
            if (update.getMessage().getContact()!=null){
                Contact contact = update.getMessage().getContact();
                bot.execute(new SendContact().setFirstName(contact.getFirstName()).setLastName(
                        contact.getLastName()).setPhoneNumber(contact.getPhoneNumber()).setChatId(factory.getManagerDao().getManagerChatId()));
                } else {
                try {
                    String phoneNumber = update.getMessage().getText();
                    String firstName   = update.getMessage().getFrom().getFirstName();
                if (update.getMessage().getFrom().getLastName()!=null){
                    lastName = update.getMessage().getFrom().getLastName();
                }
                SendContact sendContact = new SendContact().setFirstName(firstName).setLastName(lastName).setPhoneNumber(phoneNumber);
                bot.execute(new SendMessage(factory.getManagerDao().getManagerChatId(),messageDao.getMessage(81).getSendMessage().getText()));
                bot.execute(sendContact.setChatId(factory.getManagerDao().getManagerChatId()));} catch (TelegramApiException e){
                   sendErrorMessageForFormat(bot);
                   return false;
                }
            }
            sendMessageById(bot,9);
            expectNewValue = false;
            return false;
        } else {
                chatId = update.getMessage().getChatId();
                sendMessageByIdWithKeyboard(bot,7,4);
                expectNewValue = true;
            }

            return false;
    }
}
