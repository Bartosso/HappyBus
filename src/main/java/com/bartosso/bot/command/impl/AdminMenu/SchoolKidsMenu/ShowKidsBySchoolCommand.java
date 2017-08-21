package com.bartosso.bot.command.impl.AdminMenu.SchoolKidsMenu;

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

@SuppressWarnings("Duplicates")
public class ShowKidsBySchoolCommand extends Command {
    private boolean                         schoolSelected;
    private boolean                         pagesSends;
    private int                             page = 0;
    private int                             messageWithKeyboardId;
    private int                             schoolId;
    private ArrayList<InlineKeyboardMarkup> schoolPages;
    private List<SendMessage>               kidsListMessages;


    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (schoolSelected) {
            if (update.hasCallbackQuery()) {
                String callBackData = update.getCallbackQuery().getData();
                if (callBackData.equals("back")) {
                    schoolSelected = false;
                    deleteMessages(bot);
                    showFirstPage(bot);
                    return false;
                }
                if (callBackData.equals("nextPage")){
                    page++;
                    showPage(bot);
                    return false;
                }
                if (callBackData.equals("previousPage")){
                    page--;
                    showPage(bot);
                    return false;
                }
            }
            if (update.hasMessage()){
                if (update.getMessage().hasText()){
                    if (update.getMessage().getText().equals(buttonDao.getButtonText(10))){
                        deleteMessages(bot);
                        schoolSelected = false;
                        return true;
                    }
                }
            }
            sendErrorMessageForFormat(bot);
            return false;
        }
        if (update.hasMessage()){
            chatId = update.getMessage().getChatId();
            if (update.getMessage().hasText()){
                if (update.getMessage().getText().equals(buttonDao.getButtonText(10))){
                    deleteMessages(bot);
                    return true;
                }
                if (!pagesSends){
                    showFirstPage(bot);
                    pagesSends = true;
                    return false;
                }

            }
        }
        if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getFrom().getId();
            String chose = update.getCallbackQuery().getData();
            if (chose.equals("back")) {
                deleteMessages(bot);
                return true;
            }
            if (chose.contains(":")){
                schoolId = Integer.parseInt(chose.substring(chose.indexOf(":")+1));
                schoolSelected = true;
                showFirstPage(bot);
                return false;
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



        }
        return false;
    }
    private void showFirstPage(Bot bot) throws SQLException, TelegramApiException {
        List list;
        String message        = "Ошибка";
        String callBackPrefix = null;
        page = 0;
        if (schoolSelected){
            deleteMessages(bot);
            list = factory.getKidsDao().getKidsBySchool(String.valueOf(schoolId));
            if (list==null){
                sendNothingFound(bot);
                return;
            }
            //noinspection unchecked
            kidsListMessages = CustomKeyboardUtil.getEntitiesListViaMessageUndPagesSelector(list,chatId);
        } else {
            callBackPrefix = "school";
            message = messageDao.getMessage(37).getSendMessage().getText();
            list    =  factory.getSchoolDao().getAllSchools();
        }
        //noinspection unchecked
        if (list==null){
            if (schoolSelected) {
                sendMessageByIdWithKeyboard(bot,47,3);
            }
            else {
                sendMessageByIdWithKeyboard(bot,43,3);
            }
            return;
        }
        if (schoolSelected){
            messageWithKeyboardId = 0;
            messagesIdsToDelete.add(bot.execute(kidsListMessages.get(page)).getMessageId());
            return;
        }
        //noinspection unchecked
        schoolPages = CustomKeyboardUtil.getPagesWithEntitiesAsButtonText(list,callBackPrefix);
        SendMessage sendMessage = new SendMessage(chatId,message).setReplyMarkup(schoolPages.get(page));
        messageWithKeyboardId = bot.execute(sendMessage).getMessageId();
        messagesIdsToDelete.add(messageWithKeyboardId);
    }

    private void showPage(Bot bot) throws TelegramApiException {
        if (schoolSelected){
            deleteMessages(bot);
            bot.execute(kidsListMessages.get(page));
            return;
        }
        bot.execute(new EditMessageReplyMarkup().setChatId(chatId).setMessageId(messageWithKeyboardId)
                .setReplyMarkup(schoolPages.get(page)));
    }

}
