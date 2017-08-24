package com.bartosso.bot.command.impl.AdminMenu.EmployeeMenu.Drivers;

import com.bartosso.bot.Bot;
import com.bartosso.bot.Util.CustomKeyboardUtil;
import com.bartosso.bot.command.Command;
import com.bartosso.bot.command.impl.AdminMenu.BusesMenu.ChangeDriverCommand;
import com.bartosso.bot.entity.ProjectEntities.Entity;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.List;

public class ShowAllDriversCommand extends Command {
    private final long schoolId;
    private Command    changeSchool;
    private boolean    runningCommand;

    ShowAllDriversCommand(long schoolId, long constructorChatId) {
        this.schoolId = schoolId;
        this.chatId = constructorChatId;
    }


    private List<SendMessage> pages;
    private int               messageWithPageId;
    private int               page = 0;
    private boolean           sendsList;
    @SuppressWarnings("Duplicates")
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (runningCommand) {
            if (changeSchool.execute(update, bot)) {
                runningCommand = false;
                changeSchool   = null;
                if (schoolId<0){
                    if (schoolId == -404) {
                        //noinspection unchecked
                        showFirstPage(bot,factory.getDriverDao().getAllDriversWithoutSchool());
                        return false;
                    }
                } else {
                    //noinspection unchecked
                    showFirstPage(bot, factory.getDriverDao().getAllFromSchool(schoolId));
                    return false;
                }
            } else {
                return false;
            }


        }
        if (!sendsList){
            sendsList = true;
            if (schoolId<0){
                if (schoolId == -404) {
                    //noinspection unchecked
                    showFirstPage(bot,factory.getDriverDao().getAllDriversWithoutSchool());
                    return false;
                }
            }
            //noinspection unchecked
            showFirstPage(bot, factory.getDriverDao().getAllFromSchool(schoolId));
            return false;
        }
        if (update.hasMessage()){
            chatId = update.getMessage().getChatId();
            if (update.getMessage().hasText()){
                if (update.getMessage().getText().equals(buttonDao.getButtonText(10))){
                    deleteMessages(bot);
                    return true;
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
                SendMessage sendMessage = pages.get(page);
                bot.execute(new EditMessageText().setMessageId(messageWithPageId)
                        .setText(sendMessage.getText())
                        .setReplyMarkup((InlineKeyboardMarkup) sendMessage.getReplyMarkup())
                        .setChatId(chatId));
                return false;
            }
            if (chose.equals("previousPage")){
                page--;
                SendMessage sendMessage = pages.get(page);
                bot.execute(new EditMessageText().setMessageId(messageWithPageId)
                        .setText(sendMessage.getText())
                        .setReplyMarkup((InlineKeyboardMarkup) sendMessage.getReplyMarkup())
                        .setChatId(chatId));
                return false;
            }
            if (chose.equals("change")) {
                deleteMessages(bot);
                runningCommand = true;
                changeSchool = new ChangeDriverSchoolCommand(chatId, schoolId);
                if (changeSchool.execute(update, bot)) {
                    if (schoolId<0){
                        if (schoolId == -404) {
                            //noinspection unchecked
                            showFirstPage(bot,factory.getDriverDao().getAllDriversWithoutSchool());
                            return false;
                        }
                    }
                    //noinspection unchecked
                    showFirstPage(bot, factory.getDriverDao().getAllFromSchool(schoolId));
                } else {
                    return false;
                }
            }

            return false;
        }
        return false;
    }

    private void showFirstPage(Bot bot, List<Entity> entities) throws SQLException, TelegramApiException {
        //noinspection unchecked
        factory.getDriverDao().getAllFromSchool(schoolId);
        if (entities==null){
            sendNothingFound(bot);
            return;
        }
        pages = CustomKeyboardUtil.getEditDriverKeyboard(entities,chatId);
        SendMessage sendMessage = pages.get(page);
        messageWithPageId       = bot.execute(sendMessage).getMessageId();
        messagesIdsToDelete.add(messageWithPageId);
    }

}
