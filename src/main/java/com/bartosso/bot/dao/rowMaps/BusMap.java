package com.bartosso.bot.dao.rowMaps;

import com.bartosso.bot.entity.ProjectEntities.Bus;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class BusMap implements RowMapper {
    @Override
    public Object mapRow(ResultSet resultSet, int i) throws SQLException {
        Long[] toSchoolKids;
        Long[] toHomeKids;
        ArrayList<Long> toSchool = null;
        ArrayList<Long> toHome   = null;
        try {
        toSchoolKids = (Long[]) resultSet.getArray("to_school_kids").getArray();
        toSchool       = new ArrayList<>(Arrays.asList(toSchoolKids));
        }
        catch (NullPointerException ignored){ }
        try {
        toHomeKids   = (Long[]) resultSet.getArray("to_home_kids").getArray();
        toHome       = new ArrayList<>(Arrays.asList(toHomeKids));
        }
        catch (NullPointerException ignored){ }
        return new Bus(
                resultSet.getLong(  "id"),
                resultSet.getLong(  "driver_id"),
                resultSet.getString("number"),
                resultSet.getString("brand"),
                resultSet.getString("model"),
                resultSet.getString("color"),
                resultSet.getBoolean("ready_to_school"),
                resultSet.getBoolean("ready_to_home"),
                toSchool,
                toHome,
                resultSet.getString("last_gps_cords"),
                resultSet.getLong("last_cordinator_id"));
    }
}
