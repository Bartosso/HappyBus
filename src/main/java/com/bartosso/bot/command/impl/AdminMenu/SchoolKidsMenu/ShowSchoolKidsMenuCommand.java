package com.bartosso.bot.command.impl.AdminMenu.SchoolKidsMenu;


import com.bartosso.bot.command.AbstractMenuCommand;


import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ShowSchoolKidsMenuCommand extends AbstractMenuCommand {

    @Override
    protected Map<String, Integer> getButtonsCommandMap() {
        Map<String, Integer> map = new HashMap<>();
        try {
            map.put(buttonDao.getButtonText(26),36);
            map.put(buttonDao.getButtonText(27),37);
            map.put(buttonDao.getButtonText(33),38);
            map.put(buttonDao.getButtonText(32),39);
            map.put(buttonDao.getButtonText(34),40);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    @Override
    protected int getMessageMenuTextId() {
        return 42;
    }

    @Override
    protected int getKeyboardId() {
        return 12;
    }
}
