package com.bartosso.bot.dao.rowMaps;

import com.bartosso.bot.entity.ProjectEntities.Parent;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ParentMap implements RowMapper {
    @Override
    public Object mapRow(ResultSet resultSet, int i) throws SQLException {
        try {
           return new Parent(
                   resultSet.getLong(  "id"),
                   resultSet.getString("first_name"),
                   resultSet.getString("last_name"),
                   resultSet.getString("username"),
                   resultSet.getString("phone"),
                   resultSet.getLong(  "child_id"));
        } catch (SQLException e){
            return null;
        }
    }
}
