package com.bartosso.bot.command.impl.ParentsMenu;


import com.bartosso.bot.command.AbstractShowOneButtonBackMenuCommand;


public class ShowNewsCommand extends AbstractShowOneButtonBackMenuCommand {


    @Override
    protected int getMessageIdToShow() {
        return 6;
    }

    @Override
    protected int getButtonOnCallId() {
        return 5;
    }
}
