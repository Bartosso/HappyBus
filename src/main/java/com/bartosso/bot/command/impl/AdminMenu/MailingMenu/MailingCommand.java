package com.bartosso.bot.command.impl.AdminMenu.MailingMenu;

import com.bartosso.bot.Bot;
import com.bartosso.bot.command.Command;
import com.bartosso.bot.entity.ProjectEntities.Parent;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

import java.sql.SQLException;
import java.util.List;

@Slf4j
public class MailingCommand extends Command {
    private boolean expectNewValue;
    private String  mailingText;
    private SelectSchoolForMailing command;
    @Setter(AccessLevel.PACKAGE)
    private List<Parent> parentsForMailing;
    @Setter
    @Getter
    private boolean readyForMailing;
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (command!=null){
            if (command.execute(update, bot)){
                command = null;
                if (readyForMailing){
                makeMailing(bot);}
                else {
                    return true;
                }
            }
            return false;
        }
        if (update.hasMessage()) {
            chatId = update.getMessage().getChatId();
            if (update.getMessage().hasText() && update.getMessage().getText().equals(buttonDao.getButtonText(10))){
                deleteMessages(bot);
                return true;
            }
            if (!expectNewValue) {
                expectNewValue = true;
                sendMessageByIdWithKeyboard(bot,84,3);
                return false;
            } else {
                if (update.getMessage().hasText()) {
                    deleteMessages(bot);
                    mailingText = update.getMessage().getText();
                    command = new SelectSchoolForMailing(this);
                    if (command.execute(update, bot)){
                        command = null;
                        makeMailing(bot);
                    }
                    return false;
                }
            }
        }
        return false;
    }

    private void makeMailing(Bot bot) throws SQLException, TelegramApiException {
        if (parentsForMailing==null){
            sendNothingFound(bot);
            return;
        }
        SendMessage sendMessage = new SendMessage().setText(mailingText);
        parentsForMailing.forEach(parent -> {
            try {
                bot.execute(sendMessage.setChatId(parent.getId()));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        });
        sendMessageByIdWithKeyboard(bot,20,3);
    }


}
