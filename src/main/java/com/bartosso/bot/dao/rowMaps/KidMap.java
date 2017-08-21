package com.bartosso.bot.dao.rowMaps;

import com.bartosso.bot.entity.ProjectEntities.Kid;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class KidMap implements RowMapper {
    @Override
    public Object mapRow(ResultSet resultSet, int i) throws SQLException {
        return new Kid(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getString("school_id"),
                resultSet.getString("photo"));
    }
}
