package com.bartosso.bot.dao.rowMaps;

import com.bartosso.bot.entity.ProjectEntities.SickLeave;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SickLeaveMap implements RowMapper {
    @Override
    public Object mapRow(ResultSet resultSet, int i) throws SQLException {
        return new SickLeave(
                resultSet.getLong("id"),
                resultSet.getLong("child_id"),
                resultSet.getDate("sick_date").toLocalDate(),
                resultSet.getInt("place_in_the_bus"),
                resultSet.getLong("bus_id"));
    }
}
