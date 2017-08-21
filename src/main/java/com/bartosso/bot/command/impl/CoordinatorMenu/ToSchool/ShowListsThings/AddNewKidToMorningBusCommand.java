package com.bartosso.bot.command.impl.CoordinatorMenu.ToSchool.ShowListsThings;


import com.bartosso.bot.Bot;
import com.bartosso.bot.Util.CustomKeyboardUtil;
import com.bartosso.bot.command.Command;
import com.bartosso.bot.entity.ProjectEntities.Entity;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AddNewKidToMorningBusCommand extends Command {
  protected long                            busId;
  private   boolean                         messageSends;
  private   ArrayList<InlineKeyboardMarkup> pages;
  private   int                             page          = 0;
  private   int                             messageIdWithKeyboard;

  protected AddNewKidToMorningBusCommand(long chatId, long busId){
      this.chatId = chatId; this.busId = busId;
  }

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
      if (!messageSends) {
        messageSends = true;
        showAvailableKids(bot);
        return false;
      }
      if (update.hasCallbackQuery()) {
        String chose = update.getCallbackQuery().getData();

        if (chose.contains("kid")){
          deleteMessages(bot);
          long kidId = Long.parseLong(chose.substring(chose.indexOf(":")+1));
          addNewKidToThisBus(kidId);
          sendMessageByIdWithKeyboard(bot,20,15);
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
        if (chose.equals(buttonDao.getButtonText(10))) {
          deleteMessages(bot);
          return true;
        }
      }
      return false;
    }


    protected void addNewKidToThisBus(long kidId){
    List<Long> kidsInThisBus = factory.getBusesDao().getBusById(busId).getTo_school_kids();
    kidsInThisBus.add(kidId);
    factory.getBusesDao().updateMorningRoute(kidsInThisBus.toArray(new Long[kidsInThisBus.size()]),busId);
    }


    private void showAvailableKids(Bot bot) throws SQLException, TelegramApiException {
      List<Entity> availableKids = getAvailableKids();
      if (availableKids == null) {
        sendMessageByIdWithKeyboard(bot,73,15);
        return;
      }
      pages = CustomKeyboardUtil.getPagesWithEntitiesAsButtonText(availableKids,"kid");
      messageIdWithKeyboard = bot.execute(new SendMessage(chatId,messageDao.getMessage(42).getSendMessage().getText())
      .setReplyMarkup(pages.get(page))).getMessageId();
      messagesIdsToDelete.add(messageIdWithKeyboard);
    }

    public List<Entity> getAvailableKids(){
      return factory.getKidsDao().getAllKidsWhoNotInMorningBus();
    }

    private void showPage(Bot bot) throws TelegramApiException {
      bot.execute(new EditMessageReplyMarkup().setChatId(chatId).setMessageId(messageIdWithKeyboard)
      .setReplyMarkup(pages.get(page)));
    }
}
