package com.bartosso.bot.command.impl.CoordinatorMenu.ToHome.ReadyToGoBuses;

import com.bartosso.bot.command.impl.CoordinatorMenu.ToSchool.ShowListsThings.ChangeOrderCommand;
import com.bartosso.bot.entity.ProjectEntities.Kid;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class ChangeEveningRouteOrderCommand extends ChangeOrderCommand {
    ChangeEveningRouteOrderCommand(long busId, long constructorChatId) {
        super(busId, constructorChatId);
    }

    @SuppressWarnings("Duplicates")
    @Override
    protected void updateRoute(int i) {
        //noinspection unchecked
        List<Kid> kidsByBusList = kidList;
        kidsByBusList.removeIf(kid -> kid.getId()==childId);
        List<Long> kidIds = kidsByBusList.stream().map(Kid::getId).collect(toList());
        if (kidIds.size()>=i){
            if (i<1){
                i = 1;
            }
            kidIds.add((i-1),childId);
        }
        else{
            kidIds.add(childId);
        }
        factory.getBusesDao().updateEveningRoute(kidIds.toArray(new Long[kidIds.size()]),busId);
        //noinspection unchecked
        kidList = getKidList();
    }

    @Override
    protected List getKidList() {
        return factory.getKidsDao().getAllKidsFromEveningBus(busId);
    }
}
