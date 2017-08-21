package com.bartosso.bot.dao.rowMaps;

import com.bartosso.bot.entity.Button;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ButtonMap implements RowMapper {
    @Override
    public Button mapRow(ResultSet rs, int rowNum) throws SQLException {
        String url = rs.getString("url");
        if (url.equals("")){
            url = null;
        }
        return new Button(
                rs.getInt(    "id"),
                rs.getString( "text"),
                rs.getInt(    "command_id"),
                url,
                rs.getBoolean("request_contact"));
    }
}
