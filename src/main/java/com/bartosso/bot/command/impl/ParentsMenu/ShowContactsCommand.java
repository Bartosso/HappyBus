package com.bartosso.bot.command.impl.ParentsMenu;

import com.bartosso.bot.command.AbstractShowOneButtonBackMenuCommand;


public class ShowContactsCommand extends AbstractShowOneButtonBackMenuCommand {
    @Override
    protected int getMessageIdToShow() {
        return 2;
    }

    @Override
    protected int getButtonOnCallId() {
        return 4;
    }

}
