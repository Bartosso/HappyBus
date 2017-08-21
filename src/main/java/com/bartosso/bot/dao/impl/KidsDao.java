package com.bartosso.bot.dao.impl;

import com.bartosso.bot.dao.AbstractDao;
import com.bartosso.bot.dao.rowMaps.KidMap;
import com.bartosso.bot.entity.ProjectEntities.Entity;
import com.bartosso.bot.entity.ProjectEntities.Kid;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

@SuppressWarnings("unchecked")
public class KidsDao extends AbstractDao implements EntityDao {
    public KidsDao(JdbcTemplate jdbcTemplate) { super(jdbcTemplate); }


    public Kid getKidById(Long id){
        return (Kid) jdbcTemplate.queryForObject("SELECT * FROM kids WHERE id=?",new Object[]{id},new KidMap());
    }

    public List getKidsBySchool(String school_id){
        List a = jdbcTemplate.query("SELECT * FROM kids WHERE school_id=?",new Object[]{school_id},new KidMap());
        if (a.isEmpty()) return null;
        else return a;
    }

    public List<Entity> getKidsByName(String name){
        List a = jdbcTemplate.query("SELECT * FROM kids WHERE name LIKE '%'||?||'%'",new Object[]{name},new KidMap());
        if (a.isEmpty()) return null;
        else return a;
    }

    @Override
    public List getAll() {
       List a =  jdbcTemplate.query("SELECT * FROM kids;",new KidMap());
        if (a.isEmpty()) return null;
        else return a;
    }

    public List getAllKidsFromMorningBus(long busId) {
        List a =  jdbcTemplate.query("SELECT * FROM get_all_kids_from_bus(?)"
                ,new Object[]{busId},new KidMap());
        if (a.isEmpty()) return null;
        else return a;
    }

    public List getAllKidsFromEveningBus(long busId) {
        List a =  jdbcTemplate.query("SELECT * FROM get_all_kids_from_evening_bus(?)"
                ,new Object[]{busId},new KidMap());
        if (a.isEmpty()) return null;
        else return a;
    }

    public List getAllKidsWhoNotInThatEveningBus(long busId) {
        List a =  jdbcTemplate.query("\n" +
                        "SELECT * FROM kids WHERE NOT id = ANY (SELECT unnest(buses.to_home_kids)FROM buses) " +
                        "AND id = ANY (SELECT\n" +
                        "unnest(buses.to_school_kids) FROM buses WHERE buses.id=?)"
                ,new Object[]{busId},new KidMap());
        if (a.isEmpty()) return null;
        else return a;
    }

    public List<Entity> getAllKidsWhoNotInAnyEveningBus() {
        List a =  jdbcTemplate.query("\n" +
                        "SELECT * FROM kids WHERE NOT id = ANY (SELECT unnest(buses.to_home_kids)FROM buses) "
                ,new KidMap());
        if (a.isEmpty()) return null;
        else return a;
    }

    public List<Entity> getAllKidsWhoNotInMorningBus() {
        List<Entity> a =  jdbcTemplate.query("\n" +
                        "SELECT * FROM kids WHERE NOT id = ANY (SELECT unnest(buses.to_school_kids)FROM buses)"
                ,new KidMap());
        if (a.isEmpty()) return null;
        else return a;
    }

    public void changeKidThings(String whatToChange, Object newValue, Long id){
        jdbcTemplate.update("UPDATE kids SET "+whatToChange+"=? WHERE id=?", newValue,id);
    }

    public long addNewKid(Kid kid){
      return   jdbcTemplate.queryForObject("INSERT  INTO  kids (name, school_id, photo) VALUES (?,?,?) RETURNING id"
                ,new Object[]{kid.getName(),kid.getSchool_id(),kid.getPhoto()},
              ((resultSet, i) -> resultSet.getLong("id")));
    }

    public void killKid(long kidId){
        jdbcTemplate.update("DELETE FROM kids WHERE id=?",kidId);
    }


}
