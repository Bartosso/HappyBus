package com.bartosso.bot.dao.impl;

import com.bartosso.bot.dao.AbstractDao;
import org.springframework.jdbc.core.JdbcTemplate;
import org.telegram.telegrambots.api.objects.Contact;

public class ManagerDao extends AbstractDao {
    public ManagerDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

public void insertNewManager(Contact contact){
    jdbcTemplate.update("INSERT INTO manager (id, i) VALUES (?,1)" +
            "     ON CONFLICT (i) DO UPDATE SET id = ?",contact.getUserID(),contact.getUserID());
}
public long getManagerChatId(){
    return jdbcTemplate.queryForObject("SELECT id FROM manager WHERE i = 1",Long.class);
}
}
