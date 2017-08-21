package com.bartosso.bot.command.impl.ParentsMenu.PersonalAreaMenu;

import com.bartosso.bot.Bot;
import com.bartosso.bot.command.Command;
import com.bartosso.bot.dao.impl.BusesDao;
import com.bartosso.bot.dao.impl.ParentDao;
import com.bartosso.bot.entity.ProjectEntities.Parent;
import org.telegram.telegrambots.api.methods.send.SendLocation;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

public class WhereIsBusCommand extends Command {
    private ParentDao parentDao = factory.getParentDao();
    private BusesDao  busesDao  = factory.getBusesDao();

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (update.hasMessage()) {
            chatId = update.getMessage().getChatId();
            if (parentDao.isRegistred(chatId)) {
                Parent thisParent = parentDao.getParentByChatId(chatId);
                String busCords = busesDao.getBusCordsByChildId(thisParent.getChildId());
                if (busCords == null) {
                    sendMessageByIdWithKeyboard(bot,69,15);
                    return false;
                }
                try {
                SendLocation sendLocation = new SendLocation();
                sendLocation.setLatitude(Float.valueOf(busCords.substring(1,busCords.indexOf("L"))));
                sendLocation.setLongitude(Float.valueOf(busCords.substring(busCords.indexOf("L")+1)));
                sendLocation.setReplyMarkup(keyboardMarkUpDao.select(15));
                messagesIdsToDelete.add(bot.execute(sendLocation.setChatId(chatId)).getMessageId());
                return false;} catch (StringIndexOutOfBoundsException e){
                    sendMessageByIdWithKeyboard(bot,69,15);
                }
            }
            sendMessageByIdWithKeyboard(bot,68,15);
            return false;
        }
        if (update.hasCallbackQuery()) {
            if (update.getCallbackQuery().getData().equals(buttonDao.getButtonText(10))) {
                deleteMessages(bot);
                return true;
            }
        }
        return false;
    }
}
