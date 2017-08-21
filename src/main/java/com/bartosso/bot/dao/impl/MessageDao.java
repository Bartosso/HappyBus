package com.bartosso.bot.dao.impl;


import com.bartosso.bot.dao.AbstractDao;
import com.bartosso.bot.dao.rowMaps.MessageMap;
import com.bartosso.bot.entity.Message;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;




import java.sql.SQLException;

/**
 * Created by user on 12/11/16.
 */

@Component
@Repository
public class MessageDao extends AbstractDao {



    public MessageDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }



    public Message getMessage(long messageId) throws SQLException {
        //noinspection unchecked
        return (Message) jdbcTemplate.queryForObject("SELECT * FROM message WHERE ID=?", new Object[] {messageId}, new MessageMap());

    }

    public void updateMessage(String newText, String photo, long messageId){
        if (photo!=null){
            jdbcTemplate.update("UPDATE message SET (text,photo) = (?,?) WHERE id=?",newText,photo,messageId);
        } else {
            jdbcTemplate.update("UPDATE message SET (text,photo) = (?, DEFAULT ) WHERE id=?", newText,messageId);
        }
    }

}
