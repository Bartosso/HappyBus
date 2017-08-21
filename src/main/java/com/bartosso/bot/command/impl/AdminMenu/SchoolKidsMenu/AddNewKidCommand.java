package com.bartosso.bot.command.impl.AdminMenu.SchoolKidsMenu;

import com.bartosso.bot.Bot;
import com.bartosso.bot.Util.CustomKeyboardUtil;
import com.bartosso.bot.command.Command;
import com.bartosso.bot.entity.ProjectEntities.Bus;
import com.bartosso.bot.entity.ProjectEntities.Entity;
import com.bartosso.bot.entity.ProjectEntities.Kid;
import com.bartosso.bot.entity.ProjectEntities.Parent;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.api.objects.Contact;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("Duplicates")
public class AddNewKidCommand extends Command {
    private   int                             step = 0;
    private   int                             page = 0;
    private   String                          kidName;
    private   String                          photo;
    private   String                          school_id;
    private boolean                         creatingKid;
    private   ArrayList<InlineKeyboardMarkup> pages;
    private boolean                         creatingParent;
    private   int                             messageWithKeyboardId;
    private   long                            busId;
    private   ArrayList<Contact>              parents = new ArrayList<>();

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (creatingKid){
            if (update.hasMessage()){
                if (update.getMessage().hasText()){
                    if (update.getMessage().getText().equals(buttonDao.getButtonText(10))){
                        deleteMessages(bot);
                        return true;
                    }
                   if (step == 0) {
                       kidName = update.getMessage().getText();
                       step = 1;
                       sendMessageByIdWithKeyboard(bot,34,6);
                       return false;
                   }
                }
                if (step == 1) {
                    if (update.getMessage().hasPhoto()){
                        photo = update.getMessage().getPhoto().get(update.getMessage().getPhoto().size()-1).getFileId();
                        step = 2;
                        showSchoolList(bot);
                        return false;
                    }
                    else {
                        sendErrorMessageForFormat(bot);
                        return false;
                    }
                }
                sendErrorMessageForFormat(bot);
                return false;
            }
            if (update.hasCallbackQuery()){
                String chose = update.getCallbackQuery().getData();
                if (chose.equals("back")){
                    deleteMessages(bot);
                    return true;
                }
                if (chose.equals("nextPage")){
                    page++;
                    showPage(bot);
                    return false;
                }
                if (chose.equals("previousPage")){
                    page--;
                    showPage(bot);
                    return false;
                }
                switch (step){
                    case 1:
                if (chose.equals(buttonDao.getButtonText(15))){
                    photo = null;
                    step  = 2;
                    showSchoolList(bot);
                    return false;
                }
                    case 2:
                    school_id = chose.substring(chose.indexOf(":")+1);
                    showBusList(bot);
                    step = 3;
                    return false;
                    case 3:
                    busId = Long.parseLong(chose.substring(chose.indexOf(":")+1));
                    creatingKid    = false;
                    creatingParent = true;
                    step = 4;
                    sendMessageByIdWithKeyboard(bot, 39,3);
                    return false;

                }

            }
        }
        if (creatingParent) {
            if (update.hasMessage()) {
                if (update.getMessage().getContact() != null) {
                    if (update.getMessage().getContact().getUserID()==null){
                        deleteMessages(bot);
                        sendMessageByIdWithKeyboard(bot,46,3);
                        return false;
                    }
                    parents.add(update.getMessage().getContact());
                    deleteMessages(bot);
                    sendMessageByIdWithKeyboard(bot,41,6);
                    return false;
                }
            }
            if (update.hasCallbackQuery()){
                if (update.getCallbackQuery().getData().equals(buttonDao.getButtonText(15))){
                    if (parents.isEmpty()){
                        sendMessageById(bot,40);
                        return false;
                    }
                    deleteMessages(bot);
                    creatingParent = false;
                    addKidUndParents(new Kid(kidName,school_id,photo),parents);
                    parents               = new ArrayList<>();
                    step                  = 0;
                    page                  = 0;
                    kidName               = null;
                    photo                 = null;
                    school_id             = null;
                    pages                 = null;
                    messageWithKeyboardId = 0;
                    busId                 = 0;
                    creatingKid           = false;
                    parents               = new ArrayList<>();
                    sendMessageByIdWithKeyboard(bot,20,3);
                    return false;
                }
            }
        }
        if (update.hasMessage()){
            chatId = update.getMessage().getChatId();
            if (update.getMessage().hasText()){
                if (update.getMessage().getText().equals(buttonDao.getButtonText(10))){
                deleteMessages(bot);
                return true;}
            }
            sendFirstQuestion(bot);
        }
        return false;
    }

    private void sendFirstQuestion(Bot bot) throws SQLException, TelegramApiException {
        sendMessageByIdWithKeyboard(bot,36,3);
        creatingKid = true;
    }

    @SuppressWarnings("Duplicates")
    private void showSchoolList(Bot bot) throws SQLException, TelegramApiException {
        page = 0;
        deleteMessages(bot);
        //noinspection unchecked
        List<Entity> list       = factory.getSchoolDao().getAllSchools();
        if (list==null){
            sendMessageByIdWithKeyboard(bot,43,3);
            return;
        }
        pages                   = CustomKeyboardUtil.getPagesWithEntitiesAsButtonText(list,"school");
        SendMessage sendMessage = messageDao.getMessage(37).getSendMessage().setChatId(chatId)
                .setReplyMarkup(pages.get(page));
        messageWithKeyboardId   = bot.execute(sendMessage).getMessageId();
        messagesIdsToDelete.add(messageWithKeyboardId);
    }

    private void showBusList(Bot bot) throws SQLException, TelegramApiException {
        page = 0;
        deleteMessages(bot);
        //noinspection unchecked
        List<Entity> list       = factory.getBusesDao().getAll();
        if (list==null){
            sendMessageByIdWithKeyboard(bot,44,3);
            return;
        }
        pages                   = CustomKeyboardUtil.getPagesWithEntitiesAsButtonText(list,"buses");
        SendMessage sendMessage = messageDao.getMessage(38).getSendMessage().setChatId(chatId)
                .setReplyMarkup(pages.get(page));
        messageWithKeyboardId   = bot.execute(sendMessage).getMessageId();
        messagesIdsToDelete.add(messageWithKeyboardId);
    }

    private void addKidToRoute(long chatId){
        Bus bus = factory.getBusesDao().getBusById(busId);
        ArrayList<Long> toSchoolKids;
        if (bus.getTo_school_kids()!=null){
            toSchoolKids = bus.getTo_school_kids();
        }else {
            toSchoolKids = new ArrayList<>();
        }
        toSchoolKids.add(chatId);
        Long[] kids = toSchoolKids.toArray(new Long[toSchoolKids.size()]);
        factory.getBusesDao().updateMorningRoute(kids,busId);
    }

    private void addKidUndParents(Kid kid, ArrayList<Contact> contacts){
        long childId = factory.getKidsDao().addNewKid(kid);
        contacts.stream().map(contact -> new Parent(contact,childId)).forEach(parent -> parentDao.addNew(parent));
        addKidToRoute(childId);
    }


    private void showPage(Bot bot) throws TelegramApiException {
        bot.execute(new EditMessageReplyMarkup().setChatId(chatId).setMessageId(messageWithKeyboardId)
                .setReplyMarkup(pages.get(page)));
    }

}
