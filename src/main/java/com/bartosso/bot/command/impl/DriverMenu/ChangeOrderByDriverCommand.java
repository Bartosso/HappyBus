package com.bartosso.bot.command.impl.DriverMenu;

import com.bartosso.bot.Bot;
import com.bartosso.bot.Util.CustomKeyboardUtil;
import com.bartosso.bot.command.Command;
import com.bartosso.bot.entity.ProjectEntities.Entity;
import com.bartosso.bot.entity.ProjectEntities.Kid;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings({"unchecked", "Duplicates"})
public class ChangeOrderByDriverCommand extends Command {

    ChangeOrderByDriverCommand(long busId, long chatId, RouteCommand driverToSchoolCommand, List<Kid> kidList){
        this.chatId  = chatId;
        this.busId   = busId;
        this.driverToSchoolCommand = driverToSchoolCommand;
        this.kidList = (List<Entity>)(List<?>) kidList;


    }

    protected final long busId;
    private   final RouteCommand driverToSchoolCommand;
    private   final List<Entity>          kidList;
    private         int                   page              = 0;
    private         long                  childId;

    private ArrayList<InlineKeyboardMarkup> pages;
    private int messageWithKeyboardId;
    private boolean listSends;
    private boolean waitingNumber;
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        //noinspection Duplicates
        if (update.hasMessage()) {
            if (update.getMessage().hasText()) {
                if (update.getMessage().getText().equals(buttonDao.getButtonText(10))) {
                    deleteMessages(bot);
                    return true;
                }
                if (waitingNumber){
                    waitingNumber = false;
                    int i = Integer.parseInt(update.getMessage().getText());
                    updateRoute(i);
                    showKidsViaButtons(bot);
                    return false;
                }
            }
        }
        if (!listSends) {
            listSends = true;
            showKidsViaButtons(bot);
            return false;
        }
        if (update.hasCallbackQuery()) {
            String chose = update.getCallbackQuery().getData();
            if (chose.equals("back")) {
                deleteMessages(bot);
                return true;
            }
            if (chose.equals(buttonDao.getButtonText(10))) {
                showKidsViaButtons(bot);
                return false;
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
            if (chose.contains(":")){
                deleteMessages(bot);
                waitingNumber = true;
                childId = Long.parseLong(chose.substring(chose.indexOf(":")+1));
                sendMessageByIdWithKeyboard(bot,51,15);
                return false;
            }
        }
        return false;
    }

    private void showKidsViaButtons(Bot bot) throws SQLException, TelegramApiException {
        page = 0;
        deleteMessages(bot);
        if (kidList==null){
            sendNothingFound(bot);
            return;
        }
        //noinspection unchecked
        pages = CustomKeyboardUtil.getPagesWithEntitiesAsButtonText(kidList,"kid");
        SendMessage sendMessage = new SendMessage(chatId,messageDao.getMessage(74).getSendMessage().getText()).setReplyMarkup(pages.get(page));
        messageWithKeyboardId = bot.execute(sendMessage).getMessageId();
        messagesIdsToDelete.add(messageWithKeyboardId);
    }

    private void showPage(Bot bot) throws TelegramApiException {
        bot.execute(new EditMessageReplyMarkup().setChatId(chatId).setMessageId(messageWithKeyboardId)
                .setReplyMarkup(pages.get(page)));
    }

    private void updateRoute(int i){
        //noinspection ConstantConditions
        Entity entity =  kidList.stream().filter(entity1 -> entity1.getId()==childId).findFirst().get();
        kidList.removeIf(entity1 -> entity1.getId()==childId);
        if (kidList.size()>=i){
            if (i<1){
                i = 1;
            }
            kidList.add((i-1),entity);
        }
        else{
            kidList.add(entity);
        }
        driverToSchoolCommand.setKids((List<Kid>)(List<?>) kidList);
    }


}
