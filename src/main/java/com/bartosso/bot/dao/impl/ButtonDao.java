package com.bartosso.bot.dao.impl;


import com.bartosso.bot.dao.AbstractDao;
import com.bartosso.bot.dao.rowMaps.ButtonMap;
import com.bartosso.bot.entity.Button;
import com.bartosso.bot.exception.CommandNotFoundException;

import org.springframework.jdbc.core.JdbcTemplate;


import java.sql.SQLException;

/**
 * Created by user on 1/2/17.
 * ';' - separator for rows
 * ',' - separator for buttons
 */

@SuppressWarnings("unchecked")
public class ButtonDao extends AbstractDao {


    public ButtonDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }


    public Button getButton(String text) throws CommandNotFoundException, SQLException {
try {
            return (Button) jdbcTemplate.queryForObject("SELECT * FROM PUBLIC.BUTTON WHERE TEXT=?"
                    ,new Object[] {text}
                    ,new ButtonMap());}
                     catch (Exception e){
    throw new CommandNotFoundException(e);
                     }
    }


    public String getButtonText(int id) throws SQLException {
        return jdbcTemplate.queryForObject("SELECT text FROM BUTTON where id="+ id, (rs, rowNum) -> rs.getString(1));
    }

    Button getButton(int id) throws SQLException {
        try {
            return getButton(getButtonText(id));
        } catch (CommandNotFoundException e) {
            throw new RuntimeException(e);
        }
    }



}
