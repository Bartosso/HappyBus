package com.bartosso.bot.command.impl.CoordinatorMenu.ToHome.PutInBusThings;

import com.bartosso.bot.Bot;
import com.bartosso.bot.Util.CustomKeyboardUtil;
import com.bartosso.bot.command.impl.CoordinatorMenu.ToSchool.ShowListsThings.*;
import com.bartosso.bot.entity.ProjectEntities.Entity;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

class SelectKidForPutInBus extends SelectKidCommand {
    private ShowListsOfSchoolsForEveningCommand showListsOfSchoolsForEveningCommand;
    private List<Entity>     busList;
    private long             kidId;
    private boolean          busSends;
    SelectKidForPutInBus(long busId, long constructorChatId,ShowListsOfSchoolsForEveningCommand showListsOfSchoolsForEveningCommand) {
        super(busId, constructorChatId);
        this.showListsOfSchoolsForEveningCommand = showListsOfSchoolsForEveningCommand;
    }

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        //noinspection Duplicates
        if (update.hasMessage()) {
            if (update.getMessage().hasText()) {
                if (update.getMessage().getText().equals(buttonDao.getButtonText(10))) {
                    deleteMessages(bot);

                    return true;
                }
            }
        }
        if (update.hasCallbackQuery()) {
            if (update.getCallbackQuery().getData().equals(buttonDao.getButtonText(10))) {
                return true;
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
                if (busSends){
                    busSends = false;
                deleteMessages(bot);
                showKidList(bot);
                return false;} else {
                    deleteMessages(bot);
                    return true;
                }
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
            if (chose.contains("kid")){
                kidId  = Long.parseLong(chose.substring(chose.indexOf(":")+1));
                showBusesToShow(bot);
                busSends = true;
                return false;
            }
            if (chose.contains("bus")) {
                long busIdToPutIn = Long.parseLong(chose.substring(chose.indexOf(":")+1));
                addKidToBusAndMakeNewList(busIdToPutIn,bot);
                return false;
            }
        }
        return false;
    }

    private void addKidToBusAndMakeNewList(long busIdToPutIn,Bot bot) throws SQLException, TelegramApiException {
        //noinspection ConstantConditions
        Entity bus = busList.stream().filter(entity -> entity.getId()==busIdToPutIn).findFirst().get();
        factory.getBusesDao().addNewKidToEveningBusWithLastCoordinatorIds(kidId,chatId,bus.getId());
        busList.removeIf(entity -> entity.getId()==busIdToPutIn);
        busList.add(0,bus);
        showListsOfSchoolsForEveningCommand.setBusListForKidSelect(busList);
        deleteMessages(bot);
        sendMessageByIdWithKeyboard(bot,20, 15);
    }

    @Override
    protected List getKidList() {
        return factory.getKidsDao().getAllKidsWhoNotInThatEveningBus(busId);
    }

    private void showBusesToShow(Bot bot) throws SQLException, TelegramApiException {
        deleteMessages(bot);
        if (showListsOfSchoolsForEveningCommand.getBusListForKidSelect()!=null){
            busList             = showListsOfSchoolsForEveningCommand.getBusListForKidSelect();
            pages               = CustomKeyboardUtil
                    .getPagesWithEntitiesAsButtonText(busList,"bus");
            String message      = messageDao.getMessage(58).getSendMessage().getText();
            messageWithKeyboard = bot.execute(new SendMessage().setReplyMarkup(pages.get(page))
                    .setChatId(chatId).setText(message)).getMessageId();
            messagesIdsToDelete.add(messageWithKeyboard);
        } else {
            //noinspection unchecked
             busList = factory.getBusesDao().getAllBusesWhoCanDoEveningRoute();
            if (busList == null) {
                sendNothingFound(bot);
                return;
            }
            pages               = CustomKeyboardUtil
                    .getPagesWithEntitiesAsButtonText(busList,"bus");
            String message      = messageDao.getMessage(58).getSendMessage().getText();
            messageWithKeyboard = bot.execute(new SendMessage().setReplyMarkup(pages.get(page))
                    .setChatId(chatId).setText(message)).getMessageId();
            messagesIdsToDelete.add(messageWithKeyboard);
        }
    }

    @Override
    protected ArrayList<InlineKeyboardMarkup> getKeyboard(List kidList) {
        //noinspection unchecked
        return CustomKeyboardUtil.getPagesWithEntitiesAsButtonText(kidList,"kid");
    }
}
