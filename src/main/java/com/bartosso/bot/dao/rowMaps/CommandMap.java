package com.bartosso.bot.dao.rowMaps;

import com.bartosso.bot.command.Command;
import com.bartosso.bot.command.CommandFactory;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CommandMap implements RowMapper {
    @Override
    public Command mapRow(ResultSet rs, int rowNum) throws SQLException {
     Command command = CommandFactory.getCommand(rs.getLong("command_type_id"));
     command.setId(rs.getLong(                              "id"));
     command.setMessageId(rs.getLong(                       "message_id"));
     return command;
    }
}
