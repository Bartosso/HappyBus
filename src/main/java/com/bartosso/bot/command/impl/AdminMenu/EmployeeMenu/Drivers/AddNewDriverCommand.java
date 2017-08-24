package com.bartosso.bot.command.impl.AdminMenu.EmployeeMenu.Drivers;

import com.bartosso.bot.Bot;
import com.bartosso.bot.Util.CustomKeyboardUtil;
import com.bartosso.bot.command.Command;
import com.bartosso.bot.entity.ProjectEntities.Driver;
import com.bartosso.bot.entity.ProjectEntities.Entity;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.api.objects.Contact;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class AddNewDriverCommand extends Command{
    private boolean                         expectContact;
    private ArrayList<InlineKeyboardMarkup> pages;
    private int                             page                 = 0;
    private int                             messageIdWithKeyboard;
    private Contact                         contact;


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
                    expectContact = false;
                    contact = update.getMessage().getContact();
                    showFirstPage(bot);
                    return false;
                } else sendErrorMessageForFormat(bot);
                return false;
            }
            if (update.getMessage().hasText()){
                sendMessageByIdWithKeyboard(bot, 19,3);
                expectContact = true;
                return false;
            }
        }
        if (update.hasCallbackQuery()){
            chatId = update.getCallbackQuery().getFrom().getId();
            String chose = update.getCallbackQuery().getData();
            if (chose.contains("school")) {
                try{
                addNewDriverToDB(contact, Long.parseLong(chose.substring(chose.indexOf(":")+1)));
                sendMessageByIdWithKeyboard(bot, 20,15);
                return false;
                }
                catch (org.springframework.dao.DuplicateKeyException e){
                    deleteMessages(bot);
                    sendMessageByIdWithKeyboard(bot,83,15);
                    return false;
                }
            }
            if (chose.equals(buttonDao.getButtonText(10))||chose.equals("back")) {
                deleteMessages(bot);
                return true;
            }
            if (chose.equals("nextPage")) {
                page++;
                showPage(bot);
                return false;
            }
            if (chose.equals("previousPage")) {
                page--;
                showPage(bot);
                return false;
            }
        sendErrorMessageForFormat(bot);
        }
        return false;
    }

    private void  addNewDriverToDB(Contact contact,long school_id){
        if (contact.getLastName()!=null){
        factory.getDriverDao().addNew(new Driver(contact.getUserID(),contact.getFirstName() +" "
                + contact.getLastName(),
                contact.getPhoneNumber(),false,school_id));}
                else {
            factory.getDriverDao().addNew(new Driver(contact.getUserID(),contact.getFirstName(),
                    contact.getPhoneNumber(),false,school_id));
        }

    }

    private void showFirstPage(Bot bot) throws SQLException, TelegramApiException {
        deleteMessages(bot);
        //noinspection unchecked
        List<Entity> schools = factory.getSchoolDao().getAllSchools();
        if (schools == null) {
            sendMessageByIdWithKeyboard(bot, 43,15);
            return;
        }
        pages = CustomKeyboardUtil.getPagesWithEntitiesAsButtonText(schools,"school");
        messageIdWithKeyboard = bot.execute(new SendMessage
                (chatId,"Выберите школу к которой хотите прикрепить водителя").setReplyMarkup(pages.get(page)))
                .getMessageId();
        messagesIdsToDelete.add(messageIdWithKeyboard);
    }

    private void showPage(Bot bot) throws TelegramApiException {
        bot.execute(new EditMessageReplyMarkup().setChatId(chatId).setMessageId(messageIdWithKeyboard)
                .setReplyMarkup(pages.get(page)));
    }
}
