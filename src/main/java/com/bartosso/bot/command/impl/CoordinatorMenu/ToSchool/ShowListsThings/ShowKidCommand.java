package com.bartosso.bot.command.impl.CoordinatorMenu.ToSchool.ShowListsThings;

import com.bartosso.bot.Bot;
import com.bartosso.bot.command.Command;
import com.bartosso.bot.entity.ProjectEntities.Kid;
import com.bartosso.bot.entity.ProjectEntities.Parent;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.List;

@RequiredArgsConstructor
public class ShowKidCommand extends Command {
    private final long    kidId;
    private final long    constructorChatId;
    private       boolean kidSends;
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (!kidSends){
            kidSends = true;
            chatId   = constructorChatId;
            showKid(bot);
            return false;
        }
        //noinspection Duplicates
        if (update.hasMessage()) {
            if (update.getMessage().hasText()) {
                if (update.getMessage().getText().equals(buttonDao.getButtonText(10)))
                { deleteMessages(bot);
                    return true;
                }
            }
        }
        if (update.hasCallbackQuery()) {
            if (update.getCallbackQuery().getData().equals(buttonDao
            .getButtonText(10))){
                deleteMessages(bot);
                return true;
            }
        }
        sendErrorMessageForFormat(bot);
        return false;
    }

    private void showKid(Bot bot) throws SQLException, TelegramApiException {
        Kid kid = factory.getKidsDao().getKidById(kidId);
        List<Parent> parents = factory.getParentDao().getParentsByChildId(kidId);
        StringBuilder sb = new StringBuilder();
        sb.append("Вы выбрали\n").append(kid.toString()).append("\nШкола: ").append(factory.getSchoolDao()
                .getSchoolById(Long.parseLong(kid.getSchool_id())).getName()).append("\nРодители: ");
        parents.forEach(parent -> sb.append(parent.toString()));
        SendMessage sendMessage = new SendMessage().setChatId(chatId)
                .setText(sb.toString())
                .setReplyMarkup(keyboardMarkUpDao.select(15));
        deleteMessages(bot);
        if (kid.getPhoto()!=null){
            SendPhoto sendPhoto = new SendPhoto().setPhoto(kid.getPhoto()).setChatId(chatId);
            messagesIdsToDelete.add(bot.sendPhoto(sendPhoto).getMessageId());
        }
        messagesIdsToDelete.add(bot.execute(sendMessage).getMessageId());
    }
}
