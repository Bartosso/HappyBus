package com.bartosso.bot.dao.impl;



import com.bartosso.bot.dao.AbstractDao;
import com.bartosso.bot.dao.rowMaps.ParentMap;
import com.bartosso.bot.entity.ProjectEntities.Bus;
import com.bartosso.bot.entity.ProjectEntities.Entity;
import com.bartosso.bot.entity.ProjectEntities.Parent;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Created by user on 1/21/17.
 */
@Repository
public class ParentDao extends AbstractDao {
    public ParentDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @SuppressWarnings("unchecked")
    public Parent getParentByChatId(long chatId){
        try {
            return (Parent) jdbcTemplate.queryForObject("SELECT * FROM parents WHERE id=?",
                    new Object[] {chatId}, new ParentMap());}
                    catch (org.springframework.dao.EmptyResultDataAccessException e){
            return null;
                    }

    }

    public List<Parent> getParentsByChildId(long child_id){
        @SuppressWarnings("unchecked")
        List<Parent> a = jdbcTemplate.query("SELECT * FROM parents WHERE  child_id =?"
                ,new Object[]{child_id},new ParentMap());
        if (a.isEmpty()) return null;
        else return a;
    }

    public List<Parent> getAllParents(){
        @SuppressWarnings("unchecked")
        List<Parent> a = jdbcTemplate.query("SELECT * FROM parents",new ParentMap());
        if (a.isEmpty()) return null;
        else return a;
    }

    public List<Parent> getAllParentsFromSchool(long schoolId){
        @SuppressWarnings("unchecked")
        List<Parent> a = jdbcTemplate.query
                ("SELECT * FROM parents WHERE child_id = ANY (SELECT kids.id FROM kids WHERE school_id::INTEGER=?)",
                        new Object[]{schoolId}, new ParentMap());
        if (a.isEmpty()) return null;
        else return a;
    }

    public void addNew(Parent parent){
        jdbcTemplate.update("INSERT INTO parents (id, first_name, last_name, username, phone, child_id) VALUES " +
                "(?,?,?,?,?,?)",parent.getId(),  parent.getFirstName(), parent.getLastName(),
                parent.getUsername(), parent.getPhone(), parent.getChildId());
    }

    public void killParentsFromChildId(long childId){
        jdbcTemplate.update("DELETE FROM parents WHERE child_id=?",childId);
    }

    public boolean isRegistred(long chatId){
       return jdbcTemplate.queryForObject("select exists(select * from parents where id=?)",Boolean.class,chatId);
    }

    public List<Parent> getAllParentsFromMorningBus(long busId){
        @SuppressWarnings("unchecked")
        List<Parent> a = jdbcTemplate.query("SELECT * FROM parents WHERE child_id " +
                        " = ANY (SELECT unnest(buses.to_school_kids) FROM buses WHERE buses.id=?)"
                ,new Object[]{busId},new ParentMap());
        if (a.isEmpty()) return null;
        else return a;
    }

    public List<Parent> getAllParentsFromEveningBus(long busId){
        @SuppressWarnings("unchecked")
        List<Parent> a = jdbcTemplate.query("SELECT * FROM parents WHERE child_id " +
                        " = ANY (SELECT unnest(buses.to_home_kids) FROM buses WHERE buses.id=?)"
                ,new Object[]{busId},new ParentMap());
        if (a.isEmpty()) return null;
        else return a;
    }

    public void killParentById(long parentId){
        jdbcTemplate.update("DELETE FROM parents WHERE id=?",parentId);
    }


}
