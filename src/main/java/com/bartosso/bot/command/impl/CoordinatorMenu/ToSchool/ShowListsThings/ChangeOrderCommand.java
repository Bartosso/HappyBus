package com.bartosso.bot.command.impl.CoordinatorMenu.ToSchool.ShowListsThings;

import com.bartosso.bot.Bot;
import com.bartosso.bot.Util.CustomKeyboardUtil;
import com.bartosso.bot.command.Command;
import com.bartosso.bot.entity.ProjectEntities.Kid;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
public class ChangeOrderCommand extends Command {
    protected final long busId;
    private final long constructorChatId;
    private       int  page = 0;
    protected        long childId;
    protected List kidList;
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
            chatId = constructorChatId;
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
        kidList = getKidList();
        if (kidList==null){
            sendNothingFound(bot);
            return;
        }
        //noinspection unchecked
        pages = CustomKeyboardUtil.getPagesWithEntitiesAsButtonText(kidList,"kid");
        SendMessage sendMessage = new SendMessage(chatId,"Выберите ученика").setReplyMarkup(pages.get(page));
        messageWithKeyboardId = bot.execute(sendMessage).getMessageId();
        messagesIdsToDelete.add(messageWithKeyboardId);
    }

    protected List getKidList(){
        return factory.getKidsDao().getAllKidsFromMorningBus(busId);
    }

    private void showPage(Bot bot) throws TelegramApiException {
        bot.execute(new EditMessageReplyMarkup().setChatId(chatId).setMessageId(messageWithKeyboardId)
                .setReplyMarkup(pages.get(page)));
    }

    @SuppressWarnings("Duplicates")
    protected void updateRoute(int i){
        //noinspection unchecked
        List<Kid> kidsByBusList = kidList;
        kidsByBusList.removeIf(kid -> kid.getId()==childId);
        List<Long> kidIds = kidsByBusList.stream().map(Kid::getId).collect(toList());
        if (kidIds.size()>=i){
            if (i<1){
                i = 1;
            }
            kidIds.add((i-1),childId);
        }
        else{
            kidIds.add(childId);
        }
        factory.getBusesDao().updateMorningRoute(kidIds.toArray(new Long[kidIds.size()]),busId);
        //noinspection unchecked
        kidList = factory.getKidsDao().getAllKidsFromMorningBus(busId);
    }

}
