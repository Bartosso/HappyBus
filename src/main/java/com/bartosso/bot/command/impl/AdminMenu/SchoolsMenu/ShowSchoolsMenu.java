package com.bartosso.bot.command.impl.AdminMenu.SchoolsMenu;

import com.bartosso.bot.command.AbstractMenuCommand;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ShowSchoolsMenu extends AbstractMenuCommand {

    @Override
    protected Map<String, Integer> getButtonsCommandMap() {
        Map<String,Integer> map = new HashMap<>();
        try {
            map.put(buttonDao.getButtonText(25), 24);
            map.put(buttonDao.getButtonText(26), 25);
            map.put(buttonDao.getButtonText(27), 26);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    @Override
    protected int getMessageMenuTextId() {
        return 26;
    }

    @Override
    protected int getKeyboardId() {
        return 10;
    }
}
