package com.bartosso.bot.command.impl.CoordinatorMenu.ToSchool.ShowListsThings;

import com.bartosso.bot.Bot;
import com.bartosso.bot.command.impl.AdminMenu.AbstractShowAndRemoveCommand;
import com.bartosso.bot.entity.ProjectEntities.Entity;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
public class RemoveKidFromMorningBusCommand extends AbstractShowAndRemoveCommand {
    private final long         busId;
    private final long         chatIdConstructor;
    private boolean            sendsKids;
    private List<Entity>       kidListInDaBus;
    @Override
    protected List<Entity> getEntitiesToShow() {
        //noinspection unchecked
        kidListInDaBus = factory.getKidsDao().getAllKidsFromMorningBus(busId);
        return kidListInDaBus;
    }

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (!sendsKids) {
            sendsKids = true;
            chatId = chatIdConstructor;
        }
        return super.execute(update, bot);
    }

    @Override
    protected void deleteEntityFromDb(int idToDelete) {
            kidListInDaBus.removeIf(entity -> entity.getId()==idToDelete);
            factory.getBusesDao().updateMorningRoute(kidListInDaBus.stream().map(Entity::getId).collect(
                    toList()).toArray(new Long[kidListInDaBus.size()]),busId);
    }

    @Override
    protected void sendMessagesOnDelete(Bot bot) {

    }
}
