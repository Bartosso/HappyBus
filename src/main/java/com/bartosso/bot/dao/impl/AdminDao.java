package com.bartosso.bot.dao.impl;

import com.bartosso.bot.dao.AbstractDao;
import org.springframework.jdbc.core.JdbcTemplate;

public class AdminDao extends AbstractDao {
    public AdminDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public long getAdminId(){
        return jdbcTemplate.queryForObject("SELECT * FROM admin",Long.class);
    }

    public boolean isAdmin(long chatId) {
        return jdbcTemplate.queryForObject("select exists(select * from admin where id=?)",Boolean.class, chatId);
    }

    public void insertAdmin(long chatId){
        jdbcTemplate.update("INSERT  INTO  admin (id) VALUES (?)",chatId);
    }

    public boolean isAnyAdminExist() {
        return jdbcTemplate.queryForObject("select exists(select * from admin)",Boolean.class);
    }

}
