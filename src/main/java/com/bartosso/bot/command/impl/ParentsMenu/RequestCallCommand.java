package com.bartosso.bot.command.impl.ParentsMenu;

import com.bartosso.bot.Bot;
import com.bartosso.bot.command.Command;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.api.methods.send.SendContact;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Contact;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

@Slf4j
public class RequestCallCommand extends Command {
    private boolean expectNewValue;
    private String  lastName;
    @SuppressWarnings("Duplicates")
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
                try {
                    bot.execute(new SendContact().setFirstName(contact.getFirstName()).setLastName(
                            contact.getLastName()).setPhoneNumber(contact.getPhoneNumber()).setChatId(factory.getManagerDao().getManagerChatId()));
                } catch (org.springframework.dao.EmptyResultDataAccessException e){
                log.info("Пользователь пытался запросить обратный звонок но менеджер не указан");
                }
            } else {
                try {
                    String phoneNumber = update.getMessage().getText();
                    String firstName   = update.getMessage().getFrom().getFirstName();
                if (update.getMessage().getFrom().getLastName()!=null){
                    lastName = update.getMessage().getFrom().getLastName();
                }
                SendContact sendContact = new SendContact().setFirstName(firstName).setLastName(lastName).setPhoneNumber(phoneNumber);
                long managerChatId;
                try {
                    managerChatId = factory.getManagerDao().getManagerChatId();
                } catch (org.springframework.dao.EmptyResultDataAccessException e){
                    log.info("Пользователь пытался запросить обратный звонок но менеджер не указан");
                    sendMessageByIdWithKeyboard(bot,20,3);
                    return false;
                }
                bot.execute(sendContact.setChatId(managerChatId));
                bot.execute(new SendMessage(managerChatId,messageDao.getMessage(81).getSendMessage().getText()));
                } catch (TelegramApiException e){
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
