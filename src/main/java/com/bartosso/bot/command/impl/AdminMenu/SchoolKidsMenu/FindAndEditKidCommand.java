package com.bartosso.bot.command.impl.AdminMenu.SchoolKidsMenu;

import com.bartosso.bot.Bot;
import com.bartosso.bot.Util.CustomKeyboardUtil;
import com.bartosso.bot.command.Command;
import com.bartosso.bot.dao.impl.KidsDao;
import com.bartosso.bot.entity.ProjectEntities.Entity;
import com.bartosso.bot.entity.ProjectEntities.Kid;
import com.bartosso.bot.entity.ProjectEntities.Parent;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;


public class FindAndEditKidCommand extends Command{
    private boolean                         waitingName;
    private boolean                         editingKid;
    private boolean                         editingParents;
    private boolean                         removingParent;
    private boolean                         editingThingsInKid;
    private List <Entity>                   kids;
    private List <Parent>                   parents;
    private int                             page;
    private ArrayList<InlineKeyboardMarkup> pages;
    private List<SendMessage>               parentPages;
    private KidsDao                         kidsDao                = factory.getKidsDao();
    private int                             messageWithKeyboardId;
    private long                            childId;
    private String                          chose;
    @SuppressWarnings("Duplicates")
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (!editingKid){
        if (update.hasMessage()){
            chatId = update.getMessage().getChatId();
            if (update.getMessage().hasText()) {
                if (update.getMessage().getText().equals(buttonDao.getButtonText(10))){
                    deleteMessages(bot);
                    return true;
                }
                if (waitingName){
                    waitingName = false;
                    String searchString = update.getMessage().getText();
                    kids = kidsDao.getKidsByName(searchString);
                    showFirstPage(bot);
                    return false;
                }
                sendMessageByIdWithKeyboard(bot,48,3);
                waitingName = true;
                return false;
            }
        }
        }
        if (editingParents) {
            if (update.hasCallbackQuery()){
                chose = update.getCallbackQuery().getData();
                if (chose.equals("backOff")||chose.equals("back")){
                    editingParents = false;
                    deleteMessages(bot);
                    sendEditButtons(bot);
                    return false;
                }
                if (chose.equals("addParent")){
                    sendMessageByIdWithKeyboard(bot,39,3);
                    return false;
                }
                if (chose.equals("deleteParent")){
                    showParentsToDelete(bot);
                    removingParent = true;
                    return false;
                }
                if (removingParent){
                    if (chose.contains(":")){
                        long parentId = Long.parseLong(chose.substring(chose.indexOf(":")+1));
                        killParent(bot,parentId);
                    }
                    if (chose.equals("nextPage")){
                        page++;
                        showPage(bot);
                    }
                    if (chose.equals("previousPage")){
                        page--;
                        showPage(bot);
                    }
                    return false;
                }
                if (chose.equals("nextPage")) {
                    page++;
                    showParentsPage(bot);
                }
                if (chose.equals("previousPage")) {
                    page--;
                    showParentsPage(bot);
                }
                return false;
            }
            if (update.hasMessage()){
                if (update.getMessage().hasText()){
                    if (update.getMessage().getText().equals(buttonDao.getButtonText(10))){
                        deleteMessages(bot);
                        editingParents = false;
                        sendEditButtons(bot);
                        return false;
                    }
                }
                if (update.getMessage().getContact()!=null){
                    if (update.getMessage().getContact().getUserID()==null){
                        sendMessageByIdWithKeyboard(bot,46,3);
                        return false;
                    }
                    factory.getParentDao().addNew(new Parent(update.getMessage().getContact(),childId));
                    deleteMessages(bot);
                    sendEditButtons(bot);
                    editingParents = false;
                    return false;
                }

            }
        }
        if (editingKid){
            if (update.hasCallbackQuery()){
                chose = update.getCallbackQuery().getData();
                if (chose.equals("back")||chose.equals("backOff")||chose.equals(buttonDao.getButtonText(10))){
                    if (editingThingsInKid){
                        editingThingsInKid = false;
                        deleteMessages(bot);
                        sendEditButtons(bot);
                        return false;
                    }
                    deleteMessages(bot);
                    editingKid = false;
                    sendMessageByIdWithKeyboard(bot,48,3);
                    waitingName = true;
                    return false;
                }
                if (chose.equals("name")){
                    editingThingsInKid = true;
                    deleteMessages(bot);
                 sendMessageByIdWithKeyboard(bot,32,15);
                 return false;
                }
                if (chose.equals("photo")){
                    deleteMessages(bot);
                    sendMessageByIdWithKeyboard(bot,34,6);
                    return false;
                }
                if (chose.equals("school_id")){
                    editingThingsInKid = true;
                    showSchoolList(bot);
                    return false;
                }
                if (chose.equals("parents")){
                    showParentsList(bot);
                    editingParents = true;
                    return false;
                }
                if (chose.equals(buttonDao.getButtonText(15))){
                    kidsDao.changeKidThings("photo",null,childId);
                    sendEditButtons(bot);
                    return false;
                }
            }

            if (update.hasMessage()) {
                if (update.getMessage().hasText()){
                    if (update.getMessage().getText().equals(buttonDao.getButtonText(10))){
                        deleteMessages(bot);
                        return true;
                    }
                    if (chose.equals("name")){
                        kidsDao.changeKidThings(chose,update.getMessage().getText(),childId);
                        sendEditButtons(bot);
                        return false;
                    }
                }
                if (update.getMessage().hasPhoto()){
                    kidsDao.changeKidThings(chose,update.getMessage().getPhoto().get(update.getMessage().
                                    getPhoto().size()-1).getFileId(),
                            childId);
                    sendEditButtons(bot);
                    return false;
                }

            }
        }
        if (update.hasCallbackQuery()){
            String chose = update.getCallbackQuery().getData();
            if (chose.equals("backOff")){
                deleteMessages(bot);
                sendMessageByIdWithKeyboard(bot,48,3);
                waitingName = true;
            }
            if (chose.contains(":")){
            if (chose.substring(0,chose.indexOf(":")).equals("kids")){
                childId = Long.parseLong(chose.substring(chose.indexOf(":")+1));
                sendEditButtons(bot);
                return false;
            }
            if (chose.substring(0,chose.indexOf(":")).equals("school")){
                kidsDao.changeKidThings("school_id",chose.substring(chose.indexOf(":")+1),childId);
                editingKid = true;
                sendEditButtons(bot);
                return false;
            }
            }
            if (chose.equals("nextPage")){
                page++;
                showPage(bot);
            }
            if (chose.equals("previousPage")){
                page--;
                showPage(bot);
            }
            return false;
        }
        sendErrorMessageForFormat(bot);
        return false;
    }

    private void showFirstPage(Bot bot) throws SQLException, TelegramApiException {
        String message        = "Результат поиска";
        String callBackPrefix = "kids";
        page = 0;
        //noinspection unchecked
        if (kids==null){
                sendMessageByIdWithKeyboard(bot,47,3);
            return;
        }
        //noinspection unchecked
        pages = CustomKeyboardUtil.getPagesWithEntitiesAsButtonText(kids,callBackPrefix);
        SendMessage sendMessage = new SendMessage(chatId,message).setReplyMarkup(pages.get(page));
        messageWithKeyboardId = bot.execute(sendMessage).getMessageId();
        messagesIdsToDelete.add(messageWithKeyboardId);
    }
    private void showPage(Bot bot) throws TelegramApiException {
        bot.execute(new EditMessageReplyMarkup().setChatId(chatId).setMessageId(messageWithKeyboardId)
                .setReplyMarkup(pages.get(page)));
    }

    private void showParentsPage(Bot bot) throws TelegramApiException {
        deleteMessages(bot);
        SendMessage sendMessage = parentPages.get(page);
        messageWithKeyboardId = bot.execute(sendMessage.setChatId(chatId)).getMessageId();
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

    @SuppressWarnings("Duplicates")
    private void showParentsList(Bot bot) throws SQLException, TelegramApiException {
        page = 0;
        deleteMessages(bot);
        //noinspection unchecked
        parents       = factory.getParentDao().getParentsByChildId(childId);
        if (parents==null){
            sendMessageByIdWithKeyboard(bot,43,3);
            return;
        }
        parentPages                   = CustomKeyboardUtil.getParentsWithDeleteUndAddButtons(parents,chatId);
        SendMessage sendMessage = parentPages.get(page);
        messageWithKeyboardId   = bot.execute(sendMessage).getMessageId();
        messagesIdsToDelete.add(messageWithKeyboardId);
    }

    private void showParentsToDelete(Bot bot) throws SQLException, TelegramApiException {
        page = 0;
        deleteMessages(bot);
        //noinspection unchecked
        parents       = factory.getParentDao().getParentsByChildId(childId);
        if (parents==null){
            sendMessageByIdWithKeyboard(bot,43,3);
            return;
        }
        List<Entity> list = parents.stream().map(parent -> (Entity) parent).collect(toList());
        pages                   = CustomKeyboardUtil.getPagesWithEntitiesAsButtonText(list,"remove");
        SendMessage sendMessage = messageDao.getMessage(50).getSendMessage().setChatId(chatId)
                .setReplyMarkup(pages.get(page));
        messageWithKeyboardId   = bot.execute(sendMessage).getMessageId();
        messagesIdsToDelete.add(messageWithKeyboardId);
    }


    private void sendEditButtons(Bot bot) throws TelegramApiException {
        @SuppressWarnings("EqualsBetweenInconvertibleTypes")
        Kid kid = kidsDao.getKidById(childId);
        List<Parent> parents = factory.getParentDao().getParentsByChildId(childId);
        StringBuilder sb = new StringBuilder();
        sb.append("Вы выбрали\n").append(kid.toString()).append("\nШкола: ");
        try {
            sb.append(factory.getSchoolDao().getSchoolById(Long.parseLong(kid.getSchool_id())).getName());
        } catch (org.springframework.dao.EmptyResultDataAccessException e){
            sb.append("Школа удалена, выберите новую в меню редактирования ученика.");
        }
        sb.append("\nРодители: ");
        parents.forEach(parent -> sb.append(parent.toString()));
        sb.append("\nВыберите что хотите изменить");
        editingKid = true;

            SendMessage sendMessage = new SendMessage().setChatId(chatId)
                    .setText(sb.toString())
                    .setReplyMarkup(CustomKeyboardUtil.getKeyboardForEditKids());
            deleteMessages(bot);
        if (kid.getPhoto()!=null){
            SendPhoto sendPhoto = new SendPhoto().setPhoto(kid.getPhoto()).setChatId(chatId);
            messagesIdsToDelete.add(bot.sendPhoto(sendPhoto).getMessageId());
        }
            messageWithKeyboardId = bot.execute(sendMessage).getMessageId();
            messagesIdsToDelete.add(messageWithKeyboardId);
    }

    private void killParent(Bot bot,long parentId) throws SQLException, TelegramApiException {
        if (parents.size()==1){
            sendMessageByIdWithKeyboard(bot,49,3);
            return;
        }
        factory.getParentDao().killParentById(parentId);
        showParentsToDelete(bot);
    }
}
