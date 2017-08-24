package com.bartosso.bot.command.impl.AdminMenu.INFOMenu;

import com.bartosso.bot.command.AbstractMenuCommand;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ShowINFOMenuCommand extends AbstractMenuCommand {

    @Override
    protected Map<String, Integer> getButtonsCommandMap() {
        Map<String, Integer> map = new HashMap<>();
        try {
            map.put(buttonDao.getButtonText(37),33);
            map.put(buttonDao.getButtonText(36),34);
            map.put(buttonDao.getButtonText(38),35);
            map.put(buttonDao.getButtonText(55),51);
            map.put(buttonDao.getButtonText(56),52);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    @Override
    protected int getMessageMenuTextId() {
        return 35;
    }

    @Override
    protected int getKeyboardId() {
        return 14;
    }
}
