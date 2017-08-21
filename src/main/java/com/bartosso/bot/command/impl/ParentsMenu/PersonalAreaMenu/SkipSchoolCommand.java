package com.bartosso.bot.command.impl.ParentsMenu.PersonalAreaMenu;


import com.bartosso.bot.command.AbstractMenuCommand;


import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class SkipSchoolCommand extends AbstractMenuCommand {

    @Override
    protected Map<String, Integer> getButtonsCommandMap() {
       Map<String, Integer> commandMap = new HashMap<>();
       try {
           commandMap.put(buttonDao.getButtonText(12), 8);
           commandMap.put(buttonDao.getButtonText(14), 50);
       } catch (SQLException e){
           e.printStackTrace();
       }
       return commandMap;
    }

    @Override
    protected int getMessageMenuTextId() {
        return 10;
    }

    @Override
    protected int getKeyboardId() {
        return 5;
    }
}
