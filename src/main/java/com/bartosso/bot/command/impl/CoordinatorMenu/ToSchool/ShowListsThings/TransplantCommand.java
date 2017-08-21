package com.bartosso.bot.command.impl.CoordinatorMenu.ToSchool.ShowListsThings;

import com.bartosso.bot.Bot;
import com.bartosso.bot.Util.CustomKeyboardUtil;
import com.bartosso.bot.command.Command;
import com.bartosso.bot.dao.impl.BusesDao;
import com.bartosso.bot.entity.ProjectEntities.Bus;
import com.bartosso.bot.entity.ProjectEntities.Entity;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class TransplantCommand extends Command {
    private final long                            constructorChatId;
    private final List<Entity>                    kidList;
    private       boolean                         sendsKids;
    private       boolean                         sendsBuses;
    private       ArrayList<InlineKeyboardMarkup> pages;
    private       int                             page                = 0;
    private       int                             messageWithKeyboard;
    protected        long                            kidId;
    protected        BusesDao                        busesDao             = factory.getBusesDao();
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (!sendsKids) {
            sendsKids = true;
            chatId    = constructorChatId;
            sendKids(bot);
            return false;
        }
        if (update.hasCallbackQuery()) {
            String chose = update.getCallbackQuery().getData();
            if (chose.equals("back")) {
                if (sendsBuses){
                    sendsBuses = false;
                    sendKids(bot);
                    return false;
                } else {
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
            if (chose.contains(":")) {
                if (chose.contains("kid")) {
                kidId = Long.parseLong(chose.substring(chose.indexOf(":")+1));
                sendBuses(bot);
                return false;
                }
                if (chose.contains("bus")) {
                    changeBus(Long.parseLong(chose.substring(chose.indexOf(":")+1)));
                    deleteMessages(bot);
                    messageWithKeyboard = 0;
                    sendMessageByIdWithKeyboard(bot,20,3);
                    return false;
                }
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

    // Here i don't make check for kidList because it's can't be null at this stage
    private void sendKids(Bot bot) throws TelegramApiException {
        pages       = CustomKeyboardUtil.getPagesWithEntitiesAsButtonText(kidList,"kid");
        String text = "Выберите ученика";
        SendMessage sendMessage = new SendMessage(chatId,text).setReplyMarkup(pages.get(page));
        messageWithKeyboard     = bot.execute(sendMessage).getMessageId();
        messagesIdsToDelete.add(messageWithKeyboard);
    }

    private void sendBuses(Bot bot) throws SQLException, TelegramApiException {
        @SuppressWarnings("unchecked")
        List<Entity> buses = getBuses();
        if (buses.isEmpty()) {
            sendNothingFound(bot);
            return;
        }
        page  = 0;
        pages = CustomKeyboardUtil.getPagesWithEntitiesAsButtonText(buses,"bus");
        bot.execute(new EditMessageReplyMarkup().setChatId(chatId)
                .setMessageId(messageWithKeyboard).setReplyMarkup(pages.get(page)));
    }

    private void showPage(Bot bot) throws TelegramApiException {
        bot.execute(new EditMessageReplyMarkup().setChatId(chatId)
                .setMessageId(messageWithKeyboard).setReplyMarkup(pages.get(page)));
    }

    protected  List<Entity> getBuses(){
        //noinspection unchecked
        return factory.getBusesDao().getAll();
    }


    protected void changeBus(long newBusId) throws SQLException {
        Bus bus = busesDao.getBusByChildInsideInMorning(kidId);
        ArrayList<Long> oldBusKids = bus.getTo_school_kids();
        oldBusKids.removeIf(aLong -> aLong==kidId);
        busesDao.updateMorningRoute(oldBusKids.toArray(new Long[oldBusKids.size()]),bus.getId());
        bus     = busesDao.getBusById(newBusId);
        ArrayList<Long> newBusKids;
        if (bus.getTo_school_kids() != null) {
            newBusKids = bus.getTo_school_kids();
            newBusKids.add(kidId);
        } else {
            newBusKids = new ArrayList<>();
            newBusKids.add(kidId);
        }
        busesDao.updateMorningRoute(newBusKids.toArray(new Long[newBusKids.size()]),bus.getId());
    }
}
