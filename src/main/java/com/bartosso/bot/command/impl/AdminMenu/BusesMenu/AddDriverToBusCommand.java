package com.bartosso.bot.command.impl.AdminMenu.BusesMenu;

import com.bartosso.bot.Bot;
import com.bartosso.bot.Util.CustomKeyboardUtil;
import com.bartosso.bot.command.Command;
import com.bartosso.bot.dao.impl.BusesDao;
import com.bartosso.bot.entity.ProjectEntities.Entity;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("Duplicates")
public class AddDriverToBusCommand extends Command {
    private boolean                         busSelected;
    private boolean                         pagesSends;
    private int                             page = 0;
    private int                             messageWithKeyboardId;
    private int                             busId;
    private ArrayList<InlineKeyboardMarkup> pages;
    private BusesDao                        busesDao   = factory.getBusesDao();
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (busSelected) {
            if (update.hasCallbackQuery()) {
                String callBackData = update.getCallbackQuery().getData();
                if (callBackData.equals("back")) {
                    busSelected = false;
                    showFirstPage(bot);
                    return false;
                }
                if (callBackData.contains(":")){
                    String driverID = callBackData.substring(callBackData.indexOf(":")+1);
                    String textToDB = callBackData.substring(0,callBackData.indexOf(":"));
                    busesDao.updateBusThings(driverID,textToDB,busId);
                    factory.getDriverDao().setGotBus(true, Long.parseLong(driverID));
                    busSelected = false;
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
                return false;}
                else {
                    String searchNumber = update.getMessage().getText();
                    //noinspection unchecked
                    List<Entity> list = busesDao.getAllWithThatNumber(searchNumber);
                    if (list==null){
                        sendNothingFound(bot);
                        return false;
                    }
                    pages = CustomKeyboardUtil.getPagesWithEntitiesAsButtonText(list,"busId");
                    EditMessageText editMessageText = new EditMessageText().setChatId(chatId).setText("Результаты поиска")
                            .setReplyMarkup(pages.get(page)).setMessageId(messageWithKeyboardId);
                    bot.execute(editMessageText);
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
                busId = Integer.parseInt(chose.substring(chose.indexOf(":")+1));
                busSelected = true;
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
        String message;
        String callBackPrefix;
        page = 0;
        if (busSelected){
            deleteMessages(bot);
            callBackPrefix = "driver_id";
            message = "Выберите водителя";
            list = factory.getDriverDao().getAllDriversWithoutBus();
        } else {
            callBackPrefix = "busId";
            message = messageDao.getMessage(33).getSendMessage().getText();
            list = busesDao.getAllBusesWithoutDrivers();
        }
        //noinspection unchecked
        if (list==null){
            if (busSelected) {
                sendMessageByIdWithKeyboard(bot,45,3);
            }
            else {
                sendMessageByIdWithKeyboard(bot,44,3);
            }
            return;
        }
        //noinspection unchecked
        pages = CustomKeyboardUtil.getPagesWithEntitiesAsButtonText(list,callBackPrefix);
        SendMessage sendMessage = new SendMessage(chatId,message).setReplyMarkup(pages.get(page));
        messageWithKeyboardId = bot.execute(sendMessage).getMessageId();
        messagesIdsToDelete.add(messageWithKeyboardId);
    }

    private void showPage(Bot bot) throws TelegramApiException {
        if (busSelected){
            bot.execute(new EditMessageText().setText("Выберите водителя").setChatId(chatId).setMessageId(messageWithKeyboardId)
                    .setReplyMarkup(pages.get(page)));
            return;
        }
        bot.execute(new EditMessageReplyMarkup().setChatId(chatId).setMessageId(messageWithKeyboardId)
                .setReplyMarkup(pages.get(page)));
    }

}
