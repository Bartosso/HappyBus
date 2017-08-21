package com.bartosso.bot.command.impl.CoordinatorMenu.ToSchool.ShowListsThings;

import com.bartosso.bot.Bot;
import com.bartosso.bot.Util.CustomKeyboardUtil;
import com.bartosso.bot.command.Command;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ShowListsCommand extends Command {
    private int                             page = 0;
    private ArrayList<InlineKeyboardMarkup> schoolPages;
    private int                             messageWithKeyboardId;
    private boolean                         runningCommand;
    private Command                         selectBusCommand;
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (runningCommand){
            if (selectBusCommand.execute(update, bot)){
                showSchools(bot);
                selectBusCommand  = null;
                runningCommand    = false;
            }
            return false;
        }
        if (update.hasMessage()){
            chatId = update.getMessage().getChatId();
            if (update.getMessage().hasText()){
                if (update.getMessage().getText().equals(buttonDao.getButtonText(10))) {
                    deleteMessages(bot);
                    return true;
                }
            }
            showSchools(bot);
            return false;
        }
        if (update.hasCallbackQuery()){
            String chose = update.getCallbackQuery().getData();
            if (chose.equals("back")) {
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
            if (chose.contains("school")){
                selectBusCommand = getSelectBusCommand(Long.parseLong(chose.substring(chose.indexOf(":")+1)),chatId);
                return selectedSchool(update, bot);
            }
        }

            return false;
    }

    private void showSchools(Bot bot) throws SQLException, TelegramApiException {
        List list = factory.getSchoolDao().getAllSchools();
        page = 0;
            deleteMessages(bot);
            if (list==null){
                sendNothingFound(bot);
                return;
            }
            String message = messageDao.getMessage(37).getSendMessage().getText();
        //noinspection unchecked
        schoolPages = CustomKeyboardUtil.getPagesWithEntitiesAsButtonText(list,"school");
        SendMessage sendMessage = new SendMessage(chatId,message).setReplyMarkup(schoolPages.get(page));
        messageWithKeyboardId = bot.execute(sendMessage).getMessageId();
        messagesIdsToDelete.add(messageWithKeyboardId);
    }

    private void showPage(Bot bot) throws TelegramApiException {
        bot.execute(new EditMessageReplyMarkup().setChatId(chatId).setMessageId(messageWithKeyboardId)
                .setReplyMarkup(schoolPages.get(page)));
    }

    private boolean selectedSchool(Update update, Bot bot) throws SQLException, TelegramApiException {
        deleteMessages(bot);
        if (selectBusCommand.execute(update, bot)){
            showSchools(bot);
            return false;
        } else {
            runningCommand = true;
            return false; }
    }

    public Command getSelectBusCommand(long schoolId,long constructorChatId){
        return new SelectBusCommand(schoolId,constructorChatId);
    }
}
