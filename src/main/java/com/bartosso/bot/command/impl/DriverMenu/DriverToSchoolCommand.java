package com.bartosso.bot.command.impl.DriverMenu;

import com.bartosso.bot.Bot;
import com.bartosso.bot.Util.CustomKeyboardUtil;
import com.bartosso.bot.dao.impl.BusesDao;
import com.bartosso.bot.entity.ProjectEntities.*;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.List;

import static java.util.stream.Collectors.toList;

@SuppressWarnings("Duplicates")
public class DriverToSchoolCommand extends RouteCommand {
    private   Bus                 thisBus;
    private   boolean             driverChecked;
    private   Driver              thisDriver;
    private   Kid                 actualKid;
    private   List<Parent>        parents;
    private   BusesDao            busesDao        = factory.getBusesDao();
    private   ReplyKeyboardMarkup keyboard        = CustomKeyboardUtil.getKeyboardForMorningRoute();
    private   boolean             editingKidsList;
    private   ChangeOrderByDriverCommand             changeOrderCommand;
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (editingKidsList) {
            if (changeOrderCommand.execute(update, bot)){
                showKidWithButtons(bot);
                editingKidsList    = false;
                changeOrderCommand = null;
            }
            return false;
        }
        if (update.hasMessage()) {
            if (!driverChecked){
            chatId  = update.getMessage().getChatId();
            thisBus = busesDao.getBusByDriverId(chatId);
            if (thisBus!=null){
                if (thisBus.getTo_school_kids() != null) {
                    if (thisBus.isReady_to_school()){
                    if (thisBus.getTo_school_kids().size() > 0) {
                        thisDriver = factory.getDriverDao().getDriverByChatId(chatId);
                        driverChecked = true;
                        sendMessageByIdWithKeyboard(bot,65,20);
                        return false;

                    }
                    }
                }
                sendMessageByIdWithKeyboard(bot,66,15);
                return false;
            }
                sendMessageByIdWithKeyboard(bot,67,15);
                return false;
            }
            if (update.getMessage().hasLocation()) {
                String alt = "A" + update.getMessage().getLocation().getLatitude();
                String lon = "L" + update.getMessage().getLocation().getLongitude();
                busesDao.updateLastGpsCords(alt + lon,thisBus.getId());
                kids.remove(0);

                String messageText = " Ваш ребенок сел в утренний автобус. Мы направляемся в школу\n" + thisBus.toString()
                        +"\nВодитель: " + thisDriver.toString();
                sendMessageToParents(bot,messageText);
                if (kids.isEmpty()) {
                    buttonKidsInSchool(bot);
                    return false;
                }
                actualKid = kids.get(0);
                deleteMessages(bot);
                showKidWithButtons(bot);
                return false;
            }
            if (update.getMessage().hasText()) {
                if (update.getMessage().getText().equals("Не сел")) {
                    String text = "Ваш ребенок не был на месте посадки" +
                            " в установленное время. В связи с графиком мы были вынуждены продолжить маршрут." +
                            " Свяжитесь пожалуйста со своим ребенком.";
                    sendMessageToParents(bot,text);
                    parents.removeIf(parent -> parent.getChildId()==actualKid.getId());
                    kids.remove(0);
                    if (kids.isEmpty()) {
                        buttonKidsInSchool(bot);
                        return false;
                    }
                    actualKid = kids.get(0);
                    deleteMessages(bot);
                    showKidWithButtons(bot);
                    return false;
                }
                if (update.getMessage().getText().equals("Посмотреть список")) {
                    deleteMessages(bot);
                    changeOrderCommand = new ChangeOrderByDriverCommand(thisBus.getId(),chatId,
                            this,kids);
                    actualKid = null;
                    if (changeOrderCommand.execute(update, bot)){
                        showKidWithButtons(bot);
                        return false;
                    } else {
                        editingKidsList = true;
                        return false;
                    }
                }


            }







        }
        if (update.hasCallbackQuery()) {
            String chose = update.getCallbackQuery().getData();
            if (chose.equals(buttonDao.getButtonText(10))) {
                deleteMessages(bot);
                return true;
            }
            if (chose.equals(buttonDao.getButtonText(45))) {
                deleteMessages(bot);
                //noinspection unchecked
                kids    = factory.getKidsDao().getAllKidsFromMorningBus(thisBus.getId());
                parents = factory.getParentDao().getAllParentsFromMorningBus(thisBus.getId());
                showKidWithButtons(bot);
                return false;
            }
            if (chose.equals(buttonDao.getButtonText(53))) {
                deleteMessages(bot);
                busesDao.setMorningBusRouteEnd(thisBus.getId());
                busesDao.setBusCordsNull(thisBus.getId());
                sendMessageToParentsAboutKidInSchool(bot);
                sendMessageByIdWithKeyboard(bot, 71,15);
                return false;
            }
        }


        return false;
    }

    private void sendMessageToParentsAboutKidInSchool(Bot bot) throws SQLException {
        SendMessage sendMessage = messageDao.getMessage(72).getSendMessage();
        parents.forEach(parent -> {
            try {
                bot.execute(sendMessage.setChatId(parent.getId()));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        });
    }

    private void buttonKidsInSchool(Bot bot) throws SQLException, TelegramApiException {
        deleteMessages(bot);
        sendMessageByIdWithKeyboard(bot,70,21);
    }

    private void showKidWithButtons(Bot bot) throws TelegramApiException {
        if (actualKid == null) {
            actualKid = kids.get(0);
        }
            String text = actualKid.toString();
            if (actualKid.getPhoto()!=null){
                SendPhoto sendPhoto = new SendPhoto().setChatId(chatId).setPhoto(actualKid.getPhoto());
                messagesIdsToDelete.add(bot.sendPhoto(sendPhoto).getMessageId());
            }
            messagesIdsToDelete.add(bot.execute(new SendMessage(chatId,text).setReplyMarkup(keyboard)).getMessageId());

    }

    private void sendMessageToParents(Bot bot,String text){
        List<Parent> actualParents = parents.stream().filter(parent -> parent.getChildId()==actualKid.getId())
                .collect(toList());
        if (!actualParents.isEmpty()) {
            actualParents.forEach(parent -> {
                try {
                    bot.execute(new SendMessage(parent.getId(), text));
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            });
        }

    }



}
