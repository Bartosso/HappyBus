package com.bartosso.bot.command.impl.AdminMenu.EmployeeMenu.Drivers;

import com.bartosso.bot.command.AbstractMenuCommand;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ShowEmployeeDriversMenuCommand extends AbstractMenuCommand {

    @Override
    protected Map<String, Integer> getButtonsCommandMap() {
        Map<String, Integer> map = new HashMap<>();
        try {
            map.put(buttonDao.getButtonText(25),18);
            map.put(buttonDao.getButtonText(26),19);
            map.put(buttonDao.getButtonText(27),20);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    @Override
    protected int getMessageMenuTextId() {
        return 18;
    }

    @Override
    protected int getKeyboardId() {
        return 10;
    }
}
