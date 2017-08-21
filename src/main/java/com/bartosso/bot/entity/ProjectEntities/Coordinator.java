package com.bartosso.bot.entity.ProjectEntities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.api.objects.Contact;

@AllArgsConstructor
@Getter
@Setter
public class Coordinator implements Entity {
    private long   id;
    private String name;
    private String phone;

    @Override
    public String getTextToButton() {
        return name;
    }

    public Coordinator(Contact contact){
        if (contact.getLastName()!=null){
            this.name = contact.getFirstName() +" "+ contact.getLastName();
        } else {
            this.name = contact.getFirstName();
        }
        this.id= contact.getUserID();
        this.phone   = contact.getPhoneNumber();



    }
}
