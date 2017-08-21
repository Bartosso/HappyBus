package com.bartosso.bot.command.impl.AdminMenu.EmployeeMenu.Coordinators;

import com.bartosso.bot.command.AbstractMenuCommand;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ShowEmployeeCoordinatorsMenuCommand extends AbstractMenuCommand {

    @Override
    protected Map<String, Integer> getButtonsCommandMap() {
        Map<String, Integer> map = new HashMap<>();
        try {
            map.put(buttonDao.getButtonText(25),21);
            map.put(buttonDao.getButtonText(26),22);
            map.put(buttonDao.getButtonText(27),23);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    @Override
    protected int getMessageMenuTextId() {
        return 24;
    }

    @Override
    protected int getKeyboardId() {
        return 10;
    }
}
