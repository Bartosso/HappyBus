package com.bartosso.bot.command.impl.CoordinatorMenu.ToHome.ReadyToGoBuses;

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
public class ShowReadyToGoBusesCommand extends Command {
    private List                            busList;
    private ArrayList<InlineKeyboardMarkup> busPages;
    private int                             page       = 0;
    private int                             messageWithKeyboardId;
    private boolean                         sendsList;
    private boolean                         runningCommand;
    private ShowKidsInEveningBusCommand     showKidsInEveningBusCommand;

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (runningCommand){
            if (showKidsInEveningBusCommand.execute(update, bot)){
                showKidsInEveningBusCommand = null;
                runningCommand    = false;
                showBusList(bot);
            }
            return false;
        }
        if (update.hasMessage()){
            chatId = update.getMessage().getChatId();
            if (update.getMessage().hasText()) {
                if (update.getMessage().getText().equals(buttonDao.getButtonText(10))) {
                    deleteMessages(bot);
                    return true;
                }
            }
        }
        if (!sendsList){
            sendsList = true;
            showBusList(bot);
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
            if (chose.contains("bus")){
                showKidsInEveningBusCommand = getSelectKidCommand(Long.parseLong(chose.substring(chose.indexOf(":")+1)),chatId);
                return selectedBus(update, bot);
            }
        }
        return false;
    }


    private List getBusList(){
        return factory.getBusesDao().getAllBusesWhoReadyToGoEveningUndGotThisCoordinatorId(chatId);
    }



    private void showBusList(Bot bot) throws SQLException, TelegramApiException {
        page = 0;
        deleteMessages(bot);
        busList = getBusList();
        if (busList==null){
            sendNothingFound(bot);
            return;
        }
        String message = messageDao.getMessage(60).getSendMessage().getText();
        //noinspection unchecked
        busPages = CustomKeyboardUtil.getPagesWithEntitiesAsButtonText(busList,"bus");
        SendMessage sendMessage = new SendMessage(chatId,message).setReplyMarkup(busPages.get(page));
        messageWithKeyboardId = bot.execute(sendMessage).getMessageId();
        messagesIdsToDelete.add(messageWithKeyboardId);
    }

    private void showPage(Bot bot) throws TelegramApiException {
        bot.execute(new EditMessageReplyMarkup().setChatId(chatId).setMessageId(messageWithKeyboardId)
                .setReplyMarkup(busPages.get(page)));
    }

    private boolean selectedBus(Update update, Bot bot) throws SQLException, TelegramApiException {
        deleteMessages(bot);
        if (showKidsInEveningBusCommand.execute(update, bot)){
            showBusList(bot);
            return false;
        } else {
            runningCommand = true;
            return false; }
    }

    private ShowKidsInEveningBusCommand getSelectKidCommand(long busId, long constructorChatId){
        return new ShowKidsInEveningBusCommand(busId,constructorChatId);
    }

}
