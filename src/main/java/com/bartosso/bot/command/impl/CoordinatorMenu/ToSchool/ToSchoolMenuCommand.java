package com.bartosso.bot.command.impl.CoordinatorMenu.ToSchool;

import com.bartosso.bot.command.AbstractMenuCommand;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ToSchoolMenuCommand extends AbstractMenuCommand {
    @Override
    protected Map<String, Integer> getButtonsCommandMap() {
        Map<String, Integer> map = new HashMap<>();

        try {
            map.put(buttonDao.getButtonText(46),43);
            map.put(buttonDao.getButtonText(45),44);
        } catch (SQLException e) {
            e.printStackTrace();
        }
       return map;
    }

    @Override
    protected int getMessageMenuTextId() {
        return 53;
    }

    @Override
    protected int getKeyboardId() {
        return 17;
    }
}
