package com.bartosso.bot.command.impl.ParentsMenu.PersonalAreaMenu;

import com.bartosso.bot.Bot;
import com.bartosso.bot.command.impl.AdminMenu.AbstractShowAndRemoveCommand;
import com.bartosso.bot.dao.impl.BusesDao;
import com.bartosso.bot.entity.ProjectEntities.Bus;
import com.bartosso.bot.entity.ProjectEntities.Entity;
import com.bartosso.bot.entity.ProjectEntities.Parent;
import com.bartosso.bot.entity.ProjectEntities.SickLeave;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class ShowAllSkippedDaysCommand extends AbstractShowAndRemoveCommand {
    private BusesDao  busesDao = factory.getBusesDao();
    private Bus       thisBus;
    private Parent    thisParent;
    private SickLeave sickLeave;
    @Override
    protected List<Entity> getEntitiesToShow() {
        thisParent = factory.getParentDao().getParentByChatId(chatId);
        if (thisParent == null) {
            return null;
        }
        List<SickLeave> sickLeaves = factory.getSickLeaveDao().getListOfSickLeavesByChildId(thisParent.getChildId());
        if (sickLeaves != null) {
            thisBus = factory.getBusesDao().getBusById(sickLeaves.get(0).getBusId());
        }
        //noinspection unchecked
        return (List<Entity>)(List<?>) sickLeaves;
    }

    @Override
    protected void deleteEntityFromDb(int idToDelete) {
            sickLeave              = factory.getSickLeaveDao().getSickLeaveById(idToDelete);
            returnKidInTheBus(sickLeave);
            factory.getSickLeaveDao().removeSickLeaves(idToDelete);
    }

    @Override
    protected void sendMessagesOnDelete(Bot bot) {
        try {
            String    textToDriverUndManager = messageDao.getMessage(80).getSendMessage().getText() +" на "+ sickLeave.getSickDate() + thisParent.toString()
                    + "\nУченик:\n" + factory.getKidsDao().getKidById(thisParent.getChildId()).toString();
            sickLeave = null;
            SendMessage sendMessage = new SendMessage().setText(textToDriverUndManager);
            try {
                bot.execute(sendMessage.setChatId(thisBus.getDriver_id()));
                bot.execute(sendMessage.setChatId(factory.getManagerDao().getManagerChatId()));
            } catch (TelegramApiException ignored) {
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void returnKidInTheBus(SickLeave sickLeave){
        if (sickLeave.getSickDate().getDayOfYear()==LocalDate.now().getDayOfYear()){
     List<Long> kids = thisBus.getTo_school_kids();
     kids.add(sickLeave.getPlaceInTheBus(),sickLeave.getChildId());
     busesDao.updateMorningRoute(kids.toArray(new Long[kids.size()]),thisBus.getId());
        }
 }
}
