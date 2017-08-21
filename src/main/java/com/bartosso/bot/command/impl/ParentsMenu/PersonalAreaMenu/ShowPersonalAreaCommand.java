package com.bartosso.bot.command.impl.ParentsMenu.PersonalAreaMenu;

import com.bartosso.bot.command.AbstractMenuCommand;




import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ShowPersonalAreaCommand extends AbstractMenuCommand {
    @Override
    protected Map<String, Integer> getButtonsCommandMap() {
        Map<String, Integer> commandMap = new HashMap<>();
        try {
            commandMap.put(buttonDao.getButtonText(7),49);
            commandMap.put(buttonDao.getButtonText(8),7);
            commandMap.put(buttonDao.getButtonText(9),9);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return commandMap;
    }

    @Override
    protected int getMessageMenuTextId() {
        return 8;
    }

    @Override
    protected int getKeyboardId() {
        return 2;
    }

}
