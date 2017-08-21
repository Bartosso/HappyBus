package com.bartosso.bot.command.impl.AdminMenu.BusesMenu;

import com.bartosso.bot.command.AbstractMenuCommand;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ShowBusesMenuCommand extends AbstractMenuCommand {

    @Override
    protected Map<String, Integer> getButtonsCommandMap() {
        Map<String, Integer> map = new HashMap<>();
        try {
            map.put(buttonDao.getButtonText(25),27);
            map.put(buttonDao.getButtonText(26),28);
            map.put(buttonDao.getButtonText(27),29);
            map.put(buttonDao.getButtonText(29),30);
            map.put(buttonDao.getButtonText(30),31);
            map.put(buttonDao.getButtonText(31),32);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    @Override
    protected int getMessageMenuTextId() {
        return 27;
    }

    @Override
    protected int getKeyboardId() {
        return 11;
    }
}
