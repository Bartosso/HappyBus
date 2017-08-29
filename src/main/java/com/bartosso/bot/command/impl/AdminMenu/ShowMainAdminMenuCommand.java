package com.bartosso.bot.command.impl.AdminMenu;

import com.bartosso.bot.Bot;
import com.bartosso.bot.command.AbstractMenuCommand;
import com.bartosso.bot.command.Command;
import com.bartosso.bot.command.impl.ParentsMenu.ShowMainMenuCommand;
import com.bartosso.bot.dao.impl.AdminDao;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ShowMainAdminMenuCommand extends AbstractMenuCommand {
    private boolean  creatingAdmin;
    private AdminDao adminDao      = factory.getAdminDao();
    @Override
    protected Map<String, Integer> getButtonsCommandMap() {
        Map<String, Integer> map = new HashMap<>();
        try {
            map.put(buttonDao.getButtonText(17),11);
            map.put(buttonDao.getButtonText(18),12);
            map.put(buttonDao.getButtonText(19),13);
            map.put(buttonDao.getButtonText(20),14);
            map.put(buttonDao.getButtonText(21),15);
            map.put(buttonDao.getButtonText(58),54);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (creatingAdmin) {
            if (update.hasCallbackQuery()) {
                String chose = update.getCallbackQuery().getData();
                if (chose.equals(buttonDao.getButtonText(50))) {
                    creatingAdmin = false;
                    adminDao.insertAdmin(chatId);
                    deleteMessages(bot);
                    sendMessageByIdWithKeyboard(bot,20,8);
                    return false;
                }
                if (chose.equals(buttonDao.getButtonText(51))){
                    creatingAdmin = false;
                    deleteMessages(bot);
                    Command command = new ShowMainMenuCommand();
                    command.addNewMessageToDelete(bot.execute(new SendMessage(chatId,messageDao.getMessage(1).getSendMessage().getText())
                            .setReplyMarkup(keyboardMarkUpDao.select(1))).getMessageId());
                    bot.getConversation(chatId)
                            .setCommand(command);
                    return false;
                }
            }

        }
        if (update.hasMessage()){
            chatId = update.getMessage().getChatId();
            if (update.getMessage().hasText()){
                if (update.getMessage().getText().equals(buttonDao.getButtonText(22))){
                    deleteMessages(bot);
                    Command command = new ShowMainMenuCommand();
                    bot.getConversation(chatId)
                    .setCommand(command);
                    return command.execute(update, bot);
                }
            }
            if (!adminDao.isAdmin(chatId)){
                if (!factory.getAdminDao().isAnyAdminExist()) {
                    sendMessageByIdWithKeyboard(bot,82,19);
                    creatingAdmin = true;
                    return false;
                }
                Command command = new ShowMainMenuCommand();
                command.addNewMessageToDelete(bot.execute(new SendMessage(chatId,messageDao.getMessage(76).getSendMessage().getText())
                        .setReplyMarkup(keyboardMarkUpDao.select(1))).getMessageId());
                bot.getConversation(chatId)
                        .setCommand(command);
                return false;
            }

        }
        return super.execute(update, bot);
    }

    @Override
    protected int getMessageMenuTextId() {
        return 16;
    }

    @Override
    protected int getKeyboardId() {
        return 8;
    }
}
