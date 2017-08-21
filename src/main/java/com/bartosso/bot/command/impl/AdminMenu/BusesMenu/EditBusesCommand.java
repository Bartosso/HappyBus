package com.bartosso.bot.command.impl.AdminMenu.BusesMenu;

import com.bartosso.bot.Bot;
import com.bartosso.bot.Util.CustomKeyboardUtil;
import com.bartosso.bot.command.Command;
import com.bartosso.bot.dao.impl.BusesDao;
import com.bartosso.bot.entity.ProjectEntities.Entity;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class EditBusesCommand extends Command {
    private  ArrayList<InlineKeyboardMarkup> pages;
    private  List<Entity>                    busList;
    private  int                             page = 0;
    private  int                             messageWithKeyboardId;
    private  int                             idToEdit;
    private  boolean                         editingBus;
    private  boolean                         awaitNewValue;
    private  String                          chose;
    private  BusesDao                        busesDao = factory.getBusesDao();
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (editingBus){
            if (awaitNewValue){
                if (update.hasMessage()){
                    if (update.getMessage().hasText()){
                        if (update.getMessage().getText().equals(buttonDao.getButtonText(10))){
                            sendEditButtons(bot);
                            awaitNewValue = false;
                            return false;
                        }
                        busesDao.updateBusThings(update.getMessage().getText(),chose,idToEdit);
                        updateBusListUndPages();
                        awaitNewValue = false;
                        sendNewEditButtons(bot);
                        return false;
                    }
                }
                sendErrorMessageForFormat(bot);
                return false;
            }
            if (update.hasCallbackQuery()){
                chose = update.getCallbackQuery().getData();
                if (chose.equals("backOff")) {
                    //noinspection unchecked
                    updateBusListUndPages();
                    showPage(bot);
                    editingBus = false;
                    return false;
                }
                sendMessageByIdWithKeyboard(bot,32,3);
                awaitNewValue = true;
                return false;
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
                showFirstPage(bot);
                return false;
            }
            sendErrorMessageForFormat(bot);
            return false;
        }
        if (update.hasCallbackQuery()){
            chatId = update.getCallbackQuery().getFrom().getId();
            String callbackData = update.getCallbackQuery().getData();
            if (callbackData.equals("back")) {
                deleteMessages(bot);
                return true;
            }
            if (callbackData.contains(":")){
                idToEdit   = Integer.parseInt(callbackData.substring(callbackData.indexOf(":")+1));
                sendEditButtons(bot);
                editingBus = true;
                return false;
            }
            if (callbackData.equals("nextPage")) {
                page++;
                showPage(bot);
                return false;
            }
            if (callbackData.equals("previousPage")) {
                page--;
                showPage(bot);
                return false;
            }

        }
        return false;
    }

    private void showPage(Bot bot) throws TelegramApiException {
        bot.execute(new EditMessageText().setChatId(chatId).setMessageId(messageWithKeyboardId)
                .setReplyMarkup(pages.get(page)).setText("Список автобусов"));
    }

    private void showFirstPage(Bot bot) throws SQLException, TelegramApiException {
        //noinspection unchecked
        busList = busesDao.getAll();
        if (busList==null){
            sendNothingFound(bot);
            return;
        }
        pages = CustomKeyboardUtil.getPagesWithEntitiesAsButtonText(busList,"edit");
        SendMessage sendMessage = messageDao.getMessage(27).getSendMessage().setChatId(chatId)
                .setReplyMarkup(pages.get(page));
        messageWithKeyboardId = bot.execute(sendMessage).getMessageId();
        messagesIdsToDelete.add(messageWithKeyboardId);
    }


    private void sendEditButtons(Bot bot) throws TelegramApiException {
        Entity bus = busList.stream().filter(buses -> buses.getId()==idToEdit).collect(toList()).get(0);
        EditMessageText editMessageText = new EditMessageText().setChatId(chatId).setText("Вы выбрали\n"+ bus.toString()+"\nВыберите что хотите изменить")
                .setMessageId(messageWithKeyboardId).setReplyMarkup(CustomKeyboardUtil.getKeyboardForEditBuses());
        try {
        bot.execute(editMessageText);}
        catch (TelegramApiException e){
            SendMessage sendMessage = new SendMessage().setChatId(chatId)
                    .setText("Вы выбрали\n"+ bus.toString()+"\nВыберите что хотите изменить")
                    .setReplyMarkup(CustomKeyboardUtil.getKeyboardForEditBuses());
            messageWithKeyboardId = bot.execute(sendMessage).getMessageId();
            deleteMessages(bot);
            messagesIdsToDelete.add(messageWithKeyboardId);
        }
    }

    private void sendNewEditButtons(Bot bot) throws TelegramApiException {
        deleteMessages(bot);
        Entity bus = busList.stream().filter(buses -> buses.getId()==idToEdit).collect(toList()).get(0);
        SendMessage sendMessage = new SendMessage().setChatId(chatId).setText("Вы выбрали\n"+ bus.toString()+"\nВыберите что хотите изменить")
                .setReplyMarkup(CustomKeyboardUtil.getKeyboardForEditBuses());
        messageWithKeyboardId = bot.execute(sendMessage).getMessageId();
        messagesIdsToDelete.add(messageWithKeyboardId);
    }

    @SuppressWarnings("unchecked")
    private void updateBusListUndPages(){
        busList = busesDao.getAll();
        pages   = CustomKeyboardUtil.getPagesWithEntitiesAsButtonText(busList,"edit");
    }
}
