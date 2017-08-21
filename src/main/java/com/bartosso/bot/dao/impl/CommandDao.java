package com.bartosso.bot.dao.impl;


import com.bartosso.bot.command.Command;
import com.bartosso.bot.command.CommandFactory;
import com.bartosso.bot.dao.AbstractDao;
import com.bartosso.bot.dao.rowMaps.CommandMap;
import com.bartosso.bot.exception.CommandNotFoundException;
import lombok.NoArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by user on 12/14/16.
 */

public class CommandDao extends AbstractDao {


    public CommandDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public Command getCommand(long commandId) throws CommandNotFoundException, SQLException {
try {
    return (Command) jdbcTemplate.queryForObject("SELECT * FROM PUBLIC.COMMAND WHERE ID=?",
            new Object[] {commandId} ,new CommandMap());
} catch (Exception e) {
    throw new CommandNotFoundException(e);
}
    }

}
