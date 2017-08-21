package com.bartosso.bot.command.impl.DriverMenu;

import com.bartosso.bot.Bot;
import com.bartosso.bot.Util.CustomKeyboardUtil;
import com.bartosso.bot.dao.impl.BusesDao;
import com.bartosso.bot.entity.ProjectEntities.Bus;
import com.bartosso.bot.entity.ProjectEntities.Driver;
import com.bartosso.bot.entity.ProjectEntities.Kid;
import com.bartosso.bot.entity.ProjectEntities.Parent;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.List;

import static java.util.stream.Collectors.toList;

@SuppressWarnings("Duplicates")
public class DriverToHomeCommand extends RouteCommand {
    private   Bus                        thisBus;
    private   boolean                    driverChecked;
    private   Driver                     thisDriver;
    private   Kid                        actualKid;
    private   List<Parent>               parents;
    private   BusesDao                   busesDao           = factory.getBusesDao();
    private   ReplyKeyboardMarkup        keyboard           = CustomKeyboardUtil.getKeyboardForEveningRoute();
    private   boolean                    editingKidsList;
    private   ChangeOrderByDriverCommand changeOrderCommand;
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
                    if (thisBus.getTo_home_kids() != null) {
                        if (thisBus.isReady_to_home()){
                            if (thisBus.getTo_home_kids().size() > 0) {
                                thisDriver = factory.getDriverDao().getDriverByChatId(chatId);
                                driverChecked = true;
                                sendMessageByIdWithKeyboard(bot,75,22);
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
                if (kids.isEmpty()) {
                    routeIsOver(bot);
                    return false;
                }
                actualKid = kids.get(0);
                String messageText = " Ваш ребенок вышел из автомашины:\n"+ thisBus.toString() + "\n Водитель:\n" + thisDriver.toString();
                sendMessageToParents(bot,messageText);
                deleteMessages(bot);
                showKidWithButtons(bot);
                return false;
            }
            if (update.getMessage().hasText()) {
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
            if (chose.equals(buttonDao.getButtonText(54))) {
                deleteMessages(bot);
                //noinspection unchecked
                kids    = factory.getKidsDao().getAllKidsFromEveningBus(thisBus.getId());
                parents = factory.getParentDao().getAllParentsFromEveningBus(thisBus.getId());
                sendMessageToParentsAboutChildInTheBus(bot);
                showKidWithButtons(bot);
                return false;
            }
        }
        return false;
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

    private void routeIsOver(Bot bot){
     long adminChatId   = factory.getAdminDao().getAdminId();
     long coordinatorId = thisBus.getLast_coordinator_id();
     String text = "Развозка окончена\nАвтобус:\n" + thisBus.toString() +"\nВодитель:\n" + thisDriver.toString();
     SendMessage sendMessage = new SendMessage().setText(text);
        busesDao.setEveningBusRouteEnd(thisBus.getId());
        try {
            bot.execute(sendMessage.setChatId(adminChatId));
            bot.execute(sendMessage.setChatId(coordinatorId));
            messagesIdsToDelete.add(bot.execute(new SendMessage(chatId,"Развозка окончена").setReplyMarkup(keyboardMarkUpDao.select(15))).getMessageId());
        } catch (TelegramApiException | SQLException e) {
            e.printStackTrace();
        }

    }

    private void sendMessageToParentsAboutChildInTheBus(Bot bot){
        String text = "Ваш ребенок сел в автомашину:\n" + thisBus.toString() +"\nВодитель\n" + thisDriver.toString();
        SendMessage sendMessage = new SendMessage().setText(text);
        parents.forEach(parent -> {
            try {
                bot.execute(sendMessage.setChatId(parent.getId()));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        });
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
