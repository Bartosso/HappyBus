package com.bartosso.bot.command.impl.AdminMenu.BusesMenu;

import com.bartosso.bot.Bot;
import com.bartosso.bot.command.Command;
import com.bartosso.bot.entity.ProjectEntities.Bus;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

public class AddNewBusCommand extends Command {
    private int     step        = 0;
    private boolean creatingBus;
    private String  busNumber;
    private String  busBrand;
    private String  busModel;
    private String  busColor;
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (update.hasMessage()){
            chatId = update.getMessage().getChatId();
            if (update.getMessage().hasText()){
                if (update.getMessage().getText().equals(buttonDao.getButtonText(10))){
                    deleteMessages(bot);
                    return true;
                }
                if (creatingBus){
                    switch (step){
                        case 0:
                            busNumber = update.getMessage().getText();
                            step      = 1;
                            break;
                        case 1:
                            busBrand  = update.getMessage().getText();
                            step      = 2;
                            break;
                        case 2:
                            busModel  = update.getMessage().getText();
                            step      = 3;
                            break;
                        case 3:
                            busColor  = update.getMessage().getText();
                            step      = 4;
                    }
                }
                if (step==0){
                    sendMessageByIdWithKeyboard(bot,28,3);
                    creatingBus = true;
                    return false;
                }
                if (step==1){
                    sendMessageByIdWithKeyboard(bot,29,3);
                    return false;
                }
                if (step==2){
                    sendMessageByIdWithKeyboard(bot,30,3);
                    return false;
                }
                if (step==3){
                    sendMessageByIdWithKeyboard(bot,31,3);
                    return false;
                }
                if (step==4){
                    factory.getBusesDao().addNew(new Bus(busNumber,busBrand,busModel,busColor));
                    creatingBus = false;
                    step        = 0;
                    sendMessageByIdWithKeyboard(bot,20,3);
                    return false;
                }

            }
            sendErrorMessageForFormat(bot);
            return false;
        }
        sendErrorMessageForFormat(bot);
        return false;
    }

}
