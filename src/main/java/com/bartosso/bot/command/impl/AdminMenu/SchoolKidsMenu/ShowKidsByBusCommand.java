package com.bartosso.bot.command.impl.AdminMenu.SchoolKidsMenu;

import com.bartosso.bot.Bot;
import com.bartosso.bot.Util.CustomKeyboardUtil;
import com.bartosso.bot.command.Command;
import com.bartosso.bot.dao.impl.BusesDao;
import com.bartosso.bot.dao.impl.KidsDao;
import com.bartosso.bot.dao.impl.SchoolDao;
import com.bartosso.bot.entity.ProjectEntities.Entity;
import com.bartosso.bot.entity.ProjectEntities.Kid;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@SuppressWarnings("Duplicates")
public class ShowKidsByBusCommand extends Command {
    private boolean                         waitingNumber;
    private boolean                         reorder;
    private boolean showingBusList;
    private int                             page =0;
    private ArrayList<InlineKeyboardMarkup> pages;
    private int                             messageWithKeyboardId;
    private long                            busId;
    private long                            kidId;
    private List<Kid>                       kidsByBusList;
    private BusesDao                        busesDao               = factory.getBusesDao();
    private KidsDao                         kidsDao                = factory.getKidsDao();
    private SchoolDao                       schoolDao              = factory.getSchoolDao();
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        //noinspection Duplicates
        if (update.hasMessage()) {
            chatId = update.getMessage().getChatId();
            if (update.getMessage().hasText()) {
                if (update.getMessage().getText().equals(buttonDao.getButtonText(10)))
                {
                    deleteMessages(bot);
                    return true;
                }
                if (waitingNumber){
                if (Character.isDigit(update.getMessage().getText().charAt(0))){
                    waitingNumber = false;
                    int i = Integer.parseInt(update.getMessage().getText());
                    updateRoute(i);
                    showKidsList(bot);
                    return false;
                } else {
                    sendErrorMessageForFormat(bot);
                    return false;
                }
                }
                showBusList(bot);
                return false;
            }
        }
        if (update.hasCallbackQuery()) {
            String chose = update.getCallbackQuery().getData();
            if (showingBusList){
                if (chose.equals("back")){
                    deleteMessages(bot);
                    return true;}
            }
                if (chose.equals("back")){
                    showBusList(bot);
                    return false;}

            if (chose.equals(buttonDao.getButtonText(10))){
                showKidsList(bot);
                return false;
            }
            if (chose.equals("nextPage")){
                page++;
                showPage(bot);
                return false;
            }
            if (chose.equals("previousPage")){
                page--;
                showPage(bot);
                return false;
            }
            if (chose.contains("buses")){
                showingBusList = false;
                busId          = Long.parseLong(chose.substring(chose.indexOf(":")+1));
                //noinspection unchecked
                kidsByBusList  = kidsDao.getAllKidsFromMorningBus(busId);
                showKidsList(bot);
                return false;
            }
            if (chose.contains("kid")){
                kidId = Long.parseLong(chose.substring(chose.indexOf(":")+1));
                if (reorder){
                    reorder = false;
                    deleteMessages(bot);
                    sendMessageByIdWithKeyboard(bot,51,15);
                    waitingNumber = true;
                    return false;
                }
                @SuppressWarnings("ConstantConditions")
                Kid kid = kidsByBusList.stream().filter(kidInBus -> kidInBus.getId()== kidId).findFirst().get();
                showKid(bot,kid);
                return false;
            }
            if (chose.equals("reorder")){
               if (reorder){
                   sendErrorMessageForFormat(bot);
                   return false;
               }
                bot.execute(new EditMessageText().setChatId(chatId).setMessageId(messageWithKeyboardId)
                .setText("Выберите ученика").setReplyMarkup(pages.get(page)));
                reorder = true;
                return false;
            }
        }
        sendErrorMessageForFormat(bot);
        return false;
    }


    private void showBusList(Bot bot) throws SQLException, TelegramApiException {
        page = 0;
        showingBusList = true;
        deleteMessages(bot);
        //noinspection unchecked
        List<Entity> list       = factory.getBusesDao().getAll();
        if (list==null){
            sendMessageByIdWithKeyboard(bot,44,3);
            return;
        }
        pages                   = CustomKeyboardUtil.getPagesWithEntitiesAsButtonText(list,"buses");
        SendMessage sendMessage = messageDao.getMessage(38).getSendMessage().setChatId(chatId)
                .setReplyMarkup(pages.get(page));
        messageWithKeyboardId   = bot.execute(sendMessage).getMessageId();
        messagesIdsToDelete.add(messageWithKeyboardId);
    }

    private void updateRoute(int i){
        kidsByBusList.removeIf(kid -> kid.getId()==kidId);
        List<Long> kidIds = kidsByBusList.stream().map(Kid::getId).collect(toList());
        if (kidIds.size()>=i){
            kidIds.add((i-1),kidId);
        }
        else{
            kidIds.add(kidId);
        }
        busesDao.updateMorningRoute(kidIds.toArray(new Long[kidIds.size()]),busId);
        //noinspection unchecked
        kidsByBusList = kidsDao.getAllKidsFromMorningBus(busId);
    }

    private void showKidsList(Bot bot) throws SQLException, TelegramApiException {
        page = 0;
        deleteMessages(bot);
        //noinspection unchecked
        if (kidsByBusList==null){
            sendMessageByIdWithKeyboard(bot,47,3);
            return;
        }
        //noinspection unchecked
        pages                   = CustomKeyboardUtil.getPagesForReorderKidsInBusInAdminMenu(kidsByBusList);
        SendMessage sendMessage = messageDao.getMessage(42).getSendMessage().setChatId(chatId)
                .setReplyMarkup(pages.get(page));
        messageWithKeyboardId   = bot.execute(sendMessage).getMessageId();
        messagesIdsToDelete.add(messageWithKeyboardId);
    }

    private void showPage(Bot bot) throws TelegramApiException {
        bot.execute(new EditMessageReplyMarkup().setChatId(chatId).setMessageId(messageWithKeyboardId)
                .setReplyMarkup(pages.get(page)));
    }

    private void showKid(Bot bot, Kid kid) throws TelegramApiException, SQLException {
       StringBuilder sb = new StringBuilder();
       sb.append("Ученик\n").append(kid.toString()).append("\nШкола\n")
               .append(schoolDao.getSchoolById(Long.parseLong(kid.getSchool_id())).getName())
               .append("\nРодители\n");
       parentDao.getParentsByChildId(kid.getId()).forEach(parent -> sb.append(parent.toString()));
       deleteMessages(bot);
       if (kid.getPhoto()!=null){
           messagesIdsToDelete.add(bot.sendPhoto(new SendPhoto().setChatId(chatId).setPhoto(kid.getPhoto())).getMessageId());
       }
        messageWithKeyboardId = bot.execute(new SendMessage(chatId,sb.toString())
                .setReplyMarkup(keyboardMarkUpDao.select(15)
        )).getMessageId();
       messagesIdsToDelete.add(messageWithKeyboardId);
    }
}
