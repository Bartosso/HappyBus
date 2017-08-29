package com.bartosso.bot.command.impl.AdminMenu.MailingMenu;

import com.bartosso.bot.Bot;
import com.bartosso.bot.Util.CustomKeyboardUtil;
import com.bartosso.bot.command.Command;
import com.bartosso.bot.entity.ProjectEntities.Entity;
import com.bartosso.bot.entity.ProjectEntities.Parent;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;
//Sorry for that, i didn't have enough time and i'm just bored :'(
@SuppressWarnings("unchecked")
public class GetParentsForMailingCommand extends Command {
    private final long                            schoolId;
    private final MailingCommand                  mailingCommand;
    private       boolean                         creatingParentsList;
    private       boolean                         tryingGetParents;
    private       boolean                         selectingParent;
    private       List<Parent>                    chosenParents;
    private       ArrayList<InlineKeyboardMarkup> pages;
    private       int                             page = 0;
    private       int                             messageWithPages;
    private       long                            schoolIdForSelectingParentsInSchool;
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (!tryingGetParents) {
            tryingGetParents = true;
            return getParents(schoolId,bot);
        }
        if (creatingParentsList) {
            if (update.hasCallbackQuery()) {
                String chose = update.getCallbackQuery().getData();
                if (chose.equals("ready")) {
                    deleteMessages(bot);
                    mailingCommand.setReadyForMailing(true);
                    mailingCommand.setParentsForMailing(chosenParents);
                    return true;
                }
                if (chose.equals("back")){
                    selectingParent = false;
                    deleteMessages(bot);
                    showFirstPage(bot,factory.getSchoolDao().getAllSchools(),true);
                    return false;
                }
                if (chose.equals("backOff")){
                    deleteMessages(bot);
                    return true;
                }
                if (chose.equals("nextPage")) {
                    page++;
                    showPage(bot);
                    return false;
                }
                if (chose.equals("previousPage")){
                    page--;
                    showPage(bot);
                    return false;
                }
                if (chose.contains("parent")) {
                    deleteMessages(bot);
                   chosenParents.add(factory.getParentDao()
                           .getParentByChatId(Long.parseLong(chose.substring(chose.indexOf(":")+1))));
                    List<Parent> parentsInSelectedSchool = deleteAddedParent(factory.getParentDao()
                            .getAllParentsFromSchool(schoolIdForSelectingParentsInSchool));
                    showFirstPage(bot,(List<Entity>) (List<?>) parentsInSelectedSchool,false);
                    return false;
                }
                if (chose.contains("school")) {
                    selectingParent = true;
                    schoolIdForSelectingParentsInSchool = Long.parseLong(chose.substring(chose.indexOf(":")+1));
                    deleteMessages(bot);
                    showFirstPage(bot,(List<Entity>)(List<?>) factory.getParentDao()
                            .getAllParentsFromSchool(Long.parseLong(
                                    chose.substring(chose.indexOf(":") + 1))),false);
                    return false;
                }

            }
            if (update.hasMessage() && update.getMessage().hasText() && update.getMessage().getText().
                    equals(buttonDao.getButtonText(10))) {
                deleteMessages(bot);
                return true;
            }
        }
        return true;
    }

    GetParentsForMailingCommand(long schoolId, long chatId, MailingCommand mailingCommand) {
    this.schoolId = schoolId;
    this.chatId   = chatId;
    this.mailingCommand = mailingCommand;
    }

    private boolean getParents(long schoolId,Bot bot) throws SQLException, TelegramApiException {
        if (schoolId==-200){
            mailingCommand.setReadyForMailing(true);
            mailingCommand.setParentsForMailing(factory.getParentDao().getAllParents());
            return true;
        }
        if (schoolId==-300){
            creatingParentsList = true;
            chosenParents       = new ArrayList();
            //noinspection unchecked
            showFirstPage(bot,factory.getSchoolDao().getAllSchools(),true);
            return false;
        }
        if (schoolId>0){
            mailingCommand.setReadyForMailing(true);
            mailingCommand.setParentsForMailing(factory.getParentDao().getAllParentsFromSchool(schoolId));
            return true;
        }
        return true;
    }

    private void showFirstPage(Bot bot, List<Entity> list,boolean choseSchools)
            throws SQLException, TelegramApiException {
        String text;
        if (list == null) {
            if (choseSchools){
                sendNothingFound(bot);
            } else {
            sendMessageByIdWithKeyboard(bot,23,15);
            }
            return;
        }
        if (choseSchools){
            text = messageDao.getMessage(37).getSendMessage().getText();
        pages = CustomKeyboardUtil.getPagesForMailing(list,"school");
        } else {
            text = messageDao.getMessage(50).getSendMessage().getText();
            pages = CustomKeyboardUtil.getPagesWithEntitiesAsButtonText(list,"parent");
        }
        if (selectingParent){
            if (pages.size()<=(page+1)){
                page = (pages.size()-1);
            }
        }
        else {
            page = 0;
        }
        SendMessage sendMessage = new SendMessage(chatId,text)
                .setReplyMarkup(pages.get(page));
        messageWithPages    = bot.execute(sendMessage).getMessageId();
        messagesIdsToDelete.add(messageWithPages);
    }

    private void showPage(Bot bot) throws TelegramApiException {
        bot.execute(new EditMessageReplyMarkup().setChatId(chatId).setMessageId(messageWithPages)
                .setReplyMarkup(pages.get(page
        )));
    }

    private List<Parent> deleteAddedParent(List<Parent> parentsInSelectedSchool){
      return parentsInSelectedSchool.stream().filter(p -> chosenParents.stream().noneMatch(
              parent -> parent.getId()==p.getId())).collect(toList());
    }
}
