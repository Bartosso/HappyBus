package com.bartosso.bot.Util.Reminder;

import com.bartosso.bot.dao.DaoFactory;
import com.bartosso.bot.dao.impl.BusesDao;
import com.bartosso.bot.dao.impl.SickLeaveDao;
import com.bartosso.bot.entity.ProjectEntities.Bus;
import com.bartosso.bot.entity.ProjectEntities.SickLeave;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;
import java.util.TimerTask;

import static java.util.stream.Collectors.toList;
@RequiredArgsConstructor
@Slf4j
public class CheckEveryNightDB extends TimerTask {
    private DaoFactory   factory      = DaoFactory.getFactory();
    private BusesDao     busesDao     = factory.getBusesDao();
    private SickLeaveDao sickLeaveDao = factory.getSickLeaveDao();

    private final Reminder     reminder;

    @Override
    public void run() {
        List<SickLeave> sickLeaves = sickLeaveDao.getAllSickLeave();
        LocalDate localDate = LocalDate.now();
        if (sickLeaves!=null){
        List<SickLeave> endedSick  = sickLeaves.stream().filter(sickLeave -> sickLeave
                .getSickDate().isBefore(localDate)).collect(toList());
        if (endedSick != null) {
          endedSick.forEach(this::returnKidInTheBus);
        }
        List<SickLeave> todaySickLeaves = sickLeaves.stream().filter(sickLeave -> sickLeave
                .getSickDate().getDayOfYear()==localDate.getDayOfYear())
                .collect(toList());
        if (todaySickLeaves != null) {
            todaySickLeaves.forEach(this::removeSickKidsFromBus);
        }}
        reminder.setCheckEveryNightDb(localDate.plusDays(1).atTime(0,5));
    }

    private void removeSickKidsFromBus(SickLeave sickLeave){
        Bus thisBus = busesDao.getBusById(sickLeave.getBusId());
        List<Long> kids     = thisBus.getTo_school_kids();
        kids.removeIf(aLong -> aLong == sickLeave.getChildId());
        busesDao.updateMorningRoute(kids.toArray(new Long[kids.size()]),thisBus.getId());
    }

    private void returnKidInTheBus(SickLeave sickLeave){
            Bus thisBus     = busesDao.getBusById(sickLeave.getBusId());
            List<Long> kids = thisBus.getTo_school_kids();
            kids.add(sickLeave.getPlaceInTheBus(),sickLeave.getChildId());
            busesDao.updateMorningRoute(kids.toArray(new Long[kids.size()]),thisBus.getId());
            sickLeaveDao.removeSickLeaves(sickLeave.getId());
}
}
