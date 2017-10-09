package com.bartosso.bot.command.impl.ParentsMenu.PersonalAreaMenu;

import com.bartosso.bot.Bot;
import com.bartosso.bot.Util.CustomKeyboardUtil;
import com.bartosso.bot.command.Command;
import com.bartosso.bot.dao.impl.BusesDao;
import com.bartosso.bot.dao.impl.ParentDao;
import com.bartosso.bot.entity.ProjectEntities.*;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class SelectDayToSkipCommand extends Command {
    private int       callbackMenuMessageId;
    private boolean   selectingMonth;
    private ParentDao parentDao              = factory.getParentDao();
    private Parent    thisParent;
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (update.hasMessage()){
            chatId = update.getMessage().getChatId();
            if (!parentDao.isRegistred(chatId)) {
                sendMessageByIdWithKeyboard(bot,68,15);
                return false;
            }
            thisParent = parentDao.getParentByChatId(chatId);
            if (update.getMessage().hasText()){
                String text = update.getMessage().getText();
                if (text.equals(buttonDao.getButtonText(10))){
                deleteMessages(bot);
                return true;
                }
                if (text.equals(buttonDao.getButtonText(12))) {
                    callbackMenuMessageId = bot.execute(new SendMessage(chatId, "Выберите день")
                            .setReplyMarkup(CustomKeyboardUtil.getKeyboardWith9DaysWithCalendarButton())).getMessageId();
                    messagesIdsToDelete.add(callbackMenuMessageId);
                    return false;
                }
            }
            sendErrorMessageForFormat(bot);
        }
        if (update.hasCallbackQuery()) {
            String text = update.getCallbackQuery().getData();
            if (text.equals(buttonDao.getButtonText(10))) {
                deleteMessages(bot);
                return true;
            }
            if (selectingMonth){
                if (text.contains("-")){
                    bot.execute(new EditMessageText().setText("Выберите день").setChatId(chatId).setMessageId(callbackMenuMessageId)
                            .setReplyMarkup(CustomKeyboardUtil.getMonthViaButtons(text)));
                    selectingMonth = false;
                    return false;
                }
                if (text.equals("backIn9Days")){
                    bot.execute(new EditMessageText().setText("Выберите день").setChatId(chatId).setMessageId(callbackMenuMessageId)
                            .setReplyMarkup(CustomKeyboardUtil.getKeyboardWith9DaysWithCalendarButton()));
                    selectingMonth = false;
                    return false;
                }
            }
            if (text.equals("getCalendar")){
                bot.execute(new EditMessageText().setText("Выберите месяц").setChatId(chatId).setMessageId(callbackMenuMessageId)
                .setReplyMarkup(CustomKeyboardUtil.getKeyboardWith9Month()));
                selectingMonth = true;
                return false;
            }
            if (text.equals("backOff")){
                deleteMessages(bot);
                bot.execute(new DeleteMessage(chatId, callbackMenuMessageId));
                return true;
            }
            if (text.contains("-")){
                deleteMessages(bot);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                LocalDate selectedDate = LocalDate.parse(update.getCallbackQuery().getData(),formatter);
                setNewDayToSkip(selectedDate,bot);
                sendMessageByIdWithKeyboard(bot,20,3);
                return false;
            }



        }
        return false;
    }
    private void setNewDayToSkip(LocalDate dateToSkip,Bot bot) throws SQLException {
        BusesDao busesDao       = factory.getBusesDao();
        Bus thisBus             = busesDao.getBusByChildInsideInMorning(thisParent.getChildId());
        Kid thisKid             = factory.getKidsDao().getKidById(thisParent.getChildId());
        ArrayList<Long> kidList = thisBus.getTo_school_kids();
        List<Parent> allParents = factory.getParentDao().getParentsByChildId(thisParent.getChildId());
        int placeInTheBus       = kidList.indexOf(thisParent.getChildId());
        factory.getSickLeaveDao().insertNewSickLeave(new SickLeave(thisParent.getChildId(),dateToSkip,placeInTheBus,thisBus.getId()));
        String textToManagerUndDriver = "Ошибка, сообщите если видите это сообщение";
        if (dateToSkip.getDayOfYear()==LocalDate.now().getDayOfYear()){
            kidList.removeIf(aLong -> aLong==thisParent.getChildId());
            busesDao.updateMorningRoute(kidList.toArray(new Long[kidList.size()]),thisBus.getId());
            textToManagerUndDriver = messageDao.getMessage(78)
                    .getSendMessage().getText() + " сегодня\n" + thisKid.toString() +"\nРодители:"+ allParents.stream()
                    .map(Parent::toString).collect(Collectors.joining());
        }
        if (dateToSkip.isAfter(LocalDate.now())) {
            textToManagerUndDriver = messageDao.getMessage(78)
                    .getSendMessage().getText() + " "+ dateToSkip.toString() +"\n" + thisKid.toString() +"\nРодители:"+ allParents.stream()
                    .map(Parent::toString).collect(Collectors.joining());
        }
        SendMessage sendMessage = new SendMessage().setText(textToManagerUndDriver);
        //noinspection unchecked
        List<Coordinator> coordinators = factory.getCoordinatorDao().getAllCoordinators();
        try {
            bot.execute(sendMessage.setChatId(thisBus.getDriver_id()));
            bot.execute(sendMessage.setChatId(factory.getManagerDao().getManagerChatId()));
            coordinators.forEach(coordinator -> {
                try {
                    bot.execute(sendMessage.setChatId(chatId));
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            });
        } catch (TelegramApiException ignored) {
        }
    }
}
