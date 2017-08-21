package com.bartosso.bot.command.impl.DriverMenu;

import com.bartosso.bot.command.Command;
import com.bartosso.bot.entity.ProjectEntities.Kid;
import lombok.Setter;

import java.util.List;

public abstract class RouteCommand extends Command {
    @Setter
    protected List<Kid> kids;

}
