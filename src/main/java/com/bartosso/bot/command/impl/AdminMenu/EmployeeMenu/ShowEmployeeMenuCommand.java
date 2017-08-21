package com.bartosso.bot.command.impl.AdminMenu.EmployeeMenu;

import com.bartosso.bot.command.AbstractMenuCommand;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ShowEmployeeMenuCommand extends AbstractMenuCommand {

    @Override
    protected Map<String, Integer> getButtonsCommandMap() {
       Map<String, Integer> map = new HashMap<>();
        try {
            map.put(buttonDao.getButtonText(24),16);
            map.put(buttonDao.getButtonText(28),17);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    @Override
    protected int getMessageMenuTextId() {
        return 17;
    }

    @Override
    protected int getKeyboardId() {
        return 9;
    }
}
