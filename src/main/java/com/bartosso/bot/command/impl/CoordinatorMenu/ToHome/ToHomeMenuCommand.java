package com.bartosso.bot.command.impl.CoordinatorMenu.ToHome;

import com.bartosso.bot.command.AbstractMenuCommand;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ToHomeMenuCommand extends AbstractMenuCommand {
    @Override
    protected Map<String, Integer> getButtonsCommandMap() {
        Map<String, Integer> map = new HashMap<>();

        try {
            map.put(buttonDao.getButtonText(48),45);
            map.put(buttonDao.getButtonText(49),46);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    @Override
    protected int getMessageMenuTextId() {
        return 54;
    }

    @Override
    protected int getKeyboardId() {
        return 18;
    }
}
