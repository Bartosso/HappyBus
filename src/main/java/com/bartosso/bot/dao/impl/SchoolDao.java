package com.bartosso.bot.dao.impl;

import com.bartosso.bot.dao.AbstractDao;
import com.bartosso.bot.dao.rowMaps.SchoolMap;
import com.bartosso.bot.entity.ProjectEntities.Driver;
import com.bartosso.bot.entity.ProjectEntities.School;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@SuppressWarnings("unchecked")
public class SchoolDao extends AbstractDao {
    public SchoolDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public void addNew(School school){
        jdbcTemplate.update("INSERT INTO schools (name) VALUES (?)", school.getName());
    }

    public List getAllSchools(){
        List<Driver> a = jdbcTemplate.query("SELECT * FROM schools",new SchoolMap());
        if (a.isEmpty()) return null;
        else return a;
    }

    public void renameSchool(long schoolId,String newName){
        jdbcTemplate.update("UPDATE schools SET name =? WHERE id=?",newName,schoolId);
    }

    public School getSchoolById(long id){
        return (School) jdbcTemplate.queryForObject("SELECT * FROM schools WHERE id=?", new Object[]{id} ,new SchoolMap());
    }
    public void deleteSchool(Integer idToDelete){
        jdbcTemplate.update("DELETE FROM schools WHERE id=?", idToDelete);
    }
}
