package com.bartosso.bot.command.impl.AdminMenu.EmployeeMenu.Drivers;

import com.bartosso.bot.Bot;
import com.bartosso.bot.Util.CustomKeyboardUtil;
import com.bartosso.bot.command.Command;
import com.bartosso.bot.entity.ProjectEntities.Entity;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChangeDriverSchoolCommand extends Command {
    private       ArrayList<InlineKeyboardMarkup> pages;
    private       int                             page           = 0;
    private       int                             messageWithPage;
    private       boolean                         keyboardSends;
    private       boolean                         choseSchool;
    private final long                            oldSchoolId;
    private       long                            driverId;
    private       long                            newSchoolId;
    ChangeDriverSchoolCommand(long chatId, long oldSchoolId){
        this.chatId   = chatId;
        this.oldSchoolId = oldSchoolId;
    }
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (!keyboardSends) {
            keyboardSends = true;
            showFirstPageOfDrivers(bot);
            return false;
        }

        if (update.hasCallbackQuery()) {
            String chose = update.getCallbackQuery().getData();
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
            if (chose.equals("back")) {
                if (choseSchool) {
                    choseSchool = false;
                    deleteMessages(bot);
                    showFirstPageOfDrivers(bot);
                    return false;
                }
                deleteMessages(bot);
                return true;
            }
            if (chose.contains("driver")) {
                driverId = Long.parseLong(chose.substring(chose.indexOf(":")+1));
                deleteMessages(bot);
                choseSchool = true;
                showFirstPageOfSchools(bot);
                return false;
            }
            if (chose.contains("school")) {
                deleteMessages(bot);
                newSchoolId = Long.parseLong(chose.substring(chose.indexOf(":")+1));
                choseSchool = false;
                changeSchool();
                pages           = null;
                page            = 0;
                messageWithPage = 0;
                keyboardSends   = false;
                choseSchool     = false;
                driverId        = 0;
                newSchoolId     = 0;
                sendMessageByIdWithKeyboard(bot,20,3);
                return false;
            }
        }
        //noinspection Duplicates
        if (update.hasMessage()) {
            if (update.getMessage().hasText()) {
                if (update.getMessage().getText().equals(buttonDao.getButtonText(10))) {
                    deleteMessages(bot);
                    return true;
                }
            }
        }

        return false;
    }

    private void showFirstPageOfSchools(Bot bot) throws SQLException, TelegramApiException {
        //noinspection unchecked
        List<Entity> schools = factory.getSchoolDao().getAllSchools();
        if (schools == null) {
            sendNothingFound(bot);
            return;
        }
        page = 0;
        //noinspection unchecked
        pages = CustomKeyboardUtil.getPagesWithEntitiesAsButtonText(factory.getSchoolDao().getAllSchools(),"school");
        messageWithPage = bot.execute(new SendMessage(chatId,"Выберите школу к которой хотите прикрепить водителя")
                .setReplyMarkup(pages.get(page))).getMessageId();
        messagesIdsToDelete.add(messageWithPage);

    }

    private void showFirstPageOfDrivers(Bot bot) throws SQLException, TelegramApiException {
        List<Entity> drivers;
        if (oldSchoolId == -404) {
            //noinspection unchecked
            drivers = factory.getDriverDao().getAllDriversWithoutSchool();
        } else {
            //noinspection unchecked
            drivers = factory.getDriverDao().getAllFromSchool(oldSchoolId);
        }
        if (drivers == null) {
            deleteMessages(bot);
            sendNothingFound(bot);
            return;
        }
        page = 0;
        //noinspection unchecked
        pages = CustomKeyboardUtil.getPagesWithEntitiesAsButtonText(drivers,"driver");
        messageWithPage = bot.execute(new SendMessage(chatId,"Выберите водителя").setReplyMarkup(pages.get(page))).getMessageId();
        messagesIdsToDelete.add(messageWithPage);
    }
    private void showPage(Bot bot) throws TelegramApiException {
        bot.execute(new EditMessageReplyMarkup().setChatId(chatId).setMessageId(messageWithPage).setReplyMarkup(pages.get(page)));
    }

    private void changeSchool(){
        factory.getDriverDao().setNewSchool(newSchoolId,driverId);
    }
}
