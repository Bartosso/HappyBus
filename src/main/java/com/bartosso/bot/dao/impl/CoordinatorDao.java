package com.bartosso.bot.dao.impl;

import com.bartosso.bot.dao.AbstractDao;
import com.bartosso.bot.dao.rowMaps.CoordinatorMap;
import com.bartosso.bot.entity.ProjectEntities.Coordinator;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

@SuppressWarnings("unchecked")
public class CoordinatorDao extends AbstractDao {


    public CoordinatorDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public void addNew(Coordinator coordinator) {
        jdbcTemplate.update("INSERT INTO coordinators (name, id, phone) VALUES (?,?,?)",
                coordinator.getName(),coordinator.getId(),coordinator.getPhone());
    }

//    public Coordinator getCoordinatorByChatId(long chatId) {
//        return (Coordinator) jdbcTemplate.queryForObject("SELECT * FROM coordinators WHERE chat_id=?",new Object[] {chatId},
//                new CoordinatorMap());
//    }

    public boolean isCoordinator(long chatId) {
        return jdbcTemplate.queryForObject("select exists(select * from coordinators where id=?)",Boolean.class, chatId);
    }

    public List getAllCoordinators() {
        List<Coordinator> a = jdbcTemplate.query("SELECT * FROM coordinators",new CoordinatorMap());
        if (a.isEmpty()) return null;
        else return a;
    }

    public void deleteCoordinator(Integer idToDelete){
        jdbcTemplate.update("DELETE FROM coordinators WHERE id=?", idToDelete);
    }

}
