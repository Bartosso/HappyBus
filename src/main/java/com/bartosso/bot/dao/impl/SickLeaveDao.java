package com.bartosso.bot.dao.impl;

import com.bartosso.bot.dao.AbstractDao;
import com.bartosso.bot.dao.rowMaps.SickLeaveMap;
import com.bartosso.bot.entity.ProjectEntities.SickLeave;
import org.springframework.jdbc.core.JdbcTemplate;


import java.util.List;

@SuppressWarnings("unchecked")
public class SickLeaveDao extends AbstractDao {
    public SickLeaveDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

public void insertNewSickLeave(SickLeave sickLeave){
        jdbcTemplate.update("INSERT INTO sick_leave (child_id, sick_date, place_in_the_bus, bus_id) VALUES (?,?,?,?)", sickLeave
        .getChildId(),sickLeave.getSickDate(),sickLeave.getPlaceInTheBus(),sickLeave.getBusId());
}


    public List<SickLeave> getListOfSickLeavesByChildId(long child_id){
        List<SickLeave> a = jdbcTemplate.query("SELECT * FROM sick_leave WHERE child_id = ?",new Object[]{child_id},new SickLeaveMap());
        if (a.isEmpty()) return null;
        else return a;
    }

    public List<SickLeave> getAllSickLeave(){
        List<SickLeave> a = jdbcTemplate.query("SELECT * FROM sick_leave ",new SickLeaveMap());
        if (a.isEmpty()) return null;
        else return a;
    }

    public SickLeave getSickLeaveById(long id){
        return (SickLeave) jdbcTemplate.queryForObject("SELECT * FROM sick_leave WHERE id = ?",new Object[]{id},new SickLeaveMap());
    }
    public void removeSickLeaves(long id){
        jdbcTemplate.update("DELETE FROM sick_leave WHERE id=?",id);
    }

}
