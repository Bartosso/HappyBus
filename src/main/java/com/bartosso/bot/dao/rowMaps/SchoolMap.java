package com.bartosso.bot.dao.rowMaps;

import com.bartosso.bot.entity.ProjectEntities.School;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SchoolMap implements RowMapper {
    @Override
    public Object mapRow(ResultSet resultSet, int i) throws SQLException {
        try{
            return new School(
                    resultSet.getLong("id"),
                    resultSet.getString("name"));
        } catch (Exception e){
            return null;
        }
    }
}
