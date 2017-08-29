package com.bartosso.bot.command.impl.AdminMenu.MailingMenu;

import com.bartosso.bot.Bot;
import com.bartosso.bot.Util.CustomKeyboardUtil;
import com.bartosso.bot.command.Command;
import com.bartosso.bot.entity.ProjectEntities.Entity;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class SelectSchoolForMailing extends Command {
    private final MailingCommand                  mailingCommand;
    protected     int                             page = 0;
    private       ArrayList<InlineKeyboardMarkup> schoolPages;
    protected     int                             messageWithKeyboardId;
    private       boolean                         runningCommand;
    private       GetParentsForMailingCommand     getParentsForMailingCommand;
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (runningCommand){
            if (getParentsForMailingCommand.execute(update, bot)){
                if (mailingCommand.isReadyForMailing()){
                getParentsForMailingCommand  = null;
                runningCommand    = false;
                return true;}
                else {
                    showSchools(bot);
                    getParentsForMailingCommand  = null;
                    runningCommand    = false;
                }
            }
            return false;
        }
        if (update.hasMessage()){
            chatId = update.getMessage().getChatId();
            if (update.getMessage().hasText() &&
                    update.getMessage().getText().equals(buttonDao.getButtonText(10))) {
                    deleteMessages(bot);
                    return true;
            }
            showSchools(bot);
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
            if (chose.contains("school")){
                getParentsForMailingCommand = new GetParentsForMailingCommand
                        (Long.parseLong(chose.substring(chose.indexOf(":")+1)),chatId,mailingCommand);
                return selectedSchool(update, bot);
            }
        }

        return false;
    }

    @SuppressWarnings("unchecked")
    private void showSchools(Bot bot) throws SQLException, TelegramApiException {
        List list = factory.getSchoolDao().getAllSchools();
        page = 0;
        deleteMessages(bot);
        if (list==null){
            sendNothingFound(bot);
            return;
        }
        list.add(0,new Entity() {
            @Override
            public String getTextToButton() {
                return "Разослать всем";
            }

            @Override
            public long getId() {
                return -200;
            }
        });
        list.add(1,new Entity() {
            @Override
            public String getTextToButton() {
                return "Выбрать родителей";
            }

            @Override
            public long getId() {
                return -300;
            }
        });
        String message = messageDao.getMessage(85).getSendMessage().getText();
        //noinspection unchecked
        schoolPages = CustomKeyboardUtil.getPagesWithEntitiesAsButtonText(list,"school");
        SendMessage sendMessage = new SendMessage(chatId,message).setReplyMarkup(schoolPages.get(page));
        messageWithKeyboardId = bot.execute(sendMessage).getMessageId();
        messagesIdsToDelete.add(messageWithKeyboardId);
    }

    private void showPage(Bot bot) throws TelegramApiException {
        bot.execute(new EditMessageReplyMarkup().setChatId(chatId).setMessageId(messageWithKeyboardId)
                .setReplyMarkup(schoolPages.get(page)));
    }

    private boolean selectedSchool(Update update, Bot bot) throws SQLException, TelegramApiException {
        deleteMessages(bot);
        if (getParentsForMailingCommand.execute(update, bot)){
            return true;
        } else {
            runningCommand = true;
            return false; }
    }

}
