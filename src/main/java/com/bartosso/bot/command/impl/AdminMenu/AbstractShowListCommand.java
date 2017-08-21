package com.bartosso.bot.command.impl.AdminMenu;

import com.bartosso.bot.Bot;
import com.bartosso.bot.Util.CustomKeyboardUtil;
import com.bartosso.bot.command.Command;
import com.bartosso.bot.entity.ProjectEntities.Entity;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.List;

public abstract class AbstractShowListCommand extends Command {
    private List<SendMessage> pages;
    private int messageWithPageId;
    private int page = 0;
    @SuppressWarnings("Duplicates")
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (update.hasMessage()){
            chatId = update.getMessage().getChatId();
            if (update.getMessage().hasText()){
                if (update.getMessage().getText().equals(buttonDao.getButtonText(10))){
                    deleteMessages(bot);
                    return true;
                } else {
                    List<Entity> entities = getEntitiesForList();
                    if (entities==null){
                        sendNothingFound(bot);
                        return false;
                    }
                    pages = CustomKeyboardUtil.getEntitiesListViaMessageUndPagesSelector(entities,chatId);
                    SendMessage sendMessage = pages.get(page);
                    messageWithPageId       = bot.execute(sendMessage).getMessageId();
                    messagesIdsToDelete.add(messageWithPageId);
                    return false;
                }
            }
            sendErrorMessageForFormat(bot);
            return false;
        }
        if (update.hasCallbackQuery()){
            String chose = update.getCallbackQuery().getData();
            if (chose.equals("back")){
                deleteMessages(bot);
                return true;
            }
            if (chose.equals("nextPage")){
                page++;
                SendMessage sendMessage = pages.get(page);
                bot.execute(new EditMessageText().setMessageId(messageWithPageId)
                        .setText(sendMessage.getText())
                        .setReplyMarkup((InlineKeyboardMarkup) sendMessage.getReplyMarkup())
                        .setChatId(chatId));
                return false;
            }
            if (chose.equals("previousPage")){
                page--;
                SendMessage sendMessage = pages.get(page);
                bot.execute(new EditMessageText().setMessageId(messageWithPageId)
                        .setText(sendMessage.getText())
                        .setReplyMarkup((InlineKeyboardMarkup) sendMessage.getReplyMarkup())
                        .setChatId(chatId));
                return false;
            }

            return false;
        }
        return false;
    }

    protected abstract List<Entity> getEntitiesForList();

}
