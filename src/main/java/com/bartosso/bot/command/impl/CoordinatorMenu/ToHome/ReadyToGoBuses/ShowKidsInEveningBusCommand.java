package com.bartosso.bot.command.impl.CoordinatorMenu.ToHome.ReadyToGoBuses;

import com.bartosso.bot.Bot;
import com.bartosso.bot.Util.CustomKeyboardUtil;
import com.bartosso.bot.command.Command;
import com.bartosso.bot.command.impl.CoordinatorMenu.ToSchool.ShowListsThings.*;
import com.bartosso.bot.dao.impl.BusesDao;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
@SuppressWarnings("Duplicates")
@RequiredArgsConstructor
public class ShowKidsInEveningBusCommand extends Command {
    protected final long                       busId;
    protected final long                       constructorChatId;
    protected       int                        page = 0;
    private         List                       kidList;
    protected       List<InlineKeyboardMarkup> pages;
    private         boolean                    sendsList;
    private         Command                    command;
    private         boolean                    runningCommand;
    private         int                        messageWithKeyboard;
    private         BusesDao                   busesDao             = factory.getBusesDao();
    @SuppressWarnings("Duplicates")
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (runningCommand){
            if (command.execute(update, bot)){
                command           = null;
                runningCommand    = false;
                showKidList(bot);
            }
            return false;
        }
        if (update.hasMessage()) {
            if (update.getMessage().hasText()) {
                if (update.getMessage().getText().equals(buttonDao.getButtonText(10))) {
                    deleteMessages(bot);
                    return true;
                }
            }
        }
        if (!sendsList) {
            sendsList = true;
            chatId = constructorChatId;
            showKidList(bot);
            return false;
        }
        if (update.hasCallbackQuery()){
            String chose = update.getCallbackQuery().getData();
            if (chose.equals("back")) {
                deleteMessages(bot);
                return true;
            }
            if (chose.equals(buttonDao.getButtonText(10))) {
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
            if (chose.equals("reorder")){
                command = new ChangeEveningRouteOrderCommand(busId,chatId);
                return selectedSomething(update, bot);
            }
            if (chose.contains("kid")){
                command = new ShowKidCommand(Long.parseLong(chose.substring(chose.indexOf(":")+1)),chatId);
                return selectedSomething(update, bot);
            }
            if (chose.contains("remove")) {
                //noinspection unchecked
                command = new RemoveKidFromEveningBusCommand(busId,chatId);
                return selectedSomething(update, bot);
            }
            if (chose.equals("add")) {
                command = new AddNewKidToEveningBus(chatId,busId);
                return selectedSomething(update, bot);
            }
            if (chose.equals("transplant")) {
                //noinspection unchecked
                command = new TransplantFromEveningBusCommand(chatId,kidList);
                return selectedSomething(update, bot);
            }
            if (chose.equals("deleteList")) {
                busesDao.updateEveningRoute(new Long[0],busId);
                busesDao.removeLastCoordinatorId(busId);
                deleteMessages(bot);
                sendMessageByIdWithKeyboard(bot,20,3);
                return false;
            }
            if (chose.equals("giveToDriver")) {
                deleteMessages(bot);
                long driverId = busesDao.makeEveningRouteForBusAndGetDriverId(busId);
                bot.execute(new SendMessage(driverId,messageDao.getMessage(61).getSendMessage().getText()));
                sendMessageByIdWithKeyboard(bot,20,15);
                return false;
            }


        }
        return false;
    }

    private List getKidList(){
        return factory.getKidsDao().getAllKidsFromEveningBus(busId);
    }

    private void showKidList(Bot bot) throws SQLException, TelegramApiException {
        page = 0;
        deleteMessages(bot);
        kidList = getKidList();
        if (kidList==null){
            sendMessageByIdWithKeyboard(bot,59,3);
            return;
        }
        String message = messageDao.getMessage(42).getSendMessage().getText();
        //noinspection unchecked
        pages = getKeyboard(kidList);
        messageWithKeyboard = bot.execute(new SendMessage().setReplyMarkup(pages.get(page))
                .setChatId(chatId).setText(message)).getMessageId();
        messagesIdsToDelete.add(messageWithKeyboard);
    }

    private ArrayList<InlineKeyboardMarkup> getKeyboard(List kidList){
        //noinspection unchecked
        return CustomKeyboardUtil.getPagesForCoordinatorToHomeMenu(kidList);
    }

    protected void showPage(Bot bot) throws TelegramApiException {
        deleteMessages(bot);
        bot.execute(new EditMessageReplyMarkup().setMessageId(messageWithKeyboard).setChatId(chatId)
                .setReplyMarkup(pages.get(page)));
    }

    private boolean selectedSomething(Update update, Bot bot) throws SQLException, TelegramApiException {
        deleteMessages(bot);
        if (command.execute(update, bot)){
            showKidList(bot);
            return false;
        } else {
            runningCommand = true;
            return false; }
    }
}
