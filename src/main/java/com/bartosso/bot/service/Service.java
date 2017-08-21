package com.bartosso.bot.service;

import com.bartosso.bot.dao.DaoFactory;
import com.bartosso.bot.dao.impl.*;
import org.springframework.stereotype.Component;

/**
 * Created by user on 1/2/17.
 */
@Component
public class Service {
    private DaoFactory factory = DaoFactory.getFactory();
    MessageDao messageDao = factory.getMessageDao();
    KeyboardMarkUpDao keyboardMarkUpDao = factory.getKeyboardMarkUpDao();
    ButtonDao buttonDao = factory.getButtonDao();
    CommandDao commandDao = factory.getCommandDao();



}
