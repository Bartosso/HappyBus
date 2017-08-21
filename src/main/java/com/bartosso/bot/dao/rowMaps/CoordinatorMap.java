package com.bartosso.bot.dao.rowMaps;

import com.bartosso.bot.entity.ProjectEntities.Coordinator;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CoordinatorMap implements RowMapper {
    @Override
    public Object mapRow(ResultSet resultSet, int i) throws SQLException {
        return new Coordinator(
                resultSet.getLong(  "id"),
                resultSet.getString("name"),
                resultSet.getString("phone"));
    }
}
