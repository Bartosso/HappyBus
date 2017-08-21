package com.bartosso.bot.service;

import com.bartosso.bot.command.Command;
import com.bartosso.bot.entity.Button;
import com.bartosso.bot.exception.CommandNotFoundException;

import java.sql.SQLException;

/**
 * Created by user on 1/2/17.
 */
public class CommandService extends Service {

    public Command getCommand(String text) throws SQLException, CommandNotFoundException {

        Button button = buttonDao.getButton(text);
        return commandDao.getCommand(button.getCommandId());
    }
}
