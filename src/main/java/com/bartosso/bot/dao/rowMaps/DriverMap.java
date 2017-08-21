package com.bartosso.bot.dao.rowMaps;

import com.bartosso.bot.entity.ProjectEntities.Driver;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DriverMap implements RowMapper {
    @Override
    public Object mapRow(ResultSet resultSet, int i) throws SQLException {
        return new Driver(
                resultSet.getLong(   "id"),
                resultSet.getString( "name"),
                resultSet.getString( "phone"),
                resultSet.getBoolean("got_bus"));
    }
}
