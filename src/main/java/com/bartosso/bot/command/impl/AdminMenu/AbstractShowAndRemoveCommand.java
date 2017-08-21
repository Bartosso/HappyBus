package com.bartosso.bot.command.impl.AdminMenu;


import com.bartosso.bot.Bot;
import com.bartosso.bot.Util.CustomKeyboardUtil;
import com.bartosso.bot.command.Command;
import com.bartosso.bot.entity.ProjectEntities.Entity;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

import java.util.List;


import static java.util.stream.Collectors.toList;


public abstract class AbstractShowAndRemoveCommand extends Command {
    private List<InlineKeyboardMarkup> pages;
    private int                        page = 0;
    private int                        idToDelete;
    private int                        messageIdWithPages;
    private boolean                    waitingAccept;
    private String                     chosenOne;
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (waitingAccept){
            if (update.hasCallbackQuery()){
                String callbackData = update.getCallbackQuery().getData();
                if (callbackData.equals("delete")){
                    deleteEntityFromDb(idToDelete);
                    sendMessagesOnDelete(bot);
                    showSuccess(bot);
                    waitingAccept = false;
                }
                if (callbackData.equals("no, back")){
                    showPage(bot);
                    waitingAccept = false;
                }
                return false;
            } else {sendErrorMessageForFormat(bot);
            return false;
            }

        }
        if (update.hasMessage()) {
            chatId = update.getMessage().getChatId();
            if (update.getMessage().hasText()) {
                if (update.getMessage().getText().equals(buttonDao.getButtonText(10))) {
                    deleteMessages(bot);
                    return true;
                } else {
                   showFirstPage(bot);
                    return false;
                }
            }
            sendErrorMessageForFormat(bot);
        }
        if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            if (callbackData.equals("back")) {
                deleteMessages(bot);
                return true;
            }
            if (callbackData.contains(":")){
                idToDelete       = Integer.parseInt(callbackData.substring(callbackData.indexOf(":")+1));
                waitingAccept    = true;
                sendConfirmation(bot,callbackData);
                return false;
            }
            if (callbackData.equals("nextPage")) {
                page++;
                showPage(bot);
                return false;
            }
            if (callbackData.equals("previousPage")) {
                page--;
                showPage(bot);
                return false;
            }
            showFirstPage(bot);
            return false;
//            sendErrorMessageForFormat(bot);
        }
        return false;
    }

    protected abstract List<Entity> getEntitiesToShow();

    protected abstract void deleteEntityFromDb(int idToDelete);

    protected abstract void sendMessagesOnDelete(Bot bot);

    private void sendConfirmation(Bot bot, String callBackData) throws TelegramApiException {
        //noinspection ConstantConditions
        chosenOne = pages.get(page).getKeyboard().stream()
                .map(inlineKeyboardButtons -> inlineKeyboardButtons.stream().findAny().get()).collect(toList())
                .stream().filter(inlineKeyboardButton -> inlineKeyboardButton.getCallbackData().equals(callBackData))
                .map(InlineKeyboardButton::getText).collect(toList()).get(0);
        EditMessageText editMessageText = new EditMessageText().setChatId(chatId).setText("Вы уверены что хотите удалить " + chosenOne + " ?" )
                .setMessageId(messageIdWithPages).setReplyMarkup(CustomKeyboardUtil.getKeyboardForAgreeDelete());
        bot.execute(editMessageText);
    }
    private void showPage(Bot bot) throws TelegramApiException {
        bot.execute(new EditMessageReplyMarkup().setChatId(chatId).setMessageId(messageIdWithPages)
                .setReplyMarkup(pages.get(page)));
    }

    private void showFirstPage(Bot bot) throws SQLException, TelegramApiException {
        List<Entity> entities = getEntitiesToShow();
        if (entities==null){
            sendNothingFound(bot);
            return;
        }
        pages = CustomKeyboardUtil.getPagesWithEntitiesAsButtonText(entities, "remove");
        SendMessage sendMessage = new SendMessage(chatId, messageDao.getMessage(22)
                .getSendMessage().getText()).setReplyMarkup(pages.get(page));
        messageIdWithPages = bot.execute(sendMessage).getMessageId();
        messagesIdsToDelete.add(messageIdWithPages);

    }

    private void showSuccess(Bot bot) throws SQLException, TelegramApiException {
        List<Entity> entities = getEntitiesToShow();
        if (entities==null){
            sendNothingFound(bot);
            return;
        }
        pages = CustomKeyboardUtil.getPagesWithEntitiesAsButtonText(getEntitiesToShow(), "remove");
        if (page>=pages.size()){
            page--;
            if (page<0){
                sendNothingFound(bot);
                return;
            }
        }
        EditMessageText editMessageText = new EditMessageText().setChatId(chatId).setText("Вы успешно удалили "+ chosenOne + "\nВыберите пункт для удаления")
                .setMessageId(messageIdWithPages).setReplyMarkup(pages.get(page));
        bot.execute(editMessageText);
    }
}

