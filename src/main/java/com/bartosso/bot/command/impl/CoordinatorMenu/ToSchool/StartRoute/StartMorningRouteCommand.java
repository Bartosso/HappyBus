package com.bartosso.bot.command.impl.CoordinatorMenu.ToSchool.StartRoute;

import com.bartosso.bot.Bot;
import com.bartosso.bot.command.Command;
import com.bartosso.bot.dao.impl.BusesDao;
import com.bartosso.bot.entity.ProjectEntities.Bus;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.List;

public class StartMorningRouteCommand extends Command {
    private BusesDao busesDao = factory.getBusesDao();
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        //noinspection Duplicates
        if (update.hasMessage()) {
            chatId = update.getMessage().getChatId();
            if (update.getMessage().hasText()) {
                if (update.getMessage().getText().equals(buttonDao.getButtonText(10))) {
                    deleteMessages(bot);
                    return true;
                }
               sendMessageByIdWithKeyboard(bot,55,19);
               return false;
            }
        }
        if (update.hasCallbackQuery()) {
            String chose = update.getCallbackQuery().getData();
            if (chose.equals(buttonDao.getButtonText(51))) {
                deleteMessages(bot);
                return true;
            }
            if (chose.equals(buttonDao.getButtonText(50))) {
                startMorningRoute(bot);
                return false;
            }
        }
        return false;
    }

    private void startMorningRoute(Bot bot) throws SQLException, TelegramApiException {
        List<Bus> busList = busesDao.getUndSetAllBusesWhoReadyForMorningRoute();
        if (busList == null) {
            sendMessageByIdWithKeyboard(bot,56,3);
            return;
        }
        SendMessage sendMessage = messageDao.getMessage(57).getSendMessage();
        busList.forEach(bus -> {
            try {
                bot.execute(sendMessage.setChatId(bus.getDriver_id())).getMessageId();
            } catch (TelegramApiException e) {
                e.printStackTrace();
            } });
        sendMessageByIdWithKeyboard(bot,20,3);
    }


}
