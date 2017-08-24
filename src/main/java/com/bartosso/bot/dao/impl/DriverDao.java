package com.bartosso.bot.dao.impl;

import com.bartosso.bot.dao.AbstractDao;
import com.bartosso.bot.dao.rowMaps.DriverMap;
import com.bartosso.bot.entity.ProjectEntities.Driver;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

@SuppressWarnings("unchecked")
public class DriverDao extends AbstractDao implements EntityDao {
    public DriverDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }


    public void addNew(Driver driver) {
        jdbcTemplate.update("INSERT INTO drivers (id, name, phone, got_bus,school_id) VALUES (?,?,?,?,?)",
                driver.getId(),driver.getName(),driver.getPhone(),false, driver.getSchool_id());
    }

    public Driver getDriverByChatId(long chatId) {
       return (Driver) jdbcTemplate.queryForObject("SELECT * FROM drivers WHERE id=?",new Object[] {chatId},
       new DriverMap());
    }

    public List getAll() {
        List<Driver> a = jdbcTemplate.query("SELECT * FROM drivers",new DriverMap());
        if (a.isEmpty()) return null;
        else return a;
    }

    public List getAllFromSchool(long school_id) {
        List<Driver> a = jdbcTemplate.query("SELECT * FROM drivers WHERE school_id = ?",new Object[]{school_id},new DriverMap());
        if (a.isEmpty()) return null;
        else return a;
    }


    public List getAllDriversWithoutSchool() {
        List<Driver> a = jdbcTemplate.query("SELECT * FROM drivers WHERE school_id ISNULL ",new DriverMap());
        if (a.isEmpty()) return null;
        else return a;
    }

    public void removeFromSchool(long school_id){
        jdbcTemplate.update("UPDATE drivers SET school_id = NULL WHERE school_id=?",school_id);
    }

    public List getAllDriversWithoutBus() {
        List<Driver> a = jdbcTemplate.query("SELECT * FROM drivers WHERE got_bus=FALSE ",new DriverMap());
        if (a.isEmpty()) return null;
        else return a;
    }

    public boolean isDriver(long chatId) {
        return jdbcTemplate.queryForObject("select exists(select * from drivers where id=?)",Boolean.class, chatId);
    }


    public void setGotBus(boolean gotBus,long driverId){
        jdbcTemplate.update("UPDATE drivers SET got_bus=? WHERE  id=?", gotBus,driverId);
    }


    public void setNewSchool(long schoolId,long driverId){
        jdbcTemplate.update("UPDATE drivers SET school_id=? WHERE  id=?", schoolId,driverId);
    }

    public void deleteDriver(Integer idToDelete){
        jdbcTemplate.update("DELETE FROM drivers WHERE id=?", idToDelete);
    }


}
