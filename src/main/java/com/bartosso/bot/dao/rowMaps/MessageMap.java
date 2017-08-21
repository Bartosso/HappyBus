package com.bartosso.bot.dao.rowMaps;


import com.bartosso.bot.entity.Message;
import org.springframework.jdbc.core.RowMapper;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MessageMap implements RowMapper {

    @Override
    public Message mapRow(ResultSet rs, int rowNum) throws SQLException {
        Message message = new Message();
        message.setSendMessage(new SendMessage().setText(rs.getString("text")));
        message.setKeyboardMarkUpId(rs.getLong(                       "keyboard_id"));
        String photo = rs.getString("photo");
        if (photo!=null){
        if (!photo.equals("")){
            message.setSendPhoto(new SendPhoto().setPhoto(photo));
    }} return message;
    }
}
